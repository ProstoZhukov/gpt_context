package ru.tensor.sbis.base_components.adapter.sectioned.content

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import java.lang.IllegalStateException

/**
 * Класс представлющий секцию списка.
 *
 * @author am.boldinov
 */
private const val IS_ATTACHED_TO_VIEW_KEY = "IS_ATTACHED_TO_VIEW_KEY"

open class ListSection<ITEM : ListItem, CONTROLLER : ListController, ADAPTER : RecyclerView.Adapter<out RecyclerView.ViewHolder>> @JvmOverloads constructor(
    /**
     * Контроллер секции списка.
     */
    val controller: CONTROLLER,
    /**
     * Адаптер секции списка.
     */
    val adapter: ADAPTER,
    /**
     * Является ли секция обязательной для отображения.
     * Все необязательные секции не будут отображены, пока не загрузится обязательная.
     */
    var isRequired: Boolean = false
) {

    internal var sectionOffset: Int = 0

    /**
     * По умолчанию true, т.к при инициализации секции считаем, что она приаттачена ко view.
     */
    var isAttachedToView: Boolean = true
        private set

    /**
     * Возвращает смещение секции относительно элементов верхних секций внутри адаптера.
     * Необходимо использовать для определения реальной позиции холдера внутри адаптера, содержащего несколько секций.
     */
    fun getSectionOffset(): Int {
        return sectionOffset
    }

    /**
     * Возвращает признак отсутствия элементов в секции.
     */
    fun isEmpty(): Boolean {
        return getItemCount() == 0
    }

    /**
     * Возвращает количество элементов в секции.
     */
    fun getItemCount(): Int {
        return adapter.itemCount
    }

    /**
     * Получить элемент секции на указанной позиции.
     */
    fun getSectionItem(position: Int): ITEM? {
        if (adapter is ListSectionAdapter<*>) {
            @Suppress("UNCHECKED_CAST")
            return (adapter as ListSectionAdapter<ITEM>).getSectionItem(position)
        }
        throw IllegalStateException("Adapter $adapter should implement interface ${ListSectionAdapter::class.java.name}")
    }

    @CallSuper
    open fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            isAttachedToView = it.getBoolean(IS_ATTACHED_TO_VIEW_KEY, isAttachedToView)
        }
    }

    @CallSuper
    open fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(IS_ATTACHED_TO_VIEW_KEY, isAttachedToView)
    }

    /**
     * Привязать секцию к view
     */
    @CallSuper
    open fun attachToView() {
        isAttachedToView = true
    }

    /**
     * Отвязать секцию от view
     */
    @CallSuper
    open fun detachFromView() {
        isAttachedToView = false
    }
}

