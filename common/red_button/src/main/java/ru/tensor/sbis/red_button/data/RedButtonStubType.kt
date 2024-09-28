package ru.tensor.sbis.red_button.data

/**
 * Перечисление возможных типов заглушки
 * @property value целочисленное значение состояния, приходящее от контроллера.
 * Данное поле используется в маппере [RedButtonStubMapper]
 *
 * @author ra.stepanov
 */
@Suppress("KDocUnresolvedReference")
enum class RedButtonStubType(val value: Int) {

    /**
     * Требуется отобразить заглушку с текстом "Данные закрываются".
     * Заглушка с этим типом будет отображена после вызова метода [RedButtonService.confirmOn] и повторного логина в приложение.
     */
    CLOSE_STUB(1),

    /**
     * Требуется отобразить заглушку с текстом "Данные открываются".
     * Заглушка с этим типом будет отображена после вызова метода [RedButtonService.off] и повторного логина в приложение.
     */
    OPEN_STUB(0),

    /** Значение, обозначающее, что заглушка не нужна */
    NO_STUB(-1)
}