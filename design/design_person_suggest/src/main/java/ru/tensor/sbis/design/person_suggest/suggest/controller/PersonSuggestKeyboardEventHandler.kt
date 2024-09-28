package ru.tensor.sbis.design.person_suggest.suggest.controller

import android.view.View
import androidx.core.view.isVisible
import ru.tensor.sbis.design.person_suggest.suggest.PersonSuggestView
import ru.tensor.sbis.design.person_suggest.suggest.contract.PersonSuggestKeyboardBehavior

/**
 * Обработчик событий клавиатуры для организации стандартного поведения панели выбора персоны [PersonSuggestView].
 * @see [PersonSuggestKeyboardBehavior]
 *
 * @author vv.chekurda
 */
internal class PersonSuggestKeyboardEventHandler : PersonSuggestKeyboardBehavior {

    private var isKeyboardOpened = false

    /**
     * View, состояние которой будет изменяться.
     */
    lateinit var targetView: View

    /**
     * Признак наличия данных для отображения в компоненте.
     */
    var hasData: Boolean = false
        set(value) {
            val isChanged = field != value
            field = value
            if (isChanged) updateVisibilityState()
        }

    override var showOnKeyboard: Boolean = false
        set(value) {
            val isChanged = field != value
            field = value
            if (isChanged) updateVisibilityState()
        }

    override var translateOnKeyboard: Boolean = true

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        isKeyboardOpened = true
        if (translateOnKeyboard) {
            targetView.translationY = -keyboardHeight.toFloat()
        }
        updateVisibilityState()
        return true
    }

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        isKeyboardOpened = false
        if (translateOnKeyboard) {
            targetView.translationY = 0f
        }
        updateVisibilityState()
        return true
    }

    private fun updateVisibilityState() {
        targetView.isVisible = showOnKeyboard && hasData && isKeyboardOpened
    }
}