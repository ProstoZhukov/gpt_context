@file:Suppress("DEPRECATION")

package ru.saby_clients.pagination

import androidx.annotation.StringRes
import io.reactivex.Observable
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.presenter.BasePresenter
import ru.tensor.sbis.platform.generated.Subscription

/**
 * Набор интерфейсов для компонента односторонней пагинации
 */
interface SabyClientsPaginationContract {

    interface View<DM> {

        /**
         * Обновить список данных в адаптере
         */
        fun updateDataList(dataList: ArrayList<DM>)

        /**
         * Добавить элементы в список данных
         */
        fun addContentToDataList(dataList: ArrayList<DM>)

        /**
         * Показать прогресс бар
         */
        fun showMainLoadingProgress(showLoadingProgress: Boolean)

        /**
         * Показать заглушку
         */
        fun showStubView(@StringRes msgResId: Int)

        /**
         * Показать прогресс бар в списке
         */
        fun showListLoadingProgress(showLoadingProgress: Boolean)

        /**
         * Обновить список
         */
        fun notifyDataSetChanged()

        /**
         * Обновить элемент списка
         */
        fun updateItem(position: Int, item: DM)
    }

    interface Presenter<VIEW> : BasePresenter<VIEW> {

        /**
         * Функция копирования списка [ArrayList]
         */
        fun <T> ArrayList<T>.copy(): ArrayList<T> = map { it }.asArrayList()

        /**
         * Запрос на обновление списка
         */
        fun onRefresh()

        /**
         * Скролл списка
         */
        fun onScroll(dy: Int, lastVisibleItemPosition: Int, computeVerticalScrollOffset: Int)
    }

    interface Interactor<DM, QUERY_FILTER> {

        /**
         * Загрузка списка данных
         */
        fun loadList(filter: QUERY_FILTER, fromCallback: Boolean): Observable<PagedListResult<DM>>

        /**
         * Установка колбэка для получения данных
         */
        fun setListCallback(action: (HashMap<String, String>) -> Unit): Observable<Subscription>? = null

        /**
         * Установка колбэка для получения данных с колбэка
         */
        fun setListDataCallback(action: (PagedListResult<DM>) -> Unit): Observable<Subscription>? = null
    }

    interface Interactor2<DM, QUERY_FILTER> {

        /**
         * Метод получения списка для обновления. Кол-во элементов в фильтре передается только для инициализации.
         * Результат может состоять из большего числа элементов, например, при обновлении всего загруженного списка
         * вместе с доп. страницами
         */
        fun loadList(filter: QUERY_FILTER): Observable<PagedListResult<DM>>

        /**
         * Загрузка страницы списка
         */
        fun loadPage(filter: QUERY_FILTER): Observable<PagedListResult<DM>>
    }
}