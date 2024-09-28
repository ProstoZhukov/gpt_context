package ru.tensor.sbis.design.stubview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Layout
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec.EXACTLY
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.stubview.hint.StubViewHintApi
import ru.tensor.sbis.design.stubview.hint.StubViewHintStyleHolder

/**
 * Заглушка с подсказкой для расположения около плавающих кнопок в пустых представлениях.
 *
 * @author ra.geraskin
 */
class StubViewHint @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : View(
    context,
    attrs,
    defStyleAttr,
    defStyleRes
), StubViewHintApi {

    private val styleHolder = StubViewHintStyleHolder.loadStyle(context)

    /** @SelfDocumented */
    override var hintText: String = ""
        set(value) {
            if (field == value) return
            field = value
            hintTextLayout.configure { text = field }
            requestLayout()
        }

    private var hintTextLayout: TextLayout = TextLayout {
        minLines = HINT_MIN_LINES
        maxLines = HINT_MAX_LINES
        paint.color = styleHolder.hintTextColor
        paint.textSize = styleHolder.hintTextSize
        text = hintText
        alignment = Layout.Alignment.ALIGN_NORMAL
        ellipsize = TextUtils.TruncateAt.END
        needHighWidthAccuracy = true
    }

    private val arrowDrawable =
        ContextCompat.getDrawable(context, R.drawable.stub_view_hint_arrow) ?: ColorDrawable(Color.MAGENTA)

    init {
        context.withStyledAttributes(attrs, R.styleable.StubViewHint, defStyleAttr, defStyleRes) {
            hintText = getString(R.styleable.StubViewHint_StubViewHint_text) ?: ""
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        with(styleHolder) {
            val viewWidth = when (MeasureSpec.getMode(widthMeasureSpec)) {
                EXACTLY -> measureWidthExactly(widthMeasureSpec)
                else -> measureWidthAtMost(widthMeasureSpec)
            }
            minimumHeight = hintTextLayout.height +
                arrowDrawable.intrinsicHeight +
                hintTextMarginBottom +
                hintTextMarginTop

            setMeasuredDimension(viewWidth, minimumHeight)
        }
    }

    private fun measureWidthExactly(widthMeasureSpec: Int): Int {
        with(styleHolder) {
            val availableWidth = MeasureSpec.getSize(widthMeasureSpec)

            hintTextLayout.configure {
                maxWidth = availableWidth - hintTextMarginEnd
            }
            return@measureWidthExactly availableWidth
        }
    }

    /**
     * 1. Измеряем доступную ширину.
     * 2. Задаём TextLayout.maxWidth = измеренная доступная ширина.
     * 3. TextLayout сжимается, увеличивая количество строк.
     * 4. Спрашиваем у TextLayout его новую "ужатую" ширину.
     * 5. Добавляем к этому значению горизонтальный маржин.
     * 6. Это значение и будет шириной вью компонента.
     */
    private fun measureWidthAtMost(widthMeasureSpec: Int): Int {
        with(styleHolder) {
            val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
            val specWidth =
                if (availableWidth > defaultMaxWidth) defaultMaxWidth
                else availableWidth

            hintTextLayout.configure {
                maxWidth = specWidth - hintTextMarginEnd
            }

            var viewWidth =
                if (hintTextLayout.width != 0) hintTextLayout.width
                else specWidth

            viewWidth += hintTextMarginEnd

            return@measureWidthAtMost viewWidth
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        with(styleHolder) {
            // Компоненты размещаются от правого края, т.к. View предполагает размещение слева от FAB кнопки
            hintTextLayout.layout(measuredWidth - hintTextMarginEnd - hintTextLayout.width, hintTextMarginTop)
            arrowDrawable.bounds.set(
                measuredWidth - arrowDrawable.intrinsicWidth - hintTextMarginEnd,
                measuredHeight - arrowDrawable.intrinsicHeight,
                measuredWidth - hintTextMarginEnd,
                measuredHeight
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
        hintTextLayout.draw(canvas)
        arrowDrawable.draw(canvas)
    }

    companion object{
        private const val HINT_MAX_LINES = 3
        private const val HINT_MIN_LINES = 1
    }

}