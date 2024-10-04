package ru.tensor.sbis.design.video_message_view.message.children.duration

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.communication_decl.communicator.media.data.State
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.video_message_view.R
import ru.tensor.sbis.design.video_message_view.message.children.duration.drawables.BarChartDrawable
import ru.tensor.sbis.design.video_message_view.message.children.duration.drawables.DotDrawable

/**
 * Формат продолжительность видеосообщения.
 */
private const val DURATION_FORMAT = "%2d:%02d"
private const val MINUTE_TO_SECONDS = 60

/**
 * Вью для отображения продолжительности видеосообщения.
 *
 * @author da.zhukov
 */
internal class VideoMessageDurationView(
    context: Context
) : View(context) {

    private val durationLeftPadding =
        resources.getDimensionPixelSize(R.dimen.video_message_duration_time_left_padding)
    private val durationRightPadding = context.getDimenPx(RDesign.attr.offset_2xs)

    /**
     * Цвет текста продолжительности видеосообщения.
     */
    private val color = context.getThemeColorInt(RDesign.attr.contrastTextColor)

    /**
     * Продолжительность видеосообщения.
     */
    private val durationTextLayout = TextLayout().apply {
        val height = dp(15)
        configure {
            paint.color = color
            paint.textSize = FontSize.X3S.getScaleOffDimen(context)
            isSingleLine = true
            includeFontPad = false
            verticalGravity = Gravity.CENTER_VERTICAL
            minHeight = height
            maxHeight = height
            padding = TextLayout.TextLayoutPadding(durationLeftPadding, 0, durationRightPadding, 0)
        }
    }

    /**
     * Drawable для отображения анимируемой гистограммы..
     */
    private val barChartDrawable = BarChartDrawable().apply {
        params = BarChartDrawable.GraphParams(width = dp(1), maxHeight = dp(4))
        callback = this@VideoMessageDurationView
        textColor = color
        setVisible(false, false)
    }

    /**
     * Drawable для отображения точки.
     */
    private val dotDrawable = DotDrawable().apply {
        size = dp(4)
        callback = this@VideoMessageDurationView
        textColor = color
        setVisible(true, false)
    }

    init {
        setWillNotDraw(false)
        background =
            AppCompatResources.getDrawable(context, R.drawable.video_message_duration_bg).apply { alpha = 0.2f }
    }

    /**
     * Отобразить длительность видео.
     */
    fun setDuration(durationSeconds: Int) {
        val minutes = durationSeconds / MINUTE_TO_SECONDS
        val seconds = durationSeconds % MINUTE_TO_SECONDS
        val durationText = DURATION_FORMAT.format(minutes, seconds)
        val isChanged = durationTextLayout.buildLayout { text = durationText }
        if (isChanged) invalidate()
    }

    /**
     * изменить стасу проигрывания видеосообщения.
     */
    fun changeStatus(state: State) {
        when (state) {
            State.PLAYING -> {
                dotDrawable.setVisible(false, false)
                barChartDrawable.setVisible(true, true)
                barChartDrawable.start()
            }
            State.PAUSED -> {
                barChartDrawable.stop()
            }
            State.DEFAULT -> {
                dotDrawable.setVisible(true, false)
                barChartDrawable.setVisible(false, false)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = dp(41)
        val height = maxOf(
            durationTextLayout.height,
            barChartDrawable.intrinsicHeight,
            dotDrawable.intrinsicHeight
        )
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val barChartDrawableTopPos = (measuredHeight - barChartDrawable.intrinsicHeight) / 2
        durationTextLayout.layout(
            paddingStart,
            paddingTop
        )
        val drawableLeftPos = durationTextLayout.right
        barChartDrawable.setBounds(
            drawableLeftPos,
            barChartDrawableTopPos,
            drawableLeftPos + barChartDrawable.intrinsicWidth,
            barChartDrawableTopPos + barChartDrawable.intrinsicHeight
        )
        dotDrawable.setBounds(
            drawableLeftPos + dp(1),
            barChartDrawableTopPos,
            drawableLeftPos + dotDrawable.intrinsicWidth,
            barChartDrawableTopPos + dotDrawable.intrinsicHeight
        )
    }

    override fun onDraw(canvas: Canvas) {
        durationTextLayout.draw(canvas)
        barChartDrawable.draw(canvas)
        dotDrawable.draw(canvas)
    }

    override fun verifyDrawable(who: Drawable): Boolean =
        who == barChartDrawable || who == dotDrawable || super.verifyDrawable(who)
}