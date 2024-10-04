package ru.tensor.sbis.design.topNavigation.internal_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.widget.FrameLayout
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.global_variables.InlineHeight
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr

/**
 * @SelfDocumented
 *
 * @author da.zolotarev
 */
internal class FrameLayoutWithBadge(
    context: Context
) : FrameLayout(context) {

    /** @SelfDocumented */
    var isNeedBadge = true

    private val paint = Paint().apply {
        color = context.getColorFromAttr(R.attr.translucentBackgroundColorLight)
    }

    private val paintActive = Paint().apply {
        color = context.getColorFromAttr(R.attr.translucentActiveBackgroundColorLight)
    }

    private val circleRadius = InlineHeight.X2S.getDimen(context) / 2

    override fun dispatchDraw(canvas: Canvas) {
        if (isNeedBadge) {
            canvas?.drawCircle(width / 2f, height / 2f, circleRadius, if (isPressed) paintActive else paint)
        }
        super.dispatchDraw(canvas)
    }

    override fun dispatchSetPressed(pressed: Boolean) {
        super.dispatchSetPressed(pressed)
        invalidate()
    }
}