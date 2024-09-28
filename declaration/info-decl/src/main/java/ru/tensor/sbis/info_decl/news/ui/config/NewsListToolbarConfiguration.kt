package ru.tensor.sbis.info_decl.news.ui.config

import android.content.Context
import android.view.View
import io.reactivex.Observable

/**
 * Кастомная конфигурация тулбара для реестра новостей.
 *
 * @author am.boldinov
 */
interface NewsListToolbarConfiguration {

    interface ToolbarBehavior {

        /**
         * Возвращает слушатель изменения поисковой строки
         */
        fun searchQueryChangedObservable(): Observable<String>

        /**
         * Возвращает слушатель отмены поиска/очистки поисковой строки
         */
        fun cancelSearchObservable(): Observable<Any>

        /**
         * Возвращает слушатель изменения фокуса поля ввода поиска
         * Системный метод [android.widget.TextView.setOnFocusChangeListener]
         */
        fun searchFocusChangeObservable(): Observable<Boolean>

        /**
         * Возвращает слушатель действий с полем ввода, например нажатия кнопок ввода и поиска на клавиатуре.
         * Системный метод [android.widget.TextView.setOnEditorActionListener]
         */
        fun searchFieldEditorActionsObservable(): Observable<Int>

        /**
         * Возвращает слушатель нажатий на кнопку фильтра
         */
        fun filterClickObservable(): Observable<Any>

        /**
         * Устанавливает текст в поле ввода поиска
         */
        fun setSearchText(searchText: String)

        /**
         * Возвращает установленный текст из поля ввода поиска
         */
        fun getSearchText(): String

        /**
         * Устанавливает фокус и курсор в поле ввода поиска
         */
        fun showCursorInSearch()

        /**
         * Убирает фокус и курсор из поля ввода поиска
         */
        fun hideCursorFromSearch()

        /**
         * Показывает клавиатуру
         */
        fun showKeyboard()

        /**
         * Прячет клавиатуру
         */
        fun hideKeyboard()

        /**
         * Устанавливает выбранные названия фильтров
         *
         * @param filters список выбранных фильтров
         */
        fun setSelectedFilters(filters: List<String>)

        /**
         * Отобразить панель поиска
         */
        fun showSearchPanel()

        /**
         * Спрятать панель поиска
         */
        fun hideSearchPanel()
    }

    /**
     * Возвращает [View], которая будет добавлена в качестве тулбара над списком новостей
     *
     * @param context контекст фрагмента
     */
    fun inflateToolbar(context: Context): View

    /**
     * Возвращает реализацию поведения тулбара
     *
     * @param toolbar созданный тулбар с помощью метода [inflateToolbar]
     */
    fun createToolbarBehavior(toolbar: View): ToolbarBehavior
}