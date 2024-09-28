package ru.tensor.sbis.date_picker.items

/**
 * Класс-агрегатор "особенных" дат, которые необходимо пометить в календаре
 *
 * @author mb.kruglova
 */
class SpecialDates {
    var holidays: Set<Day> = emptySet()
    var unavailableDays: Set<Day> = emptySet()
    var fixedMonths: Set<Month> = emptySet()
    var counters: List<Day> = emptyList()
}