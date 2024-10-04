package ru.tensor.sbis.design_selection.contract.header_button

import androidx.annotation.LayoutRes
import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.communication_decl.selection.SelectionConfig
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import java.io.Serializable

/**
 * Контракт для работы и отображения дополнительной кнопки в шапке компонента выбора.
 *
 * @author vv.chekurda
 */
interface HeaderButtonContract<ITEM : SelectionItem, ACTIVITY : FragmentActivity> : Serializable {

    /**
     * Разметка кнопки.
     */
    @get:LayoutRes
    val layout: Int

    /**
     * Обработать клик по кнопке.
     *
     * @param selectedItems текущие выбранные элементы на момент нажатия кнопки.
     * @param config текущая конфигурация с которой работает компонент выбора.
     * @return стратегия обработки клика.
     */
    fun onButtonClicked(
        activity: ACTIVITY,
        selectedItems: List<ITEM>,
        config: SelectionConfig
    ): HeaderButtonStrategy
}

/**
 * Стратегия обработки клика на кнопку в шапке компонента выбора.
 *
 * @property hideButton скрыть кнопку по клику.
 * @property newConfig новая конфигурация, к которой необходимо перейти по клику.
 */
class HeaderButtonStrategy(
    val hideButton: Boolean = true,
    val newConfig: SelectionConfig? = null
)