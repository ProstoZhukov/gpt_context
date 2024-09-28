package ru.tensor.sbis.design.radio_group.control.models

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.radio_group.item.SbisRadioGroupItemView

/** Вид контента, который будет размещен в радиокнопке, рядом с маркером выбора. */
sealed class SbisRadioGroupContent {

    /** Стандартный вариант контента, который представляет собой текст со стандартными значениями размера и цвета. */
    class Default(title: CharSequence) : SbisRadioGroupContent() {

        private val textLayout: TextLayout by lazy {
            TextLayout {
                text = title
            }
        }

        override fun prepare(radioGroupItemView: SbisRadioGroupItemView) {
            with(radioGroupItemView.styleHolder) {
                textLayout.colorStateList = ColorStateList(
                    SELECTED_COLOR_STATE_LIST,
                    intArrayOf(
                        defaultContentTextColorSelected,
                        defaultContentTextColorReadOnly,
                        defaultContentTextColor
                    )
                )
                textLayout.isSelected = false
                textLayout.configure {
                    paint.textSize = defaultContentTextSize.toFloat()
                    padding = TextLayout.TextLayoutPadding(
                        top = topPadding,
                        bottom = bottomPadding
                    )
                    includeFontPad = false
                }
            }
        }

        override fun measure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            // Реализовано на стороне TextLayout.
        }

        override fun layout(startX: Int, startY: Int) {
            textLayout.layout(startX, startY)
        }

        override fun draw(canvas: Canvas) {
            textLayout.draw(canvas)
        }

        override fun setSelected(isSelected: Boolean) {
            textLayout.isSelected = isSelected
        }

        override fun setEnabled(isEnabled: Boolean) {
            textLayout.isEnabled = isEnabled
        }

        override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
            info.text = textLayout.text
        }

        override fun getMeasuredWidth(): Int = textLayout.width

        override fun getMeasuredHeight(): Int = textLayout.height

        private companion object {
            private val SELECTED_COLOR_STATE_LIST = arrayOf(
                intArrayOf(android.R.attr.state_selected),
                intArrayOf(-android.R.attr.state_enabled),
                intArrayOf()
            )
        }
    }

    /** Прикладной выд контента, куда может быть передана любая пользовательская view. */
    class Custom(private val view: (Context) -> View) : SbisRadioGroupContent() {

        private var customView: View? = null

        override fun prepare(radioGroupItemView: SbisRadioGroupItemView) {
            customView = view(radioGroupItemView.context).also {
                radioGroupItemView.addView(it)
            }
        }

        override fun measure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            customView?.measure(widthMeasureSpec, heightMeasureSpec)
        }

        override fun layout(startX: Int, startY: Int) {
            customView?.layout(startX, startY)
        }

        override fun draw(canvas: Canvas) {
            // Реализовано в sdk android.
        }

        override fun setSelected(isSelected: Boolean) {
            customView?.isSelected = isSelected
        }

        override fun setEnabled(isEnabled: Boolean) {
            customView?.isEnabled = isEnabled
        }

        override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
            // Прикладные вью это реализовывают.
        }

        override fun getMeasuredWidth(): Int = customView?.measuredWidth ?: 0

        override fun getMeasuredHeight(): Int = customView?.measuredHeight ?: 0
    }

    /** Инициализация и настройка компонентов. */
    internal abstract fun prepare(radioGroupItemView: SbisRadioGroupItemView)

    /** Измерить контент. */
    internal abstract fun measure(widthMeasureSpec: Int, heightMeasureSpec: Int)

    /** Расположить контент. */
    internal abstract fun layout(startX: Int, startY: Int)

    /** Отрисовать контент. */
    internal abstract fun draw(canvas: Canvas)

    /** Установить выбранное состояние для контента. */
    internal abstract fun setSelected(isSelected: Boolean)

    /** Установить состояние доступности для контента. */
    internal abstract fun setEnabled(isEnabled: Boolean)

    /** Добавить доп. информацию для автотестов. */
    internal abstract fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo)

    /** Получить измеренную ширину контента. */
    internal abstract fun getMeasuredWidth(): Int

    /** Получить измеренную высоту контента. */
    internal abstract fun getMeasuredHeight(): Int

}
