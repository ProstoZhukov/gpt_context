package ru.tensor.sbis.design.view.input.base

import android.view.View

/**
 * Слушатель изменения фокуса-обёртка для изменения состояния контейнера перерисовки линии снизу.
 * @property baseInputView базовый класс поля ввода.
 * @property inputViewUpdate обновление подсказки, заголовка и прочего при изменении фокуса.
 *
 * @author ps.smirnyh
 */
internal class InputViewFocusChangedListener(
    private val baseInputView: View,
    private val inputViewUpdate: (isInputViewFocused: Boolean) -> Unit
) : View.OnFocusChangeListener {
    /**
     * Внешний слушатель [View.OnFocusChangeListener].
     */
    var outer: View.OnFocusChangeListener? = null

    override fun onFocusChange(v: View, hasFocus: Boolean) {
        outer?.onFocusChange(baseInputView, hasFocus)
        inputViewUpdate(hasFocus)
    }
}