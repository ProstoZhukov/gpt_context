package ru.tensor.sbis.communication_decl.selection

/**
 * Режим работы шапки компонента выбора.
 *
 * @author vv.chekurda
 */
enum class SelectionHeaderMode {

    /**
     * Шапка отображается (стандарт).
     */
    VISIBLE,

    /**
     * Шапка полностью скрыта.
     */
    GONE,

    /**
     * Шапка полностью скрыта, дополнительно над списком отображается строка поиска.
     */
    ADDITIONAL_SEARCH
}