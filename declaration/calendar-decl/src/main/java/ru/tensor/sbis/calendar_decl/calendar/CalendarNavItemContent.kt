package ru.tensor.sbis.calendar_decl.calendar

/** Контент подменю элемента календаря в боковом меню */
interface CalendarNavItemContent<T> {

    /** Получить контент с типом [T] */
    fun getItemContentAsNavItem(): T

    /** @SelfDocumented */
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
}