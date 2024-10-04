package ru.tensor.sbis.design.profile.personcollagelist.util

import android.content.Context
import ru.tensor.sbis.design.profile.imageview.PersonImageView
import ru.tensor.sbis.design.profile.personcollagelist.PersonCollageLineView
import ru.tensor.sbis.design.profile_decl.person.Shape
import ru.tensor.sbis.design.utils.DEFAULT_POOL_CAPACITY
import ru.tensor.sbis.design.utils.RecentlyUsedViewPool

/**
 * Пул View, используемых в [PersonCollageLineView].
 *
 * @author us.bessonov
 */
class PersonCollageLineViewPool(
    context: Context,
    capacity: Int = DEFAULT_POOL_CAPACITY
) {
    private val factory = PersonCollageLineViewItemFactory(context)

    private val pool = RecentlyUsedViewPool<PersonImageView, String>(capacity, factory)

    /**
     * Задаёт форму создаваемых view.
     */
    internal fun setShape(shape: Shape) {
        factory.shape = shape
    }

    /**
     * @see RecentlyUsedViewPool.get
     */
    internal fun get(key: String?) = pool.get(key)

    /**
     * @see RecentlyUsedViewPool.recycle
     */
    internal fun recycle(view: PersonImageView) = pool.recycle(view)

    /**
     * @see RecentlyUsedViewPool.inflate
     */
    fun inflate(count: Int) = pool.inflate(count)

    /**
     * @see RecentlyUsedViewPool.inflateBy
     */
    fun inflateBy(count: Int) = pool.inflateBy(count)

    /**
     * @see RecentlyUsedViewPool.flush
     */
    fun flush() = pool.flush()
}