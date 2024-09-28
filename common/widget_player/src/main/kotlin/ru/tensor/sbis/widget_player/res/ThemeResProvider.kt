package ru.tensor.sbis.widget_player.res

import android.content.Context

/**
 * @author am.boldinov
 */
internal fun interface ThemeResProvider<T> : (Context) -> T {

    companion object {

        inline fun <reified T> cached(crossinline creator: (Context) -> T): ThemeResProvider<T> {
            var value: T? = null
            var oldThemeHashCode: Int? = null
            return ThemeResProvider { context ->
                val themeHashCode = context.theme.hashCode()
                value?.takeIf {
                    oldThemeHashCode == themeHashCode
                } ?: creator.invoke(context).also {
                    value = it
                    oldThemeHashCode = themeHashCode
                }
            }
        }
    }
}