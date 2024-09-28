package ru.tensor.sbis.marks.view.api

import android.widget.LinearLayout
import androidx.core.view.children
import ru.tensor.sbis.marks.item.SbisMarksElementView
import ru.tensor.sbis.marks.model.SbisMarksCheckboxStatus
import ru.tensor.sbis.marks.model.SbisMarksComponentType
import ru.tensor.sbis.marks.model.item.SbisMarksElement
import ru.tensor.sbis.marks.item.api.SbisMarksElementViewApi
import ru.tensor.sbis.marks.style.SbisMarksStyleHolder
import ru.tensor.sbis.marks.utils.checker.ColorSingleSelectionChecker
import ru.tensor.sbis.marks.utils.checker.api.MarksSelectionRulesChecker
import ru.tensor.sbis.marks.view.SbisMarksListView

/**
 * Контроллер view-компонента списка пометок с логикой.
 *
 * @property items список моделей пометок
 * @property componentType значение типа списка пометок
 * @property selectionCompleteListener слушатель завершения выбора пометок
 * @property selectionChangeListener слушатель изменения выбора пометки
 *
 * @author ra.geraskin
 */

internal class MarksListController(
    override var items: List<SbisMarksElement>,
    override var componentType: SbisMarksComponentType,
    override var selectionCompleteListener: ((selectedItems: List<SbisMarksElement>) -> Unit)?,
    override var selectionChangeListener: ((selectedItem: SbisMarksElement) -> Unit)?,
    private val selectionRulesChecker: MarksSelectionRulesChecker = ColorSingleSelectionChecker()

) : SbisMarksListViewApi {

    private lateinit var marksListView: SbisMarksListView

    /**
     * Метод прикрепления View-компонента списка к контроллеру.
     * Здесь же происходит инициализация view-компонента списка пометок.
     */
    fun attachMarksListView(
        marksListView: SbisMarksListView,
        styleHolder: SbisMarksStyleHolder = SbisMarksStyleHolder.create(
            marksListView.context,
            componentType.isCheckboxVisible
        )
    ) {
        this.marksListView = marksListView
        items.forEach { item ->
            marksListView.addView(
                SbisMarksElementView(
                    context = marksListView.context,
                    item = item,
                    isDefaultTitleStyle = componentType.isDefaultTextStyle,
                    isCheckboxVisible = componentType.isCheckboxVisible,
                    styleHolder = styleHolder,
                    selectionChangeListener = {
                        selectionRulesChecker.checkSelectionRules(item, marksListView.children)
                        selectionChangeListener?.invoke(item)
                    },
                    elementClickListener = {
                        selectionRulesChecker.checkSelectionRules(item, marksListView.children)
                        selectionCompleteListener?.invoke(getSelectedWithItem(item))
                    }
                ),
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )
        }
    }

    /**
     * Очистить чекбоксы всех пометок. Удалить все selections.
     */
    override fun clearAll() {
        marksListView.children.forEach { child ->
            if (child is SbisMarksElementViewApi) child.clearSelection()
        }
    }

    /**
     * Получить список выбранных моделей пометок.
     */
    override fun getSelected(): List<SbisMarksElement> =
        marksListView.children
            .filterIsInstance(SbisMarksElementViewApi::class.java)
            .filter { it.getSelectionStatus() == SbisMarksCheckboxStatus.CHECKED }
            .map { it.item }
            .toList()

    /**
     * Получить список выбранных пользователем пометок, в том числе с пометкой по которой произошёл клик.
     */
    private fun getSelectedWithItem(item: SbisMarksElement): List<SbisMarksElement> {
        getSelected().toMutableList().run {
            if (!contains(item)) add(item)
            return this
        }
    }

}