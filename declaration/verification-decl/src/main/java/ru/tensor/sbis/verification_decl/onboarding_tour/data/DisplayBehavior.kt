package ru.tensor.sbis.verification_decl.onboarding_tour.data

/**
 * Поведение отображения тура.
 */
enum class DisplayBehavior {

    /** Всегда. */
    ALWAYS,

    /** Один раз и только. */
    UNIQUE,

    /** Один раз и только для пользователя. */
    PER_USER;

}