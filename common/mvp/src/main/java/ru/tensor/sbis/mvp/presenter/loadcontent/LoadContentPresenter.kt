package ru.tensor.sbis.mvp.presenter.loadcontent

import ru.tensor.sbis.common.exceptions.LoadDataException
import ru.tensor.sbis.mvp.presenter.BasePresenter

/**
 * Интерфейс для презентера, отвечающего за управление экраном с загружаемым контентом.
 * @param <V> - тип представления
 *
 * @author am.boldinov
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
interface LoadContentPresenter<V> : BasePresenter<V> {

    /**
     * Инициация обновления.
     */
    fun onRefresh()

    /**
     * Начать загрузку данных.
     * @param force         - если `true` - загружать из удаленного источника, иначе - из локального
     * @param showProcess   - флаг отображения индикатора загрузки
     */
    fun startLoading(force: Boolean, showProcess: Boolean)

    /**
     * Индикация окончания загрузки данных.
     */
    fun onLoadingCompleted()

    /**
     * Индикация ошибки при загрузке данных.
     * @param exception - произошедшее исключение
     */
    fun onLoadingError(exception: LoadDataException?)

}
