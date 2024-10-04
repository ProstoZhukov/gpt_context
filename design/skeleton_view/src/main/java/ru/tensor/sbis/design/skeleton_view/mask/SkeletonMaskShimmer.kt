package ru.tensor.sbis.design.skeleton_view.mask

import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Shader
import android.os.Handler
import android.view.View
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.core.view.ViewCompat
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

/**
 * Маска с анимацией мерцания
 *
 * @property shimmerColor цвет анимации мерцания
 * @property durationInMillis длительность интервала анимации мерцания
 * @property shimmerDirection направление анимации мерцания
 *
 * @param parent родительское view
 * @param maskColor цвет маски
 *
 * @author us.merzlikina
 */
internal class SkeletonMaskShimmer(
    parent: View,
    @ColorInt maskColor: Int,
    @ColorInt private val shimmerColor: Int,
    private val durationInMillis: Long,
    private val shimmerDirection: SkeletonShimmerDirection
) : SkeletonMask(parent, maskColor) {

    private val refreshIntervalInMillis: Long by lazy {
        ((1000f / parent.context.refreshRateInSeconds()) * .9f).toLong()
    }
    private val width: Float = parent.width.toFloat()
    private val matrix: Matrix = Matrix()
    private val shimmerGradient by lazy {
        val radians = Math.toRadians(shimmerDirection.angle.toDouble())
        LinearGradient(
            0f,
            0f,
            cos(radians.toFloat()) * width,
            sin(radians.toFloat()) * width,
            intArrayOf(color, shimmerColor, color),
            null,
            Shader.TileMode.CLAMP
        )
    }

    private var animation: Handler? = null
    private var animationTask: Runnable? = null

    override fun invalidate() {
        when {
            ViewCompat.isAttachedToWindow(parent) && parent.visibility == View.VISIBLE -> start()
            else -> stop()
        }
    }

    override fun start() {
        if (animation == null) {
            animation = Handler()
            animationTask = object : Runnable {
                override fun run() {
                    updateShimmer()
                    animation?.postDelayed(this, refreshIntervalInMillis)
                }
            }
            animationTask?.let { animation?.post(it) }
        }
    }

    override fun stop() {
        animationTask?.let { animation?.removeCallbacks(it) }
        animation = null
    }

    override fun createPaint() =
        Paint().also {
            it.shader = shimmerGradient
            it.isAntiAlias = true
        }

    private fun updateShimmer() {
        matrix.setTranslate(currentOffset(), 0f)
        paint.shader.setLocalMatrix(matrix)
        parent.invalidate()
    }

    private fun currentOffset(): Float {
        val progress = when (shimmerDirection) {
            SkeletonShimmerDirection.LEFT_TO_RIGHT, SkeletonShimmerDirection.TOP_LEFT_BOTTOM_RIGHT ->
                currentProgress()
            SkeletonShimmerDirection.RIGHT_TO_LEFT, SkeletonShimmerDirection.BOTTOM_RIGHT_TOP_LEFT ->
                1 - currentProgress()
        }
        val offset = width * 2
        val min = -offset
        val max = width + offset
        return progress * (max - min) + min
    }

    // Прогресс, зависящий от времени, для поддержки синхронизации между несвязанными view
    private fun currentProgress(): Float {
        val millis = System.currentTimeMillis()
        val current = millis.toDouble()
        val interval = durationInMillis
        val divisor = floor(current / interval)
        val start = interval * divisor
        val end = start + interval
        val percentage = (current - start) / (end - start)
        return percentage.toFloat()
    }

    private fun Context.refreshRateInSeconds(): Float =
        (getSystemService(Context.WINDOW_SERVICE) as? WindowManager)?.defaultDisplay?.refreshRate ?: 60f
}