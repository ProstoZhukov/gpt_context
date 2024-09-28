package ru.tensor.sbis.business.common.ui.viewmodel

import androidx.databinding.ObservableBoolean
import ru.tensor.sbis.business.common.ui.base.contract.ToolbarContract

/**
 * Вьюмодель невидимого Тулбара
 * Используется как делегат реализации [ToolbarContract] когда необходимо использование макета
 * ожидающего вью-модель реализующую данный интерфейс но само использование тулбара не предусмотренно
 * и он может быть скрыт
 *
 * @author as.chadov
 */
class NoToolbarVM : ToolbarVM(null) {
    override val toolbarVisibility = ObservableBoolean(false)
}