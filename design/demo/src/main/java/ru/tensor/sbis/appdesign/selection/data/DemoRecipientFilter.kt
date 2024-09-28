package ru.tensor.sbis.appdesign.selection.data

/**
 * @author ma.kolpakov
 */
data class DemoRecipientFilter(
    val query: String,
    val offset: Int,
    val itemsOnPage: Int
)