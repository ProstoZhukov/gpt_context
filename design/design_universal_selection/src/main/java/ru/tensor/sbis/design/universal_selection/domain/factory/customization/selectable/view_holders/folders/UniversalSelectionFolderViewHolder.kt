package ru.tensor.sbis.design.universal_selection.domain.factory.customization.selectable.view_holders.folders

import android.text.Layout
import android.view.ViewGroup
import androidx.core.view.isVisible
import ru.tensor.sbis.design.universal_selection.domain.factory.UniversalSelectionFolderItem
import ru.tensor.sbis.design.universal_selection.domain.factory.customization.selectable.view_holders.BaseUniversalSelectionViewHolder
import ru.tensor.sbis.design_selection.contract.customization.selection.SelectionClickDelegate
import kotlin.text.Typography.ellipsis

/**
 * Ячейка папки, доступной для выбора, в компоненте универсального справочника.
 *
 * @author vv.chekurda
 */
internal class UniversalSelectionFolderViewHolder(parentView: ViewGroup) :
    BaseUniversalSelectionViewHolder<UniversalSelectionFolderItem>(parentView) {

    init {
        addTitleIconPositionSpotter()
    }

    override fun bind(
        data: UniversalSelectionFolderItem,
        clickDelegate: SelectionClickDelegate,
        isMultiSelection: Boolean
    ) {
        super.bind(data, clickDelegate, isMultiSelection)
        with(binding) {
            universalSelectionItemSelectionIcon.isVisible = data.selectable
            universalSelectionItemSelectionIconClickArea.isVisible = data.selectable
            universalSelectionItemTitleIcon.isVisible = data.openable
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
        binding.universalSelectionItemTitle.addOnLayoutChangeListener { _, left, _, right, _, _, _, _, _ ->
            val textView = binding.universalSelectionItemTitle
            val width = right - left
            if (textView.text.contains(ellipsis)) {
                val realTextWidth = Layout.getDesiredWidth(textView.text, textView.paint)
                binding.universalSelectionItemTitleIcon.translationX = realTextWidth - width
            } else {
                binding.universalSelectionItemTitleIcon.translationX = 0f
            }
        }
    }
}