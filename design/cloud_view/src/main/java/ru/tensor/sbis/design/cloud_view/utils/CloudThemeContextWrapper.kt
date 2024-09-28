package ru.tensor.sbis.design.cloud_view.utils

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.cloud_view.R
import ru.tensor.sbis.design.utils.ThemeContextBuilder

/**
 * Обертка над контекстом ячейки-облака для темизации входящих и исходящий сообщений.
 *
 * @author vv.chekurda
 */
class CloudThemeContextWrapper(
    private val context: Context,
    private val attrs: AttributeSet? = null,
    @AttrRes private val defStyleAttr: Int = R.attr.incomeCloudViewTheme,
    @StyleRes private val defaultStyle: Int = R.style.DefaultCloudViewTheme_Income
) {

    fun build(): Context {
        var outcome = false
        context.withStyledAttributes(attrs, R.styleable.CloudView, defStyleAttr, defaultStyle) {
            outcome = getBoolean(R.styleable.CloudView_CloudView_outcome, false)
        }
        val styleAttr = if (outcome) R.attr.outcomeCloudViewTheme else R.attr.incomeCloudViewTheme
        val defaultTheme = if (outcome) R.style.DefaultCloudViewTheme_Outcome else R.style.DefaultCloudViewTheme_Income
        return ThemeContextBuilder(context, attrs, styleAttr, defaultTheme).build()
    }
}