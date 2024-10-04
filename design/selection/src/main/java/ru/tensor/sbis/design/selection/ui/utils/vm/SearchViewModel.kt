package ru.tensor.sbis.design.selection.ui.utils.vm

import io.reactivex.Observable

/**
 * Вью модель, которая отвечает за механику связанную с поисковой строкой
 *
 * @author ma.kolpakov
 */
internal interface SearchViewModel {

    /**
     * Пользовательский ввод для поискового запроса
     */
    val searchText: Observable<String>

    /**
     * Поисковой запрос: пользовательский ввод с применением правил фильтрации
     */
    val searchQuery: Observable<String>

    /**
     * Поток запросов на скрытие клавиатуры
     */
    val hideKeyboardEvent: Observable<Unit>

    /**
     * Отключение реакции на пользовательский ввод через [setSearchText]
     */
    var isEnabled: Boolean

    /**
     * Состояние фокуса панели поиска
     */
    var isFocused: Boolean

    fun setSearchText(text: String)

    fun cancelSearch()

    fun clearSearch()

    fun finishEditingSearchQuery()
}