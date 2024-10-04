package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.GridLayoutManager
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.calendar_date_icon.CalendarDateIcon
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonDrawableIcon
import ru.tensor.sbis.design.period_picker.R
import ru.tensor.sbis.design.period_picker.databinding.PeriodPickerMonthModeFragmentBinding
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCountersRepository
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCustomTheme
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayType
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerFeature.Companion.periodPickerRequestKey
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerFeature.Companion.periodPickerResultKey
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerSelectionType
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_ANCHOR_DATE
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_CUSTOM_VIEW
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_DAY_COUNTERS
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_DAY_CUSTOM_THEME
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_DAY_TYPE
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_DISPLAYED_RANGE
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_END_VALUE
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_HEIGHT_PERCENT
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_IS_BOTTOM_POSITION
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_IS_COMPACT
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_IS_DAY_AVAILABLE
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_IS_ENABLED
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_IS_FRAGMENT
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_PRESET_END_VALUE
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_PRESET_START_VALUE
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_REQUEST_KEY
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_RESULT_KEY
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_SELECTION_TYPE
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_START_VALUE
import ru.tensor.sbis.design.period_picker.view.listener.CalendarGlobalLayoutListener
import ru.tensor.sbis.design.period_picker.view.models.CalendarStorage
import ru.tensor.sbis.design.period_picker.view.period_picker.big.di.PeriodPickerComponent
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.delegate.CalendarRequestResultKeysDelegate
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.delegate.CalendarUpdateDelegate
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.listener.CalendarFlingListener
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.MonthModePeriodPickerAdapter
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.day.DayView
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.holders.DayViewHolder
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.holders.EmptyViewHolder
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.di.DaggerMonthModePeriodPickerComponent
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.listener.MonthModeScrollListener
import ru.tensor.sbis.design.period_picker.view.utils.MIN_DATE
import ru.tensor.sbis.design.period_picker.view.utils.dayOfWeek
import ru.tensor.sbis.design.period_picker.view.utils.firstDay
import ru.tensor.sbis.design.period_picker.view.utils.getCalendarFromMillis
import ru.tensor.sbis.design.period_picker.view.utils.getDateToScroll
import ru.tensor.sbis.design.period_picker.view.utils.getFormattedMonthLabel
import ru.tensor.sbis.design.period_picker.view.utils.getParamsFromArgs
import ru.tensor.sbis.design.period_picker.view.utils.getPeriod
import ru.tensor.sbis.design.period_picker.view.utils.heightPercent
import ru.tensor.sbis.design.period_picker.view.utils.isSbisContainerParent
import ru.tensor.sbis.design.period_picker.view.utils.mapMonthToStringResId
import ru.tensor.sbis.design.period_picker.view.utils.month
import ru.tensor.sbis.design.period_picker.view.utils.removeGlobalLayoutListener
import ru.tensor.sbis.design.period_picker.view.utils.setGlobalLayoutListener
import ru.tensor.sbis.design.period_picker.view.utils.updateContentLayoutParams
import ru.tensor.sbis.design.period_picker.view.utils.updateStateRestorationPolicy
import ru.tensor.sbis.design.period_picker.view.utils.year
import ru.tensor.sbis.design.theme.global_variables.Elevation
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.extentions.setMargins
import ru.tensor.sbis.design.utils.getDimenPx
import java.io.Serializable
import java.util.Calendar

/**
 * Фрагмент Компактный выбор периода/Большой выбор периода в режиме Месяц.
 *
 * @author mb.kruglova
 */
internal class MonthModePeriodPickerFragment :
    BaseFragment(),
    CalendarUpdateDelegate,
    CalendarRequestResultKeysDelegate {

    private var startDate: Calendar? = null
    private var endDate: Calendar? = null
    private var currentMonth: String? = null
    private var isEnabled = true
    private var customView: ((Context) -> View)? = null
    internal var isCompact: Boolean = true
    private lateinit var selectionType: SbisPeriodPickerSelectionType
    private lateinit var dayType: SbisPeriodPickerDayType
    private lateinit var displayedRange: SbisPeriodPickerRange
    private var isBottomPosition = true
    private var isDayAvailable: ((Calendar) -> Boolean)? = null
    private var presetStartDate: Calendar? = null
    private var presetEndDate: Calendar? = null
    private var anchorDate: Calendar? = null
    internal var isFragment: Boolean = false
    private var dayCustomTheme: ((Calendar) -> SbisPeriodPickerDayCustomTheme) = { SbisPeriodPickerDayCustomTheme() }
    private var periodPickerHeightPercent: Int = heightPercent
    private var dayCountersFactory: SbisPeriodPickerDayCountersRepository.Factory? = null
    override var requestKey = periodPickerRequestKey
    override var resultKey = periodPickerResultKey

    private var singleOffset = 0

    private lateinit var binding: PeriodPickerMonthModeFragmentBinding
    private var calendarListener: CalendarGlobalLayoutListener? = null
    private var calendarScrollListener: MonthModeScrollListener? = null

    private val monthLayoutManager: GridLayoutManager?
        get() = binding.calendar.layoutManager as? GridLayoutManager

    private val monthAdapter: MonthModePeriodPickerAdapter
        get() = binding.calendar.adapter as MonthModePeriodPickerAdapter

    companion object {

        /** Максимальное количество DayViewHolder, которое будет храниться в пуле перед удалением. */
        private const val MAX_RECYCLED_DAY_VIEWS = 90

        /** Максимальное количество EmptyViewHolder, которое будет храниться в пуле перед удалением. */
        private const val MAX_RECYCLED_EMPTY_VIEWS = 20

        private const val CURRENT_MONTH = "CURRENT_MONTH"

        private const val SINGLE_OFFSET = "SINGLE_OFFSET"

        fun create(
            startValue: Calendar?,
            endValue: Calendar?,
            isEnabled: Boolean,
            selectionType: SbisPeriodPickerSelectionType,
            dayType: SbisPeriodPickerDayType,
            displayedRange: SbisPeriodPickerRange,
            requestKey: String,
            resultKey: String,
            customView: ((Context) -> View)? = null,
            isDayAvailable: ((Calendar) -> Boolean)? = null,
            isCompact: Boolean = true,
            isBottomPosition: Boolean = false,
            presetStartValue: Calendar? = null,
            presetEndValue: Calendar? = null,
            anchorDate: Calendar? = null,
            isFragment: Boolean = false,
            dayCustomTheme: ((Calendar) -> SbisPeriodPickerDayCustomTheme) = { SbisPeriodPickerDayCustomTheme() },
            periodPickerHeightPercent: Int = heightPercent,
            factory: SbisPeriodPickerDayCountersRepository.Factory?
        ) = MonthModePeriodPickerFragment().apply {
            withArgs {
                startValue?.timeInMillis?.let { putLong(ARG_START_VALUE, it) }
                endValue?.timeInMillis?.let { putLong(ARG_END_VALUE, it) }
                putBoolean(ARG_IS_ENABLED, isEnabled)
                putParcelable(ARG_SELECTION_TYPE, selectionType)
                putParcelable(ARG_DAY_TYPE, dayType)
                customView?.let { putSerializable(ARG_CUSTOM_VIEW, it as Serializable) }
                putParcelable(ARG_DISPLAYED_RANGE, displayedRange)
                putBoolean(ARG_IS_COMPACT, isCompact)
                putBoolean(ARG_IS_BOTTOM_POSITION, isBottomPosition)
                isDayAvailable?.let { putSerializable(ARG_IS_DAY_AVAILABLE, it as Serializable) }
                presetStartValue?.timeInMillis?.let { putLong(ARG_PRESET_START_VALUE, it) }
                presetEndValue?.timeInMillis?.let { putLong(ARG_PRESET_END_VALUE, it) }
                anchorDate?.timeInMillis?.let { putLong(ARG_ANCHOR_DATE, it) }
                putBoolean(ARG_IS_FRAGMENT, isFragment)
                dayCustomTheme.let { putSerializable(ARG_DAY_CUSTOM_THEME, it as Serializable) }
                putInt(ARG_HEIGHT_PERCENT, periodPickerHeightPercent)
                putString(ARG_REQUEST_KEY, requestKey)
                putString(ARG_RESULT_KEY, resultKey)
                putParcelable(ARG_DAY_COUNTERS, factory)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setValuesFromArgs()
        DaggerMonthModePeriodPickerComponent.factory().create(
            viewFactory = {
                MonthModePeriodPickerViewImpl(binding)
            },
            startDate,
            endDate,
            selectionType,
            dayType,
            displayedRange,
            isCompact,
            isDayAvailable,
            presetStartDate,
            presetEndDate,
            isFragment,
            dayCustomTheme,
            anchorDate,
            isBottomPosition,
            dayCountersFactory,
            if (isCompact) null else PeriodPickerComponent.resolveDependencies(parentFragment)
        ).injector().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.period_picker_month_mode_fragment, container, false).apply {
            if (isCompact && this@MonthModePeriodPickerFragment.isSbisContainerParent(true)) {
                updateContentLayoutParams(periodPickerHeightPercent / 100.00)
            }
        }

        binding = PeriodPickerMonthModeFragmentBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        currentMonth = savedInstanceState?.getString(CURRENT_MONTH)
        binding.monthLabel.text =
            currentMonth ?: resources.getString(mapMonthToStringResId(Calendar.getInstance().month))

        singleOffset = savedInstanceState?.getInt(SINGLE_OFFSET) ?: 0

        calendarListener =
            this.setGlobalLayoutListener(
                isCompact,
                binding.calendar,
                isBottomPosition
            ) { isContainerShown ->
                binding.calendar.removeGlobalLayoutListener(calendarListener) {
                    if (savedInstanceState == null) {
                        performScroll(isContainerShown)
                    } else {
                        updateStateRestorationPolicy(monthAdapter)
                        updateUI()
                    }
                }
            }

        calendarListener?.let {
            binding.calendar.viewTreeObserver.addOnGlobalLayoutListener(it)
        }

        if (isCompact) {
            // Текущая дата должна быть строго над воскресеньем в компактном выборе периода.
            binding.weekDays.post {
                binding.currentDate.updatePadding(
                    right = Offset.M.getDimenPx(view.context) +
                        ((binding.weekDays.width - 2 * Offset.M.getDimenPx(view.context)) / 14) -
                        (binding.currentDate.width / 2)
                )

                val calendarDrawable = CalendarDateIcon(view.context)
                calendarDrawable.dayNumber = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

                binding.currentDate.apply {
                    icon = SbisButtonDrawableIcon(icon = calendarDrawable)
                }
            }

            addCustomView(view)
        } else {
            binding.headerLayout.visibility = View.GONE
            binding.divider.visibility = View.GONE
            binding.backgroundView.elevation = Elevation.XL.getDimen(requireContext())
        }

        setCalendarRecyclerView()
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

    override fun updateScrollPosition(scrollDate: Calendar?) {
        performScroll(anchorDate ?: scrollDate ?: Calendar.getInstance())
    }

    override fun reloadCalendar(newData: CalendarStorage, addToEnd: Boolean) {
        monthAdapter.reload(newData.dayGrid, addToEnd)
        calendarScrollListener?.completeReloading()
    }

    override fun updateSelection(storage: CalendarStorage) {
        monthAdapter.update(storage.dayGrid)
    }

    override fun resetSelection() {
        startDate = null
        endDate = null

        resetSelectionPeriod()
        updateScrollPosition()
    }

    override fun setPresetSelection(dateFrom: Calendar, dateTo: Calendar) {
        resetSelectionPeriod()
        monthAdapter.listener?.onSelectPeriod(dateFrom, dateTo)
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
        calendarListener?.let { binding.calendar.viewTreeObserver.removeOnGlobalLayoutListener(it) }
        calendarScrollListener?.let { binding.calendar.removeOnScrollListener(it) }
        super.onDestroy()
    }

    /** Настроить значения полей из аргументов. */
    @Suppress("UNCHECKED_CAST")
    private fun setValuesFromArgs() {
        val sDate = getCalendarFromMillis(arguments?.getLong(ARG_START_VALUE, MIN_DATE.timeInMillis))
        val eDate = getCalendarFromMillis(arguments?.getLong(ARG_END_VALUE, MIN_DATE.timeInMillis))
        val period = getPeriod(sDate, eDate)
        startDate = period.startDate
        endDate = period.endDate

        isEnabled = arguments?.getBoolean(ARG_IS_ENABLED) ?: true

        selectionType = getParamsFromArgs(
            arguments,
            ARG_SELECTION_TYPE,
            SbisPeriodPickerSelectionType::class.java
        ) as? SbisPeriodPickerSelectionType ?: SbisPeriodPickerSelectionType.Single

        dayType = getParamsFromArgs(
            arguments,
            ARG_DAY_TYPE,
            SbisPeriodPickerDayType::class.java
        ) as? SbisPeriodPickerDayType ?: SbisPeriodPickerDayType.Simple

        displayedRange = getParamsFromArgs(
            arguments,
            ARG_DISPLAYED_RANGE,
            SbisPeriodPickerRange::class.java
        ) as? SbisPeriodPickerRange ?: SbisPeriodPickerRange()

        customView = arguments?.getSerializable(ARG_CUSTOM_VIEW) as? ((Context) -> View)?

        isCompact = arguments?.getBoolean(ARG_IS_COMPACT) ?: true

        isBottomPosition = arguments?.getBoolean(ARG_IS_BOTTOM_POSITION) ?: false

        isDayAvailable = arguments?.getSerializable(ARG_IS_DAY_AVAILABLE) as? ((Calendar) -> Boolean)?

        presetStartDate = getCalendarFromMillis(arguments?.getLong(ARG_PRESET_START_VALUE, MIN_DATE.timeInMillis))
        presetEndDate = getCalendarFromMillis(arguments?.getLong(ARG_PRESET_END_VALUE, MIN_DATE.timeInMillis))

        anchorDate = getCalendarFromMillis(arguments?.getLong(ARG_ANCHOR_DATE, MIN_DATE.timeInMillis))

        isFragment = arguments?.getBoolean(ARG_IS_FRAGMENT) ?: false

        dayCustomTheme = arguments?.getSerializable(ARG_DAY_CUSTOM_THEME)
            as? ((Calendar) -> SbisPeriodPickerDayCustomTheme) ?: { SbisPeriodPickerDayCustomTheme() }

        periodPickerHeightPercent = arguments?.getInt(ARG_HEIGHT_PERCENT) ?: heightPercent

        requestKey = arguments?.getString(ARG_REQUEST_KEY) ?: periodPickerRequestKey

        resultKey = arguments?.getString(ARG_RESULT_KEY) ?: periodPickerResultKey

        dayCountersFactory = getParamsFromArgs(
            arguments,
            ARG_DAY_COUNTERS,
            SbisPeriodPickerDayCountersRepository.Factory::class.java
        ) as? SbisPeriodPickerDayCountersRepository.Factory
    }

    /** @SelfDocumented */
    private fun addCustomView(view: View) {
        customView?.invoke(view.context)?.let {
            it.id = FrameLayout.generateViewId()

            val rootLayout: ConstraintLayout = binding.root
            val set = ConstraintSet()

            val params = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )

            params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            it.layoutParams = params

            val offset = Offset.M.getDimenPx(it.context)
            it.setMargins(offset, offset, offset, offset)

            rootLayout.addView(it)

            set.clone(rootLayout)
            set.connect(binding.calendar.id, ConstraintSet.BOTTOM, it.id, ConstraintSet.TOP)
            set.applyTo(rootLayout)
        }
    }

    /** Выполнить скроллирование. */
    private fun performScroll(isContainerShown: Boolean): Boolean {
        return when {
            monthAdapter.itemCount == 0 -> false
            isBottomPosition -> scrollToBottom(getDateToScroll(endDate, presetEndDate), isContainerShown)
            else -> scrollByDefault(getDateToScroll(startDate, presetStartDate), isContainerShown)
        }
    }

    /** Настроить RecyclerView для календаря. */
    private fun setCalendarRecyclerView() {
        with(binding.calendar) {
            recycledViewPool.setMaxRecycledViews(
                DayViewHolder.ITEM_TYPE,
                MAX_RECYCLED_DAY_VIEWS
            )
            recycledViewPool.setMaxRecycledViews(
                EmptyViewHolder.ITEM_TYPE,
                MAX_RECYCLED_EMPTY_VIEWS
            )
            setItemViewCacheSize(MAX_RECYCLED_DAY_VIEWS + MAX_RECYCLED_EMPTY_VIEWS)
            setHasFixedSize(true)

            // замедление скролла - быстрый скролл листает рывками
            onFlingListener = CalendarFlingListener(this)
        }

        binding.calendar.post {
            monthAdapter.setEnabled(isEnabled)

            calendarScrollListener = MonthModeScrollListener(binding, binding.calendar.resources, !isCompact)
            calendarScrollListener?.let { binding.calendar.addOnScrollListener(it) }
        }
    }

    /** Настроить шапку календаря. */
    private fun setCalendarHeader(date: Calendar) {
        val monthStr = resources.getString(mapMonthToStringResId(date.month))
        binding.monthLabel.text = if (Calendar.getInstance().year == date.year) {
            monthStr
        } else {
            getFormattedMonthLabel(monthStr, date.year)
        }
    }

    /** Выполнить скроллирование. */
    private fun performScroll(date: Calendar) {
        binding.calendar.post {
            val (position, offset) = if (isBottomPosition) {
                monthAdapter.getPosition(date) to binding.calendar.height - singleOffset
            } else {
                monthAdapter.getFirstMonthPosition(date.month, date.year, isCompact) to 0
            }
            monthLayoutManager?.scrollToPositionWithOffset(position, offset)

            if (!isBottomPosition) performOverScroll(position)
        }
    }

    /** Выполнить скроллирование по умолчанию. */
    private fun scrollByDefault(date: Calendar, isContainerShown: Boolean): Boolean {
        val position = monthAdapter.getFirstMonthPosition(date.month, date.year, isCompact)
        monthLayoutManager?.scrollToPositionWithOffset(position, 0)

        val firstPosition = monthLayoutManager?.findFirstVisibleItemPosition() ?: -1
        val lastVisiblePos = monthLayoutManager?.findLastVisibleItemPosition() ?: -1
        val lastPos = monthAdapter.getLastPosition()

        // Проверяем, что прокрутка календаря корректна.
        // Если календарь ограничен, то скроллирование может не произойти.
        if (isContainerShown && (firstPosition == position || lastVisiblePos == lastPos)) {
            performOverScroll(position)
            // Календарь отображается после скроллирования,
            // чтобы пользователь не видел скачок от нулевой позиции к необходимой.
            return updateUI()
        }

        return false
    }

    /** Выполнить скроллирование текущие даты/выбранного периода к низу. */
    private fun scrollToBottom(date: Calendar, isContainerShown: Boolean): Boolean {
        with(binding.calendar) {
            val calendarHeight = this.measuredHeight
            var itemHeight = 0
            // Получаем высоту элемента сетки календаря.
            setItemHeight@ for (i in 0..childCount) {
                val item = getChildAt(i) as? DayView
                if (item != null) {
                    itemHeight = item.height
                    break@setItemHeight
                }
            }
            val offset = calendarHeight - itemHeight
            if (offset > 0) {
                val position = monthAdapter.getPosition(date)
                monthLayoutManager?.scrollToPositionWithOffset(position, offset)

                val lastVisiblePos = monthLayoutManager?.findLastVisibleItemPosition() ?: -1
                val lastVisibleDate = monthAdapter.getDateByPosition(lastVisiblePos)
                val lastVisibleDayOfWeek = lastVisibleDate.dayOfWeek
                val lastPos = monthAdapter.getLastPosition()

                val firstVisiblePos = monthLayoutManager?.findFirstVisibleItemPosition() ?: -1
                val firstVisibleDate = monthAdapter.getDateByPosition(firstVisiblePos)
                val prevLimit = getPrevDate(firstVisibleDate)
                val prevLimitPosition = monthAdapter.getFirstMonthPosition(prevLimit.month, prevLimit.year, true)

                // Проверяем, что прокрутка календаря корректна.
                // Если календарь ограничен, то скроллирование может не произойти.
                if (
                    isContainerShown && (
                        (position <= lastVisiblePos && position >= (lastVisiblePos - lastVisibleDayOfWeek)) ||
                            lastVisiblePos == lastPos ||
                            (prevLimitPosition == 1 && position > 0)
                        )
                ) {
                    singleOffset = itemHeight
                    // Календарь отображается после скроллирования,
                    // чтобы пользователь не видел скачок от нулевой позиции к необходимой.
                    return updateUI()
                }
            }
            return false
        }
    }

    /** @SelfDocumented */
    private fun setCalendarVisibility() {
        if (!binding.calendar.isVisible) binding.calendar.isVisible = true
        if (!binding.monthLabel.isVisible) binding.monthLabel.isVisible = true
    }

    /** @SelfDocumented */
    private fun getDateToScroll(date: Calendar?, presetDate: Calendar?): Calendar {
        val scrollDate = anchorDate ?: date ?: presetDate

        return getDateToScroll(scrollDate, displayedRange.start, displayedRange.end, isBottomPosition)
    }

    /** @SelfDocumented */
    private fun resetSelectionPeriod() {
        if (isEnabled) {
            monthAdapter.listener?.onResetSelectionPeriod()
        }
    }

    /** @SelfDocumented */
    private fun getPrevDate(date: Calendar): Calendar {
        return (date.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, firstDay)
            add(Calendar.MONTH, -1)
        }
    }

    /** @SelfDocumented */
    private fun updateUI(): Boolean {
        val firstVisiblePos = monthLayoutManager?.findFirstVisibleItemPosition() ?: -1
        val firstVisibleDate = monthAdapter.getHeaderDateByPosition(firstVisiblePos)
        setCalendarHeader(firstVisibleDate)
        setCalendarVisibility()
        return true
    }

    /**
     * Выполнить дополнительный скролл, чтобы выделение периода не "прилипало" к шапке.
     * Только для компактного, так как в Большом ВП перед шапкой еще есть заголовок с месяцем.
     */
    private fun performOverScroll(position: Int) {
        if (isCompact) {
            val offset = binding.root.context.getDimenPx(R.attr.SelectionView_margin)
            monthLayoutManager?.scrollToPositionWithOffset(position, offset)
        }
    }
}