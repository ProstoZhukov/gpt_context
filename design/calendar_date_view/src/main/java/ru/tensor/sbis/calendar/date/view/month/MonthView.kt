package ru.tensor.sbis.calendar.date.view.month

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import org.joda.time.LocalDate
import ru.tensor.sbis.calendar.date.view.DatePickerBaseProperties
import ru.tensor.sbis.calendar.date.view.EmptySelectedDayDrawable
import ru.tensor.sbis.calendar.date.view.ItemSpacing
import ru.tensor.sbis.calendar.date.view.month.adapter.MonthAdapter
import ru.tensor.sbis.calendar.date.view.selector.MultipleSelector
import ru.tensor.sbis.calendar.date.view.selector.NoSelector
import ru.tensor.sbis.calendar.date.view.selector.OneSelector
import ru.tensor.sbis.calendar.date.view.selector.OneSelectorWithSeveralDays
import ru.tensor.sbis.calendar.date.view.selector.Selector
import ru.tensor.sbis.design.utils.delegatePropertyMT
import java.util.Calendar.DAY_OF_WEEK
import kotlin.properties.Delegates

/**
 * Компонент выбора месяца в календаре.
 *
 * @author ae.noskov
 */
class MonthView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {

    internal val monthAdapter = MonthAdapter().also { adapter = it }

    /** Селектор для реализации поведения при выборе. */
    var selector: Selector = NoSelector()

    /** Слушатель изменения выбранного периода. */
    var onSelectionChangedListener: (Pair<LocalDate?, LocalDate?>) -> Unit = {}

    /** Основные настройки компонента выбора дат. */
    var baseProperties = DatePickerBaseProperties(selectedDayDrawable = EmptySelectedDayDrawable())
        set(value) {
            field = value
            mode = value.mode
            monthAdapter.selectedDayDrawable = value.selectedDayDrawable
        }

    /** Режим выбора периода. */
    var mode by Delegates.observable(DatePickerBaseProperties.NO_SELECTOR) { _, _, newValue ->
        val previouslySelectedDates = selector.selectedDates
        selector = when (newValue) {
            DatePickerBaseProperties.ONE_SELECTOR      -> OneSelector(monthAdapter) { onSelectionChangedListener.invoke(it) }
            DatePickerBaseProperties.MULTIPLE_SELECTOR -> MultipleSelector(monthAdapter) { onSelectionChangedListener.invoke(it) }
            DatePickerBaseProperties.ONE_SELECTOR_WITH_SEVERAL_DAYS -> OneSelectorWithSeveralDays(monthAdapter) {
                onSelectionChangedListener.invoke(it)
            }
            else                                   -> NoSelector()
        }.also {
            it.selectedDates = previouslySelectedDates
            it.isEnabled = selector.isEnabled
        }
        monthAdapter.selector = selector
    }

    /** Выбранный месяц. */
    var month by delegatePropertyMT(monthAdapter::month)

    /** Выбранные даты. */
    var selectedDates by delegatePropertyMT({ selector.selectedDates }, { selector.selectedDates = it })

    init {
        layoutManager = NonScrollableGridLayoutManager(context, DAY_OF_WEEK)
        (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        addItemDecoration(ItemSpacing(context))
    }
}

/**
 * GridLayoutManager без скролла.
 *
 * @author ae.noskov
 */
class NonScrollableGridLayoutManager internal constructor(context: Context, span: Int) : GridLayoutManager(context, span) {
    override fun canScrollVertically(): Boolean = false
    override fun canScrollHorizontally(): Boolean = false
}