package ru.tensor.sbis.calendar_date_icon

/**
 * Тип выбранности иконки календаря.
 *
 * @author da.zolotarev
 */
enum class CalendarDateIconConfiguration(
    val icon: String,
    val selectedIcon: String,
) {
    /** При выборе иконка становится "залитой" */
    FILLED(
        CALENDAR_ICON,
        CALENDAR_SELECTED_ICON
    ),

    /** При выборе иконка НЕ становится "залитой" */
    BORDER_ONLY(
        CALENDAR_ICON,
        CALENDAR_ICON
    );
}

/**
 * Использовать [CalendarDateIcon] с другими иконками не предполагается.
 */
private const val CALENDAR_ICON = "\ue93b"
private const val CALENDAR_SELECTED_ICON = "\ue994"
