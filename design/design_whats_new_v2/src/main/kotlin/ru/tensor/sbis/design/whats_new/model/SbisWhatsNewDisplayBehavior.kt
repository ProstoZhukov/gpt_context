package ru.tensor.sbis.design.whats_new.model

/**
 * Варианты отображения "Что нового".
 *
 * @author ps.smirnyh
 */
enum class SbisWhatsNewDisplayBehavior {

    /** Отображение "Что нового" единожды после обновления. */
    ONLY_ONCE,

    /** Отображение "Что нового" после обновления для каждого пользователя. */
    PER_USER
}