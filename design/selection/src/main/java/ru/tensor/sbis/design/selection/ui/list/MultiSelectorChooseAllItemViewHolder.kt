package ru.tensor.sbis.design.selection.ui.list

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.vm.choose_all.FixedButtonViewModel
import androidx.core.view.isInvisible

/**
 * Реализация [RecyclerView.ViewHolder] для отображения элементов наподобие "выбрать всё"
 *
 * _Примечание: не нужно наследование от базовых реализаций так как этот элемент уникален и не поддерживает выделения
 * при поиске_
 *
 * @author ma.kolpakov
 */
internal class MultiSelectorChooseAllItemViewHolder(
    view: View
) : RecyclerView.ViewHolder(view) {

    private val title: TextView = view.findViewById(R.id.title)
    private val counter: TextView = view.findViewById(R.id.counter)

    fun bind(data: SelectorItemModel) {
        data.bindChooseAllItem(title, counter)
    }
}

/**
 * Вспомогательная функция для установки данных в элемент и кнопку "Выбрать все"
 *
 * @see MultiSelectorChooseAllItemViewHolder
 * @see FixedButtonViewModel
 */
internal fun SelectorItemModel.bindChooseAllItem(titleView: TextView, counterView: TextView) {
    titleView.text = title

    with(meta.formattedCounter) {
        counterView.isInvisible = this == null
        counterView.text = this
    }
}