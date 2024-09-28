package ru.tensor.sbis.crud4.view.items

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.crud4.R
import ru.tensor.sbis.crud4.view.viewmodel.ItemActionDelegate
import ru.tensor.sbis.design.checkbox.SbisCheckboxView
import ru.tensor.sbis.design.checkbox.models.SbisCheckboxValue
import ru.tensor.sbis.service.DecoratedProtocol
import ru.tensor.sbis.service.generated.SelectionStatus

/**
 * ViewHolder базовой ячейки реализует работу с маркером и чекбоксом мульти-выбора.
 *
 * @author ma.kolpakov
 */
class BaseItemViewHolder<DATA : DecoratedProtocol<IDENTIFIER>, IDENTIFIER>(
    view: View,
    private val actionDelegate: ItemActionDelegate<DATA, IDENTIFIER>,
    private val customViewHolder: ViewHolderDelegate<DATA, IDENTIFIER>,
    private val isSelectMode: Boolean = false
) :
    RecyclerView.ViewHolder(view) {
    private val checkboxView: SbisCheckboxView
    private val marker: View

    init {
        checkboxView = view.findViewById(R.id.crud4_item_check_box)
        marker = view.findViewById(R.id.crud4_item_marker)
    }

    fun onBind(item: DATA) {
        if (isSelectMode) {
            checkboxView.value = when (item.isSelected) {
                SelectionStatus.SET -> SbisCheckboxValue.CHECKED
                SelectionStatus.UNSET -> SbisCheckboxValue.UNCHECKED
                SelectionStatus.ACTIVE -> SbisCheckboxValue.UNDEFINED
            }

            checkboxView.setOnClickListener {
                actionDelegate.selectClick(item)
            }
        }
        marker.isVisible = item.isMarked && isSelectMode
        checkboxView.isVisible = isSelectMode
        customViewHolder.onBind(item, actionDelegate)
    }
}

/**
 * Делегат для работы с контентом внутри базовой ячейки
 */
interface ViewHolderDelegate<DATA : DecoratedProtocol<IDENTIFIER>, IDENTIFIER> {
    /**
     * Создать вью с контентом ячейки в этом методе можно сохранить ссылки на вью для последующего обновления
     * в методе [onBind].
     */
    fun createView(parentView: ViewGroup): View

    /**
     * Привязать данные к вью ячейки
     * @param item новые данные для ячейки
     * @param itemActionDelegate - делегат реализующий основные действия с ячейкой выделение раскрытие проваливание
     * необходимо вызовы своих вью делегировать соответствующим методам.
     */
    fun onBind(item: DATA, itemActionDelegate: ItemActionDelegate<DATA, IDENTIFIER>)
}