package ru.tensor.sbis.verification_decl.lockscreen

import java.util.UUID

/**
 * @author as.medvedev1
 */
interface LocalizationHelper {

    /**
     * Синхронизация выбранной локализации приложения согласно настройкам текущего пользователя
     * @param callback - в случае асинхронной реализации метода, данный функтор должен быть вызван
     * по окончании процесса синхронизации в UI потоке приложения.
     *
     * Если синхронизация локализации происходит во время авторизации и заранее известен пользователь,
     * то стоит передавать [userUuid]
     */
    fun syncLocale(userUuid: UUID?, callback: () -> Unit = { /* no-op */ }) = Unit

}