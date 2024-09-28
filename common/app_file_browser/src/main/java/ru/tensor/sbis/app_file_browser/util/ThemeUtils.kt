package ru.tensor.sbis.app_file_browser.util

import android.content.Context
import android.view.ContextThemeWrapper
import ru.tensor.sbis.app_file_browser.R
import ru.tensor.sbis.design.utils.getDataFromAttrOrNull

/**
 * Возвращает [ContextThemeWrapper] с темой из атрибута [R.attr.appFileBrowserTheme], если она задана.
 */
internal fun getThemedContext(context: Context): Context {
    val theme = context.getDataFromAttrOrNull(R.attr.appFileBrowserTheme)
        ?: tryGetFileBrowserThemeFromAppTheme(context)
    return theme?.let { ContextThemeWrapper(context, it) }
        ?: context
}

private fun tryGetFileBrowserThemeFromAppTheme(context: Context): Int? =
    ContextThemeWrapper(context.applicationContext, context.applicationInfo.theme)
        .getDataFromAttrOrNull(R.attr.appFileBrowserTheme)