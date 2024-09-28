package ru.tensor.sbis.onboarding.ui.page

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import dagger.android.support.AndroidSupportInjection
import ru.tensor.sbis.mvvm.argument
import ru.tensor.sbis.onboarding.R
import ru.tensor.sbis.onboarding.databinding.OnboardingFragmentFeaturePageBinding
import ru.tensor.sbis.onboarding.ui.base.OnboardingBaseFragment
import ru.tensor.sbis.onboarding.ui.host.ImageFrameListener
import ru.tensor.sbis.onboarding.ui.host.adapter.PageParams
import ru.tensor.sbis.onboarding.ui.utils.ThemeProvider
import ru.tensor.sbis.onboarding.ui.utils.withArgs
import javax.inject.Inject

/**
 * Фрагмент отображения конкретной фичи или заглушки
 *
 * @author as.chadov
 */
internal class OnboardingFeatureFragment :
    OnboardingBaseFragment<OnboardingFeatureVM, OnboardingFragmentFeaturePageBinding>() {

    val params: PageParams by argument(ARG_PARAMS)

    @Inject
    lateinit var themeProvider: ThemeProvider
    override val vmClass = OnboardingFeatureVM::class.java
    override val layoutId: Int = R.layout.onboarding_fragment_feature_page
    override val themeId: Int
        get() = themeProvider.getFeatureTheme(requireContext(), params.uuid)

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = super.onCreateView(inflater, container, savedInstanceState)
        image?.addOnLayoutChangeListener(layoutListener)
        return root
    }

    override fun onDestroyView() {
        image?.removeOnLayoutChangeListener(layoutListener)
        super.onDestroyView()
    }

    private val image: ImageView?
        get() = binding?.onboardingImageContainer?.onboardingImage

    private val layoutListener =
        View.OnLayoutChangeListener { view, _, _, _, _, _, oldTop, _, oldBottom ->
            val oldHeight = oldBottom - oldTop
            val newHeight = view.height
            if (oldHeight != newHeight) {
                val frameListener = parentFragment as? ImageFrameListener
                frameListener?.run { onChangeFrame(newHeight) }
            }
        }

    companion object {
        fun newInstance(params: PageParams) = OnboardingFeatureFragment().withArgs {
            putSerializable(ARG_PARAMS, params)
        }

        private const val ARG_PARAMS = "ARG_PARAMS"
    }
}