/**
 * Расширения и встраиваемые функции, упрощающие работу с компонентом выбора получателей.
 *
 * @author vv.chekurda
 */
package ru.tensor.sbis.design.recipient_selection.domain.factory

import android.content.Context
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionConfig
import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientId
import ru.tensor.sbis.design.recipient_selection.ui.di.singleton.RecipientSelectionComponentProvider
import ru.tensor.sbis.design.recipient_selection.ui.di.screen.RecipientSelectionComponent
import ru.tensor.sbis.design_selection_common.controller.SelectionControllerProviderImpl

/**
 * Получить di-компонент для выбора получателей.
 *
 * @param config настройка компонента выбора получателей.
 */
internal fun Context.getRecipientSelectionComponent(config: RecipientSelectionConfig): RecipientSelectionComponent =
    RecipientSelectionComponentProvider.getRecipientSelectionComponent(this, config)

internal typealias RecipientSelectionControllerProvider =
    SelectionControllerProviderImpl<RecipientItem, RecipientId>