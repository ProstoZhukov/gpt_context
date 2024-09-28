package ru.tensor.sbis.design.buttons.base.models.style.providers

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.annotation.StyleableRes
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.buttons.R
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonResourceStyle
import ru.tensor.sbis.design.buttons.base.models.style.StyleConsumer
import ru.tensor.sbis.design.buttons.base.utils.style.loadTitleStyle

/**
 * Поставщик стиля текста и иконки в кнопке из темы.
 *
 * @author ma.kolpakov
 */
internal class ThemeStyleProvider(
    @AttrRes val textStyleAttr: Int,
    @StyleRes val textStyleRes: Int,
    @StyleableRes val textConsumerStyleable: IntArray
) : StyleProvider {

    constructor(buttonStyle: SbisButtonResourceStyle) : this(
        buttonStyle.buttonStyle,
        buttonStyle.defaultButtonStyle,
        R.styleable.SbisButton
    )

    override fun loadStyle(context: Context, consumer: StyleConsumer) {
        context.theme.applyStyle(R.style.SbisButtonBaseTheme, false)
        context.withStyledAttributes(null, textConsumerStyleable, textStyleAttr, textStyleRes) {
            loadTitleStyle(consumer)
        }
    }
}