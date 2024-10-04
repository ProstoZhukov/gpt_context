package ru.tensor.sbis.design.audio_player_view.view.player.children

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.View
import ru.tensor.sbis.communication_decl.communicator.media.waveform.WaveformDownscaleUtil
import ru.tensor.sbis.design.audio_player_view.AudioPlayerViewPlugin
import ru.tensor.sbis.design.theme.global_variables.StyleColor

/**
 * Компонент для отображения осциллограммы в аудио сообщении.
 *
 * @author rv.krohalev
 */
class WaveformView(context: Context) : View(context) {

    private val waveformDrawable = WaveformDrawable(this)

    private var waveform: ByteArray? = null
    private var lastSeekBarWidth: Int = 0
    private var downscaledWaveform: ByteArray? = null

    private val waveformDownscaleUtil: WaveformDownscaleUtil? by lazy {
        AudioPlayerViewPlugin.waveformHelperProvider?.provideWaveformDownscaleUtil()
    }

    init {
        val seekBarColor = StyleColor.INFO.getColor(context)
        waveformDrawable.setColors(seekBarColor)
    }

    /**
     * Установить осциллограмму.
     */
    fun setWaveform(waveform: ByteArray) {
        this.waveform = waveform
        onWaveformChanged(waveform)
    }

    /**
     * Установить прогресс воспроизведения.
     */
    fun setProgress(progress: Float) {
        if (waveformDrawable.isDragging) return
        waveformDrawable.progress = progress
        invalidate()
    }

    /**
     * Установить делагат обработки перемотки аудио.
     */
    fun setSeekBarDelegate(seekBarDelegate: SeekBarDelegate) {
        waveformDrawable.setDelegate(seekBarDelegate)
    }

    /**
     * Установить состояние активности.
     */
    fun setActive(value: Boolean) = waveformDrawable.setActive(value)

    /**
     * Установить состояние паузы.
     */
    fun setPaused(value: Boolean) = waveformDrawable.setPaused(value)

    fun setAvailabilityCondition(condition: () -> Boolean) {
        waveformDrawable.isAvailable = condition
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val result = waveformDrawable.onTouch(event.action, event.x, event.y)
        if (result) invalidate()
        return result || super.onTouchEvent(event)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val width = right - left
        waveformDrawable.setSize(width, bottom - top)
        onSeekBarWidthChanged(width)
    }

    override fun onDraw(canvas: Canvas) {
        waveformDrawable.draw(canvas)
    }

    private fun onWaveformChanged(waveform: ByteArray) {
        if (lastSeekBarWidth > 0) {
            updateDownscaledWaveform(waveform, lastSeekBarWidth)
        }
    }

    private fun onSeekBarWidthChanged(width: Int) {
        if (lastSeekBarWidth != width) {
            lastSeekBarWidth = width
            waveform?.let { updateDownscaledWaveform(it, width) }
        }
    }

    private fun updateDownscaledWaveform(waveform: ByteArray, width: Int) {
        val barsCount = waveformDrawable.getBarsCount(width)
        downscaledWaveform = when {
            barsCount == waveform.size -> waveform
            barsCount > 0 && barsCount < waveform.size -> waveformDownscaleUtil?.downscaleWaveform(waveform, barsCount)
            else -> null
        }
        waveformDrawable.setWaveform(downscaledWaveform)
        invalidate()
    }

    override fun setClickable(clickable: Boolean) {
        super.setClickable(clickable)
        waveformDrawable.setClickable(clickable)
    }
}