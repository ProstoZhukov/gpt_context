package ru.tensor.sbis.design_tile_view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import androidx.core.graphics.PathParser
import ru.tensor.sbis.design.utils.getDimen
import kotlin.math.min

/**
 * Форма изображения
 *
 * @author us.bessonov
 */
sealed class SbisTileViewImageShape {

    /**
     * Получить [Drawable] формы обрезки изображения.
     */
    internal abstract fun getDrawable(context: Context): Drawable

    /**
     * Получить [Path] границ изображения, с учётом формы обрезки, для применения обводки.
     */
    internal abstract fun getBorderShapePath(
        context: Context,
        @Px width: Float,
        @Px height: Float,
        @Px borderWidth: Float
    ): Path
}

/**
 * Прямоугольник
 *
 * @param ratio соотношение сторон
 * @param needGradient требуется ли отображать градиент на границе изображения с содержимым
 */
data class Rectangle(
    internal val ratio: SbisTileViewImageRatio = SbisTileViewImageRatio.SQUARE,
    internal val needGradient: Boolean = false
) : SbisTileViewImageShape() {

    internal var radii: FloatArray = FloatArray(8) { 0f }

    override fun getDrawable(context: Context) = GradientDrawable().apply {
        color = ColorStateList.valueOf(Color.WHITE)
        cornerRadii = radii
    }

    override fun getBorderShapePath(context: Context, width: Float, height: Float, borderWidth: Float) = Path()
}

/**
 * Круг
 */
object Circle : SbisTileViewImageShape() {

    override fun getDrawable(context: Context) =
        ContextCompat.getDrawable(context, R.drawable.design_tile_view_circle_shape)!!

    override fun getBorderShapePath(context: Context, width: Float, height: Float, borderWidth: Float) = Path().apply {
        addCircle(width / 2, height / 2, width / 2, Path.Direction.CW)
    }
}

/**
 * Суперэллипс
 */
object SuperEllipse : SbisTileViewImageShape() {

    private var path: Path? = null

    private val pathBounds = RectF()

    override fun getDrawable(context: Context) =
        ContextCompat.getDrawable(context, R.drawable.design_tile_view_superellipse_shape)!!

    override fun getBorderShapePath(context: Context, width: Float, height: Float, borderWidth: Float): Path {
        val basePath = path?.let { Path(it) }
            ?: createPath(context).also { path = it }
        return basePath.transform(width, height)
    }

    private fun createPath(context: Context): Path {
        val shape = PathParser.createNodesFromPathData(
            context.getString(ru.tensor.sbis.design.R.string.design_superellipse_shape_path)
        )
        return Path().apply {
            PathParser.PathDataNode.nodesToPath(shape, this)
        }
    }

    private fun Path.transform(@Px width: Float, @Px height: Float) = apply {
        transform(
            Matrix().apply {
                this@transform.computeBounds(pathBounds, true)
                val pathWidth = pathBounds.width()
                val pathHeight = pathBounds.height()
                postTranslate((width - pathWidth) / 2, (height - pathHeight) / 2)

                val widthRatio = width / pathWidth
                val heightRatio = height / pathHeight
                val ratio = min(widthRatio, heightRatio)
                postScale(ratio, ratio, width / 2f, height / 2f)
            }
        )
    }
}

/**
 * Квадрат.
 *
 * Изображение располагается с отступами внутри плитки и может иметь скругление
 */
@Suppress("DataClassPrivateConstructor")
object SquareInside : SbisTileViewImageShape() {

    override fun getDrawable(context: Context): Drawable = GradientDrawable().apply {
        color = ColorStateList.valueOf(Color.WHITE)
        cornerRadius = getCornerRadius(context)
    }

    override fun getBorderShapePath(context: Context, width: Float, height: Float, borderWidth: Float) = Path().apply {
        /*
        Поскольку обводка рисуется вне изображения, а не по его контуру, радиус скругления для обводки увеличивается на
        половину её ширины, чтобы избежать просветов между фигурой и обводкой.
        */
        val radius = getCornerRadius(context) + borderWidth / 2
        addRoundRect(
            0f,
            0f,
            width,
            height,
            radius,
            radius,
            Path.Direction.CW
        )
    }

    private fun getCornerRadius(context: Context) = context.getDimen(ru.tensor.sbis.design.R.attr.borderRadius_xs)
}