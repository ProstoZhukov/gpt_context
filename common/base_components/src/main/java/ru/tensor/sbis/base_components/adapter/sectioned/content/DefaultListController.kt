package ru.tensor.sbis.base_components.adapter.sectioned.content

/**
 * @author am.boldinov
 */
open class DefaultListController : ListController {

    override fun knownHead(): Boolean = true

    override fun knownTail(): Boolean = true

    override fun onVisibleRangeChanged(firstVisible: Int, lastVisible: Int, direction: Int) {

    }
}