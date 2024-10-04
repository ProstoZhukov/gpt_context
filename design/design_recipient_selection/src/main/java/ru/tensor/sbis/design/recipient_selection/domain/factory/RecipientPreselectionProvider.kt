package ru.tensor.sbis.design.recipient_selection.domain.factory

import ru.tensor.sbis.communication_decl.selection.SelectionConfig
import ru.tensor.sbis.communication_decl.selection.SelectionMode
import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientDepartmentId
import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientPerson
import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientPersonId
import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientPreselectedData
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResult
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultManager
import ru.tensor.sbis.design_selection_common.PreselectedDataProvider
import ru.tensor.sbis.design_selection_common.controller.PreselectedData
import javax.inject.Inject

/**
 * Реализация поставщика предвыбранных данных для компонента выбора получателей.
 *
 * @property recipientSelectionManager менеджер для работы с результатами компонента выбора получателей.
 *
 * @author vv.chekurda
 */
internal class RecipientPreselectionProvider @Inject constructor(
    private val recipientSelectionManager: RecipientSelectionResultManager
) : PreselectedDataProvider {

    private val lastResult: RecipientSelectionResult
        get() = recipientSelectionManager.selectionResult

    private val preselectedData: RecipientPreselectedData?
        get() = recipientSelectionManager.preselectedData

    override fun getPreselectedData(config: SelectionConfig): PreselectedData =
        when {
            // Для одиночного выбора нет предвыбранных элементов.
            config.selectionMode == SelectionMode.SINGLE
                || config.selectionMode == SelectionMode.SINGLE_WITH_APPEND -> {
                PreselectedData()
            }
            // Есть последний рабочий результат - используем его для повторного открытия в том же состоянии.
            !lastResult.isCleared && lastResult.requestKey == config.requestKey -> {
                PreselectedData(
                    ids = lastResult.data.recipients.map {
                        if (it is RecipientPerson) {
                            RecipientPersonId(it.uuid)
                        } else {
                            RecipientDepartmentId(it.uuid)
                        }
                    }
                )
            }
            // Компонент находится в чистом состоянии, проверяем данные для предустановки.
            else -> {
                PreselectedData(
                    ids = preselectedData?.ids?.also {
                        recipientSelectionManager.preselect(data = null)
                    }.orEmpty()
                )
            }
        }
}