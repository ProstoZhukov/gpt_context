package ru.tensor.sbis.widget_player.layout.inline.node

import ru.tensor.sbis.objectpool.base.InflatableConcurrentObjectPool

/**
 * @author am.boldinov
 */
internal class InlineNodePool<SOURCE>(
    capacity: Int,
    private val factory: () -> InlineNode<SOURCE>
) : InflatableConcurrentObjectPool<InlineNode<SOURCE>>(capacity) {

    init {
        inflate(capacity)
    }

    override fun createInstance(): InlineNode<SOURCE> {
        return factory.invoke()
    }
}