package ru.tensor.sbis.design.selection.ui.utils.vm.choose_all

import androidx.annotation.AnyThread
import ru.tensor.sbis.design.selection.bl.contract.listener.ClickHandleStrategy
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.MultiSelectionViewModel
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * Расширение [FixedButtonViewModel] для кнопки "Выбрать все" на экране с заглушкой (при отсутствии данных выборки)
 *
 * @author ma.kolpakov
 */
internal interface ChooseAllFixedButtonViewModel : FixedButtonViewModel<SelectorItemModel> {

    /**
     * Установить модель элемента "Выбрать все" из списка. Этот элемент будет доставлен подписчикам
     * [fixedButtonClicked] при нажатии на кнопку.
     * Элемент должен должен обрабатываться как [ClickHandleStrategy.COMPLETE_SELECTION] для корректного завершения при
     * передаче [MultiSelectionViewModel.setSelected]
     */
    @AnyThread
    fun setData(data: SelectorItemModel)
}