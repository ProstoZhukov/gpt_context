package ru.tensor.sbis.swipeablelayout.view.canvas

import android.graphics.Canvas
import android.view.MotionEvent
import androidx.annotation.ColorInt

/**
 * Интерфейс canvas разметки для свайп-меню.
 *
 * @author vv.chekurda
 */
internal interface SwipeCanvasLayout {

    /**
     * Позиция левого края разметки.
     */
    val left: Int

    /**
     * Позиция верхнего края разметки.
     */
    val top: Int

    /**
     * Позиция правого края разметки.
     */
    val right: Int

    /**
     * Позиция нижнего края разметки.
     */
    val bottom: Int

    /**
     * Ширина разметки.
     */
    val width: Int

    /**
     * Высота разметки.
     */
    val height: Int

    /**
     * Измерить разметку по спецификации ширины и высоты, задаваемую родительской view.
     *
     * @param widthMeasureSpec спецификация для расчета ширины разметки.
     * @param heightMeasureSpec спецификация для расчета высоты разметки.
     */
    fun measure(widthMeasureSpec: Int, heightMeasureSpec: Int)

    /**
     * Разместить разметку на позиции, задаваемой родителем.
     *
     * @param left позиция левого края.
     * @param top позиция верхнего края.
     */
    fun layout(left: Int, top: Int)

    /**
     * Нарисовать разметку.
     *
     * @param canvas canvas родительского view представления.
     */
    fun draw(canvas: Canvas)

    /**
     * Установить цвет фона размтеки.
     *
     * @param colorInt цвет фона.
     */
    fun setBackgroundColor(@ColorInt colorInt: Int)

    /**
     * Передать событие пользовательского касания для внутренней обработки.
     *
     * @param event событие касания.
     */
    fun onTouchEvent(event: MotionEvent): Boolean = false

    /**
     * Установка слушателя кликов по разметке.
     *
     * @param listener слушатель, передавать null для очистки ссылки.
     */
    fun setOnClickListener(listener: CanvasClickListener?) = Unit

    /**
     * Слушатель кликов по canvas разметке.
     */
    fun interface CanvasClickListener {

        /**
         * Передать событие клика по разметке.
         */
        fun onClick()
    }
}