package ru.tensor.sbis.design.period_picker.view.period_picker.details.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCustomTheme
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.view.models.CalendarStorage
import ru.tensor.sbis.design.period_picker.view.models.MarkerType
import java.util.Calendar

/**
 * Репозиторий для получения данных для календаря.
 *
 * @author mb.kruglova
 */
internal class CalendarStorageRepository(
    private val repositoryDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    /**
     * Получить хранилище для календаря.
     * @param min минимальное значение (дата) хранилища.
     * @param max максимальное значение (дата) хранилища.
     * @param markerType тип маркера.
     */
    internal suspend fun getCalendarStorage(
        min: Calendar,
        max: Calendar,
        limitRange: SbisPeriodPickerRange,
        isCompact: Boolean,
        markerType: MarkerType,
        isDayAvailable: ((Calendar) -> Boolean)?,
        dayCustomTheme: ((Calendar) -> SbisPeriodPickerDayCustomTheme)
    ): CalendarStorage {
        return withContext(repositoryDispatcher) {
            generateCalendar(min, max, limitRange, isCompact, markerType, isDayAvailable, dayCustomTheme)
        }
    }

    /**
     * Добавить новые данные в хранилище.
     * @param storage текущее хранилище.
     * @param newData новые данные для хранилища.
     * @param addToEnd флаг добавления данных в конец текущего хранилища,
     * в противном случае данные будут добавлены в начало текущего хранилища.
     */
    internal suspend fun addDataToStorage(
        storage: CalendarStorage,
        newData: CalendarStorage,
        addToEnd: Boolean
    ): CalendarStorage {
        return withContext(repositoryDispatcher) {
            storage.addDataToStorage(newData, addToEnd)
        }
    }

    /**  @SelfDocumented */
    internal fun generateCalendar(
        min: Calendar,
        max: Calendar,
        limitRange: SbisPeriodPickerRange,
        isCompact: Boolean,
        markerType: MarkerType,
        isDayAvailable: ((Calendar) -> Boolean)?,
        dayCustomTheme: ((Calendar) -> SbisPeriodPickerDayCustomTheme)
    ): CalendarStorage {
        return CalendarStorage().apply {
            hasYearMode = !isCompact
            generateDays(min, max, limitRange, markerType, isDayAvailable, dayCustomTheme)
            if (!isCompact) generateQuanta(min, max, limitRange)
        }
    }
}