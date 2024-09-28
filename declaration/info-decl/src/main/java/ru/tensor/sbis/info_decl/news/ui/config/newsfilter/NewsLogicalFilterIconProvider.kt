package ru.tensor.sbis.info_decl.news.ui.config.newsfilter

import java.io.Serializable

/**
 * Поставщик иконок для логических фильтров.
 * Реализует [Serializable], инъекции в конструктор несериализуемых объектов запрещены
 *
 * @author s.r.golovkin
 */
interface NewsLogicalFilterIconProvider: Serializable {

    /**
     * Предоставляет символ в шрифте SbisMobileIcons на иконку для указанного [filterType]
     * или null, если для указанного [filterType] иконка не предусмотрена
     * @see [NewsFilterLogicalType]
     */
    fun provideIconForFilterType(filterType: NewsFilterLogicalType): Char?
}