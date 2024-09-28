package ru.tensor.sbis.calendar.date.view.month

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.withStyledAttributes
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import ru.tensor.sbis.calendar.date.R
import ru.tensor.sbis.calendar.date.databinding.HidableMonthPickerViewBinding
import ru.tensor.sbis.calendar.date.utils.asDateString
import ru.tensor.sbis.calendar.date.utils.plusAssign
import ru.tensor.sbis.calendar.date.view.DatePickerLifeData
import ru.tensor.sbis.calendar.date.view.HidableMonthPickerExpandMode
import ru.tensor.sbis.design.utils.delegatePropertyMT
import timber.log.Timber
import kotlin.properties.Delegates

/**
 * Вьюшка с календарем, с возможностью скрытия в текстовое поле.
 */
class HidableMonthPicker @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    FrameLayout(context, attrs, defStyle) {
    private val disposer = CompositeDisposable()

    val eventAdditionalTimeString: TextView get() = binding.calendarDateViewMonthPickerDateString.eventAdditionalTimeString
    val eventDateStartToDateFinish: TextView get() = binding.calendarDateViewMonthPickerDateString.eventDateStartToDateFinish
    val collapsedString: View get() = binding.calendarDateViewMonthPickerDateString.collapsedPicker
    var stringAutoSetter = true
    var maxTextSize: Float = 100f
        set(value) {
            field = value
            if (value < eventAdditionalTimeString.textSize) {
                eventAdditionalTimeString.setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
                eventDateStartToDateFinish.setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
                binding.monthPicker.maxTextSize = value
            }
        }

    private val binding: HidableMonthPickerViewBinding

    init {
        binding = HidableMonthPickerViewBinding.inflate(LayoutInflater.from(context), this, true)
        binding.calendarDateViewMonthPickerDateString.collapsedPicker.setOnClickListener {
            lifeData?.hide?.onNext(false)
        }
        context.withStyledAttributes(attrs, R.styleable.HidableMonthPicker, defStyle) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveAttributeDataForStyleable(
                    context,
                    R.styleable.HidableMonthPicker,
                    attrs,
                    this,
                    defStyle,
                    0
                )
            }
            maxTextSize = getDimension(R.styleable.HidableMonthPicker_maxTextSize, maxTextSize)
        }
    }

    var baseProperties by delegatePropertyMT(binding.monthPicker::baseProperties)

    var lifeData by Delegates.observable(null as DatePickerLifeData?) { _, _, newVal ->
        newVal?.apply {
            disposer.clear()
            disposer += hide.observable
                .filter {
                    if (expandMode == HidableMonthPickerExpandMode.ALWAYS_EXPANDED) !it
                    else true
                }
                .map { if (it) 0 else 1 }
                .subscribe {
                    binding.periodDayFlipper.displayedChild = it
                }
            monthPicker.currentMonth = month

            if (expandMode == HidableMonthPickerExpandMode.ALWAYS_EXPANDED) hide.onNext(false)

            disposer += selectedDates.observable.subscribe(
                {
                    it?.let { (startDate, endDate) ->
                        if (stringAutoSetter)
                            binding.calendarDateViewMonthPickerDateString.eventDateStartToDateFinish.text =
                                it.asDateString()

                        if (monthPicker.selectedDates != it)
                            monthPicker.selectedDates = it
                    }
                },
                { Timber.w(it) }
            )
            monthPicker.onSelectionChangedListener = {
                hide.onNext(true)
                if (selectedDates.value != it) {
                    selectedDates.onNext(it)
                    selectedDatesWithNoTimePickerSubscribe.onNext(it)
                }
            }

            disposer += data.observeOn(AndroidSchedulers.mainThread()).subscribe {
                monthPicker.binding.monthView.monthAdapter.data = it
            }

        }
    }

    internal val monthPicker: MonthPicker by lazy { binding.monthPicker }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        disposer.clear()
    }

    fun setMonthExpandable(isExpandable: Boolean) {
        binding.calendarDateViewMonthPickerDateString.collapsedPicker.isClickable = isExpandable
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        monthPicker.isSelectorEnabled = enabled
    }
}
