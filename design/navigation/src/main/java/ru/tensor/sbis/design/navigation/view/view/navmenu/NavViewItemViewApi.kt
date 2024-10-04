package ru.tensor.sbis.design.navigation.view.view.navmenu

import androidx.annotation.StringRes
import io.reactivex.disposables.Disposable
import ru.tensor.sbis.design.navigation.view.model.NavigationItemLabel
import ru.tensor.sbis.design.navigation.view.model.NavigationViewModel
import ru.tensor.sbis.design.navigation.view.model.content.NavigationItemContent

/**
 * API элемента списка аккордеона.
 *
 * @author ma.kolpakov
 */
internal interface NavViewItemViewApi {

    /** @SelfDocumented */
    var iconButtonClickListener: (() -> Unit)?

    /** @SelfDocumented */
    var expandIconButtonClickListener: (() -> Unit)?

    /** @SelfDocumented */
    var counter: String

    /** @SelfDocumented */
    var counterSecondary: String

    /** @SelfDocumented */
    var contentVisible: Boolean

    /** @SelfDocumented */
    var contentExpanded: Boolean

    /** @SelfDocumented */
    var content: NavigationItemContent?

    /** @SelfDocumented */
    var buttonIconRes: Int

    /** @see NavigationViewModel.ordinal */
    var ordinal: Int

    /** @SelfDocumented */
    fun setIconRes(@StringRes iconRes: Int)

    /** Установить день месяца на элементе календаря, если [calendarDay] - null, будет пусто. */
    fun setIconCalendarDay(@StringRes calendarDay: Int?)

    /** @SelfDocumented */
    fun setLabel(label: NavigationItemLabel?)

    /** Установить выравнивание относительно родительского элемента списка [NavigationViewModel.parentOrdinal] */
    fun setRightAlignment(parentOrdinal: Int): Disposable?
}