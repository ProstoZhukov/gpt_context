package ru.tensor.sbis.base_components.adapter.universal.swipe

/**
 * Базовая вью модель для элемента списка, содержащего свайп
 *
 * @author am.boldinov
 */
interface UniversalSwipeableVM {

    /**
     * Возвращает признак возможности удаления элемента
     */
    fun canRemove(): Boolean
}