package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.drawable

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.annotation.Px
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.PathParser
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.utils.getThemeColorInt
import kotlin.math.min
import ru.tensor.sbis.design.R as RDesign

/**
 * Drawable для отображения заглушки аватарки чата.
 *
 * @author dv.baranov
 */
internal class ChatAvatarStubDrawable(context: Context) : Drawable() {

    /**
     * Размер заглушки.
     */
    @Px
    var size: Int = context.getDimenPx(RDesign.attr.size_m_image)
        set(value) {
            val isChanged = field != value
            field = value

            if (isChanged) {
                invalidateSelf()
            }
        }

    private val pathBounds = RectF()

    private val superEllipse by lazy {
        PathParser.createNodesFromPathData(context.getString(RDesign.string.design_superellipse_shape_path))
    }

    private val paintIcon = Paint().apply {
        isAntiAlias = true
        typeface = ResourcesCompat.getFont(context, RDesign.font.sbis_mobile_icons)
        style = Paint.Style.FILL
        textSize = context.getDimenPx(RDesign.attr.iconSize_2xl).toFloat()
        color = context.getThemeColorInt(RDesign.attr.secondaryIconColor)
        textAlign = Paint.Align.CENTER
    }

    private val paintBackground = Paint().apply {
        isAntiAlias = true
        color = context.getThemeColorInt(RDesign.attr.unaccentedAdaptiveBackgroundColor)
    }

    override fun getIntrinsicWidth(): Int = size

    override fun getIntrinsicHeight(): Int = size

    override fun draw(canvas: Canvas) {
        val iconPositionX = (bounds.width() / 2).toFloat()
        val iconPositionY = bounds.height() / 2 - ((paintIcon.descent() + paintIcon.ascent()) / 2)
        val icon = SbisMobileIcon.Icon.smi_cameraBlack.character.toString()
        val shapePath = Path().apply {
            PathParser.PathDataNode.nodesToPath(superEllipse, this)
            transform(size.toFloat(), size.toFloat())
        }
        canvas.drawPath(shapePath, paintBackground)
        canvas.drawText(icon, iconPositionX, iconPositionY, paintIcon)
    }

    override fun setAlpha(alpha: Int) {
        paintIcon.alpha = alpha
        paintBackground.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paintIcon.colorFilter = colorFilter
        paintBackground.colorFilter = colorFilter
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    private fun Path.transform(@Px width: Float, @Px height: Float) = apply {
        transform(
            Matrix().apply {
                this@transform.computeBounds(pathBounds, true)
                val pathWidth = pathBounds.width()
                val pathHeight = pathBounds.height()
                postTranslate((width - pathWidth) / 2, (height - pathHeight) / 2)

                val widthRatio = width / pathWidth
                val heightRatio = height / pathHeight
                val ratio = min(widthRatio, heightRatio)
                postScale(ratio, ratio, width / 2f, height / 2f)
            },
        )
    }
}
