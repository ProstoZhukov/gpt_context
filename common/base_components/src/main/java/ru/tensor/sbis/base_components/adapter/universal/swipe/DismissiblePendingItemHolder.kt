@file:Suppress("KDocUnresolvedReference")

package ru.tensor.sbis.base_components.adapter.universal.swipe

import ru.tensor.sbis.base_components.adapter.universal.UniversalBindingItem

/**
 * Хранилище временно-удаленного элемента списка,
 * предоставляет проксирующие методы для работы с [BaseListAbstractTwoWayPaginationPresenter]
 *
 * @author am.boldinov
 */
class DismissiblePendingItemHolder {

    internal var pendingItemId: String? = null
    internal var pendingItem: UniversalBindingItem? = null
    internal var restoredItemPosition = -1
    internal val unconfirmedDeletedIds = mutableSetOf<String>()

    internal fun reset() {
        pendingItemId = null
        pendingItem = null
        restoredItemPosition = -1
    }

    /**@SelfDocumented*/
    fun processLoadingNewerPageResult(items: List<UniversalBindingItem>) {
        attachHolder(items) {
            it.reflowPage()
        }
    }

    /**@SelfDocumented*/
    fun processLoadingOlderPageResult(items: List<UniversalBindingItem>) {
        attachHolder(items) {
            it.reflowPage()
        }
    }

    /**@SelfDocumented*/
    fun processUpdatingDataListResult(items: List<UniversalBindingItem>) {
        attachHolder(items) {
            it.reflowAll()
        }
    }

    private inline fun attachHolder(
        items: List<UniversalBindingItem>,
        callback: (UniversalDismissibleItemList) -> Unit
    ) {
        if (items is UniversalDismissibleItemList) {
            items.attachPendingItemHolder(this)
            callback(items)
        } else {
            reset()
        }
    }
}