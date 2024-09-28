package ru.tensor.sbis.list.base.data

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.tensor.sbis.list.base.data.utils.SubscriptionHolder
import java.lang.ref.WeakReference

class SubscriptionHolderTest {

    private var subscription: Any? = Any()
    private val weakRef = WeakReference(subscription)
    private var subscriptionHolder: SubscriptionHolder? = SubscriptionHolder(subscription)

    @Test
    fun `keep reference`() {
        assertEquals(subscription, weakRef.get())
    }

    @Test
    fun clear() {
        //act
        subscriptionHolder!!.clear()
        //Чисти ссылку, чтобы gc собрал объект.
        @Suppress("UNUSED_VALUE")
        subscription = null
        Runtime.getRuntime().gc()

        //verify
        assertEquals(null, weakRef.get())
        //Держим ссылку, чтобы не затерлась раньше времени.
        @Suppress("UNUSED_VALUE")
        subscriptionHolder = null
    }
}