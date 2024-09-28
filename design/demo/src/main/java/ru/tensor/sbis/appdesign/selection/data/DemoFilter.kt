package ru.tensor.sbis.appdesign.selection.data

import java.util.*

/**
 * @author ma.kolpakov
 */
data class DemoFilter(
    val query: String,
    val parentId: UUID?,
    val offset: Int,
    val itemsOnPage: Int
)