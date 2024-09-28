package ru.tensor.sbis.info_decl.news.ui.config

import ru.tensor.sbis.info_decl.news.ui.config.newsfilter.NewsFilterConfiguration

/**
 * Кастомная конфигурация модуля новостей.
 *
 * @param newsListToolbarConfiguration конфигурация тулбара
 * @param newsListLayoutMode режим отображения ленты новостей (линейный, плитка)
 * @param emptyViewParamsFactory фабрика по созданию кастомных заглушек для реестра новостей
 * @param newsFilterConfiguration конфигурация фильтра новостей
 * @param attachmentOpenerConfiguration конфигурация всех заглушек для модуля новостей
 * @param supportBlockNews поддержка блокировки новостей (для случаев отсутствия фильтрации по удаленным
 * или отображения новостей на стене организации)
 *
 * @author am.boldinov
 */
class NewsModuleConfiguration(
    val newsListToolbarConfiguration: NewsListToolbarConfiguration,
    val newsListLayoutMode: NewsListLayoutMode = NewsListLayoutMode.Linear,
    val emptyViewParamsFactory: NewsListEmptyParamsFactory? = null,
    val newsFilterConfiguration: NewsFilterConfiguration? = null,
    val attachmentOpenerConfiguration: SocnetAttachmentOpenerConfiguration? = null,
    val supportBlockNews: Boolean = false
)