package ru.tensor.sbis.design.utils.animator.helper

import android.animation.Animator

/**
 * Вспомогательный класс для управления запускаемыми аниматорами.
 * Единовременно позволяет работать только одному аниматору,
 * при запуске нового аниматора - отменяет предыдущий, если тот в процессе.
 *
 * @author am.boldinov
 */
class SerialAnimatorController {

    /**
     * Последний запущенный аниматор.
     */
    private var mCurrentAnimator: Animator? = null

    /**
     * Запустить новый аниматор.
     */
    fun start(animator: Animator) {
        mCurrentAnimator?.apply {
            if (isRunning) {
                cancel()
            }
        }
        mCurrentAnimator = animator.apply {
            if (isRunning) {
                cancel()
            }
            start()
        }
    }

    /**
     * Работает ли сейчас какой-либо аниматор.
     */
    fun isRunning(): Boolean {
        return mCurrentAnimator?.isRunning ?: false
    }

    /**
     * Отменить текущий аниматор.
     */
    fun cancelCurrent() {
        mCurrentAnimator?.cancel()
        mCurrentAnimator = null
    }

}