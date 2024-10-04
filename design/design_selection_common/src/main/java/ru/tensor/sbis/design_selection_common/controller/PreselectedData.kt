package ru.tensor.sbis.design_selection_common.controller

import ru.tensor.sbis.communication_decl.selection.SelectionItemId

/**
 * Данные для установки предвыбранных элементов в компоненте выбора.
 *
 * @property ids идентификаторы элементов.
 *
 * @author vv.chekurda
 */
class PreselectedData(
    val ids: List<SelectionItemId> = emptyList()
)