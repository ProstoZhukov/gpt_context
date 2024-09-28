package ru.tensor.sbis.message_panel.viewModel.livedata

import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.common.rx.RxContainer
import ru.tensor.sbis.common.rx.livedata.dataValue
import ru.tensor.sbis.message_panel.view.AlertDialogData
import ru.tensor.sbis.message_panel.view.ProgressDialogLiveData

/**
 * @author vv.chekurda
 */
internal class MessagePanelNotificationsImpl : MessagePanelNotifications {

    override val progressDialog = BehaviorSubject.create<RxContainer<ProgressDialogLiveData>>()
    override val toast = PublishSubject.create<CharSequence>()
    override val alertDialog = PublishSubject.create<AlertDialogData>()

    override fun processDialog(dialog: ProgressDialogLiveData) {
        progressDialog.dataValue = dialog
    }

    override fun showToast(message: CharSequence) = toast.onNext(message)

    override fun showAlertDialog(alert: AlertDialogData) = alertDialog.onNext(alert)
}