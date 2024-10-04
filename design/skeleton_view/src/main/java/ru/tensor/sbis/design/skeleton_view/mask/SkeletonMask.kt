package ru.tensor.sbis.design.skeleton_view.mask

import android.annotation.SuppressLint
import android.graphics.*
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import ru.tensor.sbis.design.skeleton_view.createSkeleton
import ru.tensor.sbis.design.skeleton_view.views
import timber.log.Timber

/**
 * Маска, применяемая к Skeleton для отображения анимации
 *
 * @param color цвет маски
 *
 * @author us.merzlikina
 */
internal abstract class SkeletonMask(
    protected val parent: View,
    @ColorInt color: Int
) : SkeletonMaskable {

    var color: Int = color
        set(value) {
            paint.color = value
            field = value
        }

    private val bitmap: Bitmap by lazy { createBitmap() }
    private val canvas: Canvas by lazy { createCanvas() }
    protected val paint: Paint by lazy { createPaint() }

    protected open fun createBitmap(): Bitmap = Bitmap.createBitmap(parent.width, parent.height, Bitmap.Config.ALPHA_8)

    protected open fun createCanvas(): Canvas = Canvas(bitmap)

    protected open fun createPaint(): Paint = Paint().also { paint -> paint.color = color }

    fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
    }

    private fun draw(rectF: RectF, cornerRadius: Float, paint: Paint) {
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint)
    }

    private fun draw(rect: Rect, paint: Paint) {
        canvas.drawRect(rect, paint)
    }

    fun mask(viewGroup: ViewGroup, maskCornerRadius: Float) {
        val xferPaint = Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
            isAntiAlias = maskCornerRadius > 0
        }
        mask(viewGroup, viewGroup, xferPaint, maskCornerRadius)
    }

    private fun mask(view: View, root: ViewGroup, paint: Paint, maskCornerRadius: Float) {
        (view as? ViewGroup)?.let { viewGroup ->
            viewGroup.views().forEach { view -> mask(view, root, paint, maskCornerRadius) }
        } ?: maskView(view, root, paint, maskCornerRadius)
    }

    private fun maskView(view: View, root: ViewGroup, paint: Paint, maskCornerRadius: Float) {
        validate(view)

        val rect = Rect()
        view.getDrawingRect(rect)
        root.offsetDescendantRectToMyCoords(view, rect)

        if (maskCornerRadius > 0) {
            val rectF = RectF(rect.left.toFloat(), rect.top.toFloat(), rect.right.toFloat(), rect.bottom.toFloat())
            draw(rectF, maskCornerRadius, paint)
        } else {
            draw(rect, paint)
        }
    }

    /**
     * для [RecyclerView] и [ViewPager2] нужно использовать [createSkeleton]
     */
    @SuppressLint("BinaryOperationInTimber")
    private fun validate(view: View) {
        when (view) {
            is RecyclerView, is ViewPager2 ->
                Timber.w(
                    "Passing ViewGroup with reusable children to SkeletonView - " +
                        "consider using createSkeleton() instead"
                )
        }
    }
}