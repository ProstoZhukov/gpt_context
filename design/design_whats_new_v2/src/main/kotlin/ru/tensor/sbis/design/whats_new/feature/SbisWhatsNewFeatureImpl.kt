package ru.tensor.sbis.design.whats_new.feature

import android.content.Context
import android.view.Gravity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import ru.tensor.sbis.application_tools.DebugTools
import ru.tensor.sbis.common.util.AppConfig
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.common.util.hasFragmentOrPendingTransaction
import ru.tensor.sbis.design.whats_new.domain.ShowConditionManager
import ru.tensor.sbis.design.whats_new.domain.ShowConditionManagerImpl
import ru.tensor.sbis.design.whats_new.feature.SbisWhatsNewFeature.Companion.WHATS_NEW_FRAGMENT_TAG
import ru.tensor.sbis.design.whats_new.ui.WhatsNewFragment
import ru.tensor.sbis.design_dialogs.dialogs.container.tablet.TabletContainerDialogFragment
import ru.tensor.sbis.design_dialogs.dialogs.container.tablet.VisualParamsBuilder
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreator
import ru.tensor.sbis.verification_decl.login.LoginInterface

/**
 * Реализация [SbisWhatsNewFeature].
 *
 * @author ps.smirnyh
 */
internal class SbisWhatsNewFeatureImpl(
    private val context: Context,
    loginInterface: LoginInterface?,
    internal val showConditionManager: ShowConditionManager = ShowConditionManagerImpl(
        AppConfig.getApplicationCurrentVersion(),
        context,
        loginInterface
    )
) : SbisWhatsNewFeature {

    override fun isNeedShow() = !showConditionManager.checkShowing() && !DebugTools.isAutoTestLaunch

    override fun openIfNeeded(
        fragmentManager: FragmentManager,
        containerId: Int,
        isPopBackStackEnable: Boolean
    ): Boolean {
        if (isNeedShow() &&
            !fragmentManager.hasFragmentOrPendingTransaction(WHATS_NEW_FRAGMENT_TAG)
        ) {
            if (DeviceConfigurationUtils.isTablet(context)) {
                TabletContainerDialogFragment()
                    .setInstant(true)
                    .setCancelableContainer(false)
                    .setVisualParams(
                        VisualParamsBuilder()
                            .gravity(Gravity.CENTER)
                            .setBoundingRectFromTargetFragment()
                            .build()
                    )
                    .setContentCreator(object : ContentCreator {
                        override fun createFragment(): Fragment = WhatsNewFragment.newInstance(isPopBackStackEnable)
                    }).show(fragmentManager, WHATS_NEW_FRAGMENT_TAG)
            } else {
                val fragment = WhatsNewFragment.newInstance(isPopBackStackEnable)
                fragmentManager.commit(allowStateLoss = true) {
                    add(containerId, fragment, WHATS_NEW_FRAGMENT_TAG)
                    if (isPopBackStackEnable) {
                        addToBackStack(WHATS_NEW_FRAGMENT_TAG)
                    }
                }
            }
            return true
        }
        return false
    }
}