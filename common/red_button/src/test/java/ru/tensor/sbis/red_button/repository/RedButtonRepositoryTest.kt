package ru.tensor.sbis.red_button.repository

import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import junit.framework.Assert.assertEquals
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import ru.tensor.sbis.common.rx.RxBus
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule
import ru.tensor.sbis.red_button.data.RedButtonActions
import ru.tensor.sbis.red_button.data.RedButtonData
import ru.tensor.sbis.red_button.data.RedButtonState
import ru.tensor.sbis.red_button.events.RedButtonStateRefresh
import ru.tensor.sbis.red_button.repository.data_source.RedButtonDataSource

/**
 * @author ra.stepanov
 */
@RunWith(JUnitParamsRunner::class)
class RedButtonRepositoryTest {

    private lateinit var repository: RedButtonRepository
    private val redButtonData = spy<RedButtonData>().apply {
        pin = "pin"
        operationUuid = "uuid"
        phone = "phone"
    }
    private val stateRefreshEvent = spy<PublishSubject<RedButtonStateRefresh>>()

    private var rxBus = mock<RxBus> {
        on { subscribe(RedButtonStateRefresh::class.java) } doReturn stateRefreshEvent
    }
    private val dataSource = mock<RedButtonDataSource> {
        on { getState() } doReturn Single.just(RedButtonState.CLICK)
        on { getAction() } doReturn Single.just(RedButtonActions.HIDE_MANAGEMENT)
        on { setPinCode(anyString()) } doReturn Single.just(redButtonData)
        on { off(anyString()) } doReturn Single.just(Unit)
        on { on(anyString(), anyInt(), anyString()) } doReturn Single.just(Unit)
    }

    @get:Rule
    val rule = TrampolineSchedulerRule()

    @Before
    fun setup() {
        repository = RedButtonRepository(rxBus, dataSource)
        Mockito.clearInvocations(rxBus, dataSource)
    }

    @Test
    fun `When getRedButtonData called with empty data and CLICK state, then got accessible data`() {
        setStateWithReset(RedButtonState.CLICK)
        //act
        val testObserver = repository.getState().test()
        //verify
        assert(isEmptyData(getData()))
        testObserver.assertValue { it == RedButtonState.CLICK }
    }

    @Test
    @Parameters("ACCESS_DENIED", "ACCESS_LOCK", "NOT_CLICK", "CLOSE_IN_PROGRESS", "OPEN_IN_PROGRESS")
    fun `When getRedButtonData called with empty data and any not CLICK state, then got accessible data`(param: RedButtonState) {
        setStateWithReset(param)
        //act
        val testObserver = repository.getState().test()
        //verify
        assert(isEmptyData(getData()))
        testObserver.assertValue { it == param }
    }

    @Test
    fun `When turnOn called, then dataSource#setPinCode called`() {
        val pin = "pin"
        //act
        repository.turnOn(pin)
        //verify
        verify(dataSource).setPinCode(pin)
    }

    @Test
    fun `When turnOn called, then data is changed`() {
        repository.turnOn("pin").subscribe()
        //verify
        assertEquals(getData(), redButtonData)
    }

    @Test
    fun `When turnOff called, then dataSource#off called`() {
        val pin = "pin"
        //act
        repository.turnOff(pin)
        //verify
        verify(dataSource).off(pin)
    }

    @Test
    fun `When turnOff called, then data is empty and button is inactive`() {
        repository.turnOff("pin").subscribe()
        //act
        val testObserver = repository.getState().test()
        //verify
        assert(isEmptyData(getData()))
        testObserver.assertValue { it == RedButtonState.OPEN_IN_PROGRESS }
    }

    @Test
    fun `When turnOff called, then emitted RedButtonStateRefresh event in RxJava`() {
        repository.turnOff("pin").subscribe()
        //verify
        verify(rxBus).post(Mockito.isA(RedButtonStateRefresh::class.java))
    }

    @Test
    fun `When confirmOn called, then dataSource#on called`() {
        val sms = "1234"
        //act
        repository.confirmOn(sms)
        //verify
        verify(dataSource).on("", sms.toInt(), "")
    }

    @Test
    fun `When confirmOn called, then data is empty and button is inactive`() {
        repository.confirmOn("1234").subscribe()
        //act
        val testObserver = repository.getState().test()
        //verify
        testObserver.assertValue { it == RedButtonState.CLOSE_IN_PROGRESS }
        assert(isEmptyData(getData()))
    }

    @Test
    fun `When confirmOn called, then emitted RedButtonStateRefresh event in RxJava`() {
        repository.confirmOn("1234").subscribe()
        //verify
        verify(rxBus).post(Mockito.isA(RedButtonStateRefresh::class.java))
    }

    /**
     * Проверка, что объект данных красной кнопки пуст
     * @param data объект данных
     */
    private fun isEmptyData(data: RedButtonData) =
        data.operationUuid.isEmpty() && data.phone.isEmpty() && data.pin.isEmpty()

    /**
     * Установка нового состояния красной кнопки через Kotlin Reflection API
     * @param newState новое состояние красной кнопки
     */
    private fun setStateWithReset(newState: RedButtonState) {
        val method = repository::class.java.getDeclaredMethod("setStateWithReset", RedButtonState::class.java)
        method.isAccessible = true
        method.invoke(repository, newState)
    }

    private fun getData(): RedButtonData {
        val field = repository::class.java.getDeclaredField("redButtonData")
        field.isAccessible = true
        return field.get(repository) as RedButtonData
    }
}