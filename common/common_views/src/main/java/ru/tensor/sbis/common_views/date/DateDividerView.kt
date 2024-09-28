package ru.tensor.sbis.common_views.date

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.common_views.R
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.design.R as RDesign

/**
 * View разделителя в списке с отображением заданного текста(даты).
 *
 * @av.efimov1
 */
open class DateDividerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = R.style.common_views_DateDividerViewStyle
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val textLayout = TextLayout {
        paint.apply {
            typeface = TypefaceManager.getRobotoBoldFont(context)!!
        }
        includeFontPad = false
        padding = TextLayout.TextLayoutPadding(
            start = paddingStart,
            top = paddingTop,
            end = paddingEnd,
            bottom = paddingBottom
        )
    }

    private val arrowPaint: Paint = Paint().apply {
        style = Paint.Style.FILL
    }

    private val arrowWidth: Int
        get() = textLayout.width + textLayout.height / 2

    private val arrowHeight: Int
        get() = textLayout.height

    init {
        context.withStyledAttributes(
            attrs,
            R.styleable.CommonViewsDateDividerView,
            defStyleAttr,
            defStyleRes
        ) {
            val defaultTextColor = context.getThemeColorInt(RDesign.attr.contrastTextColor)
            textLayout.configure {
                paint.apply {
                    textSize = getDimensionPixelSize(
                        R.styleable.CommonViewsDateDividerView_CommonViewsDateDividerView_textSize,
                        FontSize.XL.getScaleOffDimenPx(context)
                    ).toFloat()
                    color = getColor(
                        R.styleable.CommonViewsDateDividerView_CommonViewsDateDividerView_textColor,
                        defaultTextColor
                    )
                }
            }
            arrowPaint.color = getColor(
                R.styleable.CommonViewsDateDividerView_CommonViewsDateDividerView_background,
                Color.MAGENTA
            )
        }
    }

    /**
     * Установить текст для отображения
     */
    fun setText(text: String) {
        textLayout.configure {
            this.text = text
        }
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            arrowWidth,
            arrowHeight
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        textLayout.layout(0, 0)
    }

    override fun onDraw(canvas: Canvas) {
        drawArrow(canvas)
        textLayout.draw(canvas)
    }

    private fun drawArrow(canvas: Canvas) {
        // Рисуем стрелку на которой будет располагаться текст
        val path = Path()
        path.moveTo(0f, 0f)
        path.lineTo(textLayout.width.toFloat(), 0f)
        path.lineTo(arrowWidth.toFloat(), arrowHeight.toFloat() / 2)
        path.lineTo(textLayout.width.toFloat(), arrowHeight.toFloat())
        path.lineTo(0f, arrowHeight.toFloat())
        path.close()
        canvas.drawPath(path, arrowPaint)
    }
}