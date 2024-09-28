package ru.tensor.sbis.verification_decl.lockscreen.data

/**
 * Статус биометрии устройства.
 *
 * @author ar.leschev.
 */
enum class BiometryStatus {
    /** Готово к использованию. */
    READY_TO_USE,

    /** Сенсоры есть, но нет заданной биометрии в настройках. */
    SUPPORTS_BUT_NOT_ENROLLED,

    /** Универстальный статус о невозможности использования биометрии, подробнее см androidx.biometric.BiometricManager. */
    NOT_AVAILABLE
}