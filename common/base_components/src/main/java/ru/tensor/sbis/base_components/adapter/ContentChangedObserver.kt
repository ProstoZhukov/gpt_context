package ru.tensor.sbis.base_components.adapter

import androidx.recyclerview.widget.RecyclerView

/**
 * Наблюдатель за изменениями содержимого адаптера.
 *
 * @author am.boldinov
 */
interface ContentChangedObserver {

    /**
     * Обработать событие изменения содержимого адаптера.
     */
    fun onContentChanged(adapter: RecyclerView.Adapter<*>)

}