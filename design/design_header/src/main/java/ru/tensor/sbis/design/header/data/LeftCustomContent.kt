package ru.tensor.sbis.design.header.data

import android.content.Context
import android.view.View
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.theme.global_variables.StyleColor

/**
 * Настройки контента слева от заголовка в шапке.
 *
 * @author ma.kolpakov
 */
sealed class LeftCustomContent {
    /**
     * Прикладной контент.
     */
    class Content(private val creator: (Context) -> View) : LeftCustomContent() {
        fun getView(context: Context) = creator(context)
    }

    /**
     * Кнопка со стрелкой назад.
     */
    class BackArrowContent(private val clickListener: () -> Unit) : LeftCustomContent() {
        fun getView(context: Context): View {
            val offset = Offset.S.getDimenPx(context)
            return SbisTextView(context).apply {
                setTextColor(StyleColor.PRIMARY.getIconColor(context))

                typeface = TypefaceManager.getSbisMobileIconTypeface(context)
                setText(R.string.design_mobile_icon_arrow_back)

                setPadding(offset, 0, offset, 0)

                setOnClickListener {
                    clickListener()
                }
            }
        }
    }

    /**
     * Нет контента слева от заголовка
     */
    object NoneContent : LeftCustomContent()

}
