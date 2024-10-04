package ru.tensor.sbis.design.confirmation_dialog

/**
 * Выравнивание кнопок в диалоге подтверждения
 * @author ma.kolpakov
 */
enum class ConfirmationButtonOrientation {
    /**
     * Горизонтально, все кнопки одного размера
     */
    HORIZONTAL,

    /**
     * Вертикально
     */
    VERTICAL,

    /**
     * Автоматически - горизонтально, если не помещается то вертикально
     */
    AUTO
}
