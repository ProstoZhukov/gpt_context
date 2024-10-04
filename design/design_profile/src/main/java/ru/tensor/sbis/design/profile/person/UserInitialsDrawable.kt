package ru.tensor.sbis.design.profile.person

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.Px
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.profile.R
import ru.tensor.sbis.design.profile_decl.util.calculateInitialsTextSize
import ru.tensor.sbis.design.theme.global_variables.ImageSize

/**
 * Рисует инициалы пользователя в центре холста.
 *
 * @author us.bessonov
 */
internal class UserInitialsDrawable(
    context: Context,
    @ColorInt
    initialsColor: Int,
    @Px
    private val initialsSize: Float?,
    @ColorInt
    private val backgroundColor: Int,
    private val initials: String,
    var initialsEnabled: Boolean = true
) : Drawable() {

    private val bordersWidth =
        context.resources.getDimensionPixelSize(R.dimen.design_profile_person_collage_line_view_item_outline_size)

    private val minWidthForTwoLetters = ImageSize.XS.getDimenPx(context)

    private val paint = Paint().apply {
        color = initialsColor
        initialsSize?.let {
            textSize = it
        }
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        typeface = TypefaceManager.getRobotoRegularFont(context)
    }

    override fun draw(canvas: Canvas) {
        canvas.drawColor(backgroundColor)
        if (!initialsEnabled) return
        if (initialsSize == null) {
            paint.textSize = calculateInitialsTextSize(bounds.width().toFloat())
        }
        val half = 2f
        val centerX = bounds.width() / half
        val centerY = (bounds.height() / half - (paint.descent() + paint.ascent()) / half)
        canvas.drawText(getDrawingInitials(), centerX, centerY, paint)
    }

    override fun getOpacity() = PixelFormat.TRANSLUCENT

    override fun setAlpha(alpha: Int) = Unit

    override fun setColorFilter(colorFilter: ColorFilter?) = Unit

    private fun getDrawingInitials() = if (bounds.width() + (bordersWidth * 2) >= minWidthForTwoLetters) {
        initials
    } else {
        initials.take(1)
    }
}