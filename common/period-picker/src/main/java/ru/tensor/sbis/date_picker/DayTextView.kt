package ru.tensor.sbis.date_picker

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.sbis_text_view.SbisTextView

/**
 * [View] для отображения дня месяца
 *
 * @author us.bessonov
 */
class DayNumberTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0
) : SbisTextView(
    context,
    attrs,
    defStyleAttr
) {

    private val counterLayout by lazy {
        TextLayout {
            paint.color = ContextCompat.getColor(context, R.color.date_picker_item_counter_text_color)
            paint.textSize = resources.getDimension(R.dimen.date_picker_item_period_counter_text_size)
        }
    }

    private val counterBackground by lazy {
        ContextCompat.getDrawable(context, R.drawable.date_picker_item_period_counter_background)!!
    }

    @Px
    private val counterPaddingHorizontal =
        resources.getDimensionPixelSize(R.dimen.date_picker_counter_padding_horizontal)

    /** @SelfDocumented */
    var dayOfMonth: Int? = null
        set(value) {
            field = value
            text = field.toString()
        }

    /** @SelfDocumented */
    var dayOfWeek: Int? = null

    /** @SelfDocumented */
    var counter: String = ""
        set(value) {
            field = value
            counterLayout.configure {
                text = value
            }
            internalLayout()
            invalidate()
        }

    init {
        setWillNotDraw(false)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        internalLayout()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (counter.isNotEmpty()) {
            counterBackground.draw(canvas)
            counterLayout.draw(canvas)
        }
    }

    private fun internalLayout() {
        if (counter.isNotEmpty()) {
            counterLayout.layout(
                measuredWidth - 2 * counterPaddingHorizontal - counterLayout.width,
                measuredHeight - counterLayout.height
            )
            counterBackground.setBounds(
                counterLayout.left - counterPaddingHorizontal,
                counterLayout.top,
                counterLayout.right + counterPaddingHorizontal,
                counterLayout.bottom
            )
        }
    }
}