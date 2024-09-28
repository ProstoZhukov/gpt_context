package ru.tensor.sbis.cashboxes_lite_decl.model

/** Фактический результат валидации серийного номера */
enum class ValidateSerialState {
    /** Успешная валидация серийника.  */
    SUCCESS,

    /** Не найден серийник в продажах (неизвестный серийный номер)  */
    UNKNOWN_SERIAL,

    /** Товар с серийным номером уже был продан  */
    SERIAL_ALREADY_SOLD,

    /** Товар с серийным номером уже был возвращён  */
    SERIAL_ALREADY_RETURNED,

    /** Серийник уже добавлен в текущую продажу  */
    SERIAL_ALREADY_IN_CURRENT_SALE,

    /** Серийник уже добавлен в текущий возврат  */
    SERIAL_ALREADY_IN_CURRENT_RETURN,

    /** Ошбика сети.  */
    NETWORK_ERROR,

    /** Неизвестная ошибка (возникла исключительная ситуация)  */
    UNKNOWN_ERROR
}