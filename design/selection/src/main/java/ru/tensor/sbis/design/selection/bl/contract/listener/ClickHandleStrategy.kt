package ru.tensor.sbis.design.selection.bl.contract.listener

/**
 * Стратегия обработки нажатия на элемент списка
 *
 * @author us.bessonov, ma.kolpakov
 */
enum class ClickHandleStrategy {

    /**
     * Поведение по умолчанию
     */
    DEFAULT,

    /**
     * Завершение выбора вместо поведения по умолчанию
     */
    COMPLETE_SELECTION,

    /**
     * Игнорировать клик
     */
    IGNORE
}
