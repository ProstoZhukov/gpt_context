package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.widget.FrameLayout
import ru.tensor.sbis.communicator.generated.MessageReadStatus
import ru.tensor.sbis.communicator.sbis_conversation.databinding.CommunicatorReadStatusListViewBinding
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.contract.ReadStatusListViewContract
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.contract.ReadStatusListViewDependency
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.helper.ReadStatusListLayoutHelper
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.helper.ReadStatusListLayoutHelperImpl

/**
 * View списка статусов прочитанности сообщения
 * Макет по лонг-клику на сообщение внутри диалога:
 * http://axure.tensor.ru/CommunicatorMobile/%D1%81%D0%BE%D0%BE%D0%B1%D1%89%D0%B5%D0%BD%D0%B8%D1%8F.html#OnLoadVariable=%D1%81%D0%BE%D0%BE%D0%B1%D1%89%D0%B5%D0%BD%D0%B8%D1%8F_%D0%BC%D0%BF&CSUM=1
 *
 * @author vv.chekurda
 */
internal class ReadStatusListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr),
    ReadStatusListViewContract {

    private val layoutHelper: ReadStatusListLayoutHelper

    init {
        CommunicatorReadStatusListViewBinding.inflate(LayoutInflater.from(context)).let {
            addView(it.root)
            layoutHelper = ReadStatusListLayoutHelperImpl(it)
        }
    }

    override fun initReadStatusListView(dependency: ReadStatusListViewDependency) {
        layoutHelper.initHelper(dependency)
    }

    override fun onMessageReceiversCountChanged(count: Int) {
        layoutHelper.onMessageReceiversCountChanged(count)
    }

    override fun selectFilter(filter: MessageReadStatus) {
        layoutHelper.selectFilter(filter)
    }

    override fun onSaveInstanceState(): Parcelable =
        layoutHelper.onSaveInstanceState(super.onSaveInstanceState())

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        state?.let(layoutHelper::onRestoreInstanceState)
    }

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean =
        layoutHelper.onKeyboardOpenMeasure(keyboardHeight)

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean =
        layoutHelper.onKeyboardCloseMeasure(keyboardHeight)

    override fun hideKeyboard() {
        layoutHelper.hideKeyboard()
    }

    override fun dispatchKeyEventPreIme(event: KeyEvent?): Boolean {
        event?.let(layoutHelper::dispatchKeyEventPreIme)
        return super.dispatchKeyEventPreIme(event)
    }
}