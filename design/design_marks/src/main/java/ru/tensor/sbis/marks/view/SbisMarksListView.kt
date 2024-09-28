package ru.tensor.sbis.marks.view

import android.content.Context
import android.widget.LinearLayout
import ru.tensor.sbis.marks.model.SbisMarksComponentType
import ru.tensor.sbis.marks.model.item.SbisMarksElement
import ru.tensor.sbis.marks.style.SbisMarksStyleHolder
import ru.tensor.sbis.marks.view.api.MarksListController
import ru.tensor.sbis.marks.view.api.SbisMarksListViewApi

/**
 * View-компонент списка пометок.
 *
 * @property items список моделей пометок
 * @property componentType значение типа списка пометок
 * @property selectionCompleteListener слушатель завершения выбора пометок
 * @property selectionChangeListener слушатель изменения выбора пометки
 * @property controller контроллер view-компонента списка пометок с логикой
 *
 * @author ra.geraskin
 */
internal class SbisMarksListView @JvmOverloads constructor(
    context: Context,
    override var items: List<SbisMarksElement> = emptyList(),
    override var componentType: SbisMarksComponentType = SbisMarksComponentType.COLOR_STYLE,
    override var selectionCompleteListener: ((selectedItems: List<SbisMarksElement>) -> Unit)? = null,
    override var selectionChangeListener: ((selectedItem: SbisMarksElement) -> Unit)? = null,
    styleHolder: SbisMarksStyleHolder = SbisMarksStyleHolder.create(context, componentType.isCheckboxVisible),
    private val controller: MarksListController = MarksListController(
        items,
        componentType,
        selectionCompleteListener,
        selectionChangeListener
    )
) : LinearLayout(context), SbisMarksListViewApi by controller {

    init {
        orientation = VERTICAL
        controller.attachMarksListView(this, styleHolder)
    }

}