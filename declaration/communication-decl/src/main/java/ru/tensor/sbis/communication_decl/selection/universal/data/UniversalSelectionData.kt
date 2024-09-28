package ru.tensor.sbis.communication_decl.selection.universal.data

/**
 * Данные результата компонента универсального выбора.
 *
 * @author vv.chekurda
 */
data class UniversalSelectionData(
    val items: List<UniversalSelectionItem> = emptyList()
)