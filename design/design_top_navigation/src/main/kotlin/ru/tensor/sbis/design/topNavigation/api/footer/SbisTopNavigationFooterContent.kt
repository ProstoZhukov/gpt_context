package ru.tensor.sbis.design.topNavigation.api.footer

import android.content.Context
import android.view.View
import ru.tensor.sbis.design.tabs.view.SbisTabsView
import ru.tensor.sbis.design.topNavigation.internal_view.SbisTopNavigationFooterView

/**
 * Тип подвала.
 *
 * @author da.zolotarev
 */
sealed class SbisTopNavigationFooterContent {

    /** Поиск.
     * Для настройки view поиска необходимо использовать метод [SbisTopNavigationFooterView.configure]
     * Пример:
     * sbisTopNavView.footerView.configure<SbisTopNavigationSearchFooterItemView>{
     *     it.searchView.setOnClickListener(...)
     * }
     * */
    object SearchInput : SbisTopNavigationFooterContent()

    /** Вкладки. */
    class Tabs(val configurator: (SbisTabsView) -> Unit = { }) : SbisTopNavigationFooterContent()

    /** Кастомный контент. */
    class Custom(val viewFactory: (Context) -> View) : SbisTopNavigationFooterContent()
}