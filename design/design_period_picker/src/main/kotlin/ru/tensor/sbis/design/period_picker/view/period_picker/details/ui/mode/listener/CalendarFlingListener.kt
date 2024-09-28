package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.listener

import androidx.recyclerview.widget.RecyclerView

/**
 * Слушатель событий замедления скроллинга календаря.
 * Замедление нужно из-за того, что быстрый скролл листает рывками.
 *
 * @author mb.kruglova
 */
internal class CalendarFlingListener(
    private val recyclerView: RecyclerView
) : RecyclerView.OnFlingListener() {

    companion object {

        /**
         * Максимальная скорость скролирования - количество пикселей в секунду.
         * Величина получена эмпирическим путем.
         */
        private const val MAX_FLING = 4000
    }

    override fun onFling(velocityX: Int, velocityY: Int): Boolean {
        return when {
            velocityY > MAX_FLING -> {
                recyclerView.fling(velocityX, MAX_FLING)
                true
            }

            velocityY < -MAX_FLING -> {
                recyclerView.fling(velocityX, -MAX_FLING)
                true
            }

            else -> false
        }
    }
}