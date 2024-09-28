package ru.tensor.sbis.design_tile_view.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import androidx.core.graphics.withSave
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import ru.tensor.sbis.design.utils.getDimen
import ru.tensor.sbis.design_tile_view.Rectangle
import ru.tensor.sbis.design_tile_view.SbisTileViewImageShape

/**
 * Предназначен для рисования обводки внутри изображения в компоненте Плитка.
 * Цвет и ширина обводки определяются глобальными переменными.
 *
 * @author us.bessonov
 */
internal class ImageBorderDrawer(private val context: Context) {

    private val borderWidth = context.getDimen(ru.tensor.sbis.design.R.attr.borderThickness_s)

    private val paint = Paint().apply {
        color = context.getColorFromAttr(ru.tensor.sbis.design.R.attr.borderColor)
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = borderWidth
    }

    private val offset = (borderWidth / 2).toInt()

    private var path = Path()
    private var bounds = RectF()

    private val width: Float
        get() = bounds.width() + 2 * offset

    private val height: Float
        get() = bounds.height() + 2 * offset

    private var shape: SbisTileViewImageShape = Rectangle()

    /**
     * Задать границы изображения для обводки.
     */
    fun setBounds(bounds: RectF) {
        if (this.bounds != bounds) {
            this.bounds = bounds
            update()
        }
    }

    /**
     * Задать форму изображения, к которому применяется обводка.
     */
    fun setShape(shape: SbisTileViewImageShape) {
        if (this.shape != shape) {
            this.shape = shape
            update()
        }
    }

    /** @SelfDocumented */
    fun draw(canvas: Canvas) {
        canvas.withSave {
            translate(bounds.left - offset, bounds.top - offset)
            drawPath(path, paint)
        }
    }

    private fun update() {
        path = shape.getBorderShapePath(context, width, height, borderWidth)
    }

}