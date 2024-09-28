package ru.tensor.sbis.calendar_decl.calendar

import androidx.fragment.app.FragmentActivity

/** Провайдер элементов взаимодействия модуля календаря с соответствующим пунктом в боковом меню */
interface CalendarMenuItemProvider<NAV_ITEM_CONTENT> {

    /** Получить контент подменю элемента календаря в боковом меню  */
    fun getCalendarItemContent(getActivity: () -> FragmentActivity): CalendarNavItemContent<NAV_ITEM_CONTENT> =
        getCalendarItemContent(getActivity, isActivityArrowAvailable = true)

    /**
     * Получить контент подменю элемента календаря в боковом меню
     * @param getActivity метод получения главного активити
     * @param isActivityArrowAvailable доступность кнопки перехода к активности в календаре
     */
    fun getCalendarItemContent(
        getActivity: () -> FragmentActivity,
        isActivityArrowAvailable: Boolean
    ): CalendarNavItemContent<NAV_ITEM_CONTENT>
}