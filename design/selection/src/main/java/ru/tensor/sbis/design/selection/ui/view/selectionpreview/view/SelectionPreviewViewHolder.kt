package ru.tensor.sbis.design.selection.ui.view.selectionpreview.view

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import ru.tensor.sbis.design.collection_view.CollectionView
import ru.tensor.sbis.design.selection.BR
import ru.tensor.sbis.design.selection.ui.view.selectionpreview.vm.SelectionPreviewListItemVm

/**
 * Реализация [CollectionView.ViewHolder], используемая в [SelectionPreviewViewAdapter]
 *
 * @author us.bessonov
 */
class SelectionPreviewViewHolder internal constructor(view: View) : CollectionView.ViewHolder(view) {

    /**@SelfDocumented**/
    fun bind(data: SelectionPreviewListItemVm) {
        val binding: ViewDataBinding = DataBindingUtil.bind(view)!!
        binding.setVariable(BR.viewModel, data)
        binding.executePendingBindings()
    }
}