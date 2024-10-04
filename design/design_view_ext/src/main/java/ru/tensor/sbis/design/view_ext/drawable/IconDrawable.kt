package ru.tensor.sbis.design.view_ext.drawable

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.text.TextPaint
import androidx.annotation.Px
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.utils.dpF
import ru.tensor.sbis.design.utils.extentions.getColorFrom
import ru.tensor.sbis.design.R as RDesign

/**
 * Рисует иконку в центре холста
 */
@SuppressLint("ResourceType")
class IconDrawable(
    context: Context,
    private val icon: String = "",
    @Px private val iconSize: Float = context.resources.dpF(24),
    private val backgroundColor: Int = context.getColorFrom(RDesign.color.palette_color_gray3),
    private val iconColor: Int = context.getColorFrom(RDesign.color.palette_color_gray4),
    private val isCircle: Boolean = false,
    private val circleWithBorder: Boolean = false
) : Drawable() {

    private val strokeWidth: Float = context.resources.dpF(2)

    private val iconPaint: TextPaint by lazy {
        TextPaint(Paint.ANTI_ALIAS_FLAG).also {
            it.typeface = TypefaceManager.getSbisMobileIconTypeface(context)
            it.color = iconColor
            it.isAntiAlias = true
            it.textAlign = Paint.Align.CENTER
            it.textSize = iconSize
        }
    }

    private val circlePaint: Paint by lazy { Paint(Paint.ANTI_ALIAS_FLAG).also { it.color = backgroundColor } }

    private val borderPaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.color = context.getColorFrom(RDesign.color.palette_color_white1)
            it.style = Paint.Style.STROKE
            it.strokeWidth = strokeWidth
        }
    }

    override fun draw(canvas: Canvas) {
        val centerX = bounds.centerX().toFloat()
        val centerY = bounds.centerY().toFloat()

        if (isCircle) {
            val radius = (bounds.width() / 2).toFloat()
            canvas.drawCircle(centerX, centerY, radius, circlePaint)
            if (circleWithBorder) {
                val borderRadius = radius - strokeWidth / 2
                canvas.drawCircle(centerX, centerY, borderRadius, borderPaint)
            }
        } else canvas.drawColor(backgroundColor)

        if (icon.isEmpty()) return

        val iconCenterY = (centerY - (iconPaint.descent() + iconPaint.ascent()) / 2)
        canvas.drawText(icon, centerX, iconCenterY, iconPaint)
    }

    override fun getOpacity() = PixelFormat.TRANSLUCENT

    override fun setAlpha(alpha: Int) = Unit

    override fun setColorFilter(colorFilter: ColorFilter?) = Unit
}