package ru.tensor.sbis.design.documentlink.utils

import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.documentlink.R

/**
 * Стили виджета документ-основание
 *
 * @author da.zolotarev
 */
sealed class DocumentLinkStyle(
    @AttrRes val styleAttr: Int,
    @StyleRes val styleRes: Int
) {
    object DocumentLinkDefaultStyle : DocumentLinkStyle(
        R.attr.documentLinkDefaultTheme,
        R.style.DocumentLinkDefaultTheme
    )
}
