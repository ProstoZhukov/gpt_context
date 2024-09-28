package ru.tensor.sbis.message_panel.viewModel.livedata

import androidx.annotation.Px
import io.reactivex.Observable

/**
 * Интерфейс управления доступностью места для отображения элементов панели ввода
 * @author Subbotenko Dmitry
 */
interface MessagePanelAvailableSpaceForContent {
    val hasSpaceForAttachments: Observable<Boolean>
    val hasSpaceForRecipients: Observable<Boolean>
    val editTextMaxHeight: Observable<Int>

    fun setHasSpaceForAttachments(hasSpaceForAttachments: Boolean)
    fun setHasSpaceForRecipients(hasSpaceForRecipients: Boolean)
    fun setEditTextMaxHeight(@Px maxHeight: Int)
}