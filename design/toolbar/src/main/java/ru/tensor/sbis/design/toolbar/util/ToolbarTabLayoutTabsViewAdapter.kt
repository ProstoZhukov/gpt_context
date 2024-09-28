package ru.tensor.sbis.design.toolbar.util

import kotlinx.coroutines.flow.Flow
import ru.tensor.sbis.design.toolbar.ToolbarTabLayout
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl
import ru.tensor.sbis.toolbox_decl.toolbar.NavigationTab
import ru.tensor.sbis.toolbox_decl.toolbar.TabsView
import kotlin.math.absoluteValue

/**
 * Реализует контракт для взаимодействия [ToolbarTabsControllerImpl] с [ToolbarTabLayout].
 *
 * @author us.bessonov
 */
internal class ToolbarTabLayoutTabsViewAdapter(override val view: ToolbarTabLayout) : TabsView {

    override val tabSelectionFlow: Flow<NavxIdDecl>
        get() = view.selectionChangeFlow

    override fun setTabs(tabs: List<NavigationTab>, selectedNavxId: NavxIdDecl?) {

        val tabModels = LinkedHashMap<Int, ToolbarTabLayout.ToolbarTab>().apply {
            tabs.forEach {
                val id = navxIdToTabId(it.navxId)
                put(id, ToolbarTabLayout.ToolbarTab(id, it.title, navxId = it.navxId))
            }
        }
        if (!view.isApplyCheckPermissions) {
            view.setTabs(tabModels, navxIdToTabId(selectedNavxId))
        }
    }

    /**
     * Не поддерживаем функциональность, т.к. компонент неактуален.
     */
    override fun updateTitle(tabs: Set<NavigationTab>) = Unit

    override fun updateTabsVisibility(visibleNavxIds: Set<NavxIdDecl>?) =
        view.updateTabsVisibility(visibleNavxIds)

    override fun setSelection(navxId: NavxIdDecl) =
        view.setSelection(navxId)

    override fun setPrimaryCounter(navxId: NavxIdDecl, value: Int): Boolean =
        view.setPrimaryCounter(navxIdToTabId(navxId), value)

    override fun setSecondaryCounter(navxId: NavxIdDecl, value: Int): Boolean =
        view.setSecondaryCounter(navxIdToTabId(navxId), value)

    private fun navxIdToTabId(navxId: NavxIdDecl?) = navxId?.hashCode()?.absoluteValue ?: -1
}