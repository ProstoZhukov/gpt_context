package ru.tensor.sbis.red_button.ui.host.data

/**
 * Перечисление возможных действий после ввода кода
 *
 * @author ra.stepanov
 */
internal enum class PinConfirmationResult {
    // Закрыть HostFragment
    CLOSE,

    // Открыть следующий шаг работы
    NAVIGATE_NEXT,

    // Проигнорировать результат
    IGNORE
}