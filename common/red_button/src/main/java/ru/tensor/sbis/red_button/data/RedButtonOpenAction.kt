package ru.tensor.sbis.red_button.data

/**
 * Перечисление возможных открываемых из вне модуля окон: 2 типа диалоговых окон и фрагмент красной кнопки
 *
 * @author ra.stepanov
 */
@Suppress("KDocUnresolvedReference")
enum class RedButtonOpenAction {

    /**
     * Открыть диалоговое окно с текстом для действия "Скрытие управленческого учёта".
     * Используется при клике на [RedButtonPreference].
     * Требуется передать его в [RedButtonOpenHelper.openRedButton] если текущее выполняемое действие [RedButtonActions.HIDE_MANAGEMENT]
     * и состояние красной кнопки [RedButtonState.NOT_CLICK]
     * @see [RedButtonOpenHelper.openDialog]
     */
    OPEN_DIALOG_MANAGEMENT,

    /**
     * Открыть диалоговое окно с текстом для действия "Переход в пустой кабинет".
     * Используется при клике на [RedButtonPreference].
     * Требуется передать его в [RedButtonOpenHelper.openRedButton] если текущее выполняемое действие [RedButtonActions.EMPTY_CABINET]
     * и состояние красной кнопки [RedButtonState.NOT_CLICK]
     * @see [RedButtonOpenHelper.openDialog]
     */
    OPEN_DIALOG_EMPTY_CABINET,

    /**
     * Открыть [PinFragment]
     * Используется при клике на [RedButtonPreference].
     * Требуется передать его в [RedButtonOpenHelper.openRedButton] если текущее состояние красной кнопки [RedButtonState.CLICK]
     * @see [RedButtonOpenHelper.openRedButtonFragment]
     */
    OPEN_FRAGMENT
}