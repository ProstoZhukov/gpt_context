package ru.tensor.sbis.share_menu.utils

import android.content.Context
import android.view.Gravity
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
import androidx.fragment.app.DialogFragment
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import ru.tensor.sbis.design_dialogs.dialogs.container.tablet.TabletContainerDialogFragment
import ru.tensor.sbis.design_dialogs.dialogs.container.tablet.VisualParamsBuilder
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreatorParcelable
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanelPeekHeight
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.share_menu.R
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableDialogFragment

/**
 * Фабрика для создания dialog-контейнера для компонента меню для "поделиться".
 *
 * @author vv.chekurda
 */
internal object ShareMenuContainerFactory {

    /**
     * Создать контейнер для компонента меню для "поделиться".
     *
     * @param context контекст.
     * @param contentCreator реализация [ContentCreatorParcelable] для встраивания в контейнер.
     */
    fun createContainer(context: Context, contentCreator: ContentCreatorParcelable): DialogFragment =
        if (DeviceConfigurationUtils.isTablet(context)) {
            createTabletDialogContainer(contentCreator)
        } else {
            createMovablePanelContainer(context, contentCreator)
        }

    private fun createMovablePanelContainer(
        context: Context,
        contentCreator: ContentCreatorParcelable,
    ): DialogFragment =
        ContainerMovableDialogFragment.Builder()
            .setContentCreator(contentCreator)
            .setExpandedPeekHeight(MovablePanelPeekHeight.FitToContent())
            .setDefaultHeaderPaddingEnabled(true)
            .setSoftInputMode(SOFT_INPUT_ADJUST_PAN)
            .setContainerBackgroundColor(context.getColorFromAttr(RDesign.attr.unaccentedAdaptiveBackgroundColor))
            .setMovablePanelTheme(R.style.ShareMenuMovablePanelTheme)
            .instant(false)
            .build()

    private fun createTabletDialogContainer(contentCreator: ContentCreatorParcelable): DialogFragment {
        val params = VisualParamsBuilder()
            .dialogStyle(R.style.ShareMenuTabletContainerDialogStyle)
            .belowActionBar()
            .gravity(Gravity.CENTER)
            .horizontalMargin()
            .fixedWidth()
            .wrapHeight(false)
            .softInputMode(SOFT_INPUT_ADJUST_PAN)
            .build()
        return TabletContainerDialogFragment()
            .setVisualParams(params)
            .setInstant(false)
            .setContentCreator(contentCreator)
    }
}