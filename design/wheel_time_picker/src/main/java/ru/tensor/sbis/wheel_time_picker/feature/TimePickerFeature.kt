package ru.tensor.sbis.wheel_time_picker.feature

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.joda.time.LocalDateTime
import ru.tensor.sbis.design.container.SbisContainer
import ru.tensor.sbis.design.container.locator.HorizontalLocator
import ru.tensor.sbis.design.container.locator.VerticalLocator
import ru.tensor.sbis.wheel_time_picker.PeriodPickerViewModel
import ru.tensor.sbis.wheel_time_picker.data.DurationMode
import ru.tensor.sbis.wheel_time_picker.data.PeriodPickerMode
import ru.tensor.sbis.wheel_time_picker.fragment.DEFAULT_MINUTES_STEP

/**
 * Фича панели с барабанами для выбора даты и времени события, а также длительности события.
 *
 * @author us.bessonov
 */
interface TimePickerFeature {

    /**
     * Оставлена для поддержки специфичной логики, используемой в Календаре.
     * Без острой необходимости обращаться к ней не нужно.
     */
    val viewModel: PeriodPickerViewModel

    /**
     * [Flow], публикующий все изменения начального времени пользователем в процессе выбора.
     */
    val startTimeChanges: Flow<LocalDateTime>

    /**
     * [Flow], публикующий все изменения конечного времени пользователем в процессе выбора.
     */
    val endTimeChanges: Flow<LocalDateTime>

    /**
     * [Flow], содержащий результат выбора начального времени.
     * Значение изменяется только при нажатии кнопки подтверждения выбранного времени.
     */
    val startTimeResult: StateFlow<LocalDateTime?>

    /**
     * [Flow], содержащий результат выбора конечного времени.
     * Значение изменяется только при нажатии кнопки подтверждения выбранного времени.
     */
    val endTimeResult: StateFlow<LocalDateTime?>

    /**
     * Отобразить панель выбора даты и времени в виде диалогового окна.
     *
     * @param horizontalLocator См. [SbisContainer.show].
     * @param verticalLocator См. [SbisContainer.show].
     * @param startTime Начальное значение даты и времени. По умолчанию - текущее время, с округлением до
     * [DEFAULT_MINUTES_STEP] минут.
     * @param startTime Конечное значение даты и времени. По умолчанию - текущее время, с округлением до
     * [DEFAULT_MINUTES_STEP] минут.
     * @param periodPickerMode Режим выбора. Определяет состав отображаемых барабанов, а также присутствие шапки и
     * текст заголовка. По умолчанию - [PeriodPickerMode.DATE_AND_TIME].
     * @param durationMode Режим выбора длительности (при [periodPickerMode] == [PeriodPickerMode.DURATION]).
     * @param canCreateZeroLengthEvent Можно ли выбрать нулевую длительность события.
     * @param isOneDay Можно ли выбирать время только в пределах одного дня.
     * @param defaultAllDayLong Должна ли быть по умолчанию длительность события задана как "на весь день".
     * @param customTimeBounds Границы интервала выбора даты, если необходимо.
     * @param minutesStep Шаг изменения выбираемого времени. По умолчанию - [DEFAULT_MINUTES_STEP].
     * @param customTag Специфичный тэг фрагмента.
     * Используется для избежания выхода тени контейнера за скруглённые края его родителя.
     */
    fun showDateTimePickerDialog(
        fragmentManager: FragmentManager,
        horizontalLocator: HorizontalLocator,
        verticalLocator: VerticalLocator,
        startTime: LocalDateTime? = viewModel.startTimeLiveData.date,
        endTime: LocalDateTime? = viewModel.endTimeLiveData.date,
        periodPickerMode: PeriodPickerMode = viewModel.mode.value!!,
        durationMode: DurationMode? = viewModel.durationMode.value,
        canCreateZeroLengthEvent: Boolean = viewModel.canCreateZeroLengthEvent,
        isOneDay: Boolean = viewModel.isOneDay,
        defaultAllDayLong: Boolean = viewModel.allDayLongValue,
        customTimeBounds: Pair<LocalDateTime, LocalDateTime>? = null,
        minutesStep: Int = DEFAULT_MINUTES_STEP,
        customTag: String? = null
    )

    /**
     * Отобразить панель выбора даты и времени в виде всплывающей снизу панели (шторки).
     *
     * @param containerViewId Идентификатор view контейнера панели.
     * @param startTime Начальное значение даты и времени. По умолчанию - текущее время, с округлением до
     * [DEFAULT_MINUTES_STEP] минут.
     * @param startTime Конечное значение даты и времени. По умолчанию - текущее время, с округлением до
     * [DEFAULT_MINUTES_STEP] минут.
     * @param periodPickerMode Режим выбора. Определяет состав отображаемых барабанов, а также присутствие шапки и
     * текст заголовка. По умолчанию - [PeriodPickerMode.DATE_AND_TIME].
     * @param durationMode Режим выбора длительности (при [periodPickerMode] == [PeriodPickerMode.DURATION]).
     * @param canCreateZeroLengthEvent Можно ли выбрать нулевую длительность события.
     * @param isOneDay Можно ли выбирать время только в пределах одного дня.
     * @param defaultAllDayLong Должна ли быть по умолчанию длительность события задана как "на весь день".
     * @param customTimeBounds Границы интервала выбора даты, если необходимо.
     * @param minutesStep Шаг изменения выбираемого времени. По умолчанию - [DEFAULT_MINUTES_STEP].
     * @param customTag Специфичный тэг фрагмента.
     */
    fun showDateTimePickerMovablePane(
        fragmentManager: FragmentManager,
        @IdRes containerViewId: Int,
        startTime: LocalDateTime? = viewModel.startTimeLiveData.date,
        endTime: LocalDateTime? = viewModel.endTimeLiveData.date,
        periodPickerMode: PeriodPickerMode = viewModel.mode.value!!,
        durationMode: DurationMode? = viewModel.durationMode.value,
        canCreateZeroLengthEvent: Boolean = viewModel.canCreateZeroLengthEvent,
        isOneDay: Boolean = viewModel.isOneDay,
        defaultAllDayLong: Boolean = viewModel.allDayLongValue,
        customTimeBounds: Pair<LocalDateTime, LocalDateTime>? = null,
        minutesStep: Int = DEFAULT_MINUTES_STEP,
        customTag: String? = null
    )
}