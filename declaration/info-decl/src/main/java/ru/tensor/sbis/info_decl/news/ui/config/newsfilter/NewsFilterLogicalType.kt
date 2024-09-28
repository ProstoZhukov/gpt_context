package ru.tensor.sbis.info_decl.news.ui.config.newsfilter

/**
 * Существующие типы фильтрации каналов соцсети
 *
 * @author s.r.golovkin
 */
enum class NewsFilterLogicalType {

    /** Все */
    ALL,
    /** Только непрочитанные */
    UNREAD_ONLY,
    /** Только избранные */
    FAVORITED_ONLY,
    /** Только удаленные */
    DELETED_ONLY,
    /** Только люди */
    HUMAN_ONLY,
    /** По определенной группе */
    BY_CHANNEL;
}