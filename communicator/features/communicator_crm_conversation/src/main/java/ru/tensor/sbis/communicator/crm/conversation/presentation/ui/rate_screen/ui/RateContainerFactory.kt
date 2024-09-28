package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.rate_screen.ui

import android.content.Context
import android.view.Gravity
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import ru.tensor.sbis.design_dialogs.dialogs.container.tablet.TabletContainerDialogFragment
import ru.tensor.sbis.design_dialogs.dialogs.container.tablet.VisualParamsBuilder
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreatorParcelable
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanelPeekHeight
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableDialogFragment

/**
 * Фабрика для создания dialog-контейнера для экрана оценки качества работы оператора.
 *
 * @author dv.baranov
 */
internal object RateContainerFactory {

    /**
     * Создать контейнер для экрана оценки качества работы оператора.
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
            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            .setContainerBackgroundColor(context.getColorFromAttr(R.attr.backgroundColorDialog))
            .instant(false)
            .build()

    private fun createTabletDialogContainer(contentCreator: ContentCreatorParcelable): DialogFragment {
        val params = VisualParamsBuilder()
            .belowActionBar()
            .gravity(Gravity.CENTER)
            .horizontalMargin()
            .fixedWidth()
            .wrapHeight(true)
            .softInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            .build()
        return TabletContainerDialogFragment()
            .setVisualParams(params)
            .setInstant(false)
            .setContentCreator(contentCreator)
    }
}