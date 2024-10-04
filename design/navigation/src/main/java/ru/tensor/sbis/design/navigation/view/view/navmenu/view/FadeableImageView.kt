package ru.tensor.sbis.design.navigation.view.view.navmenu.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Shader
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.marginStart

/**
 * Обёртка над ImageView, позволяющая плавно затенять конец изображения.
 *
 * @da.zolotarev
 */
class FadeableImageView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatImageView(context, attributeSet, defStyleAttr) {

    private val fadeMatrix by lazy { Matrix() }

    private val fadePaint by lazy {
        Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        }
    }

    /**
     * Ширина затенения изображения.
     */
    var fadeEdgeSize: Int = 0
        set(value) {
            val isChanged = field != value
            field = value
            if (isChanged) {
                fadeShader = createFadeShader(value)
            }
        }

    private var fadeShader: Lazy<Shader> = createFadeShader(fadeEdgeSize)

    override fun draw(canvas: Canvas) {
        drawFade(canvas) {
            super.draw(canvas)
        }
    }

    private fun createFadeShader(fadeSize: Int): Lazy<Shader> = lazy {
        LinearGradient(
            0f,
            0f,
            fadeSize.toFloat(),
            0f,
            Color.TRANSPARENT,
            Color.WHITE,
            Shader.TileMode.CLAMP
        ).also {
            fadePaint.shader = it
        }
    }

    private fun drawFade(canvas: Canvas, function: (Canvas) -> Unit) {
        val saveCount = canvas.saveLayer(0f, 0f, right.toFloat(), bottom.toFloat(), null)
        val fadeLeft = right.toFloat() - marginStart - paddingLeft - fadeEdgeSize
        function(canvas)
        fadeMatrix.reset()
        fadeMatrix.postTranslate(fadeLeft, 0f)
        fadeShader.value.setLocalMatrix(fadeMatrix)
        canvas.drawRect(
            fadeLeft,
            0f,
            right.toFloat(),
            bottom.toFloat(),
            fadePaint
        )
        canvas.restoreToCount(saveCount)
    }

}