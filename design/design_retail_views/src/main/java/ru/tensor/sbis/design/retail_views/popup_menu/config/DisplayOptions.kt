package ru.tensor.sbis.design.retail_views.popup_menu.config

import androidx.annotation.DimenRes
import ru.tensor.sbis.design.retail_views.R

/** Размеры окна с меню. */
data class DisplayOptions(
    @DimenRes val width: Int = R.dimen.retail_views_context_menu_width
)