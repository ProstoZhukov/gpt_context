package ru.tensor.sbis.design.navigation.view.model

/**
 * Класс всех счётчиков для Аккордеона и ННП.
 * @author mb.kruglova
 */
data class NavigationCounters(
    /** Идентификатор счётчика. */
    var name: String,
    /** Непрочитанное число счётчика. */
    var unreadCounter: Int,
    /** Непросмотренное число счётчика. */
    var unviewedCounter: Int,
    /** Общее число счётчика. */
    var totalCounter: Int
)