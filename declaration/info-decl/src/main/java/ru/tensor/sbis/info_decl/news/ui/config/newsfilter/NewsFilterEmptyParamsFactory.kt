package ru.tensor.sbis.info_decl.news.ui.config.newsfilter

import ru.tensor.sbis.info_decl.news.ui.config.NewsEmptyViewParams
import java.io.Serializable

/**
 * Фабрика по созданию параметров для отображения/переопределения стандартных заглушек фильтра новостей
 *
 * @author s.r.golovkin
 */
interface NewsFilterEmptyParamsFactory: Serializable {

    /**
     * Создает параметры для отображения заглушки
     *
     * @param mode режим отображения заглушки
     */
    fun create(mode: NewsFilterEmptyViewMode): NewsEmptyViewParams?
}