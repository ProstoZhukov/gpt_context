package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.helper

import androidx.annotation.StringRes
import ru.tensor.sbis.communicator.design.R
import ru.tensor.sbis.communicator.generated.MessageReadStatus

/**
 * Enum опций фильтра списка статусов прочитанности сообщения.
 *
 * @param textRes ресурс текста.
 *
 * @author dv.baranov
 */
internal enum class ReadStatusFilterOption(@StringRes val textRes: Int) {
    /** Все */
    ALL(R.string.communicator_message_information_filter_all),

    /** Прочитавшие */
    READ(R.string.communicator_message_information_filter_read),

    /** Не прочитавшие */
    UNREAD(R.string.communicator_message_information_filter_unread),
}

/** Получить список доступных опций для фильтра прочитанности сообщения. */
internal fun getOptions() = listOf(
    ReadStatusFilterOption.ALL,
    ReadStatusFilterOption.READ,
    ReadStatusFilterOption.UNREAD,
)

/** Преобразовать [ReadStatusFilterOption] в модель контроллера [MessageReadStatus]. */
internal fun ReadStatusFilterOption.toMessageReadStatus(): MessageReadStatus = when (this) {
    ReadStatusFilterOption.ALL -> MessageReadStatus.ALL
    ReadStatusFilterOption.READ -> MessageReadStatus.READ
    ReadStatusFilterOption.UNREAD -> MessageReadStatus.UNREAD
}

/** Сравнить [ReadStatusFilterOption] в модель контроллера [MessageReadStatus]. */
internal fun ReadStatusFilterOption.isEqual(status: MessageReadStatus): Boolean =
    this.toMessageReadStatus() == status
