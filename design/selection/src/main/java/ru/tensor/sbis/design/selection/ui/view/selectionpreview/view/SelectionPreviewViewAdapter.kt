package ru.tensor.sbis.design.selection.ui.view.selectionpreview.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import ru.tensor.sbis.design.collection_view.CollectionView
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.view.selectionpreview.vm.*

/**
 * Адаптер, используемый [SelectionPreviewView]
 *
 * @author us.bessonov
 */
class SelectionPreviewViewAdapter internal constructor(private val selectionPreviewView: ViewGroup) :
    CollectionView.Adapter<SelectionPreviewViewHolder>() {

    private val items = mutableListOf<SelectionPreviewListItemVm>()

    /**@SelfDocumented**/
    fun reload(list: List<SelectionPreviewListItemVm>) {
        items.clear()
        items.addAll(list)
        notifyDataChanged()
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(
        parent: CollectionView,
        @LayoutRes
        viewType: Int
    ): SelectionPreviewViewHolder {
        val view = LayoutInflater.from(selectionPreviewView.context)
            .inflate(viewType, selectionPreviewView, false)
        return SelectionPreviewViewHolder(view.rootView)
    }

    override fun onBindViewHolder(holder: SelectionPreviewViewHolder, position: Int) = holder.bind(items[position])

    @LayoutRes
    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is SelectionPreviewItemVm -> R.layout.selection_preview_item
        is SelectionPreviewDividerItemVm -> R.layout.selection_preview_item_divider
        is SelectionPreviewTotalCountItemVm -> R.layout.selection_preview_total_count_item
        is SelectionSuggestionHeaderItemVm -> R.layout.selection_suggestion_header_item
        is SelectionSuggestionItemVm -> R.layout.selection_suggestion_item
        is SelectionSuggestionMoreItemVm -> R.layout.selection_suggestion_more_item
    }
}