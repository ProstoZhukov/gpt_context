package ru.tensor.sbis.design.selection.ui.view.selecteditems.utils

import ru.tensor.sbis.design.selection.ui.view.selecteditems.model.SelectedTextItem
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.view.SelectedItemsContainerView

/**
 * Добавление данных для [SelectedItemsContainerView] отображения в превью
 *
 * @author ma.kolpakov
 */
internal fun SelectedItemsContainerView.showPreview() {
    setItems((0..100).map { SelectedTextItem("$it", "Demo item $it") })
}
