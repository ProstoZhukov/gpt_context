package ru.tensor.sbis.design.tabs.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ru.tensor.sbis.design.tabs.api.SbisTabViewItemContent
import ru.tensor.sbis.design.tabs.api.SbisTabsViewItem
import ru.tensor.sbis.design.tabs.view.SbisTabsView
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl
import ru.tensor.sbis.toolbox_decl.toolbar.NavigationTab
import ru.tensor.sbis.toolbox_decl.toolbar.TabsView
import ru.tensor.sbis.toolbox_decl.toolbar.ToolbarTabsController
import java.util.LinkedList

/**
 * Реализует контракт для взаимодействия [ToolbarTabsController] с [SbisTabsView].
 *
 * @author us.bessonov
 */
internal class SbisTabsViewAdapter(override val view: SbisTabsView) : TabsView {

    private val counters = mutableMapOf<NavxIdDecl, MutableCounter>()

    override val tabSelectionFlow: Flow<NavxIdDecl>
        get() = view.tabSelectionFlow

    override fun setTabs(tabs: List<NavigationTab>, selectedNavxId: NavxIdDecl?) {
        val tabContent = tabs.mapIndexed { i, tab ->
            SbisTabsViewItem(
                LinkedList<SbisTabViewItemContent>().apply {
                    add(SbisTabViewItemContent.Text(PlatformSbisString.Value(tab.title)))
                },
                navxId = tab.navxId,
                isMain = i == 0,
                position = if (tabs.size == 2 && i == tabs.lastIndex) {
                    HorizontalPosition.RIGHT
                } else {
                    HorizontalPosition.LEFT
                }
            )
        }
        view.tabs = LinkedList(tabContent)
        view.selectedTabIndex = tabs.indexOfFirst { it.navxId == selectedNavxId }
    }

    override fun updateTitle(tabs: Set<NavigationTab>) {
        val tabsMap = tabs.associateBy { it.navxId }
        if (view.tabs.none { tabsMap.keys.contains(it.navxId) }) return
        val newTabs = view.tabs
            .map {
                val serviceTab = tabsMap[it.navxId]
                    ?: return@map it
                val content = it.content.map { content ->
                    if (content is SbisTabViewItemContent.Text) {
                        SbisTabViewItemContent.Text(PlatformSbisString.Value(serviceTab.title))
                    } else {
                        content
                    }
                }
                it.copy(content = LinkedList(content))
            }
        view.tabs = LinkedList(newTabs)
    }

    override fun updateTabsVisibility(visibleNavxIds: Set<NavxIdDecl>?) {
        visibleNavxIds ?: return
        view.tabs.forEach {
            fun updateVisibilityByNavxId(navxId: NavxIdDecl) {
                if (visibleNavxIds.isEmpty() || visibleNavxIds.contains(navxId)) {
                    view.showTab(navxId)
                } else {
                    view.hideTab(navxId)
                }
            }

            it.navxId?.let(::updateVisibilityByNavxId)
        }
    }

    override fun setSelection(navxId: NavxIdDecl) {
        view.selectedTabIndex = view.tabs.indexOfFirst { it.navxId == navxId }
    }

    override fun setPrimaryCounter(navxId: NavxIdDecl, value: Int): Boolean {
        val counter = getOrPutCounter(navxId)
            ?: return false
        return counter.primary.tryEmit(value)
    }

    override fun setSecondaryCounter(navxId: NavxIdDecl, value: Int): Boolean {
        val counter = getOrPutCounter(navxId)
            ?: return false
        return counter.secondary.tryEmit(value)
    }

    private fun getOrPutCounter(navxId: NavxIdDecl): MutableCounter? {
        counters[navxId]
            ?.let { return it }

        val counterData = MutableCounter()
        val counter = SbisTabViewItemContent.Counter(counterData.primary, counterData.secondary)

        val tabUpdated = updateTab(navxId) {
            val newContent = LinkedList(content)
            val existingIndex = content.indexOfFirst { it is SbisTabViewItemContent.Counter }

            if (existingIndex < 0) {
                val counterIndex = content.indexOfLast { it is SbisTabViewItemContent.Text } + 1
                newContent.add(counterIndex, counter)
            } else {
                newContent[existingIndex] = counter
            }

            copy(content = newContent)
        }
        return if (!tabUpdated) {
            null
        } else {
            counterData.also { counters[navxId] = it }
        }
    }

    private fun updateTab(navxId: NavxIdDecl, update: SbisTabsViewItem.() -> SbisTabsViewItem): Boolean {
        if (view.tabs.none { it.navxId == navxId }) return false
        val newTabs = view.tabs.map {
            if (it.navxId == navxId) {
                it.update()
            } else {
                it
            }
        }
        view.tabs = LinkedList(newTabs)
        return true
    }
}

private class MutableCounter(
    val primary: MutableStateFlow<Int> = MutableStateFlow(0),
    val secondary: MutableStateFlow<Int> = MutableStateFlow(0)
)