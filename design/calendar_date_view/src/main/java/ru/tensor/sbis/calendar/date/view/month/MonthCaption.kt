package ru.tensor.sbis.calendar.date.view.month

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.calendar.date.R
import ru.tensor.sbis.calendar.date.databinding.CalendarDateViewDateStringLayoutBinding

/**
 *
 *
 * @author ae.noskov
 */
class MonthCaption
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : FrameLayout(context, attrs, defStyle) {

    internal val binding: CalendarDateViewDateStringLayoutBinding
    var maxTextSize: Float = 100f
        set(value) {
            field = value
            if (value < eventAdditionalTimeString.textSize) {
                eventAdditionalTimeString.setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
                eventDateStartToDateFinish.setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
            }
        }

    val eventAdditionalTimeString: TextView
        get() = binding.eventAdditionalTimeString
    val eventDateStartToDateFinish: TextView
        get() = binding.eventDateStartToDateFinish

    init {
        binding = CalendarDateViewDateStringLayoutBinding.inflate(LayoutInflater.from(context), this, true)
        context.withStyledAttributes(attrs, R.styleable.MonthCaption, defStyle) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveAttributeDataForStyleable(
                    context,
                    R.styleable.MonthCaption,
                    attrs,
                    this,
                    defStyle,
                    0
                )
            }
            maxTextSize = getDimension(R.styleable.MonthCaption_maxTextSize, maxTextSize)
        }
    }
}