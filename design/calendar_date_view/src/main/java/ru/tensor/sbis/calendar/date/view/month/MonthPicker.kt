package ru.tensor.sbis.calendar.date.view.month

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.FrameLayout
import io.reactivex.disposables.CompositeDisposable
import org.joda.time.LocalDate
import ru.tensor.sbis.calendar.date.databinding.MonthPickerBinding
import ru.tensor.sbis.calendar.date.utils.asMonthString
import ru.tensor.sbis.calendar.date.utils.getCalendarDateViewStyleId
import ru.tensor.sbis.calendar.date.utils.plusAssign
import ru.tensor.sbis.design.utils.ProviderSubject
import ru.tensor.sbis.design.utils.delegatePropertyMT
import java.util.*
import kotlin.properties.Delegates.observable

/**
 * Пикер месяца в календаре.
 *
 * @author ae.noskov
 */
internal class MonthPicker
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : FrameLayout(context, attrs, defStyle) {

    internal val binding: MonthPickerBinding
    var maxTextSize: Float = 100f
        set(value) {
            field = value
            if (value < binding.monthText.textSize) {
                binding.monthText.setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
            }
        }

    init {
        context.theme.applyStyle(context.getCalendarDateViewStyleId(), true)
        binding = MonthPickerBinding.inflate(LayoutInflater.from(context), this, true)
    }

    private val disposer = CompositeDisposable()

    /** Подписка на текущий месяц. */
    var currentMonth by observable(null as ProviderSubject<LocalDate>?){ _, _, newValue ->
        disposer.clear()
        newValue?.let { month ->
            disposer += month.subscribe{ date ->
                binding.monthView.month = date
                binding.monthText.text = date.asMonthString()
                    .replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                    }
            }
        }
    }

    /** Настройка режима выбора дат. */
    var baseProperties by delegatePropertyMT(binding.monthView::baseProperties)

    /** Выбранные даты. */
    var selectedDates by delegatePropertyMT(binding.monthView::selectedDates)

    /** Слушатель изменения выбранных дат. */
    var onSelectionChangedListener by delegatePropertyMT(binding.monthView::onSelectionChangedListener)

    init {
        binding.rightButton.setOnClickListener {
            currentMonth?.let {
                it.onNext(it.value!!.plusMonths(1))
            }
        }
        binding.leftButton.setOnClickListener {
            currentMonth?.let {
                it.onNext(it.value!!.minusMonths(1))
            }
        }
    }

    /** @SelfDocumented */
    var isSelectorEnabled: Boolean = true
        set(value) {
            field = value
            binding.monthView.selector.isEnabled = value
        }
}