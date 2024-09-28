package ru.tensor.sbis.message_panel.viewModel.livedata.keyboard

import android.view.View
import ru.tensor.sbis.message_panel.contract.FocusChangeListener

/**
 * Подписка на изменение фокуса в панели ввода
 *
 * @author vv.chekurda
 * @since 1/14/2020
 */
internal class FocusListener(
    private val mediator: KeyboardEventMediator,
    private val onFocusChanged: FocusChangeListener?
) : View.OnFocusChangeListener {

    override fun onFocusChange(ignored: View?, hasFocus: Boolean) {
        onFocusChanged?.invoke(hasFocus)
        mediator.postKeyboardEvent(if (hasFocus) OpenedByFocus else ClosedByFocus)
    }
}