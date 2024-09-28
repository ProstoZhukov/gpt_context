package ru.tensor.sbis.verification_decl.login.event

/**
 * Событие связанное с хостом приложения.
 */
sealed class HostEvent

/**
 * Произошла инициализация настроек хоста.
 */
object InitHostEvent : HostEvent()

/**
 * Произошла смена хоста.
 */
object ChangeHostEvent : HostEvent()
