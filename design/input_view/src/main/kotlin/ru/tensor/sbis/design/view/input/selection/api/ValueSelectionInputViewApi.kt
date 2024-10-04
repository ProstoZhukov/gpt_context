package ru.tensor.sbis.design.view.input.selection.api

import ru.tensor.sbis.design.view.input.base.BaseInputView
import ru.tensor.sbis.design.view.input.selection.ValueSelectionInputView

/**
 * Api поля ввода с выбором значения [ValueSelectionInputView].
 *
 * @author ps.smirnyh
 */
interface ValueSelectionInputViewApi {

    /**
     * Слушатель клика по кнопке меню.
     */
    var onListIconClickListener: ((ValueSelectionInputView) -> Unit)?

    /**
     * Текст иконки выбора.
     * По умолчанию иконка гамбургера.
     */
    var iconText: CharSequence

    /**
     * Видимость иконки.
     * При [BaseInputView.readOnly] true
     * или [BaseInputView.isProgressVisible] true
     * скрывается вне зависимости от значения видимости.
     */
    var isIconVisible: Boolean
}