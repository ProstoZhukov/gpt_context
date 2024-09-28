package ru.tensor.sbis.mvp.presenter

/**
 * Базовый класс для ошибки
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
interface BaseErrorView {

    /**
     * Показать ошибку
     */
    fun showError(errorMessage: String)

}