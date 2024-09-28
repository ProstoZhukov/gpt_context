package ru.tensor.sbis.design.whats_new.ui

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.common.util.findParentFragment
import ru.tensor.sbis.design.whats_new.SbisWhatsNewPlugin
import ru.tensor.sbis.design.whats_new.feature.SbisWhatsNewFeature
import ru.tensor.sbis.design_dialogs.dialogs.container.Container

/**
 * [OnBackPressedCallback] для [WhatsNewFragment].
 *
 * @author ps.smirnyh
 */
internal class WhatsNewBackPressedCallback(private val fragment: Fragment, enabled: Boolean = true) :
    OnBackPressedCallback(enabled) {

    private val isPopBackStackAvailable: Boolean
        get() = !fragment.parentFragmentManager.isStateSaved &&
            fragment.parentFragmentManager.backStackEntryCount >= 1

    override fun handleOnBackPressed() {
        SbisWhatsNewPlugin.whatsNewFeature.showConditionManager.saveShowing()
        if (DeviceConfigurationUtils.isTablet(fragment.requireContext())) {
            val fm = fragment.parentFragment?.parentFragmentManager ?: fragment.parentFragmentManager
            fragment.findParentFragment<Container.Closeable>()?.closeContainer()
            fm.setFragmentResult(
                SbisWhatsNewFeature.SBIS_WHATS_NEW_FRAGMENT_RESULT_KEY,
                Bundle()
            )
        } else {
            if (isPopBackStackAvailable &&
                fragment.arguments?.getBoolean(WhatsNewFragment.IS_POP_BACK_STACK_ENABLE) == true
            ) {
                fragment.parentFragmentManager.popBackStack()
            }
            fragment.setFragmentResult(SbisWhatsNewFeature.SBIS_WHATS_NEW_FRAGMENT_RESULT_KEY, Bundle())
        }
    }
}