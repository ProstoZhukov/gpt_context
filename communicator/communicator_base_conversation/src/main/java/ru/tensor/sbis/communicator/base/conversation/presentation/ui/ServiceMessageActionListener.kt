package ru.tensor.sbis.communicator.base.conversation.presentation.ui

/** Интерфейс действий с сервисными сообщениями в реестре сообщений чата. */
interface ServiceMessageActionListener {

    /** @SelfDocumented */
    fun onServiceMessageClicked(position: Int)
}