package ru.tensor.sbis.design.buttons

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.widget.Checkable
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIcon
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.design.buttons.base.utils.style.loadFontIcon
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonType
import ru.tensor.sbis.design.buttons.toggle.round.SbisToggleRoundButtonApi
import ru.tensor.sbis.design.theme.global_variables.BackgroundColor

/**
 * Унаследован от [SbisRoundButton].
 * Реализован интерфейс [Checkable].
 *
 * @author mb.kruglova
 */
class SbisToggleRoundButton(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int
) : SbisRoundButton(context, attrs, defStyleAttr, defStyleRes), Checkable, SbisToggleRoundButtonApi {

    private var checked: Boolean = false

    private lateinit var iconStates: Pair<SbisButtonIcon, SbisButtonIcon>

    @ColorInt
    private var iconDisabledColor = Color.MAGENTA

    @ColorInt
    private var backgroundDisabledColor = Color.TRANSPARENT

    private val buttonStateSet = arrayOf(
        intArrayOf(-android.R.attr.state_enabled),
        intArrayOf(-android.R.attr.state_checked),
        intArrayOf(android.R.attr.state_checked)
    )

    constructor(
        context: Context,
        attrs: AttributeSet? = null
    ) : this(context, attrs, SbisButtonStyle.DEFAULT.roundButtonStyle, SbisButtonStyle.DEFAULT.defaultRoundButtonStyle)

    init {
        controller.button.isClickable = true

        val styledAttributes = R.styleable.SbisToggleRoundButton
        controller.button.context.withStyledAttributes(attrs, styledAttributes, defStyleAttr, defStyleRes) {
            controller.styleHolder.apply {
                backgroundDisabledColor = BackgroundColor.DEFAULT.getValue(controller.button.context)
                if (controller.type == SbisRoundButtonType.Transparent) {
                    transparentBackgroundColors = ColorStateList(
                        buttonStateSet,
                        intArrayOf(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT)
                    )

                    val defaultIconColor = iconTransparentColors.defaultColor
                    iconDisabledColor = getColor(
                        R.styleable.SbisRoundButton_SbisRoundButton_iconDisabledColor,
                        defaultIconColor
                    )
                    iconTransparentColors = ColorStateList(
                        buttonStateSet,
                        intArrayOf(
                            iconDisabledColor,
                            defaultIconColor,
                            getColor(
                                R.styleable.SbisToggleRoundButton_SbisRoundButton_iconColorSelected,
                                defaultIconColor
                            )
                        )
                    )
                } else {
                    val defaultBackgroundColor = backgroundColors.defaultColor
                    backgroundColors = ColorStateList(
                        buttonStateSet,
                        intArrayOf(
                            backgroundDisabledColor,
                            defaultBackgroundColor,
                            getColor(
                                R.styleable.SbisToggleRoundButton_SbisRoundButton_backgroundColorSelected,
                                defaultBackgroundColor
                            )
                        )
                    )

                    val defaultIconColor = iconColors.defaultColor
                    iconDisabledColor = getColor(
                        R.styleable.SbisRoundButton_SbisRoundButton_iconDisabledColor,
                        defaultIconColor
                    )
                    iconColors = ColorStateList(
                        buttonStateSet,
                        intArrayOf(
                            iconDisabledColor,
                            defaultIconColor,
                            getColor(
                                R.styleable.SbisToggleRoundButton_SbisRoundButton_iconColorSelected,
                                defaultIconColor
                            )
                        )
                    )
                }

                val iconSelected: SbisButtonIcon? = loadFontIcon(
                    R.styleable.SbisToggleRoundButton_SbisRoundButton_iconSelected,
                    R.styleable.SbisRoundButton_SbisRoundButton_iconSize
                )

                iconStates = icon to (iconSelected ?: icon)
            }
        }

        controller.updateBackgroundStyle()
        controller.updateIconStyle()
    }

    override var iconSelected: SbisButtonIcon? = null
        set(value) {
            if (field == value || value == null) return
            field = value

            iconStates = icon to value
        }

    @ColorInt
    override var iconColorSelected: Int? = null
        set(value) {
            if (field == value || value == null) return
            field = value

            with(controller.styleHolder) {
                if (controller.type == SbisRoundButtonType.Transparent) {
                    val defaultIconColor = iconTransparentColors.defaultColor
                    iconTransparentColors = ColorStateList(
                        buttonStateSet,
                        intArrayOf(
                            iconDisabledColor,
                            defaultIconColor,
                            value
                        )
                    )
                } else {
                    val defaultIconColor = iconColors.defaultColor
                    iconColors = ColorStateList(
                        buttonStateSet,
                        intArrayOf(
                            iconDisabledColor,
                            defaultIconColor,
                            value
                        )
                    )
                }
            }
        }

    @ColorInt
    override var backgroundColorSelected: Int? = null
        set(value) {
            if (field == value || value == null) return
            field = value

            with(controller.styleHolder) {
                if (controller.type != SbisRoundButtonType.Transparent) {
                    val defaultBackgroundColor = backgroundColors.defaultColor
                    backgroundColors = ColorStateList(
                        buttonStateSet,
                        intArrayOf(
                            backgroundDisabledColor,
                            defaultBackgroundColor,
                            value
                        )
                    )
                }
            }
        }

    /**
     * Создать checked состояние для кнопки.
     */
    override fun onCreateDrawableState(extraSpace: Int): IntArray? {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        mergeDrawableStates(
            drawableState,
            intArrayOf(if (isChecked) android.R.attr.state_checked else -android.R.attr.state_checked)
        )
        return drawableState
    }

    /**
     * Перехватить клик по кнопке.
     */
    override fun performClick(): Boolean {
        toggle()
        return super.performClick()
    }

    override fun setChecked(checked: Boolean) {
        if (this.checked == checked) return
        this.checked = checked
        icon = if (checked) iconStates.second else iconStates.first
        invalidate()
    }

    override fun isChecked(): Boolean {
        return this.checked
    }

    override fun toggle() {
        this.isChecked = !this.checked
    }
}