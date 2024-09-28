package ru.tensor.sbis.communication_decl.selection.universal.data

/**
 * Данные для предустановки выбранных элементов для универсального выбора.
 *
 * @property ids идентификаторы элементов.
 *
 * @author vv.chekurda
 */
class UniversalPreselectedData(
    val ids: List<UniversalItemId> = emptyList()
)