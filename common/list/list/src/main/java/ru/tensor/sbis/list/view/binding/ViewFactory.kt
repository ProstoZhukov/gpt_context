package ru.tensor.sbis.list.view.binding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Фабрика используется в работе [RecyclerView.ViewHolder] для создания [View] для ячейки списка.
 */
interface ViewFactory {

    /**
     * Создать View. [parentView] может использоваться для получения context
     * и как root для аргумента метода [LayoutInflater.inflate].
     */
    fun createView(parentView: ViewGroup): View

    /**
     * Тип [RecyclerView.ViewHolder] в котором будет использоваться [View], полученная методом [createView].
     * Должен быть уникальный для группы элементов, то есть все критерии, как для
     * [RecyclerView.ViewHolder.getItemViewType]? только тип не Int, а произвольный.
     */
    fun getType(): Any
}