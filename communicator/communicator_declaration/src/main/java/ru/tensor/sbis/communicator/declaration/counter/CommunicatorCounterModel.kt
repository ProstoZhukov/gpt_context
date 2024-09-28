package ru.tensor.sbis.communicator.declaration.counter

/** Дата-класс счётчиков непрочитанных диалогов и чатов. */
data class CommunicatorCounterModel(
        val unreadDialogs: Int,
        val unviewedDialogs: Int,
        val unreadChats: Int,
        val unviewedChats: Int,
        val unreadTotal: Int,
        val unviewedTotal: Int
)