package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.helper

import android.os.Parcelable
import android.view.KeyEvent
import ru.tensor.sbis.common.util.AdjustResizeHelper

/**
 * Интерфейс вспомогательного класса view списка статусов прочитанности сообщения
 * для управления состоянием клавиатуры
 *
 * @author vv.chekurda
 */
internal interface ReadStatusListStateHelper : AdjustResizeHelper.KeyboardEventListener {

    /**
     * Показать клавиатуру
     */
    fun showKeyboard()

    /**
     * Закрыть клавиатуру
     */
    fun hideKeyboard()

    /** @SelfDocumented */
    fun onSaveInstanceState(state: Parcelable?): Parcelable

    /** @SelfDocumented */
    fun onRestoreInstanceState(state: Parcelable)

    /** @SelfDocumented */
    fun dispatchKeyEventPreIme(event: KeyEvent)
}