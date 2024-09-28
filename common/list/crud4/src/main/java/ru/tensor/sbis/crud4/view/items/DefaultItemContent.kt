package ru.tensor.sbis.crud4.view.items

import android.view.View
import android.view.ViewGroup
import ru.tensor.sbis.crud4.R
import ru.tensor.sbis.crud4.view.viewmodel.ItemActionDelegate
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.service.DecoratedProtocol

/**
 * Контент для базовой ячейки.
 * @param itemDataProvider провайдер данных для ячейки из прикладной модели.
 *
 * @author ma.kolpakov
 */
class DefaultItemContent<DATA : DecoratedProtocol<IDENTIFIER>, IDENTIFIER>(
    private val itemDataProvider: ItemDataProvider<DATA>
) :
    ViewHolderDelegate<DATA, IDENTIFIER> {

    private lateinit var textView: SbisTextView

    override fun onBind(item: DATA, itemActionDelegate: ItemActionDelegate<DATA, IDENTIFIER>) {
        textView.setTextWithHighlightRanges(itemDataProvider.provideName(item), itemDataProvider.provideHighlights(item))
        textView.setOnClickListener { itemActionDelegate.itemClick(item) }
    }

    override fun createView(parentView: ViewGroup): View {
        return SbisTextView(parentView.context).apply {
            id = R.id.crud4_item_text
            isSingleLine = true
            textView = this

            setTextColor(TextColor.DEFAULT.getValue(parentView.context))
        }
    }

    /**
     * Провайдер данных для ячейки из прикладной модели.
     */
    interface ItemDataProvider<DATA> {
        /**
         * Преобразовать прикладной тип ячейки в название
         */
        fun provideName(data: DATA): String

        /**
         * Преобразовать прикладной тип ячейки в диапазоны подсвеченных символов
         */
        fun provideHighlights(data: DATA): List<IntRange>? = null
    }
}