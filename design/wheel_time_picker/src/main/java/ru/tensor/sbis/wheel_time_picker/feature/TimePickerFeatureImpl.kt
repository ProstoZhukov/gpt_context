package ru.tensor.sbis.wheel_time_picker.feature

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.AndroidViewModel
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.rx2.asFlow
import org.joda.time.LocalDateTime
import ru.tensor.sbis.common.rx.RxContainer
import ru.tensor.sbis.common.rx.livedata.dataValue
import ru.tensor.sbis.common.util.hasFragmentOrPendingTransaction
import ru.tensor.sbis.design.container.DimType
import ru.tensor.sbis.design.container.createFragmentContainer
import ru.tensor.sbis.design.container.locator.HorizontalLocator
import ru.tensor.sbis.design.container.locator.VerticalLocator
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanelPeekHeight
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableFragment
import ru.tensor.sbis.wheel_time_picker.PeriodPickerViewModel
import ru.tensor.sbis.wheel_time_picker.data.DurationMode
import ru.tensor.sbis.wheel_time_picker.data.PeriodPickerMode
import ru.tensor.sbis.wheel_time_picker.data.TimePickerParameters
import ru.tensor.sbis.wheel_time_picker.fragment.PeriodPickerDialogFragmentCreator
import ru.tensor.sbis.wheel_time_picker.fragment.PeriodPickerMovablePanelContentCreator

private const val TIME_BOUNDS_YEARS = 10

/**
 * Реализация фичи выбора даты и времени.
 *
 * @author us.bessonov
 */
internal class TimePickerFeatureImpl(application: Application) : AndroidViewModel(application),
    TimePickerFeatureInternal {

    private val timeBoundsSubject = BehaviorSubject.createDefault(
        Pair(
            LocalDateTime.now().minusYears(TIME_BOUNDS_YEARS),
            LocalDateTime.now().plusYears(TIME_BOUNDS_YEARS)
        )
    )

    override val startTimeSubject = BehaviorSubject.createDefault(RxContainer<LocalDateTime>(null))
    override val endTimeSubject = BehaviorSubject.createDefault(RxContainer<LocalDateTime>(null))

    override val startTimeChanges = startTimeSubject.asFlow().mapNotNull { it.value }
    override val endTimeChanges = endTimeSubject.asFlow().mapNotNull { it.value }

    override val startTimeResult = MutableStateFlow<LocalDateTime?>(null)
    override val endTimeResult = MutableStateFlow<LocalDateTime?>(null)

    override val viewModel: PeriodPickerViewModel by lazy {
        PeriodPickerViewModel(application, startTimeSubject, endTimeSubject, timeBoundsSubject)
    }

    override fun showDateTimePickerDialog(
        fragmentManager: FragmentManager,
        horizontalLocator: HorizontalLocator,
        verticalLocator: VerticalLocator,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?,
        periodPickerMode: PeriodPickerMode,
        durationMode: DurationMode?,
        canCreateZeroLengthEvent: Boolean,
        isOneDay: Boolean,
        defaultAllDayLong: Boolean,
        customTimeBounds: Pair<LocalDateTime, LocalDateTime>?,
        minutesStep: Int,
        customTag: String?,
    ) {
        val tag = customTag ?: FRAGMENT_TAG
        if (fragmentManager.hasFragmentOrPendingTransaction(tag)) return
        val properEndTime = correctEndTimeToMakeSureIntervalIsNotNegative(startTime, endTime)
        onShowDialog(startTime, properEndTime, customTimeBounds)
        val container = createFragmentContainer(
            PeriodPickerDialogFragmentCreator(
                TimePickerParameters(
                    defaultAllDayLong,
                    startTime,
                    properEndTime,
                    periodPickerMode,
                    durationMode,
                    minutesStep,
                    canCreateZeroLengthEvent,
                    isOneDay,
                    customTag
                )
            ),
            tag = tag
        )
        container.show(
            fragmentManager,
            horizontalLocator,
            verticalLocator
        )
    }

    override fun showDateTimePickerMovablePane(
        fragmentManager: FragmentManager,
        containerViewId: Int,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?,
        periodPickerMode: PeriodPickerMode,
        durationMode: DurationMode?,
        canCreateZeroLengthEvent: Boolean,
        isOneDay: Boolean,
        defaultAllDayLong: Boolean,
        customTimeBounds: Pair<LocalDateTime, LocalDateTime>?,
        minutesStep: Int,
        customTag: String?
    ) {
        val tag = customTag ?: FRAGMENT_TAG
        if (fragmentManager.hasFragmentOrPendingTransaction(tag)) return
        val properEndTime = correctEndTimeToMakeSureIntervalIsNotNegative(startTime, endTime)
        onShowDialog(startTime, properEndTime, customTimeBounds)
        fragmentManager.beginTransaction()
            .add(
                containerViewId, createTimePickerMovablePanelFragment(
                    TimePickerParameters(
                        defaultAllDayLong,
                        startTime,
                        properEndTime,
                        periodPickerMode,
                        durationMode,
                        minutesStep,
                        canCreateZeroLengthEvent,
                        isOneDay,
                        customTag
                    )
                ), tag
            )
            .addToBackStack(tag)
            .commit()
    }

    override fun publishResult() {
        startTimeSubject.dataValue?.let {
            startTimeResult.value = it
        }
        endTimeSubject.dataValue?.let {
            endTimeResult.value = it
        }
    }

    private fun onShowDialog(
        startTime: LocalDateTime?,
        endTime: LocalDateTime?,
        customTimeBounds: Pair<LocalDateTime, LocalDateTime>?
    ) {
        startTimeSubject.onNext(RxContainer(startTime))
        endTimeSubject.onNext(RxContainer(endTime))
        customTimeBounds?.let(timeBoundsSubject::onNext)
    }

    private fun createTimePickerMovablePanelFragment(parameters: TimePickerParameters): Fragment {
        return ContainerMovableFragment.Builder()
            .instant(true)
            .setExpandedPeekHeight(MovablePanelPeekHeight.FitToContent())
            .setContentCreator(PeriodPickerMovablePanelContentCreator(parameters))
            .build()
    }

    private fun correctEndTimeToMakeSureIntervalIsNotNegative(
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): LocalDateTime? {
        if (startTime == null) return endTime
        return endTime?.let {
            if (it < startTime) startTime else it
        }
    }

}

private const val FRAGMENT_TAG = "TIME_PICKER"