package ru.tensor.sbis.design.buttons.group.api

import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.RectF
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.buttons.R
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.buttons.SbisButtonGroup
import ru.tensor.sbis.design.buttons.base.models.state.SbisButtonState
import ru.tensor.sbis.design.buttons.base.models.state.SbisButtonState.DISABLED
import ru.tensor.sbis.design.buttons.base.models.state.SbisButtonState.ENABLED
import ru.tensor.sbis.design.buttons.base.models.state.SbisButtonState.IN_PROGRESS
import ru.tensor.sbis.design.buttons.base.utils.style.loadColorStateList
import ru.tensor.sbis.design.buttons.base.utils.style.loadEnum
import ru.tensor.sbis.design.buttons.base.utils.style.loadState
import ru.tensor.sbis.design.buttons.group.models.SbisButtonGroupSize
import ru.tensor.sbis.design.buttons.group.utils.ButtonGroupStyleHolder
import ru.tensor.sbis.design.buttons.group.utils.OvalOutlineProvider
import ru.tensor.sbis.design.theme.models.AbstractHeight

/**
 * Контроллер для управления состоянием и внешним видом группы кнопок [SbisButtonGroup].
 *
 * @author ma.kolpakov
 */
internal class SbisButtonGroupController : SbisButtonGroupApi {

    private val styleHolder = ButtonGroupStyleHolder()

    private val backgroundOutlineProvider = OvalOutlineProvider()

    private lateinit var group: ViewGroup

    override var state = ENABLED
        set(value) {
            field = value
            updateChildState(group, field)
        }

    override var size = SbisButtonGroupSize.M
        set(value) {
            field = value
            group.minimumHeight = field.buttonSize.globalVar.getDimenPx(group.context)
            group.background = createBackground(group, styleHolder)
            updateChildSize(group, field)
        }

    override val inlineHeight = size

    override fun setInlineHeight(height: AbstractHeight) {
        size = SbisButtonGroupSize.values()
            .lastOrNull { it.buttonSize.globalVar.getDimen(group.context) <= height.getDimen(group.context) } ?: size
    }

    // TODO: 10/22/2021 выглядит бесполезным https://online.sbis.ru/opendoc.html?guid=95db5ec5-5ecd-46f4-aadd-7704bcdf8768
    override var buttons: List<SbisButton> = emptyList()

    fun attach(
        groupView: ViewGroup,
        attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int,
        @StyleRes defStyleRes: Int
    ) =
        with(groupView) {
            group = groupView

            context.theme.applyStyle(R.style.SbisButtonGroupDefaultPaleTheme, false)

            context.withStyledAttributes(attrs, R.styleable.SbisButtonGroup, defStyleAttr, defStyleRes) {
                loadStyle(this)
                state = loadState(R.styleable.SbisButtonGroup_SbisButtonGroup_state, ENABLED)
                size = loadEnum(
                    R.styleable.SbisButtonGroup_SbisButtonGroup_size,
                    size,
                    *SbisButtonGroupSize.values()
                )
            }

            background = createBackground(group, styleHolder)
            outlineProvider = backgroundOutlineProvider
        }

    fun onChildAdded(child: View, isPrimary: Boolean) {
        if (child is SbisButton) {
            child.setNested(size.buttonSize, isPrimary, styleHolder.secondaryButtonProgressColor)
            updateChildState(child, state)
        } else {
            updateChildState(child, state)
        }
    }

    internal fun updateVisibilityBackground(visible: Boolean) = with(group) {
        val newAlpha = if (visible) BACKGROUND_ALPHA_VISIBLE else BACKGROUND_ALPHA_INVISIBLE
        if (background.alpha != newAlpha) background.alpha = newAlpha
    }

    private fun updateChildState(group: ViewGroup, state: SbisButtonState) {
        repeat(group.childCount) { childIndex ->
            updateChildState(group.getChildAt(childIndex), state)
        }
    }

    private fun updateChildState(child: SbisButton, state: SbisButtonState) {
        when (child.state) {
            ENABLED, DISABLED -> {
                child.state = state
            }
            IN_PROGRESS -> {
                // кнопки в состоянии прогресса менять не нужно. Это прикладная логика
            }
        }
    }

    private fun updateChildState(child: View, state: SbisButtonState) {
        child.isEnabled = state == ENABLED
    }

    private fun updateChildSize(group: ViewGroup, groupSize: SbisButtonGroupSize) {
        repeat(group.childCount) { childIndex ->
            (group.getChildAt(childIndex) as? SbisButton)?.apply {
                size = groupSize.buttonSize
            }
        }
    }

    /**
     * Загрузить стиль атрибутов.
     */
    private fun loadStyle(array: TypedArray) = with(array) {
        styleHolder.apply {
            backgroundColors = loadColorStateList(
                R.styleable.SbisButtonGroup_SbisButtonGroup_backgroundColor,
                R.styleable.SbisButtonGroup_SbisButtonGroup_backgroundColorPressed,
                R.styleable.SbisButtonGroup_SbisButtonGroup_backgroundColorDisabled
            )
            borderColors = loadColorStateList(
                R.styleable.SbisButtonGroup_SbisButtonGroup_borderColor,
                R.styleable.SbisButtonGroup_SbisButtonGroup_borderColorPressed,
                R.styleable.SbisButtonGroup_SbisButtonGroup_borderColorDisabled
            )
            borderWidth = getDimension(R.styleable.SbisButtonGroup_SbisButtonGroup_borderWidth, 0F)
            secondaryButtonProgressColor =
                getColor(R.styleable.SbisButtonGroup_SbisButtonGroup_secondaryButtonProgressColor, Color.BLACK)
        }
    }

    private fun createBackground(group: View, styleHolder: ButtonGroupStyleHolder) =
        with(styleHolder) {
            val radius = group.minimumHeight / 2F
            val radii = FloatArray(8) { radius }
            val background = ShapeDrawable(RoundRectShape(radii, null, null))
            background.setTintList(backgroundColors)
            if (borderWidth == 0F) {
                background
            } else {
                val borderInset = RectF(borderWidth, borderWidth, borderWidth, borderWidth)
                val border = ShapeDrawable(RoundRectShape(radii, borderInset, radii))
                border.setTintList(borderColors)
                LayerDrawable(arrayOf(background, border))
            }
        }

    companion object {
        /**
         * К кнопке применяются стандартные правила стиля в группе.
         */
        const val SBIS_BUTTON_VIEW_GROUP_STYLE_STANDARD = 1

        /**
         * Кнопка сохраняет собственный стиль, считается "главной" в группе.
         */
        const val SBIS_BUTTON_VIEW_GROUP_STYLE_MAIN = 2

        private const val BACKGROUND_ALPHA_INVISIBLE = 0
        private const val BACKGROUND_ALPHA_VISIBLE = 255
    }
}
