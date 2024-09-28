package ru.tensor.sbis.mvp.presenter.loadcontent

/**
 * Базовый интерфейс представление карточки.
 *
 * @author am.boldinov
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
interface BaseCardView<VIEW_MODEL, EMPTY_DATA> : LoadContentView {

    /**
     * Отобразить данные.
     */
    fun displayData(data: VIEW_MODEL)

    /**
     * Очистить данные.
     */
    fun clearData()

    /**
     * Задать информацию для empty view.
     */
    fun updateEmptyView(data: EMPTY_DATA?)

    /**
     * Отобразить ошибку загрузки.
     */
    fun showLoadingError(message: String)

}