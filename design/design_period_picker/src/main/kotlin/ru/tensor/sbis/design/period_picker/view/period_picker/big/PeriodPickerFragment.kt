package ru.tensor.sbis.design.period_picker.view.period_picker.big

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.calendar_date_icon.CalendarDateIcon
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonDrawableIcon
import ru.tensor.sbis.design.period_picker.R
import ru.tensor.sbis.design.period_picker.databinding.PeriodPickerFragmentBinding
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerFeature.Companion.periodPickerRequestKey
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerFeature.Companion.periodPickerResultKey
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerHeaderMask
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerMode
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerMode.YEAR
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerSelectionType
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerSelectionType.Range
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerSelectionType.Single
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_ANCHOR_DATE
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_DISPLAYED_RANGE
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_END_VALUE
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_HEADER_MASK
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_IS_BOTTOM_POSITION
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_IS_ENABLED
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_IS_ONE_DAY_SELECTION
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_MODE
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_PRESET_END_VALUE
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_PRESET_START_VALUE
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_REQUEST_KEY
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_RESULT_KEY
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_SELECTION_TYPE
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_START_VALUE
import ru.tensor.sbis.design.period_picker.view.period_picker.big.di.DaggerPeriodPickerComponent
import ru.tensor.sbis.design.period_picker.view.period_picker.big.di.PeriodPickerComponent
import ru.tensor.sbis.design.period_picker.view.period_picker.big.di.PeriodPickerComponentHolder
import ru.tensor.sbis.design.period_picker.view.utils.MIN_DATE
import ru.tensor.sbis.design.period_picker.view.utils.getCalendarFromMillis
import ru.tensor.sbis.design.period_picker.view.utils.getCalendarMode
import ru.tensor.sbis.design.period_picker.view.utils.getParamsFromArgs
import ru.tensor.sbis.design.period_picker.view.utils.getPeriod
import ru.tensor.sbis.design.period_picker.view.utils.isSbisContainerParent
import ru.tensor.sbis.design.period_picker.view.utils.manageFragmentTransaction
import ru.tensor.sbis.design.period_picker.view.utils.updateContentLayoutParams
import ru.tensor.sbis.design.period_picker.view.utils.updateDate
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import java.util.Calendar

/**
 * Фрагмент компонента Большой выбор периода.
 *
 * @author mb.kruglova
 */
internal class PeriodPickerFragment : BaseFragment(), PeriodPickerComponentHolder {

    override val diComponent: PeriodPickerComponent by lazy {
        DaggerPeriodPickerComponent.factory()
            .create(
                fragment = this,
                startDate,
                endDate,
                selectionType,
                displayedRange,
                isEnabled,
                isOneDaySelection,
                isBottomPosition,
                presetStartDate,
                presetEndDate,
                anchorDate,
                mode,
                requestKey,
                resultKey
            )
    }

    private var startDate: Calendar? = null
    private var endDate: Calendar? = null
    private var isEnabled = true
    private var selectionType: SbisPeriodPickerSelectionType = Range
    private var displayedRange: SbisPeriodPickerRange = SbisPeriodPickerRange()
    private var headerMask = SbisPeriodPickerHeaderMask.DEFAULT
    private var isBottomPosition = false
    private var presetStartDate: Calendar? = null
    private var presetEndDate: Calendar? = null
    private var anchorDate: Calendar? = null
    private var isOneDaySelection = false
    private var mode = YEAR
    private var requestKey: String = periodPickerRequestKey
    private var resultKey: String = periodPickerResultKey

    private lateinit var binding: PeriodPickerFragmentBinding

    companion object {

        fun create(
            startValue: Calendar?,
            endValue: Calendar?,
            isEnabled: Boolean,
            selectionType: SbisPeriodPickerSelectionType,
            displayedRange: SbisPeriodPickerRange,
            headerMask: SbisPeriodPickerHeaderMask,
            isBottomPosition: Boolean,
            presetStartValue: Calendar?,
            presetEndValue: Calendar?,
            requestKey: String,
            resultKey: String,
            mode: SbisPeriodPickerMode = YEAR,
            isOneDaySelection: Boolean = false,
            anchorDate: Calendar? = null
        ) = PeriodPickerFragment().apply {
            withArgs {
                startValue?.timeInMillis?.let { putLong(ARG_START_VALUE, it) }
                endValue?.timeInMillis?.let { putLong(ARG_END_VALUE, it) }
                putBoolean(ARG_IS_ENABLED, isEnabled)
                putParcelable(ARG_SELECTION_TYPE, selectionType)
                putBoolean(ARG_IS_ONE_DAY_SELECTION, isOneDaySelection)
                putParcelable(ARG_DISPLAYED_RANGE, displayedRange)
                putParcelable(ARG_HEADER_MASK, headerMask)
                putBoolean(ARG_IS_BOTTOM_POSITION, isBottomPosition)
                presetStartValue?.timeInMillis?.let { putLong(ARG_PRESET_START_VALUE, it) }
                presetEndValue?.timeInMillis?.let { putLong(ARG_PRESET_END_VALUE, it) }
                anchorDate?.timeInMillis?.let { putLong(ARG_ANCHOR_DATE, it) }
                putString(ARG_REQUEST_KEY, requestKey)
                putString(ARG_RESULT_KEY, resultKey)
                putParcelable(ARG_MODE, mode)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setValuesFromArgs()
        diComponent.injector().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.period_picker_fragment, container, false).apply {
            if (this@PeriodPickerFragment.isSbisContainerParent(true)) {
                updateContentLayoutParams()
            }
        }
        binding = PeriodPickerFragmentBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            setModeFragment()
        }

        setHeader()
        setIconCurrentDayButton()
        binding.completeButton.isEnabled = isEnabled
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
        ) as? SbisPeriodPickerSelectionType ?: Range

        displayedRange = getParamsFromArgs(
            arguments,
            ARG_DISPLAYED_RANGE,
            SbisPeriodPickerRange::class.java
        ) as? SbisPeriodPickerRange ?: SbisPeriodPickerRange()

        headerMask = getParamsFromArgs(arguments, ARG_HEADER_MASK, SbisPeriodPickerHeaderMask::class.java) as?
            SbisPeriodPickerHeaderMask ?: SbisPeriodPickerHeaderMask.DEFAULT

        isBottomPosition = arguments?.getBoolean(ARG_IS_BOTTOM_POSITION) ?: false

        val presetSDate = getCalendarFromMillis(arguments?.getLong(ARG_PRESET_START_VALUE, MIN_DATE.timeInMillis))
        val presetEDate = getCalendarFromMillis(arguments?.getLong(ARG_PRESET_END_VALUE, MIN_DATE.timeInMillis))
        val presetPeriod = getPeriod(presetSDate, presetEDate)
        presetStartDate = presetPeriod.startDate
        presetEndDate = presetPeriod.endDate

        isOneDaySelection = arguments?.getBoolean(ARG_IS_ONE_DAY_SELECTION) ?: false

        anchorDate = getCalendarFromMillis(arguments?.getLong(ARG_ANCHOR_DATE, MIN_DATE.timeInMillis))

        requestKey = arguments?.getString(ARG_REQUEST_KEY) ?: periodPickerRequestKey

        resultKey = arguments?.getString(ARG_RESULT_KEY) ?: periodPickerResultKey

        val start = startDate ?: presetStartDate
        val end = endDate ?: presetEndDate

        mode = getCalendarMode(
            start,
            end,
            isOneDaySelection,
            getParamsFromArgs(arguments, ARG_MODE, SbisPeriodPickerMode::class.java) as? SbisPeriodPickerMode ?: YEAR
        )
    }

    /** Настроить шапку ввода периода. */
    private fun setHeader() {
        binding.startDate.readOnly = !isEnabled
        binding.endDate.readOnly = !isEnabled

        binding.startDate.mask = headerMask.mask
        binding.endDate.mask = headerMask.mask

        startDate?.let { binding.startDate.updateDate(it) }
        endDate?.let { binding.endDate.updateDate(it) }

        binding.periodPickerPeriodDate.updateDate(startDate ?: endDate, endDate ?: startDate)

        binding.modeButton.isChecked = mode == SbisPeriodPickerMode.MONTH

        if (isOneDaySelection) {
            binding.modeButton.isEnabled = false
            binding.endDate.visibility = View.GONE
            binding.dashTextView.visibility = View.INVISIBLE
        } else {
            if (selectionType == Single) {
                binding.apply {
                    periodPickerPeriodDate.isVisible = true
                    startDate.isVisible = false
                    endDate.isVisible = false
                    dashTextView.isVisible = false
                }
            } else {
                binding.apply {
                    periodPickerPeriodDate.isVisible = false
                    startDate.isVisible = true
                    endDate.isVisible = true
                    dashTextView.isVisible = true
                }
            }
        }
    }

    /** Настроить иконку для кнопки текущего дня. */
    private fun setIconCurrentDayButton() {
        val calendarDrawable = CalendarDateIcon(binding.root.context)
        calendarDrawable.dayNumber = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        binding.currentDate.apply {
            icon = SbisButtonDrawableIcon(icon = calendarDrawable)
        }
    }

    /** Настроить режим отображения календаря. */
    private fun setModeFragment() {
        manageFragmentTransaction(
            childFragmentManager,
            mode,
            startDate,
            endDate,
            presetStartDate,
            presetEndDate,
            isEnabled,
            selectionType,
            displayedRange,
            isBottomPosition,
            anchorDate,
            requestKey,
            resultKey,
            null
        )
    }
}