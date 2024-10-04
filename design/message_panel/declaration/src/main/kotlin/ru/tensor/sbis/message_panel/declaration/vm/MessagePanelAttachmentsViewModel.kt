package ru.tensor.sbis.message_panel.declaration.vm

import androidx.annotation.IntRange
import androidx.lifecycle.LiveData
import io.reactivex.Observable
import ru.tensor.sbis.attachments.models.AttachmentRegisterModel
import ru.tensor.sbis.attachments.models.property.MAX_PROGRESS

/**
 * TODO: 11/13/2020 Добавить документацию
 *
 * @author ma.kolpakov
 */
interface MessagePanelAttachmentsViewModel {

    val attachments: LiveData<List<AttachmentRegisterModel>>
    val hasAttachments: LiveData<Boolean>
    /**
     * Подписка на обновление прогресса [Pair.second] для по позиции [Pair.first]
     */
    val uploadProgress: Observable<Pair<Int, Int>>

    val attachmentsButtonEnabled: LiveData<Boolean>
    val attachmentsButtonVisible: LiveData<Int>
    val attachmentsVisibility: LiveData<Int>

    fun setAttachments(attachments: List<AttachmentRegisterModel>)
    fun setAttachmentProgress(position: Int, @IntRange(from = 0L, to = MAX_PROGRESS.toLong()) progress: Int)
    fun setAttachmentsButtonVisible(visible: Boolean)

    fun onAttachButtonClick()
}