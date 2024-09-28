package ru.tensor.sbis.communicator.dialog_selection.di

import android.content.Context
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent

/**
 * Расширение для получения di компонента экрана выбора диалога/участников
 * @return [DialogSelectionComponent] компонент экрана
 *
 * @author vv.chekurda
 */
internal fun Context.getDialogSelectionComponent(): DialogSelectionComponent =
    DaggerDialogSelectionComponent.builder()
        .communicatorCommonComponent(CommunicatorCommonComponent.getInstance(this))
        .build()
