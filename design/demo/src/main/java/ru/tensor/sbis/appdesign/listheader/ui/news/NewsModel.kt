package ru.tensor.sbis.appdesign.listheader.ui.news

import org.joda.time.LocalDateTime

/**
 * @author ra.petrov
 */
data class NewsModel(
    val title: String,
    val body: String,
    val date: LocalDateTime?,
)