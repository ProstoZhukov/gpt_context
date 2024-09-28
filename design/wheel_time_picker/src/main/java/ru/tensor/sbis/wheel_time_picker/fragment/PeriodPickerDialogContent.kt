package ru.tensor.sbis.wheel_time_picker.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import io.reactivex.disposables.CompositeDisposable
import org.joda.time.LocalDateTime
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.common.rx.livedata.dataValue
import ru.tensor.sbis.common.rx.plusAssign
import ru.tensor.sbis.common.util.getParentAs
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.design.container.SbisContainerImpl
import ru.tensor.sbis.design.cylinder.picker.time.CylinderViewType
import ru.tensor.sbis.design.header.createContainerHeaderTabbed
import ru.tensor.sbis.design.header.createContainerHeaderTitled
import ru.tensor.sbis.design.toolbar.ToolbarTabLayout
import ru.tensor.sbis.design_dialogs.dialogs.container.Container
import ru.tensor.sbis.design_dialogs.dialogs.content.Content
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableFragment
import ru.tensor.sbis.wheel_time_picker.PeriodPickerViewModel
import ru.tensor.sbis.wheel_time_picker.R
import ru.tensor.sbis.wheel_time_picker.data.DurationMode
import ru.tensor.sbis.wheel_time_picker.data.PeriodPickerMode
import ru.tensor.sbis.wheel_time_picker.data.TimePickerParameters
import ru.tensor.sbis.wheel_time_picker.databinding.WheelTimePickerPeriodPickerBinding
import ru.tensor.sbis.wheel_time_picker.feature.getTimePickerFeatureInternal
import ru.tensor.sbis.wheel_time_picker.getMidnight

/** @SelfDocumented */
const val DEFAULT_MINUTES_STEP = 5

/**
 * Содержимое экрана выбора даты и времени.
 *
 * @author us.bessonov
 */
internal class PeriodPickerDialogContent : BaseFragment(), Content {

    companion object {
        private const val TIME_PICKER_PARAMETERS = "TIME_PICKER_PARAMETERS"
        /**
         * Создание фрагмента экрана выбора времени.
         */
        fun newInstance(parameters: TimePickerParameters): PeriodPickerDialogContent = with(parameters) {
            return PeriodPickerDialogContent().withArgs {
                putParcelable(TIME_PICKER_PARAMETERS, parameters)
            }
        }
    }

    private var savedStartDate: LocalDateTime? = null
    private var savedEndDate: LocalDateTime? = null
    private var savedAllDayLong: Boolean = false
    private var minutesStep = DEFAULT_MINUTES_STEP

    private var isAccepted = false

    private val parameters: TimePickerParameters
        get() = requireArguments().getParcelable(TIME_PICKER_PARAMETERS)!!

    private val periodPickerMode: PeriodPickerMode?
        get() = parameters.periodPickerMode

    private val disposer = CompositeDisposable()

    private var _binding: WheelTimePickerPeriodPickerBinding? = null
    private val binding get() = _binding!!

    private val feature by lazy {
        getTimePickerFeatureInternal(
            requireParentFragment().parentFragment ?: requireActivity(),
            parameters.viewModelKey
        )
    }

    private val viewModel: PeriodPickerViewModel by lazy {
        feature.viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parameters.periodPickerMode?.let {
            viewModel.mode.onNext(it)
        }
        savedStartDate = parameters.defaultStartDateTime
        savedEndDate = parameters.defaultEndDateTime
        savedAllDayLong = parameters.defaultAllDayLong
        viewModel.canCreateZeroLengthEvent = parameters.canCreateZeroLengthEvent
        viewModel.isOneDay = parameters.isOneDay
        viewModel.allDayLongValue = savedAllDayLong
        if (savedAllDayLong) {
            viewModel.canCreateAllDayLongEvent = true
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = WheelTimePickerPeriodPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        minutesStep = parameters.minutesStep
        parameters.durationMode
            ?.let { viewModel.durationMode.onNext(it) }

        initPickers()
        observeDurationMode()
        observePeriodPickerMode()

        addHeaderView()
        getParentAs<ContainerMovableFragment>()?.let {
            it.lockPanel(true)
            binding.root.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            disableNestedScrolling(binding.root)
        }
    }

    private fun disableNestedScrolling(view: View) {
        view.isNestedScrollingEnabled = false
        if (view is ViewGroup) {
            view.children.forEach {
                disableNestedScrolling(it)
            }
        }
    }

    private fun addWholeDayViewToHeader(parent: ViewGroup) {
        with(parent) {
            val allDayTextView =
                LayoutInflater.from(requireContext()).inflate(R.layout.wheel_time_picker_whole_day, this, false)
            this.addView(allDayTextView)
            allDayTextView.updateLayoutParams<FrameLayout.LayoutParams> {
                gravity = Gravity.END or Gravity.CENTER_VERTICAL
            }
            allDayTextView.setOnClickListener {
                viewModel.allDayLongValue = true
                viewModel.configureEndLiveData(showMidnightAs24 = true, scrollEnabled = false)
                feature.endTimeSubject.dataValue = getMidnight(feature.startTimeSubject.dataValue!!)
                closeContainer()
            }
        }
    }

    override fun onDestroy() {
        clearSubscriptions()
        viewModel.durationMode.onNext(DurationMode.DURATION)
        super.onDestroy()
    }

    /** @SelfDocumented */
    fun onCancel() {
        feature.startTimeSubject.dataValue = savedStartDate
        feature.endTimeSubject.dataValue = savedEndDate ?: savedStartDate
        viewModel.allDayLongValue = savedAllDayLong
        onCloseDialog()
    }

    /** @SelfDocumented */
    fun onDismiss() {
        onCloseDialog()
    }

    override fun onCloseContent() {
        super.onCloseContent()
        if (!isAccepted) onCancel()
    }

    /**
     * Выбор режима работы пикеров
     * @param durationMode режим работы пикеров
     */
    private fun onDurationModeSelected(durationMode: DurationMode, withAnimation: Boolean = true) {
        binding.periodSelection.periodDayFlipper.apply {
            if (durationMode == DurationMode.DURATION && viewModel.durationMode.value != DurationMode.DURATION) {
                if (withAnimation) {
                    setInAnimation(context, R.anim.wheel_time_picker_left_in)
                    setOutAnimation(context, R.anim.wheel_time_picker_right_out)
                }
                viewModel.durationMode.onNext(DurationMode.DURATION)
            }

            if (durationMode == DurationMode.END && viewModel.durationMode.value != DurationMode.END) {
                if (withAnimation) {
                    setInAnimation(context, R.anim.wheel_time_picker_right_in)
                    setOutAnimation(context, R.anim.wheel_time_picker_left_out)
                }
                viewModel.durationMode.onNext(DurationMode.END)
            }
        }
    }

    /** Нажатие на кнопку подтверждения */
    private fun onAccept() {
        isAccepted = true
        if (periodPickerMode == PeriodPickerMode.START) {
            viewModel.allDayLongValue = false
            viewModel.configureEndLiveData(showMidnightAs24 = false, scrollEnabled = true)
        }
        feature.publishResult()
        onCloseDialog()
        closeContainer()
    }

    private fun clearSubscriptions() {
        disposer.clear()
        viewModel.pickerDataChanges = null
    }

    private fun onCloseDialog() {
        clearSubscriptions()
        if (viewModel.mode.value != PeriodPickerMode.DATE_AND_TIME) {
            viewModel.mode.onNext(PeriodPickerMode.DATE_AND_TIME)
        }
    }

    private fun initPickers() {
        binding.periodSelection.apply {
            periodTimePicker.init(viewModel.endTimeLiveData)
            viewModel.pickerDataChanges = {
                periodTimePicker.updateValues(viewModel.pickerData)
            }
            periodDatePicker.init(
                viewModel.endTimeLiveData,
                CylinderViewType.DAY
            )
            periodHoursPicker.init(viewModel.endTimeLiveData, CylinderViewType.HOUR)
            periodMinutePicker.init(viewModel.endTimeLiveData, CylinderViewType.MINUTE, minutesStep)
        }

        binding.startTimeSelection.apply {
            startDatePicker.init(viewModel.startTimeLiveData, CylinderViewType.DAY)
            startHoursPicker.init(viewModel.startTimeLiveData, CylinderViewType.HOUR)
            startMinutePicker.init(viewModel.startTimeLiveData, CylinderViewType.MINUTE, minutesStep)
        }
        if (viewModel.mode.value == PeriodPickerMode.DATE_ONLY) {
            binding.dateOnlySelection.apply {
                monthPicker.init(viewModel.onlyDateDataMonth)
                yearPicker.init(viewModel.onlyDateDataYear)
                dayPicker.init(viewModel.onlyDateDataDay)
            }
        }
    }

    private fun observeDurationMode() {
        disposer += viewModel.durationMode.subscribe {
            it?.let { mode ->
                when (mode) {
                    DurationMode.DURATION -> {
                        binding.periodSelection.periodDayFlipper.displayedChild = 0
                    }
                    DurationMode.END -> {
                        binding.periodSelection.periodDayFlipper.displayedChild = 1
                    }
                }
                Handler(Looper.getMainLooper()).post {
                    with(feature.endTimeSubject) {
                        val value = if (hasValue() && dataValue != null) {
                            value!!
                        } else {
                            feature.startTimeSubject.value!!
                        }
                        onNext(value)
                    }
                }
            }
        }
    }

    private fun observePeriodPickerMode() {
        disposer += viewModel.mode.subscribe {
            when (it) {
                PeriodPickerMode.START -> binding.periodPickerFlipper.displayedChild = 0
                PeriodPickerMode.DURATION -> binding.periodPickerFlipper.displayedChild = 1
                PeriodPickerMode.ONE_DAY_DURATION -> binding.periodPickerFlipper.displayedChild = 1
                PeriodPickerMode.DATE_ONLY -> binding.periodPickerFlipper.displayedChild = 2
                else -> Unit
            }
            binding.periodPickerSingleOval.visibility = if (it == PeriodPickerMode.START) View.VISIBLE else View.GONE
            binding.periodPickerRightOval.visibility = if (it != PeriodPickerMode.START) View.VISIBLE else View.GONE
            binding.periodPickerLeftOval.visibility = if (it != PeriodPickerMode.START) View.VISIBLE else View.GONE
            if (it == PeriodPickerMode.DATE_ONLY) {
                binding.periodPickerSingleOval.visibility = View.VISIBLE
                binding.periodPickerRightOval.visibility = View.GONE
                binding.periodPickerLeftOval.visibility = View.GONE
            }
        }
    }

    private fun addHeaderView() {
        val headerView = when {
            periodPickerMode == null || periodPickerMode == PeriodPickerMode.DATE_AND_TIME ->
                createContainerHeaderTitled(
                    requireContext(),
                    R.string.wheel_time_picker_time_selection_date_and_time,
                    ::onAccept
                )

            periodPickerMode == null || periodPickerMode == PeriodPickerMode.DATE_ONLY ->
                createContainerHeaderTitled(
                    requireContext(),
                    R.string.wheel_time_picker_time_selection_date,
                    ::onAccept
                )

            else ->
                createContainerHeaderTabbed(requireContext(), getTabs(), getSelectedTabId(), ::onTabChanged, ::onAccept)
        }

        if (viewModel.canCreateAllDayLongEvent && periodPickerMode == PeriodPickerMode.START) {
            headerView
                .findViewById<ViewGroup>(ru.tensor.sbis.design.header.R.id.right_custom_content_container)
                ?.run {
                    isVisible = true
                    addWholeDayViewToHeader(this)
                }
        }
        headerView.id = ru.tensor.sbis.design.container.R.id.header_view
        binding.root.addView(
            headerView,
            0,
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        )
        ConstraintSet().apply {
            clone(binding.root)
            connect(headerView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            connect(headerView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            connect(headerView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            applyTo(binding.root)
        }
    }

    private fun getTabs(): LinkedHashMap<Int, ToolbarTabLayout.ToolbarTab> =
        when (periodPickerMode) {
            PeriodPickerMode.START -> linkedMapOf(
                R.id.wheel_time_picker_time_selection_start_tab to ToolbarTabLayout.ToolbarTab(
                    R.id.wheel_time_picker_time_selection_start_tab,
                    R.string.wheel_time_picker_time_selection_start,
                    isEnabled = false
                )
            )
            PeriodPickerMode.DURATION, PeriodPickerMode.ONE_DAY_DURATION -> linkedMapOf(
                R.id.wheel_time_picker_time_selection_duration_tab to ToolbarTabLayout.ToolbarTab(
                    R.id.wheel_time_picker_time_selection_duration_tab,
                    R.string.wheel_time_picker_time_selection_duration_picker,
                    isEnabled = periodPickerMode == PeriodPickerMode.DURATION
                ),
                R.id.wheel_time_picker_time_selection_end_tab to ToolbarTabLayout.ToolbarTab(
                    R.id.wheel_time_picker_time_selection_end_tab,
                    R.string.wheel_time_picker_time_selection_end,
                    isEnabled = periodPickerMode == PeriodPickerMode.DURATION
                )
            )
            PeriodPickerMode.DATE_AND_TIME, PeriodPickerMode.DATE_ONLY, null -> linkedMapOf()
        }

    private fun onTabChanged(tabId: Int) {
        if (periodPickerMode == PeriodPickerMode.DURATION) {
            val durationMode = when (tabId) {
                R.id.wheel_time_picker_time_selection_duration_tab -> DurationMode.DURATION
                R.id.wheel_time_picker_time_selection_end_tab -> DurationMode.END
                else -> null
            }
            durationMode?.let { onDurationModeSelected(it) }
        }
    }

    private fun getSelectedTabId(): Int {
        return if (viewModel.mode.value == PeriodPickerMode.DURATION) {
            if (viewModel.durationMode.value == DurationMode.END) {
                R.id.wheel_time_picker_time_selection_end_tab
            } else {
                R.id.wheel_time_picker_time_selection_duration_tab
            }
        } else 0
    }

    private fun closeContainer() {
        getParentAs<SbisContainerImpl>()?.getViewModel()?.closeContainer()
        getParentAs<Container.Closeable>()?.closeContainer()
    }

}