package ru.tensor.sbis.design.tabs.util

import android.content.Context
import android.view.View
import ru.tensor.sbis.design.tabs.R
import ru.tensor.sbis.design.tabs.api.SbisTabsViewItem
import ru.tensor.sbis.design.tabs.tabItem.SbisTabItemStyleHolder
import ru.tensor.sbis.design.tabs.tabItem.SbisTabView
import java.util.LinkedList

/**
 * Пулл, для переиспользования [SbisTabView].
 *
 * @author da.zolotarev
 */
internal class TabViewPool(
    private val context: Context,
    internal var tabStyleHolder: SbisTabItemStyleHolder,
    private val mainTabStyleHolder: SbisTabItemStyleHolder
) {
    private val tabFactory = { styleHolder: SbisTabItemStyleHolder ->
        SbisTabView(context, styleHolder)
    }

    private val tabPool: LinkedList<SbisTabView> = LinkedList<SbisTabView>()
    private val mainTabPool: LinkedList<SbisTabView> = LinkedList<SbisTabView>()

    /**
     * Незанятые id для вкладок.
     */
    private var emptyTabsIds: MutableSet<Int> = mutableSetOf()
    private var isMainTabIdReserved: Boolean = false

    /**
     * Взять view из пула, либо создать новую, если там пусто.
     */
    fun get(model: SbisTabsViewItem) = if (model.isMain) {
        mainTabPool.poll(tabFactory, mainTabStyleHolder).apply {
            id = if (!isMainTabIdReserved) R.id.sbis_tabs_view_main_tab else View.NO_ID
            isMainTabIdReserved = true
        }
    } else {
        val styleHolder = tabStyleHolder
        tabPool.poll(tabFactory, styleHolder).apply {
            id = emptyTabsIds.firstOrNull() ?: View.NO_ID
            emptyTabsIds.remove(id)
        }
    }.apply {
        data = model
    }

    /**
     * Добавить [view] в пулл.
     */
    fun recycle(view: SbisTabView, isMain: Boolean) {
        view.id = View.NO_ID
        if (isMain) {
            mainTabPool.addIfExistSpace(view)
        } else {
            tabPool.addIfExistSpace(view)
        }
    }

    /**
     * Обновить незанятые id для вкладок.
     */
    fun fullEmptyTabsId() {
        emptyTabsIds = mutableSetOf(
            R.id.sbis_tabs_view_tab_1,
            R.id.sbis_tabs_view_tab_2,
            R.id.sbis_tabs_view_tab_3,
            R.id.sbis_tabs_view_tab_4,
            R.id.sbis_tabs_view_tab_5,
            R.id.sbis_tabs_view_tab_6,
            R.id.sbis_tabs_view_tab_7
        )
        isMainTabIdReserved = false
    }

    private fun LinkedList<SbisTabView>.addIfExistSpace(view: SbisTabView) {
        if (this.size >= POOL_CAPACITY) return
        this.add(view)
    }

    private fun LinkedList<SbisTabView>.poll(
        viewFactory: (SbisTabItemStyleHolder) -> SbisTabView,
        styleHolder: SbisTabItemStyleHolder
    ) =
        this.poll() ?: viewFactory(styleHolder)

    companion object {
        private const val POOL_CAPACITY = 15
    }
}