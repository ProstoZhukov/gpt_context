package ru.tensor.sbis.info_decl.news.ui.config.newsfilter

import androidx.annotation.StringRes
import java.io.Serializable

/**
 * Класс для передачи дополнительных параметров в экран фильтра каналов
 * @property [titleRes] Строковый ресурс заголовка главного окна фильтра
 * @property [emptyParamsFactory] Фабрика заглушек для фильтра
 * @property [iconProvider] поставщик иконок для логических фильтров
 * @property [searchInTop] флаг о необходимости показа строки поиска в заголовке списка
 *
 * @author s.r.golovkin
 */
class NewsFilterConfiguration(
    @StringRes val titleRes: Int,
    val emptyParamsFactory: NewsFilterEmptyParamsFactory? = null,
    val iconProvider: NewsLogicalFilterIconProvider? = null,
    val searchInTop: Boolean = false
): Serializable