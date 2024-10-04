package ru.tensor.sbis.design.message_view.utils

import ru.tensor.sbis.communicator.generated.PersonList
import ru.tensor.sbis.communicator.generated.ServiceMessage
import ru.tensor.sbis.communicator.generated.ServiceMessageGroup

/** @SelfDocumented */
internal infix fun ServiceMessageGroup?.equals(other: ServiceMessageGroup?): Boolean =
    if (this != null && other != null) {
        text == other.text &&
            messagesCount == other.messagesCount &&
            folded == other.folded &&
            hasMore == other.hasMore &&
            unfoldedMessagesLimit == other.unfoldedMessagesLimit &&
            firstMessageUuid == other.firstMessageUuid &&
            unreadCount == other.unreadCount
    } else {
        this == other
    }

/** @SelfDocumented */
internal infix fun ServiceMessage?.equals(other: ServiceMessage?): Boolean = if (this != null && other != null) {
    type == other.type &&
        text == other.text &&
        activeChatClosedMessage == other.activeChatClosedMessage &&
        personList equals other.personList
} else {
    this == other
}

private infix fun PersonList?.equals(other: PersonList?): Boolean = if (this != null && other != null) {
    brief == other.brief &&
        foldedCount == other.foldedCount &&
        unfoldedCount == other.unfoldedCount
} else {
    this == other
}