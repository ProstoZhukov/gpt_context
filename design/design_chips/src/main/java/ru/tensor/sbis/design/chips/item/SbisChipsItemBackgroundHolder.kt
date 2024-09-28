package ru.tensor.sbis.design.chips.item

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import ru.tensor.sbis.design.chips.models.SbisChipsBackgroundStyle
import ru.tensor.sbis.design.chips.models.SbisChipsViewMode
import ru.tensor.sbis.design.theme.global_variables.BackgroundColor
import ru.tensor.sbis.design.theme.global_variables.BorderColor
import ru.tensor.sbis.design.theme.global_variables.BorderThickness

/**
 * Класс для управления фоном элемента.
 *
 * @author ps.smirnyh
 */
internal class SbisChipsItemBackgroundHolder(
    private val context: Context,
    private val styleHolder: SbisChipsItemStyleHolder,
    style: SbisChipsBackgroundStyle
) {
    private val readOnlyBackgroundColor = BackgroundColor.READ_ONLY.getValue(context)
    private val readOnlyBorderColor = BorderColor.READ_ONLY.getValue(context)
    private val readOnlyBorderThickness = BorderThickness.S.getDimenPx(context)

    private val mainDrawable = GradientDrawable().apply {
        setColor(style.getBackgroundColor(context))
    }
    private val selectedDrawable = GradientDrawable().apply {
        setColor(style.getBackgroundColorSelected(context))
    }
    private val pressedDrawable = GradientDrawable().apply {
        setColor(styleHolder.clickedColor)
    }
    private val readOnlyDrawable = GradientDrawable().apply {
        setColor(readOnlyBackgroundColor)
    }

    /**
     * Изменить фон в зависимости от стиля.
     */
    fun changeSelectedStyle(style: SbisChipsBackgroundStyle) {
        mainDrawable.setColor(style.getBackgroundColor(context))
        selectedDrawable.setColor(style.getBackgroundColorSelected(context))
    }

    /**
     * Изменить фон элемента при изменении выбора.
     */
    fun changeSelected(isSelected: Boolean) {
        if (isSelected) {
            readOnlyDrawable.setColor(readOnlyBackgroundColor)
            readOnlyDrawable.setStroke(0, 0)
        } else {
            readOnlyDrawable.color = null
            readOnlyDrawable.setStroke(readOnlyBorderThickness, readOnlyBorderColor)
        }
    }

    /**
     * Пересоздать фон.
     */
    fun changeBackground(isCanSelected: Boolean, isCanPressed: Boolean): Drawable =
        StateListDrawable().apply {
            addState(intArrayOf(-android.R.attr.state_enabled), readOnlyDrawable)
            if (isCanSelected) {
                addState(
                    intArrayOf(android.R.attr.state_selected),
                    selectedDrawable
                )
            }
            if (isCanPressed) {
                addState(intArrayOf(android.R.attr.state_pressed), pressedDrawable)
            }
            addState(
                intArrayOf(),
                mainDrawable
            )
        }

    /**
     * Изменить размер.
     */
    fun changeSize(height: Int) {
        val borderRadius = height / 2f
        mainDrawable.cornerRadius = borderRadius
        selectedDrawable.cornerRadius = borderRadius
        pressedDrawable.cornerRadius = borderRadius
        readOnlyDrawable.cornerRadius = borderRadius
    }

    /**
     * Изменить режим отображения фона.
     */
    fun changeViewMode(viewMode: SbisChipsViewMode, style: SbisChipsBackgroundStyle) {
        mainDrawable.color =
            if (viewMode == SbisChipsViewMode.FILLED) {
                ColorStateList.valueOf(style.getBackgroundColor(context))
            } else {
                null
            }
    }
}