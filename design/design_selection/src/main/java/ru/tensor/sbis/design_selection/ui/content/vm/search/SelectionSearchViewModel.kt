package ru.tensor.sbis.design_selection.ui.content.vm.search

import io.reactivex.Observable

/**
 * Вью-модель, которая отвечает за механику связанную с поисковой строкой.
 *
 * @author vv.chekurda
 */
internal interface SelectionSearchViewModel {

    /**
     * Текущий поисковый запрос.
     */
    val searchQuery: String

    /**
     * Пользовательский ввод для поискового запроса
     */
    val searchTextObservable: Observable<String>

    /**
     * Поисковой запрос: пользовательский ввод с применением правил фильтрации
     */
    val searchQueryObservable: Observable<String>

    /**
     * Поток запросов на скрытие клавиатуры
     */
    val hideKeyboardEventObservable: Observable<Unit>

    /**
     * Отключение реакции на пользовательский ввод через [setSearchText]
     */
    var isEnabled: Boolean

    /**
     * Состояние фокуса панели поиска
     */
    var isFocused: Boolean

    /**
     * Установить текст для поисковой строки.
     *
     * @param text текст, который нужно установить в поисковую строку.
     */
    fun setSearchText(text: String)

    /**
     * Закрыть поиск: очистить поисковую строку и закрыть клавиатуру.
     */
    fun cancelSearch()

    /**
     * Очистить поисковую строку.
     */
    fun clearSearch()

    /**
     * Опустить клавиатуру.
     */
    fun hideKeyboard()
}