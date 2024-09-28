package ru.tensor.sbis.message_panel.viewModel.livedata.keyboard

import android.view.KeyEvent
import ru.tensor.sbis.common_views.sbisview.SbisEditTextWithHideKeyboardListener

/**
 * Подписка на нажатие кнопки "Назад" для потери фокуса на поле ввода
 *
 * @author vv.chekurda
 * @since 1/14/2020
 */
internal class BackButtonListener(
    private val mediator: KeyboardEventMediator
) : SbisEditTextWithHideKeyboardListener.OnKeyPreImeListener {

    override fun onKeyPreImeEvent(keyCode: Int, ignored: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            mediator.postKeyboardEvent(ClosedByRequest)
            true
        } else {
            false
        }
    }
}