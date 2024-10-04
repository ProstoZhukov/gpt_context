package ru.tensor.sbis.design.message_view.utils

import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntRange
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Простейшая реализация пула для хранения, предсоздания и получения view.
 *
 * @author vv.chekurda
 */
class SimpleViewPool<T : View>(
    private val createView: () -> T
) {

    private val pool = ConcurrentLinkedQueue<T>()

    /** Размер пула. */
    val size: Int
        get() = pool.size

    /** Предварительно создать и поместить в пул вью в количестве [count]. */
    fun prefetch(@IntRange(from = 0) count: Int = 1) {
        for (i in 0 until count) {
            pool.add(createView())
        }
    }

    /** Добавить view в пул. */
    fun addView(view: T) {
        view.tryRemoveParent()
        pool.add(view)
    }

    /** Получить view из пула. */
    fun getView(): T =
        pool.poll() ?: createView()

    /** Создать новую view. */
    fun createNewView(): T =
        createView()

    /** Очистить пул. */
    fun clear() {
        pool.clear()
    }

    private fun View.tryRemoveParent() {
        parent.castTo<ViewGroup>()?.removeViewInLayout(this)
    }
}