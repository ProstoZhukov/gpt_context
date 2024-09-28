package ru.tensor.sbis.date_picker

import ru.tensor.sbis.date_picker.items.CalendarGridItemVM
import ru.tensor.sbis.date_picker.items.NamedItemVM

/**
 * Базовый класс для основных элементов календарной сетки (год и месяц)
 *
 * @author mb.kruglova
 */
abstract class PeriodsVM(override val label: String, open val items: List<CalendarGridItemVM>) : NamedItemVM(label)