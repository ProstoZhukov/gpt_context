/**
 * Набор функций для установки данных "Фиксированных кнопок" в их макеты. Каждая функция должна иметь уникальное имя и
 * специализацию на конкретном типе кнопки
 *
 * @author ma.kolpakov
 */
package ru.tensor.sbis.design.selection.ui.utils.fixed_button

import android.view.View
import androidx.databinding.BindingAdapter
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.list.bindChooseAllItem
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.vm.choose_all.ChooseAllFixedButtonViewModel

/**
 * Функция для установки данных в кнопку "Выбрать все"
 *
 * @see ChooseAllFixedButtonViewModel
 */
@BindingAdapter("bindChooseAllData")
internal fun View.bindChooseAllData(data: SelectorItemModel?) {
    data?.bindChooseAllItem(findViewById(R.id.title), findViewById(R.id.counter))
}