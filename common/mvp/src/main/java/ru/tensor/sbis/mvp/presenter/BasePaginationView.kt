@file:Suppress("unused")

package ru.tensor.sbis.mvp.presenter

/**
 * Базовый интерфейс для вью с разбивкой на страницы
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
interface BasePaginationView<T> {

    /** @SelfDocumented */
    fun showLoadingProcess(showProgress: Boolean)

    /** @SelfDocumented */
    fun setData(data: T)

    /** @SelfDocumented */
    fun addData(dataToAdd: T)

    /** @SelfDocumented */
    fun showLoadingError()

}