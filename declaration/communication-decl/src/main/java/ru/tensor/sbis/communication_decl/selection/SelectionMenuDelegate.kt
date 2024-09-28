package ru.tensor.sbis.communication_decl.selection

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Делегат для управления компонентом меню выбора.
 *
 * @author vv.chekurda
 */
interface SelectionMenuDelegate {

    /**
     * Поставщик делегата [SelectionMenuDelegate].
     */
    interface Provider {

        fun getSelectionMenuDelegate(): SelectionMenuDelegate
    }

    /**
     * Установить поисковый запрос.
     */
    val searchQuery: MutableStateFlow<String>

    /**
     * Для подписки на признак наличия элементов доступных для выбора.
     * Если false - в списке отображается заглушка.
     */
    val hasSelectableItems: StateFlow<Boolean>

    /**
     * Для подписки на состояние видимости меню.
     */
    val isShowingState: StateFlow<Boolean>

    /**
     * Показать меню.
     */
    fun show()

    /**
     * Скрыть меню.
     */
    fun hide()
}