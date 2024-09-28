package ru.tensor.sbis.base_components.adapter.universal

import ru.tensor.sbis.base_components.autoscroll.BaseAutoScroller

/**
 * Реализация [BaseAutoScroller.Matcher] для [UniversalBindingItem].
 *
 * @author am.boldinov
 */
class UniversalItemMatcher : BaseAutoScroller.Matcher {

    override fun areItemsTheSame(item1: Any?, item2: Any?): Boolean {
        return item1 is UniversalBindingItem && item2 is UniversalBindingItem
                && item1.itemTypeId == item2.itemTypeId
    }

}