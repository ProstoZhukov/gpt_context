package ru.tensor.sbis.communicator.common.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.graphics.withSave
import ru.tensor.sbis.communicator.common.R
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.R as RDesign

/**
 * Layout для отображения чекбокса в ячейках реестра.
 *
 * @author vv.chekurda
 */
class CheckBoxLayout(
    parentView: View,
    private val layoutSize: Int = parentView.dp(CHECK_BOX_SIZE_DP),
    private val leftPadding: Int = parentView.context.getDimenPx(RDesign.attr.offset_st)
) {

    private val checkedDrawable = getDrawable(parentView.context, RDesign.drawable.checkbox_full_icon)
    private val uncheckedDrawable = getDrawable(parentView.context, RDesign.drawable.checkbox_empty_icon)
    private var targetDrawable: Drawable = uncheckedDrawable!!

    /**
     * Ширина разметки.
     */
    val width = layoutSize + leftPadding

    /**
     * Высота разметки.
     */
    val height = layoutSize

    /**
     * Обновить/получить состояние чек-бокса.
     * true для отображения галки выбранного элемента [RDesign.drawable.checkbox_full_icon],
     * false отобразит пустой чек-бокс [RDesign.drawable.checkbox_empty_icon].
     */
    var isChecked: Boolean = false
        set(value) {
            field = value
            targetDrawable =
                if (isChecked) checkedDrawable!!
                else uncheckedDrawable!!
        }

    /**
     * Разместить разметку чебокса по левой [left] и верхней [top] позициям,
     * определяемой родителем.
     */
    fun layout(left: Int, top: Int) {
        checkedDrawable!!.setBounds(left, top)
        uncheckedDrawable!!.setBounds(left, top)
    }

    private fun Drawable.setBounds(left: Int, top: Int) {
        val checkBoxLeft = left + leftPadding
        this.setBounds(
            checkBoxLeft,
            top,
            checkBoxLeft + layoutSize,
            top + layoutSize
        )
    }

    /**
     * Нарисовать разметку чекбокса.
     *
     * @param canvas canvas родительской view, в которой будет нарисована разметка.
     */
    fun draw(canvas: Canvas) {
        canvas.withSave(targetDrawable::draw)
    }

    /**
     * Вернуть пару ключ - значение для записи об отметке чекбокса в верстке аппиума.
     */
    fun getNodeInfo(context: Context): Pair<String, String> = Pair(
        context.resources.getResourceEntryName(R.id.communicator_checkbox_layout_id),
        isChecked.toString()
    )
}

/** Размер чекбокса в dp.
 * Сказали не переводить на глобальные переменные
 * подробности тут: https://online.sbis.ru/page/dialog/652e5d52-6a84-4bf4-9efb-fc29d905f231?message=1e659f1c-5e31-4382-91f8-5e1f5bb57fce&inviteduser=6148dfb3-2e78-4328-89f3-6cff9625ceae
 */
private const val CHECK_BOX_SIZE_DP = 22