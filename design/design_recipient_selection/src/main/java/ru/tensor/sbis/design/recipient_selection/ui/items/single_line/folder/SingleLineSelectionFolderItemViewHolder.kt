package ru.tensor.sbis.design.recipient_selection.ui.items.single_line.folder

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design_selection.contract.customization.selection.SelectionClickDelegate
import ru.tensor.sbis.design_selection.contract.data.SelectionFolderItem

/**
 * Вью-холдер однострочной папки для списка компонента выбора.
 *
 * @author vv.chekurda
 */
class SingleLineSelectionFolderItemViewHolder private constructor(
    private val folderItemView: SingleLineSelectionFolderItemView
) : RecyclerView.ViewHolder(folderItemView) {

    constructor(parentView: ViewGroup) : this(SingleLineSelectionFolderItemView(parentView.context))

    private lateinit var data: SelectionFolderItem
    private lateinit var clickDelegate: SelectionClickDelegate

    /**
     * Признак активированного мульти-выбора.
     * После изменения спецификации игрорируется при отображении папок,
     * тк возможность выбора папки доступна и в одиночном и во множественном режиме,
     * но только если папки можно выбирать в качестве результата [SelectionFolderItem.selectable].
     */
    private var isMultiSelection: Boolean = false

    init {
        setupClickListeners()
    }

    /**
     * Привязать данные к ячейке.
     *
     * @param data данные для отображения.
     * @param clickDelegate делегат обработки кликов по ячейке.
     */
    fun bind(
        data: SelectionFolderItem,
        clickDelegate: SelectionClickDelegate,
        isMultiSelection: Boolean
    ) {
        this.data = data
        this.clickDelegate = clickDelegate
        this.isMultiSelection = isMultiSelection

        folderItemView.setData(data)
    }

    private fun setupClickListeners() {
        folderItemView.selectionIconView.setOnClickListener {
            clickDelegate.onAddButtonClicked(data)
        }
    }
}