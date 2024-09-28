package ru.tensor.sbis.design.radio_group.control.layout

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.view.ViewGroup
import androidx.core.view.forEach
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.radio_group.control.RadioGroupStyleHolder
import ru.tensor.sbis.design.radio_group.control.api.SbisRadioGroupTitlePosition
import ru.tensor.sbis.design.radio_group.control.api.SbisRadioGroupViewApi
import ru.tensor.sbis.design.radio_group.control.models.SbisRadioGroupValidationStatus

/**
 * Layout для размещения элементов в зависимости от выбранной [strategy].
 * Также имеет поддержки отрисовки валидации при [validationStatus] равной [SbisRadioGroupValidationStatus.INVALID].
 *
 * @author ps.smirnyh
 */
@SuppressLint("ViewConstructor")
internal class RadioGroupLayoutView(
    context: Context,
    private val styleHolder: RadioGroupStyleHolder,
    private val validationDrawer: RadioGroupValidationDrawer = RadioGroupValidationDrawer(styleHolder)
) : ViewGroup(context) {

    /** Выбранная стратегия размещения элементов. */
    internal var strategy: RadioGroupLayoutStrategy = RadioGroupLayoutVerticalStrategy(styleHolder::hierarchyPadding)
        set(value) {
            field = value
            safeRequestLayout()
        }

    /** @see SbisRadioGroupViewApi.validationStatus */
    internal var validationStatus: SbisRadioGroupValidationStatus = SbisRadioGroupValidationStatus.VALID
        set(value) {
            field = value
            validationDrawer.validationStatus = value
            safeRequestLayout()
        }

    internal var titlePosition: SbisRadioGroupTitlePosition = SbisRadioGroupTitlePosition.RIGHT

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        forEach { it.isEnabled = enabled }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var (width, height) = strategy.measure(this, widthMeasureSpec, heightMeasureSpec)
        width += validationDrawer.getValidationOffset()
        validationDrawer.updateValidationRectSize(width.toFloat(), height.toFloat())
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val x = if (titlePosition == SbisRadioGroupTitlePosition.RIGHT) {
            0
        } else {
            validationDrawer.getValidationOffset()
        }
        strategy.layout(this, x, 0)
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        validationDrawer.draw(canvas)
    }
}