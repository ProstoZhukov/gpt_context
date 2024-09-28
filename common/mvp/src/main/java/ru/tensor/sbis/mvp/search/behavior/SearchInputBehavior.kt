package ru.tensor.sbis.mvp.search.behavior

import io.reactivex.Observable

/**
 * Поведение компонента поиска
 *
 * @author am.boldinov
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
interface SearchInputBehavior {

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
}

/**
 * Расширенный интерфейс поведения для строки поиска, с возможностью ее показа и скрытия.
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
interface AppearanceSearchInputBehavior : SearchInputBehavior {

    /**
     * Показать панель поиска.
     */
    fun showSearchPanel()

    /**
     * Скрыть панель поиска.
     */
    fun hideSearchPanel()
}