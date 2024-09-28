package ru.tensor.sbis.info_decl.news

/**
 * Модель счетчика новостей
 */
data class NewsCounterModel(val news: Int, val humans: Int) {
    
    fun getUnreadCount(): Int = news

}