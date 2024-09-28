package ru.tensor.sbis.design.navigation.view.view.tabmenu.item

import android.view.View
import androidx.annotation.StringRes
import ru.tensor.sbis.design.counters.sbiscounter.SbisCounterStyle
import ru.tensor.sbis.design.navigation.view.model.NavigationItemLabel

/**
 * API элемента ННП.
 *
 * @author ma.kolpakov
 */
internal interface TabItemViewApi {

    /** @SelfDocumented */
    var icon: String

    /** @SelfDocumented */
    fun setIconRes(@StringRes iconRes: Int)

    /** Установить день месяца на элементе календаря, если [calendarDay] - null, будет пусто. */
    fun setIconCalendarDay(@StringRes calendarDay: Int?)

    /** @SelfDocumented */
    val text: String

    /** @see NavigationItemLabel */
    var viewLabel: NavigationItemLabel?

    /** @see View.layout */
    fun layout()

    /** @SelfDocumented */
    var counter: Int

    /** @see SbisCounterStyle */
    var counterStyle: SbisCounterStyle

    /** @see TabMenuItemSharedPaints */
    var paints: TabMenuItemSharedPaints
}