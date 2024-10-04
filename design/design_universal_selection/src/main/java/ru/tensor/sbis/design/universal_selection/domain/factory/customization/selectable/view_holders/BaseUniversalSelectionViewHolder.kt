package ru.tensor.sbis.design.universal_selection.domain.factory.customization.selectable.view_holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.universal_selection.domain.factory.UniversalItem
import ru.tensor.sbis.design.universal_selection.R
import ru.tensor.sbis.design.universal_selection.databinding.DesignUniversalSelectionListItemBinding
import ru.tensor.sbis.design_selection.contract.customization.selection.SelectionClickDelegate

/**
 * Базовая реализация ячейки доступной для выбора для универсального справочника.
 *
 * @author vv.chekurda
 */
internal open class BaseUniversalSelectionViewHolder<DATA : UniversalItem>(
    parentView: ViewGroup
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parentView.context)
        .inflate(R.layout.design_universal_selection_list_item, parentView, false)
) {
    protected val binding = DesignUniversalSelectionListItemBinding.bind(itemView)
    protected lateinit var data: UniversalItem
    private lateinit var clickDelegate: SelectionClickDelegate

    private val isSubtitleVisible: Boolean
        get() = !data.subtitle.isNullOrEmpty()

    init {
        binding.universalSelectionItemSelectionIconClickArea.setOnClickListener {
            clickDelegate.onAddButtonClicked(data)
        }
    }

    /**
     * Привязать данные к ячейке.
     *
     * @param data данные для отображения.
     * @param clickDelegate делегат обработки кликов по ячейке.
     * @param isMultiSelection true, если ячейка должна отображаться в режиме мультивыбора, false для одиночного.
     */
    open fun bind(
        data: DATA,
        clickDelegate: SelectionClickDelegate,
        isMultiSelection: Boolean
    ) {
        this.data = data
        this.clickDelegate = clickDelegate
        with(binding) {
            universalSelectionItemTitle.setTextWithHighlight(data.title, data.titleHighlights)
            universalSelectionItemSubtitle.apply {
                text = data.subtitle
                isVisible = isSubtitleVisible
            }
            val photoUrl = data.photoData?.photoUrl
            when {
                !photoUrl.isNullOrBlank() -> {
                    universalSelectionItemIcon.isVisible = false
                    universalSelectionItemImage.isVisible = true
                    universalSelectionItemTitleSpace.isVisible = false
                    universalSelectionItemImage.setImageURI(photoUrl)
                }
                universalSelectionItemIcon.text.isNotEmpty() -> {
                    universalSelectionItemIcon.isVisible = true
                    universalSelectionItemImage.isVisible = false
                    universalSelectionItemTitleSpace.isVisible = false
                }
                else -> {
                    universalSelectionItemIcon.isVisible = false
                    universalSelectionItemImage.isVisible = false
                    universalSelectionItemTitleSpace.isVisible = true
                }
            }
        }
    }
}