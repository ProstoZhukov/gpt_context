package ru.tensor.sbis.widget_player.widget.emoji

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View

/**
 * @author am.boldinov
 */
class EmojiView private constructor(
    context: Context,
    private val emojiDrawable: EmojiDrawable
) : View(context), EmojiApi by emojiDrawable {

    constructor(context: Context): this(context, EmojiDrawable(context))

    init {
        emojiDrawable.callback = this
    }

    override fun verifyDrawable(who: Drawable): Boolean {
        return who === emojiDrawable || super.verifyDrawable(who)
    }
}