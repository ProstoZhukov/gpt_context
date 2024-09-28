package ru.tensor.sbis.toolbox_decl.toolbar

import android.view.View
import kotlinx.coroutines.flow.Flow
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl

/**
 * Контракт компонента, обеспечивающего внешний контроль содержимого верхней навигационной панели.
 * Иными словами, адаптер [View] панели вкладок для взаимодействия с сервисом навигации.
 *
 * @author us.bessonov
 */
interface TabsView {

    /** @SelfDocumented */
    val view: View

    /**
     * События выбора вкладки.
     */
    val tabSelectionFlow: Flow<NavxIdDecl>

    /** @SelfDocumented */
    fun setTabs(tabs: List<NavigationTab>, selectedNavxId: NavxIdDecl?)

    /** @SelfDocumented */
    fun updateTitle(tabs: Set<NavigationTab>)

    /** @SelfDocumented */
    fun updateTabsVisibility(visibleNavxIds: Set<NavxIdDecl>?)

    /** @SelfDocumented */
    fun setSelection(navxId: NavxIdDecl)

    /** @SelfDocumented */
    fun setPrimaryCounter(navxId: NavxIdDecl, value: Int): Boolean

    /** @SelfDocumented */
    fun setSecondaryCounter(navxId: NavxIdDecl, value: Int): Boolean

}