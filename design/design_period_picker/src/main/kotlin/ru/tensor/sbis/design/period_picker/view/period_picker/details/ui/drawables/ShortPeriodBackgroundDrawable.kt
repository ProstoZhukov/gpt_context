package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.style_holder.BackgroundDrawableStyleHolder

/**
 * Drawable для отрисовки фона периода в календаре быстрого выбора.
 *
 * @author mb.kruglova
 */
internal class ShortPeriodBackgroundDrawable @JvmOverloads constructor(
    val context: Context,
    private val styleHolder: BackgroundDrawableStyleHolder = BackgroundDrawableStyleHolder.create(context)
) : Drawable() {

    private lateinit var cornerPaint: Paint
    private lateinit var cornerRectF: RectF
    private lateinit var cornerPath: Path

    private lateinit var borderPaint: Paint
    private lateinit var borderRectF: RectF
    private lateinit var borderPath: Path
    private var borderRadius: Float = styleHolder.borderRadius

    private lateinit var basePaint: Paint
    private lateinit var baseRectF: RectF
    private lateinit var basePath: Path
    private var baseRadius: Float = 0F

    private var rect: RectF = RectF()
    private val borderThickness: Float = styleHolder.borderThickness.toFloat()

    init {
        initCorner()
        initBase()
        initBorder()
    }

    override fun draw(canvas: Canvas) {
        canvas.drawPath(cornerPath, cornerPaint)
        canvas.drawPath(borderPath, borderPaint)
        canvas.drawPath(basePath, basePaint)
    }

    override fun setAlpha(alpha: Int) {
        cornerPaint.alpha = alpha
        borderPaint.alpha = alpha
        basePaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        cornerPaint.colorFilter = colorFilter
        borderPaint.colorFilter = colorFilter
        basePaint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun onBoundsChange(bounds: Rect) {
        cornerPath.reset()
        basePath.reset()
        borderPath.reset()

        setCorner()
        setBackground()
    }

    /** Инициализировать поля для отрисовки уголков начала и конца периода. */
    private fun initCorner() {
        cornerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        cornerPaint.style = Paint.Style.FILL
        cornerPaint.color = styleHolder.cornerColor

        cornerPath = Path()
        cornerRectF = RectF()
    }

    /** Инициализировать поля для отрисовки фона дня. */
    private fun initBase() {
        basePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        basePaint.style = Paint.Style.FILL
        basePaint.color = styleHolder.backgroundColor

        basePath = Path()
        baseRectF = RectF()
    }

    /** Инициализировать поля для отрисовки границы выбранного периода. */
    private fun initBorder() {
        borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        borderPaint.style = Paint.Style.FILL
        borderPaint.color = styleHolder.borderColor

        borderPath = Path()
        borderRectF = RectF()
    }

    /** Настроить отображение уголков начала и конца периода. */
    private fun setCorner() {
        val cornerRect = Rect(bounds.left, bounds.top, bounds.right, bounds.bottom)
        val cornerRadius = styleHolder.cornerRadius
        val radius = (bounds.bottom - bounds.top) / 2F
        val corners = floatArrayOf(
            cornerRadius,
            cornerRadius,
            radius,
            radius,
            cornerRadius,
            cornerRadius,
            radius,
            radius
        )

        cornerRectF.set(cornerRect)
        cornerPath.addRoundRect(cornerRectF, corners, Path.Direction.CW)
    }

    /** Настроить фон дня и границу периода. */
    private fun setBackground() {
        rect = RectF(bounds.left.toFloat(), bounds.top.toFloat(), bounds.right.toFloat(), bounds.bottom.toFloat())
        borderRadius = styleHolder.borderRadius
        baseRadius = styleHolder.borderRadius - borderThickness

        borderPath.addRoundRectByRadius(rect, borderRadius)

        val baseRect = RectF(
            rect.left + borderThickness,
            rect.top + borderThickness,
            rect.right - borderThickness,
            rect.bottom - borderThickness
        )
        basePath.addRoundRectByRadius(baseRect, baseRadius)
    }

    /** @SelfDocumented */
    private fun Path.addRoundRectByRadius(rectF: RectF, radius: Float) {
        this.addRoundRect(rectF, radius, radius, Path.Direction.CW)
    }
}