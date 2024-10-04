package ru.tensor.sbis.design.period_picker.view.short_period_picker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.design.period_picker.R
import ru.tensor.sbis.design.period_picker.databinding.ShortPeriodPickerFragmentBinding
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerFeature.Companion.periodPickerRequestKey
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerFeature.Companion.periodPickerResultKey
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.decl.SbisShortPeriodPickerVisualParams
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_ANCHOR_DATE
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_DISPLAYED_RANGE
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_END_VALUE
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_IS_BOTTOM_POSITION
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_IS_ENABLED
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_REQUEST_KEY
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_RESULT_KEY
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_START_VALUE
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_VISUAL_PARAMS
import ru.tensor.sbis.design.period_picker.view.listener.CalendarGlobalLayoutListener
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.ShortPeriodPickerItem
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.ShortPeriodPickerListAdapter
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.holders.BaseViewHolder
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.utils.getPeriodPickerItems
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.utils.getShortPeriodPickerSelection
import ru.tensor.sbis.design.period_picker.view.short_period_picker.listener.ShortPeriodPickerScrollListener
import ru.tensor.sbis.design.period_picker.view.short_period_picker.models.PeriodPickerParams
import ru.tensor.sbis.design.period_picker.view.utils.MAX_DATE
import ru.tensor.sbis.design.period_picker.view.utils.MIN_DATE
import ru.tensor.sbis.design.period_picker.view.utils.getCalendarFromMillis
import ru.tensor.sbis.design.period_picker.view.utils.getDateToScroll
import ru.tensor.sbis.design.period_picker.view.utils.getHalfYear
import ru.tensor.sbis.design.period_picker.view.utils.getParamsFromArgs
import ru.tensor.sbis.design.period_picker.view.utils.getQuarter
import ru.tensor.sbis.design.period_picker.view.utils.getYearKey
import ru.tensor.sbis.design.period_picker.view.utils.getYearRange
import ru.tensor.sbis.design.period_picker.view.utils.isSbisContainerParent
import ru.tensor.sbis.design.period_picker.view.utils.month
import ru.tensor.sbis.design.period_picker.view.utils.removeGlobalLayoutListener
import ru.tensor.sbis.design.period_picker.view.utils.setGlobalLayoutListener
import ru.tensor.sbis.design.period_picker.view.utils.updateContentLayoutParams
import ru.tensor.sbis.design.period_picker.view.utils.updateStateRestorationPolicy
import ru.tensor.sbis.design.period_picker.view.utils.year
import ru.tensor.sbis.design.theme.global_variables.InlineHeight
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design_dialogs.dialogs.container.Container
import timber.log.Timber
import java.util.Calendar

/**
 * Фрагмент для компонента Быстрый выбор периода.
 *
 * @author mb.kruglova
 */
internal class ShortPeriodPickerFragment : BaseFragment() {

    private lateinit var binding: ShortPeriodPickerFragmentBinding
    private val calendarLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    private val calendarAdapter: ShortPeriodPickerListAdapter
        get() = binding.shortPeriodPickerList.adapter as ShortPeriodPickerListAdapter

    private lateinit var calendar: RecyclerView

    private var periodPickerParams: PeriodPickerParams? = null
    private var startDate: Calendar? = null
    private var endDate: Calendar? = null
    private var isEnabled = true
    private lateinit var displayedRange: SbisPeriodPickerRange
    private var isBottomPosition = false
    private var anchorDate: Calendar? = null

    private lateinit var visualParams: SbisShortPeriodPickerVisualParams
    private var calendarListener: CalendarGlobalLayoutListener? = null
    private var calendarScrollListener: ShortPeriodPickerScrollListener? = null

    private var startCalendar = MIN_DATE.year
    private var endCalendar = MAX_DATE.year

    private lateinit var requestKey: String
    private lateinit var resultKey: String

    private var currentScrollDate: Calendar? = null

    companion object {
        private const val CURRENT_SCROLL_DATE = "CURRENT_SCROLL_DATE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setValuesFromArgs()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.short_period_picker_fragment, container, false).apply {
            if (this@ShortPeriodPickerFragment.isSbisContainerParent(true)) {
                updateContentLayoutParams()
            }
        }
        binding = ShortPeriodPickerFragmentBinding.bind(view)
        calendar = binding.shortPeriodPickerList
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedInstanceState?.let {
            currentScrollDate = getCalendarFromMillis(it.getLong(CURRENT_SCROLL_DATE))
        }

        calendarListener =
            this.setGlobalLayoutListener(true, calendar, isBottomPosition) { isContainerShown ->
                calendar.removeGlobalLayoutListener(calendarListener) {
                    if (savedInstanceState == null) {
                        performScroll(isContainerShown)
                    } else {
                        updateStateRestorationPolicy(calendarAdapter)
                        updateUI()
                    }
                }
            }

        setPeriodPickerList()
        setPeriodPickerHeader()
    }

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val inflater = super.onGetLayoutInflater(savedInstanceState)
        return inflater.cloneInContext(
            ThemeContextBuilder(
                requireContext(),
                R.attr.sbisPeriodPickerViewTheme,
                R.style.SbisPeriodPickerViewTheme
            ).build()
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val currentDate = try {
            getYearKey(getDisplayedYear())
        } catch (nfe: NumberFormatException) {
            Calendar.getInstance()
        }
        outState.putLong(CURRENT_SCROLL_DATE, currentDate.timeInMillis)
    }

    override fun onDestroy() {
        calendarScrollListener?.let { calendar.removeOnScrollListener(it) }
        super.onDestroy()
    }

    /** Настроить значения полей из аргументов. */
    private fun setValuesFromArgs() {
        visualParams = getParamsFromArgs(
            arguments,
            ARG_VISUAL_PARAMS,
            SbisShortPeriodPickerVisualParams::class.java
        ) as? SbisShortPeriodPickerVisualParams ?: SbisShortPeriodPickerVisualParams()

        startDate = getCalendarFromMillis(arguments?.getLong(ARG_START_VALUE, MIN_DATE.timeInMillis))
        endDate = getCalendarFromMillis(arguments?.getLong(ARG_END_VALUE, MIN_DATE.timeInMillis))

        isEnabled = arguments?.getBoolean(ARG_IS_ENABLED) ?: true

        displayedRange = getParamsFromArgs(
            arguments,
            ARG_DISPLAYED_RANGE,
            SbisPeriodPickerRange::class.java
        ) as? SbisPeriodPickerRange ?: SbisPeriodPickerRange()

        startCalendar = displayedRange.startYear
        endCalendar = displayedRange.endYear

        isBottomPosition = arguments?.getBoolean(ARG_IS_BOTTOM_POSITION) ?: false

        anchorDate =
            getCalendarFromMillis(arguments?.getLong(ARG_ANCHOR_DATE, MIN_DATE.timeInMillis))

        requestKey = arguments?.getString(ARG_REQUEST_KEY) ?: periodPickerRequestKey

        resultKey = arguments?.getString(ARG_RESULT_KEY) ?: periodPickerResultKey
    }

    /** Настроить шапку периодов. */
    private fun setPeriodPickerHeader() {
        if (visualParams.isYearMode()) {
            binding.shortPeriodPickerHeader.visibility = View.GONE
        } else {
            if (visualParams.chooseYears) {
                binding.shortPeriodPickerHeaderTitle.setOnClickListener {
                    val year = binding.shortPeriodPickerHeaderTitle.text.toString().toInt()
                    closeFragmentWithResult(getYearRange(year))
                }
            }

            if (!visualParams.arrowVisible) {
                binding.shortPeriodPickerHeaderButtonLeft.visibility = View.GONE
                binding.shortPeriodPickerHeaderButtonRight.visibility = View.GONE
            }

            binding.shortPeriodPickerHeaderButtonLeft.setOnClickListener {
                var displayedYear = getDisplayedYear() - 1
                if (displayedYear < displayedRange.startYear) {
                    displayedYear = displayedRange.startYear
                }
                updateScrollPosition(displayedYear)
            }

            binding.shortPeriodPickerHeaderButtonRight.setOnClickListener {
                var displayedYear = getDisplayedYear() + 1
                if (displayedYear > displayedRange.endYear) {
                    displayedYear = displayedRange.endYear
                }
                updateScrollPosition(displayedYear)
            }
        }
    }

    /** Настроить список периодов. */
    private fun setPeriodPickerList() {
        calendar.layoutManager = calendarLayoutManager

        startDate?.let { startDate ->
            endDate?.let { endDate ->
                periodPickerParams = getShortPeriodPickerSelection(startDate, endDate)
            }
        }

        calendar.adapter = ShortPeriodPickerListAdapter(
            { item, position, range -> updatePeriodSelection(item, position, range) },
            periodPickerParams,
            isEnabled,
            displayedRange
        )

        calendarScrollListener = ShortPeriodPickerScrollListener(binding, displayedRange.startYear)

        calendarAdapter.updateData(
            getPeriodPickerItems(visualParams, startCalendar, endCalendar)
        )

        calendar.post {
            calendarListener?.let { calendar.viewTreeObserver.addOnGlobalLayoutListener(it) }
            calendarScrollListener?.let { calendar.addOnScrollListener(it) }
        }
    }

    /**
     * Обновить позицию прокрутки.
     */
    private fun updateScrollPosition(year: Int) {
        calendarLayoutManager.scrollToPositionWithOffset(getScrollPosition(year), 0)
    }

    /**
     * Обновить выделение периода.
     */
    private fun updatePeriodSelection(item: ShortPeriodPickerItem, position: Int, range: SbisPeriodPickerRange) {
        if (item is ShortPeriodPickerItem.YearItem) {
            val holder = calendar.findViewHolderForAdapterPosition(position + 1) as? BaseViewHolder<*>
            holder?.setSelection()
        }

        periodPickerParams?.let {
            if (item.year != it.startDate.get(Calendar.YEAR)) {
                val previousSelectionPos = getScrollPosition(it.startDate.get(Calendar.YEAR))

                val holder = calendar.findViewHolderForAdapterPosition(previousSelectionPos) as? BaseViewHolder<*>
                holder?.resetSelection()
            }
        }

        closeFragmentWithResult(range)
    }

    private fun closeFragmentWithResult(range: SbisPeriodPickerRange) {
        parentFragment?.setFragmentResult(requestKey, bundleOf(resultKey to range))

        when (parentFragment) {
            is Container.Closeable -> {
                // Если закрывать шторку как DialogFragment, то не будет анимации у шторки.
                (parentFragment as Container.Closeable).closeContainer()
            }

            is DialogFragment -> {
                (parentFragment as DialogFragment).dismiss()
            }

            else -> {
                Timber.e("ShortPeriodPicker parent fragment is undefined!")
            }
        }
    }

    /** Получить позицию для подскроллинга. */
    private fun getScrollPosition(year: Int): Int {
        return if (visualParams.isYearMode()) year - startCalendar else 2 * (year - startCalendar) + 1
    }

    /** Выполнить скроллирование. */
    private fun performScroll(isContainerShown: Boolean): Boolean {
        return if (isBottomPosition) {
            scrollToBottom(getDateToScroll(endDate), isContainerShown)
        } else {
            scrollByDefault(getDateToScroll(startDate), isContainerShown)
        }
    }

    /** Выполнить скроллирование по умолчанию. */
    private fun scrollByDefault(date: Calendar, isContainerShown: Boolean): Boolean {
        val position = getScrollPosition(date.year)
        calendarLayoutManager.scrollToPositionWithOffset(position, 0)

        val lastVisiblePos = calendarLayoutManager.findLastCompletelyVisibleItemPosition()
        val firstVisiblePos = calendarLayoutManager.findFirstVisibleItemPosition()
        // Проверяем, что прокрутка календаря корректна.
        // Если календарь ограничен, то скроллирование может не произойти.
        if (isContainerShown && (position == lastVisiblePos || firstVisiblePos == position)) {
            currentScrollDate = date
            // Календарь отображается после скроллирования,
            // чтобы пользователь не видел скачок от нулевой позиции к необходимой.
            return updateUI()
        }

        return false
    }

    /** Выполнить скроллирование текущие даты/выбранного периода к низу. */
    private fun scrollToBottom(date: Calendar, isContainerShown: Boolean): Boolean {
        with(binding.shortPeriodPickerList) {
            val calendarHeight = this.measuredHeight

            val height = InlineHeight.X2L.getDimenPx(this.context) * getMultiplicity(date)
            val offset = calendarHeight - height
            if (offset != 0) {
                val position = getScrollPosition(date.year)
                calendarLayoutManager.scrollToPositionWithOffset(position, offset)

                val lastVisiblePos = calendarLayoutManager.findLastVisibleItemPosition()
                val lastPos = calendarAdapter.getLastPosition()
                // Проверяем, что прокрутка календаря корректна.
                // Если календарь ограничен, то скроллирование может не произойти.
                if (isContainerShown && (lastVisiblePos == lastPos || lastVisiblePos == position)) {
                    currentScrollDate = date
                    // Календарь отображается после скроллирования,
                    // чтобы пользователь не видел скачок от нулевой позиции к необходимой.
                    return updateUI()
                }
            }
        }

        return false
    }

    /** @SelfDocumented */
    private fun setCalendarVisibility() {
        if (!binding.shortPeriodPickerList.isVisible) binding.shortPeriodPickerList.isVisible = true
    }

    /** @SelfDocumented */
    private fun updateHeaderTitleText() {
        val pos = calendarLayoutManager.findFirstVisibleItemPosition()
        if (pos != -1) {
            val date = calendarAdapter.getItemByPosition(pos).year
            binding.shortPeriodPickerHeaderTitle.text = date.toString()
        }
    }

    /** @SelfDocumented */
    private fun getDateToScroll(date: Calendar?): Calendar {
        val scrollDate = anchorDate ?: currentScrollDate ?: date

        return getDateToScroll(scrollDate, displayedRange.start, displayedRange.end, isBottomPosition)
    }

    /** @SelfDocumented */
    private fun getMultiplicity(date: Calendar): Int {
        return when {
            visualParams.chooseMonths -> date.month + 1
            visualParams.chooseQuarters -> date.getQuarter()
            visualParams.chooseHalfYears -> date.getHalfYear()
            visualParams.chooseYears -> 1
            else -> 0
        }
    }

    /** @SelfDocumented */
    private fun getDisplayedYear(): Int {
        return binding.shortPeriodPickerHeaderTitle.text.toString().toInt()
    }

    /** @SelfDocumented */
    private fun updateUI(): Boolean {
        updateHeaderTitleText()
        setCalendarVisibility()
        return true
    }
}