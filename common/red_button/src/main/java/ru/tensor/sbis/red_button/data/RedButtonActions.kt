package ru.tensor.sbis.red_button.data

/**
 * Перечисление всех возможных действий компонента "Красная кнопка",
 * используется при показе диалогового окна с подтвержением
 * @property value целочисленное значение выполняемого действия, приходящее от контроллера.
 * Данное поле используется в маппере [RedButtonActionsMapper]
 *
 * @author ra.stepanov
 */
@Suppress("KDocUnresolvedReference")
enum class RedButtonActions(val value: Int) {

    /**
     * Скрытие управленческого учета
     */
    HIDE_MANAGEMENT(0),

    /**
     * Переход в пустой кабинет
     */
    EMPTY_CABINET(1)
}