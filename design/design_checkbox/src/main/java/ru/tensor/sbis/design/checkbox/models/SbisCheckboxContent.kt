package ru.tensor.sbis.design.checkbox.models

import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt

/**
 * Описание контента, который является меткой в чекбоксе
 *
 * @author mb.kruglova
 */
sealed class SbisCheckboxContent {
    /**
     * Чекбокс без метки
     */
    object NoContent : SbisCheckboxContent()

    /**
     * Чекбокс с текстовой меткой
     */
    data class TextContent(
        val text: String,
        @ColorInt val color: Int? = null,
        var isMaxLines: Boolean? = null
    ) : SbisCheckboxContent()

    /**
     * Чекбокс с меткой-иконкой
     */
    @Suppress("DataClassPrivateConstructor")
    data class IconContent private constructor(
        val drawable: Drawable?,
        val iconText: CharSequence?,
        @ColorInt val color: Int? = null
    ) : SbisCheckboxContent() {

        constructor(drawable: Drawable, color: Int? = null) : this(drawable, null, color)

        constructor(iconText: CharSequence, color: Int? = null) : this(null, iconText, color)
    }
}