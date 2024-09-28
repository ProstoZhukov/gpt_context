package ru.tensor.sbis.review.action

/**
 * Состояния сервиса оценок
 * @author ma.kolpakov
 */
enum class ReviewState {
    /**
     * Ждет процесс оценки не запущен
     */
    WAIT,

    /**
     * Стартовал процесс оценки
     */
    START,

    /**
     * Процесс оценки завершился
     */
    FINISH
}