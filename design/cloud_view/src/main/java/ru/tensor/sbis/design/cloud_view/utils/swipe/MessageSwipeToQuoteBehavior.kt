package ru.tensor.sbis.design.cloud_view.utils.swipe

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.cloud_view.CloudView

/**
 * Поведение компонента ячейка-облако [CloudView] для цитирования по свайпу.
 *
 * @author vv.chekurda
 */
interface MessageSwipeToQuoteBehavior {

    /**
     * Установить/получить признак доступности цитирования.
     */
    var canBeQuoted: Boolean

    /**
     * Установить/получить слушателя активации цитирования по свайпу.
     */
    var swipeToQuoteListener: CloudSwipeToQuoteListener?

    /**
     * Получить составной флаг разрешенных направлений для свайпа.
     * @see [ItemTouchHelper.Callback.getMovementFlags]
     */
    val movementFlags: Int

    /**
     * Нарисовать смещение по свайпу.
     *
     * @param canvas холст списка [RecyclerView].
     * @param dx смещение по оси X.
     * @param isSwiping true, если пользователь удерживает ячейку.
     * @return true, если необходима повторная отрисовка для анимации.
     */
    fun draw(canvas: Canvas, dx: Float, isSwiping: Boolean): Boolean
}

/**
 * Слушатель активации цитирования по свайпу.
 */
typealias CloudSwipeToQuoteListener = () -> Unit