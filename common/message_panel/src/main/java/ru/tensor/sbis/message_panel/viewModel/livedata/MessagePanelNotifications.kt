package ru.tensor.sbis.message_panel.viewModel.livedata

import io.reactivex.Observable
import ru.tensor.sbis.common.rx.RxContainer
import ru.tensor.sbis.message_panel.view.AlertDialogData
import ru.tensor.sbis.message_panel.view.ProgressDialogLiveData

/**
 * Контракт для публикации уведомлений из панели ввода
 *
 * @author vv.chekurda
 */
@Deprecated("https://online.sbis.ru/opendoc.html?guid=bb1754f3-4936-4641-bdc2-beec53070c4b")
interface MessagePanelNotifications {
    val progressDialog: Observable<RxContainer<ProgressDialogLiveData>>
    val toast: Observable<CharSequence>
    val alertDialog: Observable<AlertDialogData>

    fun processDialog(dialog: ProgressDialogLiveData)
    fun showToast(message: CharSequence)
    fun showAlertDialog(alert: AlertDialogData)
}