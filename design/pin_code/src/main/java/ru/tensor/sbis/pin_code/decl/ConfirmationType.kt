package ru.tensor.sbis.pin_code.decl

/**
 * Тип подтверждения завершения ввода пин-кода.
 *
 * @author mb.kruglova
 */
enum class ConfirmationType {
    /**
     * По нажатию кнопки подтверждения. При достижении максимальной длинны ввода кода, будет показана кнопка подтверждения.
     * По нажатию произойдет вызов [PinCodeRepository.onCodeEntered]
     */
    BUTTON,

    /**
     * По достижению максимальной длинны ввода кода. После ввода последнего символа произоет вызов [PinCodeRepository.onCodeEntered]
     */
    INPUT_COMPLETION
}