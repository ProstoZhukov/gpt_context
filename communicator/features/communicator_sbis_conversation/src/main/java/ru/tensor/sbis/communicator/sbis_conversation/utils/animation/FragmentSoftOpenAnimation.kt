package ru.tensor.sbis.communicator.sbis_conversation.utils.animation

import android.content.Context
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation
import androidx.tracing.Trace

/** @SelfDocumented */
internal class FragmentSoftOpenAnimation(context: Context) : Animation() {

    /**
     * Последнее системное время, с которым был запрос [getTransformation]
     */
    private var lastSystemCurrentTime = EMPTY_LAST_TIME

    /**
     * Текущее время анимации, в процессе может отличаться от системного [lastSystemCurrentTime],
     * если были пропуски фреймов.
     */
    private var animationCurrentTime = EMPTY_LAST_TIME

    /**
     * Признак процесса анимации.
     */
    private var isAnimationRunning = false

    private val closedXDelta: Float =
        CLOSED_X_DELTA_PERCENT * context.resources.displayMetrics.widthPixels

    init {
        interpolator = DecelerateInterpolator()
        duration = ANIMATION_TIME
    }

    override fun getTransformation(currentTime: Long, outTransformation: Transformation?): Boolean {
        // Запоминаем время начала анимации
        if (animationCurrentTime == EMPTY_LAST_TIME) {
            Trace.beginAsyncSection("FragmentSoftOpenAnimation.running", 0)
            animationCurrentTime = currentTime
            lastSystemCurrentTime = currentTime
        }
        // Считаем diff предыдущего и нового времени
        val lastFrameDiff = currentTime - lastSystemCurrentTime
        lastSystemCurrentTime = currentTime
        // Если анимация уже запущена или если не было пропусков фреймов с момента предыдущего запроса трансформации ->
        // изменяем текущее время анимации, иначе оставляем прежним (анимация не начнется).
        // Это необходимо, чтобы не показывать анимацию с фризами из-за загруженности главного потока на момент старта анимации,
        // что часто бывает на средних девайсах при первичной установки списка.
        if (lastFrameDiff in 0..(ONE_FRAME_INTERVAL + 1) || isAnimationRunning) {
            isAnimationRunning = true
            animationCurrentTime += ONE_FRAME_INTERVAL
        }
        return super.getTransformation(animationCurrentTime, outTransformation)
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        t.alpha = getInterpolatedValue(MIN_ALPHA, MAX_ALPHA, interpolatedTime)
        t.matrix.setTranslate(
            getInterpolatedValue(closedXDelta, OPENED_X_DELTA, interpolatedTime),
            0f
        )
    }

    override fun restrictDuration(durationMillis: Long) = Unit

    private fun getInterpolatedValue(fromValue: Float, toValue: Float, interpolatedTime: Float) =
        fromValue + (toValue - fromValue) * interpolatedTime
}

private const val ANIMATION_TIME = 140L
private const val EMPTY_LAST_TIME = -1L
private const val ONE_FRAME_INTERVAL = 16L
private const val MIN_ALPHA = 0f
private const val MAX_ALPHA = 1f
private const val CLOSED_X_DELTA_PERCENT = 0.2f
private const val OPENED_X_DELTA = 0f