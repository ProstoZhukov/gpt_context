package ru.tensor.sbis.design.buttons

import android.content.Context
import android.util.AttributeSet
import android.view.View
import ru.tensor.sbis.design.buttons.base.zentheme.ButtonZenThemeSupport
import ru.tensor.sbis.design.buttons.cut.SbisCutButtonDrawable
import ru.tensor.sbis.design.buttons.cut.SbisCutButtonMode
import ru.tensor.sbis.design.buttons.cut.SbisCutButtonType

/**
 * Реализация [SbisCutButtonDrawable] как самостоятельной view.
 *
 * @author ps.smirnyh
 */
class SbisCutButton private constructor(
    context: Context,
    attrs: AttributeSet? = null,
    private val cutDrawable: SbisCutButtonDrawable
) : View(context, attrs),
    ButtonZenThemeSupport by cutDrawable {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null
    ) : this(context, attrs, SbisCutButtonDrawable(context, attrs))

    /**
     * Режим отображения иконки.
     */
    var mode: SbisCutButtonMode
        get() = cutDrawable.mode
        set(value) {
            cutDrawable.mode = value
        }

    /**
     * Тип кнопки.
     */
    var type: SbisCutButtonType
        get() = cutDrawable.type
        set(value) {
            cutDrawable.type = value
            updateClickability()
        }

    init {
        background = cutDrawable
        updateClickability()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(background.minimumWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(background.minimumHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        cutDrawable.state = drawableState
        invalidate()
    }

    private fun updateClickability() {
        if (!isClickable && cutDrawable.type == SbisCutButtonType.ACTIVE) {
            isClickable = true
            isFocusable = true
        }
    }
}