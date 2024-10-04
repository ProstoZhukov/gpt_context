package ru.tensor.sbis.design.profile.personcollagelist.contentviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.TextPaint
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.graphics.withSave
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.utils.StaticLayoutConfigurator
import ru.tensor.sbis.design.profile.R
import ru.tensor.sbis.design.profile.personcollagelist.util.FontSizeForBoundsSize
import ru.tensor.sbis.design.profile_decl.person.PhotoSize
import ru.tensor.sbis.design.theme.global_variables.BackgroundColor
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.design.utils.formatCount

/**
 * Предназначен для отрисовки счётчика в коллаже поверх изображения, с затемнением.
 *
 * @author us.bessonov
 */
internal class CounterDrawer(private val context: Context) {

    @ColorInt
    private val backgroundColor = BackgroundColor.DIM.getValue(context)

    private val fillPaint = Paint().apply {
        color = backgroundColor
    }
    private val counterPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = TypefaceManager.getRobotoRegularFont(context)
        textSize = FontSize.S.getScaleOffDimen(context)
        color = TextColor.CONTRAST.getValue(context)
    }

    private val counterSizes by lazy {
        PhotoSize.values()
            .filterNot { it == PhotoSize.UNSPECIFIED }
            .map {
                FontSizeForBoundsSize(
                    it.photoImageSize!!.getDimenPx(context),
                    it.collageCounterTextSize!!.getScaleOffDimen(context)
                )
            }
            .sortedBy { it.boundsSize }
    }

    private var counterLeft = 0f
    private var counterTop = 0f

    private var counterLayout: Layout? = null

    var currentCount = 0
        private set

    @Px
    private var itemSize = 0

    /** @SelfDocumented */
    fun setCount(count: Int) {
        if (count == currentCount) return
        currentCount = count
        updateCounterLayout()
        ensureCounterFits()
    }

    /** @SelfDocumented */
    fun hasCounter() = currentCount > 0

    /** @SelfDocumented */
    fun setItemSize(@Px size: Int) {
        itemSize = size
        counterPaint.textSize = getCounterFontSize()
        ensureCounterFits()
    }

    /** @SelfDocumented */
    fun draw(canvas: Canvas) {
        if (!hasCounter()) return

        counterLeft = (itemSize - getCounterWidth()).toFloat() / 2
        counterTop = (itemSize - getCounterHeight()).toFloat() / 2

        counterLayout?.let {
            canvas.withSave {
                canvas.drawPaint(fillPaint)
                translate(counterLeft, counterTop)
                it.draw(canvas)
            }
        }
    }

    private fun getFormattedCount(count: Int) = "+${formatCount(count)}"

    @Px
    private fun getCounterWidth() = counterLayout?.width ?: 0

    @Px
    private fun getCounterHeight() = counterLayout?.height ?: 0

    @Px
    private fun getCounterFontSize(): Float {
        return (counterSizes.find { it.boundsSize >= itemSize } ?: counterSizes.last())
            .textSize
    }

    private fun updateCounterLayout() {
        counterLayout = StaticLayoutConfigurator.createStaticLayout(getFormattedCount(currentCount), counterPaint) {
            includeFontPad = false
        }
    }

    private fun ensureCounterFits() {
        val text = counterLayout?.text ?: return
        val counterPadding = context.resources.getDimensionPixelSize(R.dimen.design_profile_xs_counter_badge_padding)
        if (itemSize - counterPadding <= 0) return
        if (counterPaint.measureText(text.toString()) <= (itemSize - counterPadding)) return
        var smallerSize = counterSizes.first().textSize
        while (counterPaint.measureText(text.toString()) > (itemSize - counterPadding)) {
            counterPaint.textSize = --smallerSize
        }
        updateCounterLayout()
    }
}