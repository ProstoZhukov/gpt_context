package ru.tensor.sbis.modalwindows.optionscontent

import android.content.Context
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.StyleRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.design_dialogs.dialogs.container.tablet.VisualParams
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreatorParcelable
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanelPeekHeight
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableDialogFragment

/**
 * Утилита для создания контейнера в зависимости от конфигурации устройства.
 *
 * @author ev.grigoreva
 */
object ContainerMovableUtil {

    /**
     * Показать контент внутри диалогового окна в зависимости от конфигурации устройства.
     * На телефоне отображает контент в шторке.
     *
     * @see ContainerUtil.showInContainer
     */
    fun showInContainer(
        context: Context,
        contentCreator: ContentCreatorParcelable,
        fragmentManager: FragmentManager,
        isInstant: Boolean = true,
        fragmentTag: String?,
        customVisualParams: VisualParams? = null,
        viewTag: String? = null,
        @IdRes viewId: Int = View.NO_ID,
        addToBackStack: Boolean = false,
        sheetSoftInputMode: Int? = null,
        forceStandardWindowSizeOnTablet: Boolean = false,
        peekMode: MovablePanelPeekHeight = MovablePanelPeekHeight.FitToContent(),
        isTablet: Boolean = DeviceConfigurationUtils.isTablet(context),
        @StyleRes
        movablePanelThemeRes: Int = ru.tensor.sbis.design.design_dialogs.R.style.MovablePanelDefaultTheme,
        ignoreLock: Boolean = false,
        isAutoClosable: Boolean = true
    ) {
        if (isTablet) {
            ContainerUtil.showInContainer(
                context,
                contentCreator,
                fragmentManager,
                isInstant,
                fragmentTag,
                customVisualParams,
                viewTag,
                viewId,
                addToBackStack = addToBackStack,
                sheetSoftInputMode = sheetSoftInputMode,
                forceStandardWindowSizeOnTablet = forceStandardWindowSizeOnTablet
            )
        } else {
            prepareNewDialog(
                fragmentManager,
                fragmentTag,
                    ContainerMovableDialogFragment.Builder()
                    .instant(isInstant)
                    .setContentCreator(contentCreator)
                    .setExpandedPeekHeight(peekMode)
                    .setMovablePanelTheme(movablePanelThemeRes)
                    .setIgnoreLock(ignoreLock)
                    .setAutoCloseable(isAutoClosable)
                    .build()
            ).apply {
                if (addToBackStack) {
                    show(fragmentManager.beginTransaction().addToBackStack(fragmentTag), fragmentTag)
                } else {
                    show(fragmentManager, fragmentTag)
                }
            }
        }
    }

    private fun prepareNewDialog(
        fragmentManager: FragmentManager,
        fragmentTag: String?,
        newContainer: ContainerMovableDialogFragment
    ): DialogFragment {
        var containerDialog = fragmentManager.findFragmentByTag(fragmentTag) as? ContainerMovableDialogFragment
        containerDialog?.dismissAllowingStateLoss()
        containerDialog = newContainer
        return containerDialog
    }
}