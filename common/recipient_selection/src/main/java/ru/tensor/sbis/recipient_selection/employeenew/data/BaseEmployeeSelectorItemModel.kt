package ru.tensor.sbis.recipient_selection.employeenew.data

import ru.tensor.sbis.mvp.multiselection.data.MultiSelectionItem
import ru.tensor.sbis.design.selection.ui.model.HierarchySelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.RecipientSelectorItemModel
import ru.tensor.sbis.recipient_selection.employee.ui.data.result.EmployeesSelectionResultData

/**
 * Базовый интерфейс ячейки данных для экрана выбора сотрудников
 * @property[oldVm] Старая вью-модель для совместимости с [EmployeesSelectionResultData]
 *
 * @author sr.golovkin on 03.08.2020
 */
interface BaseEmployeeSelectorItemModel: RecipientSelectorItemModel, HierarchySelectorItemModel {

    val oldVm: MultiSelectionItem
}