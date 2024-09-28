package ru.tensor.sbis.list.view.item

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Делегат для [RecyclerView.Adapter], в процессе его работы вызываются соответствующие методы этого интерфейса.
 * Таким способом логика работы с вью холдерами инкапсулируется внутри элементов списка, что позволяет использовать
 * произвольный набор элементов списка, без указания конечного списка типов ячеек и написания switch'ей, а так же,
 * позволяет держать логику в отдельных модулях, без зависимости от них.
 *
 * @param DATA произвольные данные ячейки.
 * @param VIEW_HOLDER тип вью холдера.
 */
interface ViewHolderHelper<DATA, VIEW_HOLDER : RecyclerView.ViewHolder> {

    /**
     * Создание вью холдера для адаптера.
     *
     * @param parentView родительская вью для вью ячейки - сам RecyclerView.
     * @return новый вью холдер.
     */
    fun createViewHolder(parentView: ViewGroup): VIEW_HOLDER

    /**
     * Байндинг данных во вью холдер.
     *
     * @param data данные, которые нужно забайндить.
     * @param viewHolder вью холдер, в который будут забайндены данные.
     */
    fun bindToViewHolder(data: DATA, viewHolder: VIEW_HOLDER)

    /**
     * Обновить вью новыми данными. В отличие от [bindToViewHolder] вызывается только когда элемент уже был на экране и изменился.
     */
    fun update(data: DATA, viewHolder: VIEW_HOLDER) = bindToViewHolder(data, viewHolder)

    /**
     * Должен всегда отдавать уникальное значение для каждой конкретной реализации.
     */
    fun getViewHolderType(): Any = javaClass

    /**
     * "Освободить" [viewHolder]
     *
     * @see RecyclerView.Adapter.onViewRecycled
     */
    fun recycleViewHolder(viewHolder: VIEW_HOLDER) = Unit
}