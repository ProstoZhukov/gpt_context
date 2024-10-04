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
import androidx.annotation.ColorInt
import androidx.annotation.VisibleForTesting
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models.BoundaryDrawableType
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models.BoundaryDrawableType.*
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models.CentralDrawableType
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models.CentralDrawableType.*
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models.QuantumDrawableType
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models.QuantumDrawableType.*
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models.QuantumType
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models.QuantumType.*
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.style_holder.BackgroundDrawableStyleHolder

/**
 * Базовый drawable для отрисовки фона кванта в календаре.
 *
 * @author mb.kruglova
 */
internal abstract class BaseBackgroundDrawable @JvmOverloads constructor(
    context: Context,
    @ColorInt private val customColor: Int? = null,
    internal val styleHolder: BackgroundDrawableStyleHolder = BackgroundDrawableStyleHolder.create(context)
) : Drawable() {

    internal var quantumType: QuantumType = NO_SELECTION
    internal var drawableType: QuantumDrawableType = DefaultDrawableType

    abstract var cornerRadius: Float
    abstract var radius: Float

    private lateinit var cornerPaint: Paint
    private lateinit var cornerRectF: RectF
    private lateinit var cornerPath: Path

    private lateinit var borderPaint: Paint
    private lateinit var borderPath: Path

    private lateinit var roundBorderPaint: Paint
    private lateinit var roundBorderPath: Path

    private lateinit var basePaint: Paint
    private lateinit var basePath: Path

    private lateinit var customPaint: Paint
    private lateinit var customPath: Path

    private var rect: RectF = RectF()
    private val borderThickness: Float = styleHolder.borderThickness.toFloat()
    private val halfBorderThickness = styleHolder.borderThickness / 2.0F

    init {
        initCorner()
        initBase()
        initBorder()

        customColor?.let { initCustomBase(it) }
    }

    override fun draw(canvas: Canvas) {
        if (quantumType == NO_SELECTION) {
            customColor?.let { canvas.drawPath(customPath, customPaint) }
            return
        }

        if (quantumType != STANDARD) canvas.drawPath(cornerPath, cornerPaint)
        canvas.drawPath(basePath, basePaint)
        customColor?.let { canvas.drawPath(customPath, customPaint) }
        canvas.drawPath(borderPath, borderPaint)
        canvas.drawPath(roundBorderPath, roundBorderPaint)
    }

    override fun onBoundsChange(bounds: Rect) {
        cornerPath.reset()
        basePath.reset()
        borderPath.reset()
        roundBorderPath.reset()

        setCorner()
        setBackground()
        setCustomBackground()
    }

    override fun setAlpha(alpha: Int) {
        cornerPaint.alpha = alpha
        borderPaint.alpha = alpha
        roundBorderPaint.alpha = alpha
        basePaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        cornerPaint.colorFilter = colorFilter
        borderPaint.colorFilter = colorFilter
        roundBorderPaint.colorFilter = colorFilter
        basePaint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

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
    }

    /** Инициализировать поля для отрисовки прикладного фона дня. */
    private fun initCustomBase(@ColorInt customColor: Int) {
        customPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        customPaint.style = Paint.Style.FILL
        customPaint.color = customColor

        customPath = Path()
    }

    /** Инициализировать поля для отрисовки границы выбранного периода. */
    private fun initBorder() {
        borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        borderPaint.style = Paint.Style.STROKE
        borderPaint.color = styleHolder.borderColor
        borderPaint.strokeWidth = 2 * borderThickness

        borderPath = Path()

        roundBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        roundBorderPaint.style = Paint.Style.STROKE
        roundBorderPaint.color = styleHolder.borderColor
        roundBorderPaint.strokeWidth = borderThickness

        roundBorderPath = Path()
    }

    /** Настроить отображение уголков начала и конца периода. */
    private fun setCorner() {
        var cornerRect = Rect()
        var corners: FloatArray? = null
        val radius = cornerRadius
        when (quantumType) {
            START -> {
                cornerRect = Rect(bounds.left, bounds.top, bounds.right / 2, bounds.bottom / 2)
                corners = floatArrayOf(radius, radius, 0f, 0f, 0f, 0f, 0f, 0f)
            }

            END -> {
                cornerRect = Rect(
                    bounds.left + bounds.right / 2,
                    bounds.top + bounds.bottom / 2,
                    bounds.right,
                    bounds.bottom
                )
                corners = floatArrayOf(0f, 0f, 0f, 0f, radius, radius, 0f, 0f)
            }

            SINGLE -> {
                cornerRect = Rect(bounds.left, bounds.top, bounds.right, bounds.bottom)
                val hidden = this.radius + 2 * borderThickness
                corners = floatArrayOf(radius, radius, hidden, hidden, radius, radius, hidden, hidden)
            }

            else -> Unit
        }

        corners?.let {
            cornerRectF.set(cornerRect)
            cornerPath.addRoundRect(cornerRectF, corners, Path.Direction.CW)
        }
    }

    /** @SelfDocumented */
    private fun setCustomBackground() {
        customColor?.let {
            customPath.reset()

            val rectF = when (val type = drawableType) {
                is LeftDrawableType -> {
                    getBaseRect(type.bound, true)
                }

                is RightDrawableType -> getBaseRect(type.bound, false)

                else -> RectF(rect.left, rect.top, rect.right, rect.bottom)
            }

            customPath.addRoundRectByRadius(rectF, radius)
        }
    }

    /** Настроить фон дня и границу периода. */
    private fun setBackground() {
        rect = RectF(bounds.left.toFloat(), bounds.top.toFloat(), bounds.right.toFloat(), bounds.bottom.toFloat())

        setBackgroundByDrawableType()
    }

    /** @SelfDocumented */
    private fun setBackgroundByDrawableType() {
        when (val type = drawableType) {
            SingleDrawableType -> setSingleDay()
            is LeftDrawableType -> setBoundaryDay(type.bound)
            is RightDrawableType -> setBoundaryDay(type.bound, false)
            is InternalDrawableType -> setCentralDay(type.center)
            DefaultDrawableType -> Unit
        }
    }

    /** @SelfDocumented */
    private fun setSingleDay() {
        val rectF = RectF(
            rect.left + halfBorderThickness,
            rect.top + halfBorderThickness,
            rect.right - halfBorderThickness,
            rect.bottom - halfBorderThickness
        )
        roundBorderPath.addRoundRectByRadius(rectF, radius)
        basePath.addRoundRectByRadius(rectF, radius)
    }

    /** @SelfDocumented */
    private fun setBoundaryDay(type: BoundaryDrawableType, isLeft: Boolean = true) {
        if (type == BORDER) {
            val borderRect = RectF(
                if (isLeft) rect.left else rect.left - borderThickness,
                rect.top - borderThickness,
                if (isLeft) rect.right + borderThickness else rect.right,
                rect.bottom + borderThickness
            )
            val baseRect = RectF(
                if (isLeft) rect.left + halfBorderThickness else rect.left,
                rect.top,
                if (isLeft) rect.right else rect.right - halfBorderThickness,
                rect.bottom
            )
            setRect(borderRect, baseRect)
        } else {
            setRoundRect(
                getBorderRect(type, isLeft),
                getBaseRect(type, isLeft),
                getCorners(type, radius, isLeft)
            )
        }
    }

    /** @SelfDocumented */
    private fun setCentralDay(type: CentralDrawableType) {
        var borderRect: RectF? = null
        var baseRect: RectF? = null

        when (type) {
            VERTICAL_BORDER -> {
                borderRect = RectF(
                    rect.left - borderThickness,
                    rect.top,
                    rect.right + borderThickness,
                    rect.bottom
                )
                baseRect = RectF(
                    rect.left,
                    rect.top + halfBorderThickness,
                    rect.right,
                    rect.bottom - halfBorderThickness
                )
            }

            TOP_BORDER -> {
                borderRect = RectF(
                    rect.left - borderThickness,
                    rect.top,
                    rect.right + borderThickness,
                    rect.bottom + borderThickness
                )
                baseRect = RectF(
                    rect.left,
                    rect.top + halfBorderThickness,
                    rect.right,
                    rect.bottom
                )
            }

            BOTTOM_BORDER -> {
                borderRect = RectF(
                    rect.left - borderThickness,
                    rect.top - borderThickness,
                    rect.right + borderThickness,
                    rect.bottom
                )
                baseRect = RectF(
                    rect.left,
                    rect.top,
                    rect.right,
                    rect.bottom - halfBorderThickness
                )
            }

            NO_BORDER -> basePath.addRect(rect)
        }

        if (borderRect != null && baseRect != null) {
            setRect(borderRect, baseRect)
        }
    }

    /** @SelfDocumented */
    private fun getCorners(type: BoundaryDrawableType, radius: Float, isLeft: Boolean = true): FloatArray {
        val radiiX = if (isLeft) radius else 0f
        val radiiY = if (isLeft) 0f else radius
        return when (type) {
            VERTICAL_ROUNDING -> floatArrayOf(
                radiiY,
                radiiY,
                radiiY,
                radiiY,
                radiiX,
                radiiX,
                radiiX,
                radiiX
            )
            HORIZONTAL_ROUNDING -> floatArrayOf(
                radiiX,
                radiiX,
                radiiY,
                radiiY,
                radiiY,
                radiiY,
                radiiX,
                radiiX
            )
            TOP_ROUNDING -> floatArrayOf(radiiX, radiiX, radiiY, radiiY, 0f, 0f, 0f, 0f)
            BOTTOM_ROUNDING -> floatArrayOf(0f, 0f, 0f, 0f, radiiY, radiiY, radiiX, radiiX)
            else -> floatArrayOf()
        }
    }

    /** @SelfDocumented */
    @VisibleForTesting
    internal fun getBorderRect(type: BoundaryDrawableType, isLeft: Boolean = true): RectF {
        val left: Float
        val top: Float
        val right: Float
        val bottom: Float
        when (type) {
            VERTICAL_ROUNDING -> {
                left = rect.left + halfBorderThickness
                top = if (isLeft) rect.top - borderThickness else rect.top + halfBorderThickness
                right = rect.right - halfBorderThickness
                bottom = if (isLeft) rect.bottom - halfBorderThickness else rect.bottom + borderThickness
            }

            HORIZONTAL_ROUNDING -> {
                left = if (isLeft) rect.left + halfBorderThickness else rect.left - borderThickness
                top = rect.top + halfBorderThickness
                right = if (isLeft) rect.right + borderThickness else rect.right - halfBorderThickness
                bottom = rect.bottom - halfBorderThickness
            }

            TOP_ROUNDING -> {
                left = if (isLeft) rect.left + halfBorderThickness else rect.left - borderThickness
                top = rect.top + halfBorderThickness
                right = if (isLeft) rect.right + borderThickness else rect.right - halfBorderThickness
                bottom = rect.bottom + borderThickness
            }

            BOTTOM_ROUNDING -> {
                left = if (isLeft) rect.left + halfBorderThickness else rect.left - borderThickness
                top = rect.top - borderThickness
                right = if (isLeft) rect.right + borderThickness else rect.right - halfBorderThickness
                bottom = rect.bottom - halfBorderThickness
            }

            BORDER -> {
                left = rect.left
                top = rect.top
                right = rect.right
                bottom = rect.bottom
            }
        }

        return RectF(left, top, right, bottom)
    }

    /** @SelfDocumented */
    private fun getBaseRect(type: BoundaryDrawableType, isLeft: Boolean = true): RectF {
        val left: Float
        val top: Float
        val right: Float
        val bottom: Float
        when (type) {
            VERTICAL_ROUNDING -> {
                left = rect.left + halfBorderThickness
                top = if (isLeft) rect.top else rect.top + halfBorderThickness
                right = rect.right - halfBorderThickness
                bottom = if (isLeft) rect.bottom - halfBorderThickness else rect.bottom
            }

            HORIZONTAL_ROUNDING -> {
                left = if (isLeft) rect.left + halfBorderThickness else rect.left
                top = rect.top + halfBorderThickness
                right = if (isLeft) rect.right else rect.right - halfBorderThickness
                bottom = rect.bottom - halfBorderThickness
            }

            TOP_ROUNDING -> {
                left = if (isLeft) rect.left + halfBorderThickness else rect.left
                top = rect.top + halfBorderThickness
                right = if (isLeft) rect.right else rect.right - halfBorderThickness
                bottom = rect.bottom
            }

            BOTTOM_ROUNDING -> {
                left = if (isLeft) rect.left + halfBorderThickness else rect.left
                top = rect.top
                right = if (isLeft) rect.right else rect.right - halfBorderThickness
                bottom = rect.bottom - halfBorderThickness
            }

            BORDER -> {
                left = rect.left
                top = rect.top
                right = rect.right
                bottom = rect.bottom
            }
        }

        return RectF(left, top, right, bottom)
    }

    /** @SelfDocumented */
    private fun setRoundRect(
        borderRect: RectF,
        baseRect: RectF,
        corners: FloatArray
    ) {
        roundBorderPath.addRoundRectByCorners(borderRect, corners)
        basePath.addRoundRectByCorners(baseRect, corners)
    }

    /** @SelfDocumented */
    private fun setRect(borderRect: RectF, baseRect: RectF) {
        borderPath.addRect(borderRect, Path.Direction.CW)
        basePath.addRect(baseRect, Path.Direction.CW)
    }

    /** @SelfDocumented */
    private fun Path.addRoundRectByRadius(rectF: RectF, radius: Float) {
        this.addRoundRect(rectF, radius, radius, Path.Direction.CW)
    }

    /** @SelfDocumented */
    private fun Path.addRoundRectByCorners(rectF: RectF, corners: FloatArray) {
        this.addRoundRect(rectF, corners, Path.Direction.CW)
    }

    /** @SelfDocumented */
    private fun Path.addRect(rectF: RectF) {
        this.addRect(rectF, Path.Direction.CW)
    }
}