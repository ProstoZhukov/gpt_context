package ru.tensor.sbis.design.selection.bl.contract.listener

/**
 * Псевдоним метода для обработки переходов ко вложенным элементам в компоненте выбора
 *
 * @author ma.kolpakov
 */
internal typealias OpenHierarchyListener<DATA> = (item: DATA) -> Unit