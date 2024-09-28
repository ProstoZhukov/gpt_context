package ru.tensor.sbis.design.profile.util.clippath

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.view.View
import androidx.annotation.Px

/**
 * Предназначен для обрезки [View] в требуемой форме.
 *
 * @author us.bessonov
 */
abstract class ViewClipPath {

    /** @SelfDocumented */
    protected open val shapePath = Path()
    private val clearPath = Path()
    private val clearPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    /**
     * Подготваливает [View] для корректной обрезки.
     */
    fun initClippingView(view: View) = with(view) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        isDrawingCacheEnabled = true
        setWillNotDraw(false)
    }

    /**
     * Обновляет [Path] для обрезки, учитывая размеры [View].
     */
    fun setupClearPath(@Px width: Int, @Px height: Int) {
        if (width <= 0 || height <= 0) {
            return
        }
        with(clearPath) {
            reset()
            setupShapePath(width, height)
            addPath(shapePath)

            transform(getTransform(width, height))
            fillType = Path.FillType.INVERSE_EVEN_ODD
        }
    }

    /**
     * Применяет обрезку [View].
     */
    fun drawPath(canvas: Canvas) {
        canvas.drawPath(clearPath, clearPaint)
    }

    /**
     * Возвращает матрицу трансформации, применяемую к контуру обрезки.
     */
    protected open fun getTransform(@Px width: Int, @Px height: Int) = Matrix()

    /**
     * Настраивает контур обрезки с учётом размеров [View].
     */
    protected open fun setupShapePath(@Px width: Int, @Px height: Int) = Unit
}
