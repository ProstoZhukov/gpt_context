package ru.tensor.sbis.message_panel.recorder.viewmodel

import org.mockito.kotlin.*
import io.reactivex.schedulers.TestScheduler
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.message_panel.recorder.RECORDER_HINT_HIDE_DELAY
import ru.tensor.sbis.message_panel.recorder.util.DEFAULT_TIME
import ru.tensor.sbis.message_panel.recorder.viewmodel.listener.RecordViewModelListener
import ru.tensor.sbis.recorder.decl.RecorderService
import ru.tensor.sbis.recorder.decl.RecorderViewListener
import java.util.concurrent.TimeUnit

/**
 * Тестирование реакций [RecorderViewModelImpl] на вызовы методов [RecorderViewModel]
 *
 * @author vv.chekurda
 * @since 7/25/2019
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class RecorderViewModelContractTest {

    private val permissionMediator = RecordPermissionMediatorMock()

    private val recipientMediator = RecordRecipientMediatorMock()

    private val scheduler = TestScheduler()

    @Mock
    private lateinit var service: RecorderService

    @Mock
    private lateinit var listener: RecordViewModelListener

    private lateinit var vm: RecorderViewModelImpl

    @Before
    fun setUp() {
        vm = RecorderViewModelImpl(service, permissionMediator, recipientMediator, listener, scheduler, scheduler)
    }

    @Test
    fun `Default state test`() {
        scheduler.triggerActions()
        verify(listener).onShowHint(false)
        verify(listener).onTimeChanged(DEFAULT_TIME)
        verifyNoMoreInteractions(listener)
    }

    @Test
    fun `Show hint on icon tap`() {
        var captor = argumentCaptor<Boolean>()

        vm.onIconClick()

        scheduler.advanceTimeBy(RECORDER_HINT_HIDE_DELAY - 1L, TimeUnit.MILLISECONDS)
        // до последней миллисекунды ничего не поменялось
        verify(listener, times(2)).onShowHint(captor.capture())
        assertThat(captor.allValues, equalTo(listOf(false, true)))

        // после задержки возвращается начение по умолчанию
        scheduler.advanceTimeBy(1L, TimeUnit.MILLISECONDS)

        captor = argumentCaptor()
        verify(listener, times(3)).onShowHint(captor.capture())
        assertThat(
            captor.allValues, equalTo(
                listOf(
                    // значение по умолчанию
                    false,
                    // нажатие устанавливает подсказку
                    true,
                    // в конце возвращаемся к значению по умолчанию
                    false
                )
            )
        )
    }

    @Test
    fun `Show time test`() {
        scheduler.triggerActions()
        var captor = argumentCaptor<String>()

        vm.onIconLongClick()

        // нажатие не меняет время
        verify(listener).onTimeChanged(captor.capture())
        assertThat(captor.allValues, equalTo(listOf(DEFAULT_TIME)))

        scheduler.advanceTimeBy(999, TimeUnit.MILLISECONDS)
        // до первой секунды ничего не менялось
        captor = argumentCaptor()
        verify(listener).onTimeChanged(captor.capture())
        assertThat(captor.allValues, equalTo(listOf(DEFAULT_TIME)))

        scheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)
        captor = argumentCaptor()
        verify(listener, times(2)).onTimeChanged(captor.capture())
        assertThat(captor.allValues, equalTo(listOf(DEFAULT_TIME, "00:01")))

        scheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        captor = argumentCaptor()
        verify(listener, times(3)).onTimeChanged(captor.capture())
        assertThat(captor.allValues, equalTo(listOf(DEFAULT_TIME, "00:01", "00:02")))
    }

    @Test
    fun `Default time on icon released`() {
        val captor = argumentCaptor<String>()

        vm.onIconLongClick()
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        vm.onIconReleased()
        scheduler.triggerActions()
        verify(listener, times(3)).onTimeChanged(captor.capture())
        assertThat(
            captor.allValues, equalTo(
                listOf(
                    DEFAULT_TIME, "00:01",
                    DEFAULT_TIME
                )
            )
        )
    }

    @Test
    fun `Icon state not changed on tap`() {
        vm.onIconClick()

        verify(listener, never()).onStateChanged(any())
    }

    @Test
    fun `Icon state changed to RECORD on long tap`() {
        val captor = argumentCaptor<RecorderIconState>()

        vm.onIconLongClick()

        verify(listener).onStateChanged(captor.capture())
        assertThat(captor.allValues, equalTo(listOf(RecorderIconState.RECORD)))
    }

    @Test(expected = IllegalStateException::class)
    fun `Exception on icon released with DEFAULT state`() {
        vm.onIconReleased()
    }

    @Test
    fun `Icon state changed to DEFAULT state on icon released on RECORD state`() {
        val captor = argumentCaptor<RecorderIconState>()

        vm.onIconLongClick()
        vm.onIconReleased()

        verify(listener, times(2)).onStateChanged(captor.capture())
        assertThat(captor.allValues, equalTo(listOf(RecorderIconState.RECORD, RecorderIconState.DEFAULT)))
    }

    @Test(expected = IllegalStateException::class)
    fun `Exception when finger out of icon on DEFAULT state`() {
        vm.onOutOfIcon(true)
    }

    @Test(expected = IllegalStateException::class)
    fun `Exception when finger is not out of icon on DEFAULT state`() {
        vm.onOutOfIcon(false)
    }

    @Test
    fun `Icon state changed to CANCEL state when finger out of icon on RECORD state`() {
        val captor = argumentCaptor<RecorderIconState>()

        vm.onIconLongClick()
        vm.onOutOfIcon(true)

        verify(listener, times(2)).onStateChanged(captor.capture())
        assertThat(captor.allValues, equalTo(listOf(RecorderIconState.RECORD, RecorderIconState.CANCEL)))
    }

    @Test
    fun `Icon state changed to RECORD state when finger return on icon on CANCEL state`() {
        val captor = argumentCaptor<RecorderIconState>()

        vm.onIconLongClick()
        vm.onOutOfIcon(true)
        vm.onOutOfIcon(false)

        verify(listener, times(3)).onStateChanged(captor.capture())
        assertThat(
            captor.allValues, equalTo(
                listOf(
                    RecorderIconState.RECORD,
                    RecorderIconState.CANCEL,
                    RecorderIconState.RECORD
                )
            )
        )
    }

    @Test
    fun `Icon state didn't changed when finger out of icon on CANCEL state`() {
        val captor = argumentCaptor<RecorderIconState>()

        vm.onIconLongClick()
        vm.onOutOfIcon(true)
        vm.onOutOfIcon(true)

        verify(listener, times(2)).onStateChanged(captor.capture())
        assertThat(captor.allValues, equalTo(listOf(RecorderIconState.RECORD, RecorderIconState.CANCEL)))
    }

    @Test
    fun `Icon state didn't changed when finger return on icon on RECORD state`() {
        val captor = argumentCaptor<RecorderIconState>()

        vm.onIconLongClick()
        vm.onOutOfIcon(false)

        verify(listener).onStateChanged(captor.capture())
        assertThat(captor.allValues, equalTo(listOf(RecorderIconState.RECORD)))
    }

    @Test
    fun `Hint not invoked without permission`() {
        permissionMediator.allow = false
        clearInvocations(listener)

        vm.onIconClick()

        verifyNoMoreInteractions(listener)
    }

    @Test
    fun `Record not started without permission`() {
        permissionMediator.allow = false
        clearInvocations(listener)

        assertThat(vm.onIconLongClick(), equalTo(false))

        verifyNoMoreInteractions(listener)
    }

    /**
     * Не нужно завть подписку [RecorderViewListener.onRecordCompleted] во время инициализации.
     * Подписчики могут быть не готовы
     *
     * Fix https://online.sbis.ru/opendoc.html?guid=1e01d919-af19-441f-bf75-f1f12fe840b4
     */
    @Test
    fun `When view model initialised, then listener shouldn't receive state change`() {
        verify(listener, never()).onStateChanged(any())
    }
}