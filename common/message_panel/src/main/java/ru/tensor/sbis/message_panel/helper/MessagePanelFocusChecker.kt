package ru.tensor.sbis.message_panel.helper

import android.view.View
import android.view.ViewParent
import android.view.ViewTreeObserver
import android.widget.EditText
import ru.tensor.sbis.message_panel.view.MessagePanel

/**
 * Утилита для обработки состояния фокуса
 * Распознает, находится ли фокус в [MessagePanel] или в любой иной [EditText]
 *
 * Created by sr.golovkin on 17.02.2019
 */

abstract class MessagePanelFocusChecker(private val rootView: View) {

    /**
     * Перечисление, описывающее получателя фокуса
     */
    enum class FocusTarget {
        MESSAGE_PANEL, //панель ввода комментария
        EDITTEXT, //любое поле ввода, отличное от панели ввода комментария
        VIEW, //Любая View, не являющаяся полем ввода
        NO_TARGET // получатель не определен
    }

    /**
     * Перечисление, описывающее действие, которое фактически произошло
     */
    enum class FocusState {
        FOCUS_CHANGED, //фокус переместился на другой экземпляр компонента. Ранее фокус был кем-то захвачен
        FOCUS_RELEASED //фокус отпущен
    }

    private val listener by lazy {
        //пренебрегаем oldFocus, тк мы не можем однозначно понять, был ли действительно фокус до смены или нет
        //тк в разных версиях API данный колбэк вернется с разными данными
        //например в pre-M при очистке фокуса методом clearFocus() oldFocus = null
        //когда в post-M будет ссылка на вью, которая потеряла фокус
        ViewTreeObserver.OnGlobalFocusChangeListener { _, newFocus ->
            val focusTarget: FocusTarget
            val focusState = when {
                newFocus != null -> FocusState.FOCUS_CHANGED
                else -> FocusState.FOCUS_RELEASED //в случае newFocus == null. Ситуация с oldFocus == null и newFocus == null невозможна
            }
            focusTarget = if (focusState != FocusState.FOCUS_RELEASED) {
                val isMessagePanel = newFocus.getParentAs<MessagePanel>() != null
                when {
                    isMessagePanel -> FocusTarget.MESSAGE_PANEL
                    newFocus is EditText && !isMessagePanel -> FocusTarget.EDITTEXT
                    else -> FocusTarget.VIEW
                }
            } else {
                FocusTarget.NO_TARGET //Если FOCUS_RELEASED то целевой вью нет
            }
            currentTarget = focusTarget
            onFocusStateChanged(focusState, focusTarget)
        }
    }

    var currentTarget: FocusTarget = FocusTarget.NO_TARGET
        private set

    /**
     * Начать прослушивать состояние фокуса
     */
    fun startListen() {
        rootView.viewTreeObserver.addOnGlobalFocusChangeListener(listener)
    }

    /**
     * Прекратить прослушивание фокуса
     */
    fun stopListen() {
        rootView.viewTreeObserver.removeOnGlobalFocusChangeListener(listener)
    }

    private inline fun <reified PARENT_TYPE : View> View.getParentAs(): PARENT_TYPE? {
        var parent = parent
        while (parent != null && parent !is PARENT_TYPE && parent != rootView) {
            parent = (parent as ViewParent).parent
        }
        return parent as? PARENT_TYPE
    }

    /**
     * Обработать изменение фокуса
     */
    abstract fun onFocusStateChanged(state: FocusState, target: FocusTarget)

}