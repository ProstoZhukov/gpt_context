package ru.tensor.sbis.appdesign.navigation

import androidx.annotation.StringRes
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.design.navigation.view.model.NavigationItemIcon
import ru.tensor.sbis.design.navigation.view.model.NavigationItemLabel

enum class NavigationTab(
    @StringRes val labelResId: Int,
    @StringRes iconId: Int,
    @StringRes iconSelectedId: Int,
    val parentItem: NavigationTab? = null,
    val isIconVisible: Boolean = true,
    val isAlignedRight: Boolean = false,
    val labelForRightAlignmentId: Int? = null,
    val tabNavViewHorizontalLabel: Int? = null,
    val tabNavViewVerticalLabel: Int? = null
) : NavigationItem {
    NOTIFICATION(
        R.string.navigation_notifications,
        R.string.design_mobile_icon_notification,
        R.string.design_mobile_icon_notification_filled,
        tabNavViewHorizontalLabel = R.string.navigation_notifications_reduced
    ),
    MESSAGES(
        R.string.navigation_messages,
        R.string.design_mobile_icon_menu_messages,
        R.string.design_mobile_icon_menu_messages_filled
    ),
    CALENDAR(
        R.string.navigation_calendar,
        R.string.design_mobile_icon_calendar,
        R.string.design_mobile_icon_calendar_filled
    ),
    DISK(
        R.string.navigation_disk,
        R.string.design_mobile_icon_disk,
        R.string.design_mobile_icon_disk_filled
    ),
    ADDRESS(
        R.string.navigation_address,
        R.string.design_mobile_icon_address,
        R.string.design_mobile_icon_address_filled
    ),
    INFO(
        R.string.navigation_info,
        R.string.design_mobile_icon_info,
        R.string.design_mobile_icon_info_filled,
        tabNavViewHorizontalLabel = R.string.navigation_info_short
    ),
    TASKS(
        R.string.navigation_task,
        R.string.design_mobile_icon_task,
        R.string.design_mobile_icon_task_filled,
        tabNavViewHorizontalLabel = R.string.navigation_task_horizontal,
        tabNavViewVerticalLabel = R.string.navigation_task_vertical
    ),
    TASKS_FROM_ME(
        R.string.navigation_tasks_from_me,
        R.string.design_mobile_icon_task,
        R.string.design_mobile_icon_task_filled,
        parentItem = TASKS,
        isIconVisible = false,
        isAlignedRight = true,
        labelForRightAlignmentId = R.string.navigation_task
    ),
    FAVORITES(
        R.string.navigation_favorites,
        R.string.design_mobile_icon_favorites,
        R.string.design_mobile_icon_favorites_filled
    ),
    CALL(
        R.string.navigation_call,
        R.string.design_mobile_icon_call,
        R.string.design_mobile_icon_call_filled
    ),
    KASSA(
        R.string.navigation_kassa,
        R.string.design_mobile_icon_kassa,
        R.string.design_mobile_icon_kassa_filled
    ),
    NEWS(
        R.string.navigation_news,
        R.string.design_mobile_icon_menu_news,
        R.string.design_mobile_icon_menu_news_filled
    ),
    STATISTICS(
        R.string.navigation_statistics,
        R.string.design_mobile_icon_menu_statistics,
        R.string.design_mobile_icon_menu_statistics_filled
    ),
    CONTACTS(
        R.string.navigation_contacts,
        R.string.design_mobile_icon_menu_contacts,
        R.string.design_mobile_icon_menu_contacts_filled
    ),
    SETTINGS(
        R.string.navigation_settings,
        R.string.design_mobile_icon_menu_settings,
        R.string.design_mobile_icon_menu_settings_filled
    );

    /**
     * Модель иконки, изменяемая
     */
    private val iconSubject = BehaviorSubject.createDefault(NavigationItemIcon(iconId, iconSelectedId, isIconVisible))
    private val labelSubject = BehaviorSubject.createDefault(
        NavigationItemLabel(
            labelResId,
            isAlignedRight = isAlignedRight,
            labelForRightAlignment = labelForRightAlignmentId ?: labelResId,
            tabNavViewHorizontalLabel = tabNavViewHorizontalLabel ?: labelResId,
            tabNavViewVerticalLabel = tabNavViewVerticalLabel ?: tabNavViewHorizontalLabel ?: labelResId
        )
    )

    /**
     * В данном случае текст пунктов меню неизменный и не имеет сокращённой формы
     */
    override val labelObservable: Observable<NavigationItemLabel> = labelSubject
    override val iconObservable: Observable<NavigationItemIcon> = iconSubject

    /**
     * Пример реализации методов для изменения состояния пунктов меню
     */
    companion object IconUpdater {

        @JvmStatic
        fun changeCalendarIcons(@StringRes iconId: Int, @StringRes iconSelectedId: Int) =
            CALENDAR.changeIcons(iconId, iconSelectedId)

        @JvmStatic
        fun changeCalendarText(@StringRes labelResId: Int) =
            CALENDAR.changeText(labelResId)

        @JvmStatic
        fun changeTasksText(@StringRes labelResId: Int) {
            TASKS.changeText(labelResId)
            val tasksFromMeSubject = TASKS_FROM_ME.labelSubject
            tasksFromMeSubject.onNext(tasksFromMeSubject.value!!.copy(labelForRightAlignment = labelResId))
        }

        @JvmStatic
        fun changeTasksFromMeText(@StringRes labelResId: Int) {
            val tasksFromMeSubject = TASKS_FROM_ME.labelSubject
            tasksFromMeSubject.onNext(tasksFromMeSubject.value!!.copy(labelResId))
        }

        @JvmStatic
        fun NavigationTab.changeIcons(@StringRes iconId: Int, @StringRes iconSelectedId: Int) {
            iconSubject.onNext(NavigationItemIcon(iconId, iconSelectedId, isIconVisible))
        }

        @JvmStatic
        fun NavigationTab.changeText(@StringRes labelResId: Int) {
            labelSubject.onNext(NavigationItemLabel(labelResId))
        }
    }
}