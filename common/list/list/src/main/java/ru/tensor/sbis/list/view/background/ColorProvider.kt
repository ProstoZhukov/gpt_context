package ru.tensor.sbis.list.view.background

import android.content.Context
import android.content.res.ColorStateList
import android.util.TypedValue
import android.view.ContextThemeWrapper
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import ru.tensor.sbis.design.theme.global_variables.SeparatorColor
import ru.tensor.sbis.list.R
import ru.tensor.sbis.design.R as RDesign

/**
 * Поставщик цвета из ресурсов.
 *
 * @property context контекст для извлечения ресурсов.
 */
class ColorProvider(private val context: Context) {

    /**
     * Ресурс цвета фона элемента списка.
     */
    val itemColorStateList get() = getDrawableFromTheme2(R.attr.list_item_background_color_states)

    /**
     * Белый цвет.
     */
    @get:ColorInt
    val contentBackground: Int by lazy {
        getColorFromTheme(
            R.attr.list_content_background,
            RDesign.color.palette_color_white1
        )
    }

    /**
     * Темный цвет.
     */
    @get:ColorInt
    val contentDarkBackground: Int by lazy {
        getColorFromTheme(
            R.attr.list_content_dark_background,
            RDesign.color.palette_color_gray3
        )
    }

    @get:ColorInt
    val separatorColor: Int by lazy {
        SeparatorColor.DEFAULT.getValue(context)
    }

    private fun getColorFromTheme(listContentBackground: Int, defaultResId: Int): Int {
        val typedValue = TypedValue()
        ContextThemeWrapper(context, getThemeRes()).theme.resolveAttribute(
            listContentBackground,
            typedValue,
            true
        )
        return if (typedValue.data == 0) ContextCompat.getColor(context, defaultResId)
        else typedValue.data
    }

    private fun getDrawableFromTheme2(listContentBackground: Int): ColorStateList {
        val typedValue = TypedValue()
        ContextThemeWrapper(context, getThemeRes()).theme.resolveAttribute(
            listContentBackground,
            typedValue,
            true
        )
        return ContextCompat.getColorStateList(context, typedValue.resourceId)!!
    }

    @StyleRes
    private fun getThemeRes(): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.list_theme, typedValue, true)

        return if (typedValue.data == 0) R.style.List else typedValue.data
    }
}