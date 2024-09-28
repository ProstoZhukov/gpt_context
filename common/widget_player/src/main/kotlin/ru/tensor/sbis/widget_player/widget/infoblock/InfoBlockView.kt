package ru.tensor.sbis.widget_player.widget.infoblock

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.core.view.updatePadding
import ru.tensor.sbis.widget_player.layout.VerticalBlockLayout
import ru.tensor.sbis.widget_player.widget.emoji.Emoji
import ru.tensor.sbis.widget_player.widget.emoji.EmojiApi
import ru.tensor.sbis.widget_player.widget.emoji.EmojiDrawable
import kotlin.math.max

/**
 * @author am.boldinov
 */
@SuppressLint("ViewConstructor")
internal class InfoBlockView private constructor(
    context: Context,
    private val options: InfoBlockOptions,
    private val emojiDrawable: EmojiDrawable
) : VerticalBlockLayout(context), EmojiApi by emojiDrawable {

    constructor(context: Context, options: InfoBlockOptions) : this(context, options, EmojiDrawable(context))

    private val contentPaddingLeft get() = emojiDrawable.intrinsicWidth.takeIf { it > 0 }?.let {
        it + options.paddingLeft.getValuePx(context)
    } ?: 0

    override var emoji: Emoji?
        get() = emojiDrawable.emoji
        set(value) {
            emojiDrawable.emoji = value
            updatePadding(
                left = options.paddingLeft.getValuePx(context) + contentPaddingLeft
            )
            val minHeight = value?.height ?: 0
            if (minimumHeight != minHeight) {
                minimumHeight = minHeight
            }
        }

    init {
        setWillNotDraw(false)
        emojiDrawable.callback = this
        setPadding(
            options.paddingLeft.getValuePx(context) + contentPaddingLeft,
            options.paddingTop.getValuePx(context),
            options.paddingRight.getValuePx(context),
            options.paddingBottom.getValuePx(context)
        )
        background = getBackgroundDrawable()
    }

    override fun verifyDrawable(who: Drawable): Boolean {
        return who === emojiDrawable || super.verifyDrawable(who)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        val dx = max(paddingLeft - contentPaddingLeft, 0)
        val dy = max(paddingTop - (emoji?.padding ?: 0).toFloat() / 2, 0f)
        canvas.translate(dx.toFloat(), dy)
        emojiDrawable.draw(canvas)
        canvas.restore()
    }

    private fun getBackgroundDrawable() = GradientDrawable().apply {
        setColor(options.backgroundColor.getValue(context))
        cornerRadius = options.borderRadius.getValue(context)
        setStroke(
            options.borderThickness.getValuePx(context),
            options.borderColor.getValue(context)
        )
    }
}