package ru.tensor.sbis.design.toolbar.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import ru.tensor.sbis.design.toolbar.ToolbarTabLayout
import ru.tensor.sbis.design.utils.extentions.doOnDetachedFromWindow
import ru.tensor.sbis.toolbox_decl.navigation.NavigationItemHostScreen
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl
import ru.tensor.sbis.toolbox_decl.toolbar.NavigationTab
import ru.tensor.sbis.toolbox_decl.toolbar.TabsView
import ru.tensor.sbis.toolbox_decl.toolbar.ToolbarTabsController

/**
 * Управляет видимостью вкладок в компонентах [ToolbarTabLayout], отображаемых на экране.
 *
 * @author us.bessonov
 */
internal class ToolbarTabsControllerImpl : ToolbarTabsController {

    private val attachedViews = mutableSetOf<TabsView>()

    private var visibleNavxIds = emptySet<NavxIdDecl>()

    private var visibleTabs = setOf<NavigationTab>()

    private val disabledTabIds = mutableSetOf<NavxIdDecl>()

    override val tabSelectionFlow = MutableStateFlow<NavxIdDecl?>(null)

    private val scope = CoroutineScope(SupervisorJob())

    // Предотвращает зацикливание при обновлении вкладок.
    private var areTabsUpdating = false

    /** @SelfDocumented */
    override fun attachView(tabsView: TabsView) {
        if (attachedViews.contains(tabsView)) return
        attachedViews.add(tabsView)
        val job = scope.launch {
            tabsView.tabSelectionFlow
                .filterNotNull()
                .collect {
                    if (it != tabSelectionFlow.value) {
                        tabSelectionFlow.tryEmit(it)
                    }
                }
        }
        updateTabs(visibleTabs)
        tabsView.view.doOnDetachedFromWindow {
            attachedViews.remove(tabsView)
            job.cancel()
        }
    }

    /** @SelfDocumented */
    override fun onTabsChanged(view: TabsView) {
        updateViews(visibleTabs)
        applyTabsVisibility(view)
    }

    override fun setTabDisabled(navxId: NavxIdDecl, isDisabled: Boolean) {
        val wasDisabled = disabledTabIds.contains(navxId)
        if (isDisabled) {
            disabledTabIds.add(navxId)
        } else {
            disabledTabIds.remove(navxId)
        }
        if (disabledTabIds.contains(navxId) != wasDisabled && visibleTabs.isNotEmpty()) {
            updateTabs(visibleTabs)
        }
    }

    override fun setPrimaryCounter(navxId: NavxIdDecl, count: Int) {
        attachedViews.forEach {
            if (it.setPrimaryCounter(navxId, count)) return@forEach
        }
    }

    override fun setSecondaryCounter(navxId: NavxIdDecl, count: Int) {
        attachedViews.forEach {
            if (it.setSecondaryCounter(navxId, count)) return@forEach
        }
    }

    override fun resetDisabledTabs() {
        if (disabledTabIds.isEmpty()) return
        disabledTabIds.clear()
        if (visibleTabs.isNotEmpty()) {
            updateTabs(visibleTabs)
        }
    }

    override fun setSelectedTabValue(navxId: NavxIdDecl?) {
        tabSelectionFlow.tryEmit(navxId)
    }

    override fun updateTabs(tabs: Set<NavigationTab>) {
        updateViews(tabs)
        updateSelectionInIndirectNavigationItemHostPlacedTabs()
        updateTabsVisibility(getActiveTabs(tabs).mapNotNull { it.navxId }.toSet())
        visibleTabs = tabs
    }

    private fun updateViews(tabs: Set<NavigationTab>) {
        val activeTabs = getActiveTabs(tabs)
            .groupBy { it.parentId }
        if (activeTabs.isEmpty() || areTabsUpdating) return
        areTabsUpdating = true
        val tabsViewWithHost = attachedViews.map { tabsView ->
            val host = try {
                FragmentManager.findFragment<Fragment>(tabsView.view) as? NavigationItemHostScreen
            } catch (e: IllegalStateException) {
                null
            }
            tabsView to host
        }
        tabsViewWithHost.forEach { (tabsView, host) ->
            if (host != null) {
                activeTabs[host.navxId]?.let {
                    tabsView.setTabs(it, getSelectedNavxId(it))
                }
            } else {
                tabsView.updateTitle(tabs)
            }
        }
        areTabsUpdating = false
    }

    private fun getActiveTabs(tabs: Set<NavigationTab>) = tabs.filterNot { disabledTabIds.contains(it.navxId) }

    // TODO: 06.12.23 Будет удалено по https://online.sbis.ru/opendoc.html?guid=251e0863-84ab-4c9d-8789-bc94d910bf70&client=3
    private fun updateSelectionInIndirectNavigationItemHostPlacedTabs() {
        val selectedId = tabSelectionFlow.value
            ?: return
        attachedViews
            .filter { FragmentManager.findFragment<Fragment>(it.view).parentFragment is NavigationItemHostScreen }
            .forEach {
                it.setSelection(selectedId)
            }
    }

    private fun getSelectedNavxId(tabs: List<NavigationTab>) = tabSelectionFlow.value.takeIf { selectedNavxId ->
        tabs.any { it.navxId == selectedNavxId }
    } ?: tabs.first().navxId

    private fun applyTabsVisibility(view: TabsView) {
        view.updateTabsVisibility(visibleNavxIds.takeUnless { it.isEmpty() })
    }

    private fun updateTabsVisibility(visibleNavxIds: Set<NavxIdDecl>) {
        this.visibleNavxIds = visibleNavxIds
        attachedViews.forEach(::applyTabsVisibility)
    }
}