package ru.tensor.sbis.date_picker

import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.*

/**
 * @author mb.kruglova
 */
interface MonthMarkersRepository : Feature {
    fun getMarkedMonths(): List<Calendar>
}