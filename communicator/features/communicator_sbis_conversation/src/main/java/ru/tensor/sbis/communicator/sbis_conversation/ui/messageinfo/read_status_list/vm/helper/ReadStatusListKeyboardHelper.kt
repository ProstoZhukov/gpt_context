package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.helper

import android.os.Parcel
import android.os.Parcelable
import android.view.KeyEvent
import android.view.View
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.sbis_conversation.databinding.CommunicatorReadStatusListViewBinding
import androidx.core.view.updatePadding

/**
 * Интерфейс вспомогательного класса view списка статусов прочитанности сообщения
 * для обработки клавиатуры
 * @see [ReadStatusListStateHelper]
 *
 * @author vv.chekurda
 */
internal interface ReadStatusListKeyboardHelper :
    ReadStatusListStateHelper {

    /**
     * Обработать дейтивие для закрытия клавиатуры
     */
    fun handleHideKeyboardAction()

    /** @SelfDocumented */
    fun viewIsResumed()

    /** @SelfDocumented */
    fun viewIsPaused()

    /**
     * Очистить ссылки
     */
    fun cleanReferences()
}

/**
 * Вспомогательный класс view списка статусов прочитанности сообщения
 * для обработки клавиатуры
 * @see [ReadStatusListKeyboardHelper]
 *
 * @property binding binding view списка статусов прочитанности сообщения
 *
 * @author vv.chekurda
 */
internal class ReadStatusListKeyboardDelegate(
    private var binding: CommunicatorReadStatusListViewBinding?
) : ReadStatusListKeyboardHelper {

    /**
     * true, если необходимо отображать клавиатуру
     */
    private var needShowKeyboard: Boolean = false

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        needShowKeyboard = true
        setViewBottomPadding(keyboardHeight)
        return true
    }

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        setViewBottomPadding(0)
        return true
    }

    override fun dispatchKeyEventPreIme(event: KeyEvent) {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            needShowKeyboard = false
            setViewBottomPadding(0)
        }
    }

    override fun onSaveInstanceState(state: Parcelable?): Parcelable =
        SavedState(state, needShowKeyboard)

    override fun onRestoreInstanceState(state: Parcelable) {
        needShowKeyboard = state.castTo<SavedState>()!!.needShowKeyboard
    }

    private fun setViewBottomPadding(bottomPadding: Int) {
        binding?.let {
            it.communicatorReadStatusSbisList.setPadding(0, 0, 0, bottomPadding)
            it.communicatorReadStatusProgress.updatePadding(0, 0, 0, bottomPadding)
        }
    }

    override fun handleHideKeyboardAction() {
        if (!needShowKeyboard) return
        needShowKeyboard = false
        hideKeyboard()
    }

    override fun showKeyboard() {
        binding?.communicatorReadStatusSearchInput?.showKeyboard()
    }

    override fun hideKeyboard() {
        binding?.let  {
            setViewBottomPadding(0)
            binding?.communicatorReadStatusSearchInput?.hideKeyboard()
        }
    }

    override fun viewIsResumed() {
        if (needShowKeyboard) {
            binding!!.root.post {
                showKeyboard()
            }
        }
    }

    override fun viewIsPaused() {
        hideKeyboard()
    }

    override fun cleanReferences() {
        binding = null
    }
}

private class SavedState : View.BaseSavedState {

    var needShowKeyboard: Boolean = false

    constructor(
        superState: Parcelable?,
        needShowKeyboard: Boolean
    ) : super(superState) {
        this.needShowKeyboard = needShowKeyboard
    }

    private constructor(`in`: Parcel) : super(`in`) {
        needShowKeyboard = `in`.readByte().toInt() != 0
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeByte(if (needShowKeyboard) 1 else 0)
    }

    companion object CREATOR : Parcelable.Creator<SavedState> {
        override fun createFromParcel(parcel: Parcel): SavedState = SavedState(parcel)

        override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
    }

    override fun describeContents(): Int = 0
}