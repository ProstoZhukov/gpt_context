package ru.tensor.sbis.calendar_decl.calendar.events

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.plugin_struct.feature.Feature

/** Провайдер активити создания/просмотра события */
interface CalendarDayEventActivityProvider : Feature {

    companion object {
        /** Событие создано */
        const val EVENT_IS_CREATED = "event_is_created"

        /** Тип созданного события */
        const val EVENT_TYPE = "event_type"
    }

    /** Тип события - командировка */
    val businessTripEventType: String

    /** Тип события - отгул */
    val timeOffEventType: String

    /** Ключ для фичи новых отпусков */
    val newVacationCardFeatureKey: String

    /**
     * Получение итента активити создания/открытия события
     * @param calendarEventCardUseCase CalendarDayEventCardArg
     * @param context Context
     * @return Intent
     */
    fun getDayEventActivityIntent(
        calendarEventCardUseCase: CalendarDayEventCardArg,
        context: Context,
    ): Intent

    /**
     * Показать фрагмент списка регламентов для создания ДРВ (в дальнейшем при выборе он открывает экран создания ДРВ).
     * @param fragmentManager менеджер фрагментов, в котором произойдет запуск
     * @param tag тэг для фрагмента
     */
    fun showCalendarDurvRegulationsListFragment(
        fragmentManager: FragmentManager,
        eventType: String,
        tag: String? = null,
    )

    /** Получить вспомогательный объект для открытия меню создания события */
    fun getCreateEventComponentCreator(): CalendarCreateEventDIComponentCreator
}