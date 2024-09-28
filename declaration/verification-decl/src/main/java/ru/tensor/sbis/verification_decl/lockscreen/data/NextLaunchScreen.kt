package ru.tensor.sbis.verification_decl.lockscreen.data

/**
 * Следующий экран при запуске приложения.
 *
 * @author ar.leschev
 */
enum class NextLaunchScreen {
    /** Главный экран (MainActivity). */
    MAIN,

    /** Экран авторизации по логину\паролю. (LoginActivity). */
    LOGIN,

    /** Экран авторизации по пин-коду. */
    @Deprecated("Можно не реализовывать, если используется EntryPointActivity.")
    LOCK
}