package ru.tensor.sbis.design_selection.contract.customization.selection.folder

import android.text.Layout
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.*
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design_selection.R
import ru.tensor.sbis.design_selection.contract.customization.selection.SelectionClickDelegate
import ru.tensor.sbis.design_selection.contract.data.SelectionFolderItem
import ru.tensor.sbis.design_selection.databinding.DesignSelectionListFolderItemBinding

/**
 * Вью-холдер папки для списка компонента выбора.
 *
 * @author vv.chekurda
 */
class SelectionFolderItemViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parentView.context)
        .inflate(R.layout.design_selection_list_folder_item, parentView, false)
) {
    private val binding = DesignSelectionListFolderItemBinding.bind(itemView)
    private lateinit var data: SelectionFolderItem
    private lateinit var clickDelegate: SelectionClickDelegate

    /**
     * Признак активированного мульти-выбора.
     * После изменения спецификации игрорируется при отображении папок,
     * тк возможность выбора папки доступна и в одиночном и во множественном режиме,
     * но только если папки можно выбирать в качестве результата [SelectionFolderItem.selectable].
     */
    private var isMultiSelection: Boolean = false

    private val isSubtitleVisible: Boolean
        get() = !data.subtitle.isNullOrEmpty()

    init {
        binding.selectionFolderSelectionIconClickArea.setOnClickListener {
            clickDelegate.onAddButtonClicked(data)
        }
        addTitleIconPositionSpotter()
    }

    /**
     * Привязать данные к ячейке.
     *
     * @param data данные для отображения.
     * @param clickDelegate делегат обработки кликов по ячейке.
     * @param isMultiSelection true, если ячейка должна отображаться в режиме мультивыбора, false для одиночного.
     */
    fun bind(
        data: SelectionFolderItem,
        clickDelegate: SelectionClickDelegate,
        isMultiSelection: Boolean
    ) {
        this.data = data
        this.clickDelegate = clickDelegate
        this.isMultiSelection = isMultiSelection
        with(binding) {
            selectionFolderTitle.setTextWithHighlight(data.title, data.titleHighlights)
            selectionFolderSubtitle.apply {
                text = data.subtitle
                isVisible = isSubtitleVisible
            }
            val photoUrl = data.photoData?.photoUrl
            when {
                !photoUrl.isNullOrBlank() -> {
                    selectionFolderIcon.isVisible = false
                    selectionFolderImage.isVisible = true
                    selectionFolderTitleSpace.isVisible = false
                    selectionFolderImage.setImageURI(photoUrl)
                }
                selectionFolderIcon.text.isNotEmpty() -> {
                    selectionFolderIcon.isVisible = true
                    selectionFolderImage.isVisible = false
                    selectionFolderTitleSpace.isVisible = false
                }
                else -> {
                    selectionFolderIcon.isVisible = false
                    selectionFolderImage.isVisible = false
                    selectionFolderTitleSpace.isVisible = true
                }
            }
            selectionFolderSelectionIcon.isVisible = data.selectable
            selectionFolderSelectionIconClickArea.isVisible = data.selectable
            selectionFolderTitleIcon.isVisible = data.openable
        }
    }

    /**
     * Добавить корректировщик положения иконки заголовка.
     * Позволяет размещать иконку строго в конце текста, независимо от размеров TextView-заголовка.
     *
     * Суть проблемы:
     * Наш любимый android не может сразу пересчитать ширину TextView, которая огринчивается констрейнтом.
     * По достижению максимальной ширины внутри TextView происходит сокращение текста,
     * в связи с которым текст обрезается, а ширина TextView не пересчитывается.
     * Из-за этого в некоторых случаях обрезанный текст находится за 10+ dp от правой границы TextView,
     * что не позволяет корректно отобразить иконку в виде другого TextView в конце текста заголовка.
     * Можно было бы с этим смириться, но любой requestLayout на экране вызывает повторное перестроение
     * с правильной позицией, которое выглядит как перемаргивание.
     *
     * Более правильное решение: написать кастомный компонент, который будет содержать 2 текстовых элемента
     * и будет их правильно пересчитывать.
     */
    private fun addTitleIconPositionSpotter() {
        binding.selectionFolderTitle.addOnLayoutChangeListener { _, left, _, right, _, _, _, _, _ ->
            val textView = binding.selectionFolderTitle
            val width = right - left
            if (textView.text.contains(ellipsis)) {
                val realTextWidth = Layout.getDesiredWidth(textView.text, textView.paint)
                binding.selectionFolderTitleIcon.translationX = realTextWidth - width
            } else {
                binding.selectionFolderTitleIcon.translationX = 0f
            }
        }
    }
}

private const val ellipsis = "\u2026"