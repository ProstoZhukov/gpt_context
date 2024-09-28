package ru.tensor.sbis.design.list_header

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.list_header.format.FormattedDateTime
import ru.tensor.sbis.design.list_header.format.ListDateFormatter
import java.util.Date

/**
 *
 * Обеспечивает взаимодействие списка с датой в заголовке списка.
 * Форматирует дату с учётом предыдущей, определяет формат
 *
 * @param format форматтер даты. Поддерживает динамическое обновление
 * @see ListDateFormatter
 *
 * @author ra.petrov
 */
class ListDateViewUpdater(var format: ListDateFormatter) {

    /**
     * HeaderDateView - заголовок
     */
    private lateinit var header: HeaderDateView

    /**
     * recyclerView - источник данных
     * Будем отслеживать скролл чтобы определить дату для заголовка (и ячейки)
     */
    private lateinit var recyclerView: RecyclerView

    /**
     * Возвращает форматированную дату по позиции
     *
     * Пример:
     * ```
     * override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
     *      ...
     *      holder.date.formattedDateTime = formattedDateProvider.getFormattedDate(position)
     * }
     * ```
     * @param position позиция элемента. Используется в адаптере
     */
    fun getFormattedDate(position: Int): FormattedDateTime? {
        val adapter = recyclerView.adapter
        checkNotNull(adapter, { "Adapter not found" })
        val date = getDateFromAdapter(adapter, position) ?: return null
        val previousPosition = recyclerView.layoutManager.let { layoutManager ->
            val reverseLayout = layoutManager is LinearLayoutManager && layoutManager.reverseLayout
            if (reverseLayout) {
                position + 1
            } else {
                position - 1
            }
        }
        return format.format(
            date,
            getDateFromAdapter(adapter, previousPosition),
            (recyclerView.layoutManager as? LinearLayoutManager)?.reverseLayout ?: false
        )
    }

    /**
     * Метод для "подключения" к recyclerView
     *
     * @param recyclerView
     * @param header view заголовка - в ней будем обновлять данные
     *
     * @see recyclerView
     */
    fun bind(recyclerView: RecyclerView, header: HeaderDateView) {
        this.recyclerView = recyclerView
        this.header = header
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                updateHeader()
            }
        })
        // TODO https://online.sbis.ru/opendoc.html?guid=19d1e63e-23dc-421b-83b4-840a029e93cd
        // Когда при опускании клавиатуры от потери фокуса происходит переход от скролируемого к нескролируемому состоянию списка - заголовок остается видимым
        recyclerView
            .addOnLayoutChangeListener { _: View, _: Int, _: Int, _: Int, _: Int, _: Int, _: Int, _: Int, _: Int ->
                updateHeader()
            }
    }

    /**
     * Обновление заголовка
     */
    private fun updateHeader() {
        val position = topChildPosition() ?: return

        recyclerView.adapter?.let { adapter ->
            val date = getDateFromAdapter(adapter, position)
            if (date == null)
                header.text = null
            else
                header.setFormattedDateTime(format.format(date))
        }

        header.visibility =
            (if (recyclerView.canScrollVertically(-1) && !header.text.isNullOrBlank()) View.VISIBLE else View.INVISIBLE)
    }

    /**
     * Возвращает дату по позиции из адаптера
     *
     * @see DateTimeAdapter
     */
    private fun getDateFromAdapter(adapter: RecyclerView.Adapter<*>, position: Int): Date? {
        require(adapter is DateTimeAdapter) {
            "Adapter should implement " + DateTimeAdapter::class.java
        }

        return adapter.getItemDateTime(position)
    }

    /**
     * Определяет позицию первой видимой ячейки с учётом направления
     *
     * @see LinearLayoutManager.getReverseLayout
     */
    private fun topChildPosition(): Int? {
        recyclerView.layoutManager.let { layoutManager ->
            if (layoutManager != null && layoutManager is LinearLayoutManager) {
                return if (!layoutManager.reverseLayout) layoutManager.findFirstVisibleItemPosition()
                else layoutManager.findLastVisibleItemPosition()
            } else {
                val topChild: View = recyclerView.getChildAt(0) ?: return null
                return recyclerView.getChildAdapterPosition(topChild)
            }
        }
    }
}