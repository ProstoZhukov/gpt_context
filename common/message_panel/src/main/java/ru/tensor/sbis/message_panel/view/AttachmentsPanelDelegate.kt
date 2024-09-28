package ru.tensor.sbis.message_panel.view

import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import ru.tensor.sbis.attachments.ui.view.register.AttachmentsView
import ru.tensor.sbis.attachments.ui.view.register.AttachmentsViewVisibility
import ru.tensor.sbis.common.rx.plusAssign
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.message_panel.R
import ru.tensor.sbis.message_panel.viewModel.livedata.MessagePanelLiveData

/**
 * Подписка панели вложений на события появления/скрытия клавиатуры для реализации поведения сжатия
 * и скрытия в альбомной ориентации
 *
 * @author vv.chekurda
 */
internal class AttachmentsPanelDelegate(
    private val attachments: AttachmentsView?
) {

    private val disposer = CompositeDisposable()

    fun bind(liveData: MessagePanelLiveData) {
        unbind()
        attachments ?: return
        disposer += liveData.attachments
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(attachments::submitList)
        attachments.actionListener = liveData.onAttachmentsActionsListener

        disposer += liveData.locationProgressUpdater
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { (id, progress) -> attachments.updateProgress(id, progress) }

        // управление видимостью
        disposer += liveData.attachmentsVisibility
                .subscribe { visibility ->
                    // удаление отступа снизу при частичном отображении, чтобы максимально прижать к низу
                    with(attachments.layoutParams as ViewGroup.MarginLayoutParams) {
                        with (attachments.resources) {
                            bottomMargin = if (visibility == AttachmentsViewVisibility.PARTIALLY)
                                getDimensionPixelSize(R.dimen.message_attachments_top_margin_partial)
                            else
                                getDimensionPixelSize(R.dimen.message_attachments_bottom_margin)
                        }
                    }
                    attachments.setVisibility(visibility)
                    attachments.safeRequestLayout()
                }
    }

    fun unbind() {
        disposer.clear()
    }
}