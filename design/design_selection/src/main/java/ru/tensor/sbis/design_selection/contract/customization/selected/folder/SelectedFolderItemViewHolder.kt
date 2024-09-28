package ru.tensor.sbis.design_selection.contract.customization.selected.folder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.design_selection.R
import ru.tensor.sbis.design_selection.contract.customization.selected.SelectedItemClickDelegate
import ru.tensor.sbis.design_selection.contract.data.SelectionFolderItem
import ru.tensor.sbis.design_selection.databinding.DesignSelectionSelectedFolderItemBinding

/**
 * Вью-холдер выбранной папки компонента выбора.
 *
 * @property clickDelegate делегат обработки кликов.
 *
 * @author vv.chekurda
 */
class SelectedFolderItemViewHolder(
    parentView: ViewGroup,
    private val clickDelegate: SelectedItemClickDelegate
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parentView.context)
        .inflate(R.layout.design_selection_selected_folder_item, parentView, false)
) {
    private val binding = DesignSelectionSelectedFolderItemBinding.bind(itemView)
    private lateinit var data: SelectionFolderItem

    init {
        with(binding) {
            selectedFolderCloseIcon.setOnClickListener {
                clickDelegate.onUnselectClicked(data)
            }
        }
    }

    /**
     * Привязать данные [data] к ячейке.
     */
    fun bind(data: SelectionFolderItem) {
        this.data = data
        binding.selectedFolderTitle.setTextWithHighlight(data.formattedTitle, data.titleHighlights)
        binding.selectedFolderTitlePostfix.text = data.formattedCounter
    }
}

private val SelectionFolderItem.formattedTitle: String
    get() = if (counter != null) {
        title + TITLE_COUNTER_PREFIX
    } else {
        title
    }

private val SelectionFolderItem.formattedCounter: String
    get() = counter?.let { COUNTER_FORMAT.format(it) }.orEmpty()

private const val COUNTER_FORMAT = "(%d)"
private const val TITLE_COUNTER_PREFIX = StringUtils.SPACE