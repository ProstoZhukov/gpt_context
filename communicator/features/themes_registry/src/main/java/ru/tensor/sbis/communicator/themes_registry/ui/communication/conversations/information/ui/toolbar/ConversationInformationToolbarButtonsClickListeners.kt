package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.toolbar

/**
 * Интерфейс слушателей кликов по кнопкам в правой части тулбара.
 *
 * @author dv.baranov
 */
internal interface ConversationInformationToolbarButtonsClickListeners {

    /** Обработать клик на кнопку поиска. */
    fun onSearchButtonClick()

    /** Обработать клик на кнопку троеточия. */
    fun onMoreButtonClick()

    /** Обработать клик на кнопку фильтра. */
    fun onFilterButtonClick()

    /** Обработать клик на кнопку применения изменений из поля ввода. */
    fun onDoneButtonClick()
}