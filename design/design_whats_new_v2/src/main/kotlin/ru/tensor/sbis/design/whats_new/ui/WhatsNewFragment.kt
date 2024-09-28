package ru.tensor.sbis.design.whats_new.ui

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.design.utils.getDataFromAttrOrNull
import ru.tensor.sbis.design.whats_new.R
import ru.tensor.sbis.design.whats_new.SbisWhatsNewPlugin
import ru.tensor.sbis.design.whats_new.databinding.SbisWhatsNewFragmentBinding
import ru.tensor.sbis.design_dialogs.dialogs.content.Content
import timber.log.Timber

/**
 * Фрагмент для компонента "Что нового".
 *
 * @author ps.smirnyh
 */
internal class WhatsNewFragment : Fragment(), Content {

    private val backPressedCallback = WhatsNewBackPressedCallback(this)
    private val systemUIManager = WhatsNewSystemUIManager()
    private val itemListFactory = WhatsNewItemListFactory()

    private var binding: SbisWhatsNewFragmentBinding? = null
    private var widthForShowImage: Int = 0

    private var isOrientationLocked: Boolean = false
    private var originOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isOrientationLocked = savedInstanceState?.getBoolean(ORIENTATION_KEY) ?: false
        if (!isOrientationLocked) {
            lockOrientation()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val newInflater = applyStyle(inflater = inflater, themeId = R.attr.sbisWhatsNewTheme)
        widthForShowImage = newInflater.context.getDataFromAttrOrNull(R.attr.sbisWhatsNewHideImageBeforeWidth) ?: 0
        return SbisWhatsNewFragmentBinding.inflate(newInflater, container, false).apply {
            binding = this
            itemListFactory.setWhatsNewItems(whatsNewDescriptionContainerInner)
            whatsNewGradient.background = createWhatsNewGradientDrawable(root.context)
            whatsNewButtonStart.style = SbisWhatsNewPlugin.customizationOptions.buttonStyle.style
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        systemUIManager.init(this)
        binding?.let { binding ->
            binding.whatsNewButtonStart.setOnClickListener { onCloseFragment() }
            binding.whatsNewButtonClose.setOnClickListener { onCloseFragment() }
            if (resources.configuration.screenWidthDp < widthForShowImage) {
                binding.whatsNewImage.isVisible = false
            }
            binding.whatsNewLogo.type = SbisWhatsNewPlugin.customizationOptions.bannerLogo
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(backPressedCallback)
    }

    override fun onPause() {
        super.onPause()
        backPressedCallback.remove()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        systemUIManager.reset(this)
        binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isOrientationLocked) return
        if (isRemoving || parentFragment?.isRemoving == true || requireActivity().isFinishing) {
            requireActivity().requestedOrientation = originOrientation
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(ORIENTATION_KEY, isOrientationLocked)
    }

    override fun onBackPressed(): Boolean {
        onCloseFragment()
        return true
    }

    /**
     * Метод для закрытия "Что нового" и выход на главный экран.
     */
    private fun onCloseFragment() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    private fun applyStyle(inflater: LayoutInflater, themeId: Int): LayoutInflater =
        inflater.cloneInContext(
            ContextThemeWrapper(
                inflater.context,
                inflater.context.getDataFromAttrOrNull(themeId) ?: R.style.SbisWhatsNewDefaultTheme
            )
        )

    @SuppressLint("SourceLockedOrientationActivity")
    private fun lockOrientation() {
        if (DeviceConfigurationUtils.isTablet(requireContext())) return
        try {
            isOrientationLocked = true
            originOrientation = requireActivity().requestedOrientation
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    internal companion object {

        /** @SelfDocumented */
        fun newInstance(isPopBackStackEnable: Boolean) =
            WhatsNewFragment().withArgs {
                putBoolean(IS_POP_BACK_STACK_ENABLE, isPopBackStackEnable)
            }

        /** @SelfDocumented */
        internal const val IS_POP_BACK_STACK_ENABLE = "IS_POP_BACK_STACK_ENABLE"
        private const val ORIENTATION_KEY = "ORIENTATION_KEY"
    }
}