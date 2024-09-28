package ru.tensor.sbis.mvp.presenter.loadcontent

/**
 * Интерфейс для представления с возможностью загрузки данных.
 *
 * @author am.boldinov
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
interface LoadContentView {

    /**
     * Обновить состояние загрузки.
     * @param show - показать/скрыть процесс загрузки
     */
    fun showLoadingProcess(show: Boolean)

}