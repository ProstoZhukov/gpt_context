/**
 * Инструменты для подготовки тестов
 *
 * @author vv.chekurda
 * Создан 8/10/2019
 */
package ru.tensor.sbis.message_panel

import org.mockito.kotlin.whenever
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import ru.tensor.sbis.common.rx.RxContainer
import ru.tensor.sbis.attachments.models.AttachmentRegisterModel
import ru.tensor.sbis.message_panel.viewModel.livedata.MessagePanelLiveData

fun MessagePanelLiveData.setUpDefaultSubjects(
    text: Subject<RxContainer<String>> = PublishSubject.create(),
    attachments: Subject<List<AttachmentRegisterModel>> = PublishSubject.create(),
    recipientSelection: Subject<Boolean> = PublishSubject.create(),
    originalMessageVisibility: Subject<Boolean> = PublishSubject.create()
) = setUpSubjects(text, attachments, recipientSelection, originalMessageVisibility)

fun MessagePanelLiveData.setUpSubjects(
    text: Subject<RxContainer<String>>? = null,
    attachments: Subject<List<AttachmentRegisterModel>>? = null,
    recipientSelection: Subject<Boolean>? = null,
    originalMessageVisibility: Subject<Boolean>? = null
) {
    whenever(this.messageText).thenReturn(text)
    whenever(this.attachments).thenReturn(attachments)
    whenever(this.recipientsSelected).thenReturn(recipientSelection)
    whenever(this.quotePanelVisible).thenReturn(originalMessageVisibility)
}