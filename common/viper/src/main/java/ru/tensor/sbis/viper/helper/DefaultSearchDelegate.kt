package ru.tensor.sbis.viper.helper

import ru.tensor.sbis.mvp.interactor.crudinterface.filter.ListFilter

/**
 * Класс для делегирования обработки начала поиска. Для работы поиска нужно определить вызов [onUpdateData].
 * Это может быть, например, базовый метод класса AbstractTwoWayPaginationPresenter updateDataList(true)
 *
 * @property filter - фильтр списка, к которому будет пременяться изменение строки поиска
 * @property onUpdateData - метод для обновления списка с учетом УЖЕ измененного [filter]
 */
class DefaultSearchDelegate constructor(
    private val filter: ListFilter?,
    private val onUpdateData: (withQuery: String) -> Unit
) {

    /**
     * Последняя строка, по которой применялся поиск
     */
    var lastSearchQuery = ""
        private set

    /**@SelfDocumented*/
    fun onSearchQueryChanged(searchQuery: String) {
        val trimmedText = searchQuery.trimStart().let {
            if (it.length >= MIN_CHAR_TO_SEARCH) it else ""
        }
        if (lastSearchQuery != trimmedText) {
            lastSearchQuery = trimmedText

            filter?.setSearchQuery(trimmedText)
            onUpdateData(trimmedText)
        }
    }

    companion object {
        /**@SelfDocumented*/
        const val MIN_CHAR_TO_SEARCH = 3
    }
}