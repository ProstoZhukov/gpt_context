package ru.tensor.sbis.design.buttons

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.widget.Checkable
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIcon
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.design.buttons.base.utils.style.CHECKED_STATE_SET
import ru.tensor.sbis.design.buttons.base.utils.style.loadFontIcon
import ru.tensor.sbis.design.buttons.button.models.SbisButtonModel
import ru.tensor.sbis.design.buttons.toggle.link.SbisToggleLinkButtonApi

/**
 * Унаследован от [SbisLinkButton].
 * Реализован интерфейс [Checkable].
 *
 * @author mb.kruglova
 */
class SbisToggleLinkButton(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int
) :
    SbisLinkButton(context, attrs, defStyleAttr, defStyleRes),
    Checkable,
    SbisToggleLinkButtonApi {

    private var checked: Boolean = false

    private lateinit var modelStates: Pair<SbisButtonModel, SbisButtonModel>

    constructor(
        context: Context,
        attrs: AttributeSet? = null
    ) : this(context, attrs, SbisButtonStyle.DEFAULT.linkButtonStyle, SbisButtonStyle.DEFAULT.defaultLinkButtonStyle)

    init {
        controller.button.isClickable = true

        val styledAttributes = R.styleable.SbisToggleLinkButton
        controller.button.context.withStyledAttributes(attrs, styledAttributes, defStyleAttr, defStyleRes) {
            controller.styleHolder.apply {
                val defaultTitleColor = titleColors.defaultColor
                setTitleColors(
                    defaultTitleColor,
                    getColor(
                        R.styleable.SbisToggleLinkButton_SbisLinkButton_titleColorSelected,
                        defaultTitleColor
                    )
                )

                val iconSelected: SbisButtonIcon? = loadFontIcon(
                    R.styleable.SbisToggleLinkButton_SbisLinkButton_iconSelected,
                    R.styleable.SbisLinkButton_SbisLinkButton_iconSize
                )

                val titleSelected = model.title?.copy(
                    text = getString(R.styleable.SbisToggleLinkButton_SbisLinkButton_titleSelected)
                )

                val selectedModel = model.copy(icon = iconSelected, title = titleSelected)

                modelStates = model to selectedModel
            }
        }

        controller.updateTitleStyle()
    }

    override var titleSelected: String? = null
        set(value) {
            if (field == value || value == null) return
            field = value

            val titleSelected = model.title?.copy(text = value)
            val selectedModel = modelStates.second.copy(title = titleSelected)
            modelStates = model to selectedModel
        }

    override var iconSelected: SbisButtonIcon? = null
        set(value) {
            if (field == value || value == null) return
            field = value

            val selectedModel = modelStates.second.copy(icon = value)
            modelStates = model to selectedModel
        }

    @ColorInt
    override var titleColorSelected: Int? = null
        set(value) {
            if (field == value || value == null) return
            field = value

            setTitleColors(controller.styleHolder.titleColors.defaultColor, value)
            controller.updateTitleStyle()
        }

    /**
     * Создать checked состояния для кнопки.
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
        model = if (checked) modelStates.second else modelStates.first
    }

    override fun isChecked(): Boolean {
        return this.checked
    }

    override fun toggle() {
        this.isChecked = !this.checked
    }

    private fun setTitleColors(defaultColor: Int, selectedColor: Int) =
        with(controller.styleHolder) {
            titleColors = ColorStateList(
                CHECKED_STATE_SET,
                intArrayOf(
                    defaultColor,
                    selectedColor
                )
            )
        }
}