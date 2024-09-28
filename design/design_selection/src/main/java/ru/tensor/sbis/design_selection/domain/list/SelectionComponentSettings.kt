package ru.tensor.sbis.design_selection.domain.list

/**
 * Настройки списка компонента выбора.
 *
 * @author vv.chekurda
 */
internal data class SelectionComponentSettings(
    val showStubs: Boolean = true,
    val showPagingLoaders: Boolean = true
)