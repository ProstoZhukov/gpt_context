package ru.tensor.sbis.design.message_panel.recorder_common.record_control.children

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.core.graphics.withTranslation
import org.apache.commons.lang3.StringUtils.EMPTY
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.utils.SimpleTextPaint
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.global_variables.TextColor

/**
 * Drawable для отображения времени записи.
 *
 * @author vv.chekurda
 */
class RecordTimeDrawable(context: Context) : Drawable() {

    private val paint = SimpleTextPaint {
        typeface = TypefaceManager.getRobotoRegularFont(context)
        textSize = FontSize.X3S.getScaleOffDimenPx(context).toFloat()
        color = TextColor.LABEL_CONTRAST.getValue(context)
    }

    private var isRunning = false
    private val textBounds by lazy {
        Rect().apply {
            paint.getTextBounds(formattedRecordTime, 0, formattedRecordTime.length, this)
        }
    }

    private var formattedRecordTime: String = EMPTY
    private var recordStartTimeMs = 0L
    var recordingTimeSeconds = 0L
        private set(value) {
            val isChanged = field != value
            field = value
            if (isChanged) updateFormattedRecordTime()
        }

    /**
     * Смещение отрисовки по оси X.
     */
    var translationX: Float = 0f

    init {
        updateFormattedRecordTime()
    }

    /**
     * Начать анимацию.
     */
    fun start() {
        if (isRunning) return
        isRunning = true
        recordStartTimeMs = System.currentTimeMillis()
        invalidateSelf()
    }

    /**
     * Остановить анимацию.
     */
    fun stop() {
        if (!isRunning) return
        isRunning = false
        invalidateSelf()
    }

    /**
     * Очистить анимацию.
     */
    fun clear() {
        isRunning = false
        alpha = 0
        recordingTimeSeconds = 0L
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        if (isRunning) {
            val dt = System.currentTimeMillis() - recordStartTimeMs
            recordingTimeSeconds = dt / 1_000
            invalidateSelf()
        }
        canvas.withTranslation(x = translationX) {
            drawText(formattedRecordTime, bounds.left.toFloat(), bounds.bottom.toFloat(), paint)
        }
    }

    private fun updateFormattedRecordTime() {
        val minutes = recordingTimeSeconds / 60
        val seconds = recordingTimeSeconds - minutes * 60
        val timeFormat = "%2d:%02d"

        formattedRecordTime = timeFormat.format(minutes, seconds)
    }

    override fun getIntrinsicWidth(): Int =
        textBounds.width()

    override fun getIntrinsicHeight(): Int =
        textBounds.height()

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int =
        PixelFormat.TRANSLUCENT
}