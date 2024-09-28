package ru.tensor.sbis.info_decl.news.ui.config

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat

/**
 * Модель параметров заглушки
 *
 * @author am.boldinov
 */
class NewsEmptyViewParams @JvmOverloads constructor(
    @DrawableRes val icon: Int = ResourcesCompat.ID_NULL,
    @StringRes val title: Int = ResourcesCompat.ID_NULL,
    @StringRes val description: Int = ResourcesCompat.ID_NULL
)