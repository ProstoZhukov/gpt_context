package ru.tensor.sbis.appdesign.selection.data

import java.util.*

/**
 * @author ma.kolpakov
 */
data class DemoServiceData(
    val id: UUID,
    val title: String,
    val subtitle: String?,
    val counter: Int,
    val hasNested: Boolean
)