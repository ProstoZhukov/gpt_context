package ru.tensor.sbis.business.common.domain

import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.tensor.sbis.business.common.ui.base.event.ToastEvent
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule

internal class ToastHelperTest {

    @get:Rule
    @Suppress("RedundantVisibilityModifier")
    public var rule = TrampolineSchedulerRule()

    private class TestException : Throwable()

    private val defaultId = "Id"
    private val defaultMessage = "text"

    private var toastHelper = ToastHelper()

    @Before
    fun setUp() {
        toastHelper = ToastHelper()
    }

    @Test
    fun `Post only message`() {
        val testObserver = TestObserver<ToastEvent>()

        toastHelper.observe().subscribe(testObserver)
        toastHelper.post(defaultMessage)

        testObserver.assertValue(ToastEvent("", defaultMessage, null))
    }

    @Test
    fun `Post message and id`() {
        val testObserver = TestObserver<ToastEvent>()

        toastHelper.observe().subscribe(testObserver)
        toastHelper.post(defaultMessage, defaultId)

        testObserver.assertValue(ToastEvent(defaultId, defaultMessage, null))
    }

    @Test
    fun `Post error`() {
        val exception = TestException()
        val testObserver = TestObserver<ToastEvent>()

        toastHelper.observe().subscribe(testObserver)
        toastHelper.post(defaultMessage, exception)

        testObserver.assertValue(ToastEvent("", defaultMessage, exception))
    }

    @Test
    fun `Do not receive error if excluded`() {
        val toastHelper = ToastHelper().apply { addExclusion(TestException::class.java) }
        val exception = TestException()
        val testObserver = TestObserver<ToastEvent>()

        toastHelper.observe().subscribe(testObserver)
        toastHelper.post(defaultMessage, exception)

        testObserver.assertEmpty()
    }

    @Test
    fun `Receive error if was excluded and then cleared`() {
        val toastHelper = ToastHelper().apply {
            addExclusion(TestException::class.java)
            clearExclusions()
        }
        val exception = TestException()
        val testObserver = TestObserver<ToastEvent>()

        toastHelper.observe().subscribe(testObserver)
        toastHelper.post(defaultMessage, exception)

        testObserver.assertValue(ToastEvent("", defaultMessage, exception))
    }

    @Test
    fun `Receive if ids the same`() {
        val testObserver = TestObserver<ToastEvent>()

        toastHelper.observe(defaultId).subscribe(testObserver)
        toastHelper.post(defaultMessage, defaultId)

        testObserver.assertValue(ToastEvent(defaultId, defaultMessage, null))
    }

    @Test
    fun `Do not receive if ids are different`() {
        val testObserver = TestObserver<ToastEvent>()

        toastHelper.observe(defaultId).subscribe(testObserver)
        toastHelper.post(defaultMessage, "id888")

        testObserver.assertEmpty()
    }

    @Test
    fun `Receive only events with the same id if many was emited`() {
        val testObserver = TestObserver<ToastEvent>()

        toastHelper.observe(defaultId).subscribe(testObserver)
        toastHelper.post(defaultMessage, "id111")
        toastHelper.post(defaultMessage, defaultId)
        toastHelper.post(defaultMessage, "id333")
        toastHelper.post(defaultMessage, "id444")

        testObserver.assertValueCount(1)
        testObserver.assertValue(ToastEvent(defaultId, defaultMessage, null))
    }

    @Test
    fun `Post postponed event after subscription`() {
        val testObserver = TestObserver<ToastEvent>()
        toastHelper.post(defaultMessage)

        toastHelper.observe().subscribe(testObserver)

        testObserver.assertValueCount(1)
        testObserver.assertValue { it.text == defaultMessage }
    }

    @Test
    fun `Post postponed event once for first observer`() {
        val testObserver1 = TestObserver<ToastEvent>()
        val testObserver2 = TestObserver<ToastEvent>()
        toastHelper.post(defaultMessage)

        toastHelper.observe().subscribe(testObserver1)
        toastHelper.observe().subscribe(testObserver2)

        testObserver1.assertValueCount(1)
        testObserver2.assertNoValues()
    }

    @Test
    fun `Continue post next events after postponed event`() {
        val testObserver1 = TestObserver<ToastEvent>()
        val testObserver2 = TestObserver<ToastEvent>()
        toastHelper.post(defaultMessage)

        toastHelper.observe().subscribe(testObserver1)
        toastHelper.post("text2")
        toastHelper.post("text3")
        toastHelper.post("text4")

        testObserver1.assertValueCount(4)
        testObserver2.assertNoValues()
    }
}
