package ru.tensor.sbis.communicator.communicator_crm_chat_list.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.TextPaint
import androidx.core.content.res.ResourcesCompat
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.utils.getThemeColorInt

/**
 * Drawable кнопки взятия следующего чата в работу.
 *
 * @author dv.baranov
 */
internal class TakeOldestDrawable(context: Context) : Drawable() {

    private val iconTextLayout = TextLayout {
        paint = TextPaint().apply {
            typeface = ResourcesCompat.getFont(context, R.font.sbis_mobile_icons)
            textSize = context.getDimenPx(R.attr.iconSize_4xl).toFloat()
            color = context.getThemeColorInt(R.attr.contrastIconColor)
        }
        text = SbisMobileIcon.Icon.smi_BidUp.character.toString()
    }.apply {
        rotation = 90f
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        val startPosition = (maxOf(bounds.width(), iconTextLayout.height) - iconTextLayout.height) / 2
        val topPosition = (maxOf(bounds.height(), iconTextLayout.width) - iconTextLayout.width) / 2
        iconTextLayout.layout(bounds.left + startPosition, bounds.top + topPosition)
    }

    override fun draw(canvas: Canvas) {
        iconTextLayout.draw(canvas)
    }

    override fun setAlpha(alpha: Int) {
        iconTextLayout.alpha = alpha.toFloat()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        iconTextLayout.configure {
            paint.colorFilter = colorFilter
        }
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}
