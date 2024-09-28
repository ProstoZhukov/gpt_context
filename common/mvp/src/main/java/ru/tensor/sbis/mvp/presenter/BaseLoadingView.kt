package ru.tensor.sbis.mvp.presenter

/**
 * Базовый класс для показа крутилки
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
interface BaseLoadingView {

    /** @SelfDocumented */
    fun showLoading()

    /** @SelfDocumented */
    fun hideLoading()

}
