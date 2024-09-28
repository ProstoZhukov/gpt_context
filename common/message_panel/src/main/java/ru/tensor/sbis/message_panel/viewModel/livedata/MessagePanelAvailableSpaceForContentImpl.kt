package ru.tensor.sbis.message_panel.viewModel.livedata

import io.reactivex.subjects.BehaviorSubject

/**
 * Релизация интерфейса для управления доступностью места для элементов панели ввода
 * @author Subbotenko Dmitry
 */
internal class MessagePanelAvailableSpaceForContentImpl : MessagePanelAvailableSpaceForContent {

    override val hasSpaceForAttachments = BehaviorSubject.createDefault(true)
    override val hasSpaceForRecipients = BehaviorSubject.createDefault(true)
    override val editTextMaxHeight = BehaviorSubject.create<Int>()

    override fun setHasSpaceForAttachments(hasSpaceForAttachments: Boolean) {
        this.hasSpaceForAttachments.onNext(hasSpaceForAttachments)
    }

    override fun setHasSpaceForRecipients(hasSpaceForRecipients: Boolean) {
        this.hasSpaceForRecipients.onNext(hasSpaceForRecipients)
    }

    override fun setEditTextMaxHeight(maxHeight: Int) {
        editTextMaxHeight.onNext(maxHeight)
    }
}