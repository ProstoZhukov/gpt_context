package ru.tensor.sbis.whats_new

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.PaintDrawable
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import ru.tensor.sbis.application_tools.DebugTools
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.utils.getDataFromAttrOrNull
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.onboarding.ui.host.OnboardingHostFragment
import ru.tensor.sbis.whats_new.databinding.WhatsNewFragmentBinding
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.onboarding.R as ROnBoarding

/**
 * Фрагмент для компонента "Что нового".
 *
 * @author ps.smirnyh
 */
internal class WhatsNewFragment : Fragment() {

    /**
     * Поле для хранения радительского фрагмента [OnboardingHostFragment].
     * Можно использовать только в featurePage и customPage в Onboarding.
     */
    private val onboardingHostFragment: OnboardingHostFragment
        get() = checkNotNull(parentFragment as? OnboardingHostFragment)

    private var animator: ValueAnimator? = null
    private var binding: WhatsNewFragmentBinding? = null
    private var widthForShowImage: Int = 0
    private val textBounds = Rect()
    private var animatorUpdateListener: ValueAnimator.AnimatorUpdateListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val themeId = onboardingHostFragment.themeProvider.getFeatureTheme(requireContext(), "")
        val newInflater = applyStyle(inflater = inflater, themeId = themeId)
        widthForShowImage = newInflater.context.getDataFromAttrOrNull(R.attr.whats_new_hide_image_before_width) ?: 0
        return WhatsNewFragmentBinding.inflate(newInflater, container, false).apply {
            binding = this
            val whatsNewText = getString(WhatsNewPlugin.customizationOptions.whatsNewRes)
            val whatsNewItems = whatsNewText.split("|")
            if (whatsNewItems.size == 1) {
                fillSingleItemWhatsNew(whatsNewText, newInflater.context)
            } else {
                fillLotsItemWhatsNew(whatsNewItems, newInflater.context)
            }
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.whatsNewButtonStart?.setOnClickListener { onCloseOnboarding() }
        initAnimator()
        initStatusBar()
        setupMarginsViews()
        if (resources.configuration.screenWidthDp < widthForShowImage) {
            binding?.whatsNewImage?.isVisible = false
        }
    }

    private fun setupMarginsViews() {
        val onboardingBanner = requireParentFragment().requireView()
            .findViewById<ConstraintLayout>(ROnBoarding.id.onboarding_banner_container)
        val defaultOffsetTitle = requireContext().getDimenPx(RDesign.attr.offset_2xl)

        ViewCompat.setOnApplyWindowInsetsListener(
            requireParentFragment().requireView()
        ) { _, insets ->
            onboardingBanner.updatePadding(top = insets.systemWindowInsetTop)
            binding?.whatsNewTitle?.updateLayoutParams {
                this as MarginLayoutParams
                this.topMargin = defaultOffsetTitle + onboardingBanner.measuredHeight
            }
            insets.consumeSystemWindowInsets()
        }
    }

    private fun initStatusBar() {
        requireParentFragment().requireView().systemUiVisibility =
            SYSTEM_UI_FLAG_LAYOUT_STABLE or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        requireActivity().window.statusBarColor = Color.TRANSPARENT
    }


    private fun initAnimator() {
        animatorUpdateListener = ValueAnimator.AnimatorUpdateListener { newValue ->
            binding?.whatsNewBlurShapes?.translationY = -(newValue.animatedValue as Float)
        }
        animator = ValueAnimator.ofFloat(
            0f, resources.getDimension(R.dimen.whats_new_blur_shapes_margin_bottom)
        ).apply {
            addUpdateListener(animatorUpdateListener)
            interpolator = AccelerateDecelerateInterpolator()
            duration = 3100L
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
        }
        if (!DebugTools.isAutoTestLaunch) animator?.start()
    }

    override fun onDestroyView() {
        animator?.cancel()
        animator?.removeAllUpdateListeners()
        animator = null
        animatorUpdateListener = null
        binding = null
        super.onDestroyView()
    }

    /**
     * Метод для создания одного пункта "Что нового" из строкового ресурса.
     */
    private fun fillSingleItemWhatsNew(whatsNewText: String, context: Context) {
        // Без отступов.
        val layoutParamsDefault = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )

        // Текст новой фичи.
        val descriptionTextView =
            SbisTextView(context, null, ROnBoarding.attr.onboardingDescriptionStyle).apply {
                id = R.id.whats_new_description_text_id
                layoutParams = layoutParamsDefault
                textAlignment = TextView.TEXT_ALIGNMENT_INHERIT
                gravity = Gravity.START
                minLines = 0
                includeFontPadding = false
                text = whatsNewText.trim()
            }

        binding?.whatsNewDescriptionContainerInner?.addView(descriptionTextView)
    }

    /**
     * Метод для создания списка "Что нового" из строкового ресурса с разделителем "|".
     */
    private fun fillLotsItemWhatsNew(
        whatsNewArrayItems: List<String>,
        context: Context
    ) {
        for (whatsNewItem in whatsNewArrayItems) {
            // С отступом сверху.
            val layoutParamsWithMarginTop = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val marginTop =
                if (whatsNewArrayItems.indexOf(whatsNewItem) == 0) 0 else context.getDimenPx(RDesign.attr.offset_l)

            layoutParamsWithMarginTop.setMargins(0, marginTop, 0, 0)

            // С отступами по бокам.
            val layoutParamsWithMarginLeft = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParamsWithMarginLeft.setMargins(
                context.getDimenPx(RDesign.attr.offset_l), 0,
                0, 0
            )

            // Контейнер для параграфа текста.
            val descriptionLayout = LinearLayout(context).apply {
                layoutParams = layoutParamsWithMarginTop
                orientation = LinearLayout.HORIZONTAL
                isBaselineAligned = false
            }

            // Текст новой фичи.
            val descriptionTextView =
                SbisTextView(context, null, ROnBoarding.attr.onboardingDescriptionStyle).apply {
                    id = R.id.whats_new_description_text_id
                    layoutParams = layoutParamsWithMarginLeft
                    gravity = Gravity.START
                    textAlignment = TextView.TEXT_ALIGNMENT_INHERIT
                    minLines = 0
                    includeFontPadding = false
                    text = whatsNewItem.trim()
                }

            // Маркер списка.
            val descriptionViewDot = View(context).apply {
                layoutParams = MarginLayoutParams(
                    resources.getDimensionPixelSize(R.dimen.whats_new_marker_width),
                    resources.getDimensionPixelSize(R.dimen.whats_new_marker_height)
                ).apply {
                    descriptionTextView.paint.getTextBounds(
                        descriptionTextView.text.toString(),
                        0,
                        descriptionTextView.text?.length ?: 0,
                        textBounds
                    )
                    val margin = (textBounds.height() + this.height) / 2
                    setMargins(0, margin, 0, 0)
                }
                background = PaintDrawable(context.getThemeColorInt(RDesign.attr.markerColor)).apply {
                    setCornerRadius(resources.getDimension(R.dimen.whats_new_marker_height) / 2)
                }

            }

            descriptionLayout.addView(descriptionViewDot)
            descriptionLayout.addView(descriptionTextView)

            binding?.whatsNewDescriptionContainerInner?.addView(descriptionLayout)
        }
    }

    /**
     * Метод для закрытия "Что нового" и выход на главный экран.
     */
    private fun onCloseOnboarding() {
        val onboardingHostViewModel = onboardingHostFragment.getViewModel
        onboardingHostViewModel.onCloseOnboarding()
    }

    private fun applyStyle(inflater: LayoutInflater, themeId: Int): LayoutInflater {
        return if (themeId != 0) {
            val themeWrapper = ContextThemeWrapper(activity, themeId)
            themeWrapper.theme.applyStyle(R.style.FeatureTheme_WhatsNew, true)
            inflater.cloneInContext(themeWrapper)
        } else inflater
    }
}