package ru.tensor.sbis.design.selection.ui.list

import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.list.base.domain.ListInteractor
import ru.tensor.sbis.list.base.domain.boundary.View

/**
 * Расширение [ListInteractor], которое позволяет учитывать в списке выбранные элементы и их состояние отображения
 *
 * @author ma.kolpakov
 */
internal interface SelectionListInteractor<SERVICE_RESULT : Any, FILTER, ANCHOR, ENTITY> : ListInteractor<ENTITY>
        where ENTITY : SelectionListScreenEntity<SERVICE_RESULT, FILTER, ANCHOR> {

    fun applySelection(
        selection: List<SelectorItemModel>,
        entity: SelectionListScreenEntity<SERVICE_RESULT, FILTER, ANCHOR>,
        view: View<SelectionListScreenEntity<SERVICE_RESULT, FILTER, ANCHOR>>
    )
}