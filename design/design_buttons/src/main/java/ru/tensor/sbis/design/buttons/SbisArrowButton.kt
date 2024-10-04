package ru.tensor.sbis.design.buttons

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.buttons.arrow.model.ArrowButtonStyleSet
import ru.tensor.sbis.design.buttons.arrow.api.SbisArrowButtonApi
import ru.tensor.sbis.design.buttons.arrow.model.SbisArrowButtonBackgroundType
import ru.tensor.sbis.design.buttons.arrow.model.SbisArrowButtonStyle
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonTextIcon
import ru.tensor.sbis.design.buttons.base.models.style.PaleButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonCustomStyle
import ru.tensor.sbis.design.buttons.base.utils.style.loadEnum
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonSize
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonType
import ru.tensor.sbis.design.theme.HorizontalPosition

/**
 * Кнопка-перелистывания.
 *
 * Унаследован от [SbisRoundButton]
 *
 * @author mb.kruglova
 */
class SbisArrowButton private constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int
) : SbisRoundButton(context, attrs, defStyleAttr, defStyleRes), SbisArrowButtonApi {

    constructor(
        context: Context,
        attrs: AttributeSet? = null
    ) : this(context, attrs, PaleButtonStyle.roundButtonStyle, PaleButtonStyle.defaultRoundButtonStyle)

    override var mode: HorizontalPosition = HorizontalPosition.LEFT
        set(value) {
            if (field == value) return
            field = value
            updateIcon()
        }

    override var arrowButtonBackgroundType: SbisArrowButtonBackgroundType = SbisArrowButtonBackgroundType.FILLED
        set(value) {
            if (field == value) return
            field = value
            updateArrowButtonStyle()
        }

    override var arrowButtonStyle: SbisArrowButtonStyle = SbisArrowButtonStyle.PALE
        set(value) {
            if (field == value) return
            field = value
            updateArrowButtonStyle()
        }

    init {
        type = SbisRoundButtonType.Filled
        val styledAttributes = R.styleable.SbisArrowButton
        controller.button.context.withStyledAttributes(attrs, styledAttributes, defStyleAttr, defStyleRes) {
            arrowButtonStyle = loadArrowButtonStyle()

            mode = loadEnum(
                R.styleable.SbisArrowButton_SbisArrowButton_mode,
                HorizontalPosition.LEFT,
                *HorizontalPosition.values()
            )

            arrowButtonBackgroundType = loadBackgroundType()

            size = loadSize()

            updateArrowButtonStyle()
        }
    }

    private fun updateArrowButtonStyle() {
        style = ArrowButtonStyleSet.getStyleSet(arrowButtonBackgroundType, arrowButtonStyle).getButtonStyle(context)
        updateIcon()
    }

    private fun updateIcon() {
        icon = SbisButtonTextIcon(
            char = if (mode == HorizontalPosition.LEFT) {
                SbisMobileIcon.Icon.smi_ArrowNarrowLeft.character
            } else {
                SbisMobileIcon.Icon.smi_ArrowNarrowRight.character
            },
            style = (style as? SbisButtonCustomStyle)?.iconStyle
        )
        setIconChar(
            if (mode == HorizontalPosition.LEFT) {
                SbisMobileIcon.Icon.smi_ArrowNarrowLeft.character
            } else {
                SbisMobileIcon.Icon.smi_ArrowNarrowRight.character
            }
        )
    }

    private fun TypedArray.loadBackgroundType(): SbisArrowButtonBackgroundType {
        return when (
            val typeCode =
                if (getType(R.styleable.SbisArrowButton_SbisArrowButton_type) == TypedValue.TYPE_ATTRIBUTE) {
                    0
                } else {
                    getInteger(R.styleable.SbisArrowButton_SbisArrowButton_type, 0)
                }
        ) {
            0, 1 -> SbisArrowButtonBackgroundType.FILLED
            2 -> SbisArrowButtonBackgroundType.FILLED_ON_TAP
            else -> error("Unexpected type $typeCode")
        }
    }

    private fun TypedArray.loadSize(): SbisRoundButtonSize {
        return when (
            val sizeCode =
                if (getType(R.styleable.SbisArrowButton_SbisArrowButton_size) == TypedValue.TYPE_ATTRIBUTE) {
                    0
                } else {
                    getInteger(R.styleable.SbisArrowButton_SbisArrowButton_size, 0)
                }
        ) {
            0, 1 -> SbisRoundButtonSize.S
            2 -> SbisRoundButtonSize.M
            3 -> SbisRoundButtonSize.L
            else -> error("Unexpected type $sizeCode")
        }
    }

    private fun TypedArray.loadArrowButtonStyle(): SbisArrowButtonStyle {
        return when (
            val styleCode =
                if (getType(R.styleable.SbisArrowButton_SbisArrowButton_style) == TypedValue.TYPE_ATTRIBUTE) {
                    0
                } else {
                    getInteger(R.styleable.SbisArrowButton_SbisArrowButton_style, 0)
                }
        ) {
            0, 1 -> SbisArrowButtonStyle.PALE
            2 -> SbisArrowButtonStyle.DEFAULT
            else -> error("Unexpected type $styleCode")
        }
    }
}