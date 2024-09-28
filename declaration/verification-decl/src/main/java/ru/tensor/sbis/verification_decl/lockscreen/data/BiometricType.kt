package ru.tensor.sbis.verification_decl.lockscreen.data

/**
 * Тип биометрического входа.
 *
 * @author ar.leschev
 */
enum class BiometricType {
    /** Лицо. */
    FACE_ID,

    /** Отпечаток пальца. */
    FINGER,

    /** Пин-код. */
    PIN,

    /** Нет. */
    NONE
}