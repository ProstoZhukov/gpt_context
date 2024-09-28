package ru.tensor.sbis.calendar_decl.calendar

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.Date
import java.util.UUID

/** Провайдер экранов совещаний */
interface CalendarMeetingEventProvider: Feature {

    /**
     * Получить фрагмент создания обычного совещания на дату [date] пользователю [profileUuid].
     * При передаче [profileUuid] в совещание будет сразу добавлен пользователь с этим uuid.
     * При передаче [date] совещание будет создано на переданную дату.
     */
    fun getCreateMeetingFragment(date: Date?, profileUuid: UUID?): Fragment

    /**
     * Получить фрагмент создания видеосовещания на дату [date] пользователю [profileUuid].
     * При передаче [profileUuid] в видеосовещание будет сразу добавлен пользователь с этим uuid.
     * При передаче [date] видеосовещание будет создано на переданную дату.
     */
    fun getCreateVideoMeetingFragment(date: Date?, profileUuid: UUID?): Fragment

    /**
     * Получить фрагмент открытия совещания по его [eventUuid].
     */
    fun getShowMeetingFragment(eventUuid: UUID): Fragment

    /**
     * Получить фрагмент открытия вебинара по его [eventUuid].
     */
    fun getShowWebinarFragment(eventUuid: UUID): Fragment
}