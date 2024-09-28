package ru.tensor.sbis.widget_player.widget.emoji

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.text.TextPaint
import ru.tensor.sbis.design.TypefaceManager

/**
 * @author am.boldinov
 */
class EmojiDrawable(
    context: Context
) : Drawable(), EmojiApi {

    private val paint = TextPaint(TextPaint.ANTI_ALIAS_FLAG).apply {
        typeface = TypefaceManager.getRobotoRegularFont(context)
        textAlign = Paint.Align.CENTER
    }

    override var emoji: Emoji? = null
        set(value) {
            if (field != value) {
                field = value
                value?.let {
                    paint.setEmoji(it)
                }
                setBounds(0, 0, value?.width ?: 0, value?.height ?: 0)
                invalidateSelf()
            }
        }

    override fun draw(canvas: Canvas) {
        emoji?.takeIf { it.char.isNotEmpty() }?.let { emoji ->
            val centerX = bounds.centerX().toFloat()
            val centerY = (bounds.centerY().toFloat() - (paint.descent() + paint.ascent()) / 2)
            canvas.drawText(emoji.char, centerX, centerY, paint)
        }
    }

    override fun getIntrinsicWidth() = bounds.width()

    override fun getIntrinsicHeight() = bounds.height()

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getOpacity() = PixelFormat.TRANSLUCENT

    override fun setAlpha(alpha: Int) = Unit

    override fun setColorFilter(colorFilter: ColorFilter?) = Unit

    private fun TextPaint.setEmoji(emoji: Emoji) {
        textSize = emoji.size
    }
}