package ru.tensor.sbis.design.toolbar.util

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import timber.log.Timber

/**
 * Элемент лениво инициализируемой иерархии [View].
 *
 * @param factory Используется для создания [View]
 *
 * @author us.bessonov
 */
internal class LazyViewContainer<T : View>(private val factory: () -> T) {
    private val children = mutableListOf<LazyViewContainer<*>>()

    var instance: T? = null
        private set

    /**
     * Контейнер, в который должен быть добавлен [View] после создания
     */
    var parent: LazyViewContainer<*>? = null

    /**
     * Видимость, применяемая для созданного [View]
     */
    var defaultVisibility: Int? = null

    /**
     * Создаёт, либо возвращает созданный ранее [View].
     * При создании обеспечивается добавление в иерархию самого [View] и родительских элементов.
     */
    fun get(): T {
        if (instance == null) {
            instance = factory().apply {
                defaultVisibility?.let { visibility = it }
            }
            parent?.ensureChildrenAdded()
        }
        return instance!!
    }

    /**
     * Добавляет дочерний элемент
     */
    fun add(child: LazyViewContainer<*>) {
        children.add(child)
        child.parent = this
    }

    @SuppressLint("BinaryOperationInTimber")
    private fun ensureChildrenAdded() {
        val viewGroup = get() as? ViewGroup
            ?: return
        var lastAddedIndex = -1
        children.forEach { it ->
            it.instance?.let { view ->
                val index = try {
                    viewGroup.children.indexOf(view)
                } catch (e: IndexOutOfBoundsException) {
                    Timber.e(
                        e,
                        "Index out of bounds during children iteration. Parent: $viewGroup. Target child: " +
                            "$view. Lazy children: ${children.map { it.instance?.javaClass?.simpleName }}. " +
                            "Children: ${viewGroup.children.toList().map { it.javaClass.simpleName }}"
                    )
                    viewGroup.childCount
                }
                if (index >= 0) {
                    lastAddedIndex = index
                } else {
                    lastAddedIndex++
                    viewGroup.addView(view, lastAddedIndex)
                }
            }
        }
    }
}
