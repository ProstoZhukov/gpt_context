package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.design.period_picker.R
import ru.tensor.sbis.design.period_picker.databinding.PeriodPickerYearModeFragmentBinding
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerFeature.Companion.periodPickerRequestKey
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerFeature.Companion.periodPickerResultKey
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerSelectionType
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_ANCHOR_DATE
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_DISPLAYED_RANGE
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_END_VALUE
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_IS_BOTTOM_POSITION
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_IS_ENABLED
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_PRESET_END_VALUE
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_PRESET_START_VALUE
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_REQUEST_KEY
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_RESULT_KEY
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_SELECTION_TYPE
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_START_VALUE
import ru.tensor.sbis.design.period_picker.view.models.CalendarStorage
import ru.tensor.sbis.design.period_picker.view.listener.CalendarGlobalLayoutListener
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.delegate.CalendarRequestResultKeysDelegate
import ru.tensor.sbis.design.period_picker.view.utils.getCalendarFromMillis
import ru.tensor.sbis.design.period_picker.view.utils.getParamsFromArgs
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.delegate.CalendarUpdateDelegate
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.listener.CalendarFlingListener
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.YearModePeriodPickerAdapter
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.holders.YearLabelViewHolder
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.holders.YearModePeriodPickerViewHolder
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.di.DaggerYearModePeriodPickerComponent
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.header.YearLabelAdapter
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.header.YearLabelLayoutManager
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.listener.YearModeScrollListener
import ru.tensor.sbis.design.period_picker.view.utils.MIN_DATE
import ru.tensor.sbis.design.period_picker.view.utils.getDateToScroll
import ru.tensor.sbis.design.period_picker.view.utils.getQuarter
import ru.tensor.sbis.design.period_picker.view.utils.removeGlobalLayoutListener
import ru.tensor.sbis.design.period_picker.view.utils.setGlobalLayoutListener
import ru.tensor.sbis.design.period_picker.view.utils.updateStateRestorationPolicy
import ru.tensor.sbis.design.period_picker.view.utils.year
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import java.util.Calendar

/**
 * Фрагмент Большой выбор периода в режиме Год.
 *
 * @author mb.kruglova
 */
internal class YearModePeriodPickerFragment :
    BaseFragment(),
    CalendarUpdateDelegate,
    CalendarRequestResultKeysDelegate {

    private var startDate: Calendar? = null
    private var endDate: Calendar? = null
    private var isEnabled = true
    private lateinit var selectionType: SbisPeriodPickerSelectionType
    private lateinit var displayedRange: SbisPeriodPickerRange
    private var isBottomPosition = false
    private var singleOffset = 0
    private var presetStartDate: Calendar? = null
    private var presetEndDate: Calendar? = null
    private var anchorDate: Calendar? = null
    override var requestKey = periodPickerRequestKey
    override var resultKey = periodPickerResultKey

    private lateinit var binding: PeriodPickerYearModeFragmentBinding
    private var calendarListener: CalendarGlobalLayoutListener? = null
    private var calendarScrollListener: YearModeScrollListener? = null

    private val yearsLayoutManager: LinearLayoutManager?
        get() = binding.calendar.layoutManager as? LinearLayoutManager

    private val yearLabelLayoutManager: LinearLayoutManager?
        get() = binding.yearLabels.layoutManager as? LinearLayoutManager

    private val yearsAdapter: YearModePeriodPickerAdapter
        get() = binding.calendar.adapter as YearModePeriodPickerAdapter

    private val yearLabelAdapter: YearLabelAdapter
        get() = binding.yearLabels.adapter as YearLabelAdapter

    companion object {

        /** Максимальное количество ViewHolder, которое будет храниться в пуле перед удалением. */
        private const val MAX_RECYCLED_YEAR_VIEWS = 20

        private const val SINGLE_OFFSET = "SINGLE_OFFSET"

        fun create(
            startValue: Calendar? = null,
            endValue: Calendar? = null,
            isEnabled: Boolean = true,
            selectionType: SbisPeriodPickerSelectionType,
            displayedRange: SbisPeriodPickerRange,
            isBottomPosition: Boolean = false,
            presetStartValue: Calendar? = null,
            presetEndValue: Calendar? = null,
            anchorDate: Calendar? = null,
            requestKey: String = periodPickerRequestKey,
            resultKey: String = periodPickerResultKey
        ) = YearModePeriodPickerFragment().apply {
            withArgs {
                startValue?.timeInMillis?.let { putLong(ARG_START_VALUE, it) }
                endValue?.timeInMillis?.let { putLong(ARG_END_VALUE, it) }
                putBoolean(ARG_IS_ENABLED, isEnabled)
                putParcelable(ARG_SELECTION_TYPE, selectionType)
                putParcelable(ARG_DISPLAYED_RANGE, displayedRange)
                putBoolean(ARG_IS_BOTTOM_POSITION, isBottomPosition)
                presetStartValue?.timeInMillis?.let { putLong(ARG_PRESET_START_VALUE, it) }
                presetEndValue?.timeInMillis?.let { putLong(ARG_PRESET_END_VALUE, it) }
                anchorDate?.timeInMillis?.let {
                    putLong(
                        SbisPeriodPickerFeatureImpl.ARG_ANCHOR_DATE,
                        it
                    )
                }
                putString(ARG_REQUEST_KEY, requestKey)
                putString(ARG_RESULT_KEY, resultKey)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setValuesFromArgs()
        DaggerYearModePeriodPickerComponent.factory().create(
            viewFactory = {
                YearModePeriodPickerViewImpl(
                    PeriodPickerYearModeFragmentBinding.bind(it)
                )
            },
            startDate,
            endDate,
            selectionType,
            displayedRange,
            presetStartDate,
            presetEndDate,
            anchorDate,
            isBottomPosition
        ).injector().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.period_picker_year_mode_fragment, container, false)
        binding = PeriodPickerYearModeFragmentBinding.bind(view)
        return view
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        singleOffset = savedInstanceState?.getInt(SINGLE_OFFSET) ?: 0
        calendarListener = this.setGlobalLayoutListener(
            false,
            binding.calendar,
            isBottomPosition
        ) { isContainerShown ->
            binding.calendar.removeGlobalLayoutListener(calendarListener) {
                if (savedInstanceState == null) {
                    performScroll(isContainerShown)
                } else {
                    updateStateRestorationPolicy(yearsAdapter)
                    updateStateRestorationPolicy(yearLabelAdapter)
                    setCalendarVisibility()
                    true
                }
            }
        }

        calendarListener?.let { binding.calendar.viewTreeObserver.addOnGlobalLayoutListener(it) }

        setYearHeaderRecyclerView()
        setCalendarRecyclerView()
    }

    override fun updateScrollPosition(scrollDate: Calendar?) {
        performScroll(anchorDate ?: scrollDate ?: Calendar.getInstance(), scrollDate != null)
    }

    override fun reloadCalendar(newData: CalendarStorage, addToEnd: Boolean) {
        (binding.calendar.adapter as? YearModePeriodPickerAdapter)?.reload(newData.getYearModeCalendar(), addToEnd)
        calendarScrollListener?.completeReloading()
    }

    override fun updateSelection(storage: CalendarStorage) {
        (binding.calendar.adapter as? YearModePeriodPickerAdapter)?.update(storage.getYearModeCalendar())
        (binding.yearLabels.adapter as? YearLabelAdapter)?.update(storage.yearLabelsGrid)
    }

    override fun resetSelection() {
        startDate = null
        endDate = null

        resetSelectionPeriod()
        updateScrollPosition()
    }

    override fun setPresetSelection(dateFrom: Calendar, dateTo: Calendar) {
        resetSelectionPeriod()
        yearsAdapter.listener?.onSelectPeriod(dateFrom, dateTo)
        performScroll(
            if (isBottomPosition) {
                anchorDate ?: dateTo
            } else {
                anchorDate ?: dateFrom
            }
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SINGLE_OFFSET, singleOffset)
    }

    override fun onDestroy() {
        calendarListener?.let { binding.calendar.viewTreeObserver.removeOnGlobalLayoutListener(calendarListener) }
        calendarScrollListener?.let { binding.calendar.removeOnScrollListener(it) }
        super.onDestroy()
    }

    /** Настроить значения полей из аргументов. */
    private fun setValuesFromArgs() {
        startDate = getCalendarFromMillis(arguments?.getLong(ARG_START_VALUE, MIN_DATE.timeInMillis))
        endDate = getCalendarFromMillis(arguments?.getLong(ARG_END_VALUE, MIN_DATE.timeInMillis))

        isEnabled = arguments?.getBoolean(ARG_IS_ENABLED) ?: true

        selectionType = getParamsFromArgs(
            arguments,
            ARG_SELECTION_TYPE,
            SbisPeriodPickerSelectionType::class.java
        ) as? SbisPeriodPickerSelectionType ?: SbisPeriodPickerSelectionType.Single

        displayedRange = getParamsFromArgs(
            arguments,
            ARG_DISPLAYED_RANGE,
            SbisPeriodPickerRange::class.java
        ) as? SbisPeriodPickerRange ?: SbisPeriodPickerRange()

        isBottomPosition = arguments?.getBoolean(ARG_IS_BOTTOM_POSITION) ?: false

        presetStartDate = getCalendarFromMillis(arguments?.getLong(ARG_PRESET_START_VALUE, MIN_DATE.timeInMillis))
        presetEndDate = getCalendarFromMillis(arguments?.getLong(ARG_PRESET_END_VALUE, MIN_DATE.timeInMillis))

        anchorDate = getCalendarFromMillis(arguments?.getLong(ARG_ANCHOR_DATE, MIN_DATE.timeInMillis))

        requestKey = arguments?.getString(ARG_REQUEST_KEY) ?: periodPickerRequestKey

        resultKey = arguments?.getString(ARG_RESULT_KEY) ?: periodPickerResultKey
    }

    /** Настроить RecyclerView для годов в шапке. */
    private fun setYearHeaderRecyclerView() {
        with(binding.yearLabels) {
            layoutManager = YearLabelLayoutManager(binding.root.context)
            isNestedScrollingEnabled = false
            setHasFixedSize(true)
        }

        binding.yearLabels.post {
            yearLabelAdapter.setEnabled(isEnabled)
        }

        binding.leftButton.setOnClickListener {
            val layoutManager = yearLabelLayoutManager
            layoutManager?.let { manager ->
                val firstItemPosition = manager.findFirstVisibleItemPosition()
                if (firstItemPosition > 0) {
                    performReloadAndScroll(firstItemPosition - 1, false)
                }
            }
        }

        binding.rightButton.setOnClickListener {
            val layoutManager = yearLabelLayoutManager
            layoutManager?.let { manager ->
                val lastItemPosition = manager.findLastVisibleItemPosition()
                binding.yearLabels.adapter?.let {
                    if (lastItemPosition < it.itemCount - 1) {
                        performReloadAndScroll(lastItemPosition + 1, true)
                    }
                }
            }
        }
    }

    /** Настроить RecyclerView для календаря. */
    private fun setCalendarRecyclerView() {
        with(binding.calendar) {
            layoutManager = LinearLayoutManager(context)

            recycledViewPool.setMaxRecycledViews(
                YearModePeriodPickerViewHolder.ITEM_TYPE,
                MAX_RECYCLED_YEAR_VIEWS
            )
            recycledViewPool.setMaxRecycledViews(
                YearLabelViewHolder.ITEM_TYPE,
                MAX_RECYCLED_YEAR_VIEWS
            )
            setItemViewCacheSize(MAX_RECYCLED_YEAR_VIEWS * 2)
            setHasFixedSize(true)

            // замедление скролла - быстрый скролл листает рывками
            onFlingListener = CalendarFlingListener(this)
        }

        binding.calendar.post {
            yearsAdapter.setEnabled(isEnabled)

            calendarScrollListener = YearModeScrollListener(binding)
            calendarScrollListener?.let { binding.calendar.addOnScrollListener(it) }
        }
    }

    /** Выполнить скроллирование. */
    private fun performScroll(date: Calendar, isOnlyCalendar: Boolean = false) {
        binding.calendar.post {
            yearsLayoutManager?.scrollToPositionWithOffset(
                yearsAdapter.getFirstYearPosition(date.year),
                if (isBottomPosition) {
                    binding.calendar.height - singleOffset * date.getQuarter()
                } else {
                    0
                }
            )
            if (!binding.calendar.isVisible) binding.calendar.isVisible = true
        }

        if (!isOnlyCalendar) {
            binding.yearLabels.post {
                yearLabelLayoutManager?.scrollToPositionWithOffset(
                    yearLabelAdapter.getYearPositionWithShift(date.year),
                    0
                )
                if (!binding.yearLabels.isVisible) binding.yearLabels.isVisible = true
            }
        }
    }

    /** Выполнить скроллирование. */
    private fun performScroll(isContainerShown: Boolean): Boolean {
        return when {
            yearsAdapter.itemCount == 0 || yearLabelAdapter.itemCount == 0 -> false
            isBottomPosition -> scrollToBottom(getDateToScroll(endDate, presetEndDate), isContainerShown)
            else -> scrollByDefault(
                getDateToScroll(startDate, presetStartDate),
                getDateToScroll(endDate, presetEndDate),
                isContainerShown
            )
        }
    }

    /** Выполнить скроллирование по умолчанию. */
    private fun scrollByDefault(date: Calendar, dateForYearLabels: Calendar, isContainerShown: Boolean): Boolean {
        val yearsPosition = yearsAdapter.getFirstYearPosition(date.year)
        yearsLayoutManager?.scrollToPositionWithOffset(yearsPosition, 0)

        val yearLabelPosition = yearLabelAdapter.getYearPositionWithShift(dateForYearLabels.year)
        yearLabelLayoutManager?.scrollToPositionWithOffset(yearLabelPosition, 0)

        val lastVisiblePos = yearsLayoutManager?.findLastCompletelyVisibleItemPosition() ?: -1
        val firstVisiblePos = yearsLayoutManager?.findFirstVisibleItemPosition()

        // Проверяем, что прокрутка календаря корректна.
        // Если календарь ограничен, то скроллирование может не произойти.
        if (isContainerShown && (lastVisiblePos == yearsPosition || firstVisiblePos == yearsPosition)) {
            // Календарь отображается после скроллирования,
            // чтобы пользователь не видел скачок от нулевой позиции к необходимой.
            setCalendarVisibility()
            return true
        }

        return false
    }

    /** Выполнить скроллирование текущие даты/выбранного периода к низу. */
    private fun scrollToBottom(date: Calendar, isContainerShown: Boolean): Boolean {
        with(binding.calendar) {
            val calendarHeight = this.measuredHeight
            // Получаем высоту элемента сетки календаря.
            if (childCount > 0) {
                var calendarItem: ConstraintLayout? = null
                findItem@ for (i in 0 until childCount) {
                    calendarItem = getChildAt(i) as? ConstraintLayout
                    if (calendarItem != null) break@findItem
                }

                if (calendarItem != null && calendarItem.childCount > 0) {
                    val itemHeight = calendarItem.getChildAt(0).height / 4
                    val height = itemHeight * date.getQuarter()
                    val offset = calendarHeight - height
                    if (offset > 0) {
                        val yearsPosition = yearsAdapter.getFirstYearPosition(date.year)
                        yearsLayoutManager?.scrollToPositionWithOffset(yearsPosition, offset)

                        val yearLabelPosition = yearLabelAdapter.getYearPositionWithShift(date.year)
                        yearLabelLayoutManager?.scrollToPositionWithOffset(yearLabelPosition, 0)

                        val lastVisiblePos = yearsLayoutManager?.findLastVisibleItemPosition() ?: -1
                        val lastPos = yearsAdapter.getLastPosition()

                        val firstVisiblePos = yearsLayoutManager?.findFirstVisibleItemPosition() ?: -1
                        val firstVisibleYear = yearsAdapter.getYearByPosition(firstVisiblePos)
                        val prevLimitPosition = yearsAdapter.getYearPosition(firstVisibleYear - 1)

                        // Проверяем, что прокрутка календаря корректна.
                        // Если календарь ограничен, то скроллирование может не произойти.
                        if (
                            isContainerShown && (
                                lastVisiblePos == lastPos ||
                                    lastVisiblePos == yearsPosition ||
                                    prevLimitPosition == -1 && yearsPosition > 0
                                )
                        ) {
                            // Календарь отображается после скроллирования,
                            // чтобы пользователь не видел скачок от нулевой позиции к необходимой.
                            setCalendarVisibility()
                            singleOffset = itemHeight
                            return true
                        }
                    }
                }
            }
        }

        return false
    }

    /** Настроить видимость календаря и его шапки. */
    private fun setCalendarVisibility() {
        if (!binding.calendar.isVisible) binding.calendar.isVisible = true
        if (!binding.yearLabels.isVisible) binding.yearLabels.isVisible = true
    }

    /** @SelfDocumented */
    private fun getDateToScroll(date: Calendar?, presetDate: Calendar?): Calendar {
        val scrollDate = anchorDate ?: date ?: presetDate
        return getDateToScroll(scrollDate, displayedRange.start, displayedRange.end, isBottomPosition)
    }

    /** @SelfDocumented */
    private fun resetSelectionPeriod() {
        if (isEnabled) {
            yearsAdapter.listener?.onResetSelectionPeriod(Calendar.getInstance(), Calendar.getInstance())
        }
    }

    /** @SelfDocumented */
    private fun performReloadAndScroll(position: Int, isNextPage: Boolean) {
        yearLabelAdapter.performCalendarReloading(
            isNextPage,
            yearLabelAdapter.getYearByPosition(position)
        )
        binding.yearLabels.scrollToPosition(position)
    }
}