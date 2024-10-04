package ru.tensor.sbis.design.selection.ui.view.selecteditems.panel

import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * Базовый класс для взаимодействия компонента выбора с панелью выбранных элементов. Типы элементов определяются
 * реализациями
 *
 * @author us.bessonov
 */
internal interface AbstractSelectionPanel<in DATA : SelectorItemModel> {

    /**
     * Отображает выбранные элементы типа [DATA]
     */
    fun setSelectedItems(list: List<DATA>)
}