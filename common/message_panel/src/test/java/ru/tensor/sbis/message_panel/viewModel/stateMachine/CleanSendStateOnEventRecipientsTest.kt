package ru.tensor.sbis.message_panel.viewModel.stateMachine

import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule
import ru.tensor.sbis.message_panel.setUpDefaultSubjects
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel
import ru.tensor.sbis.message_panel.viewModel.livedata.MessagePanelLiveData
import java.util.*

/**
 * Тестирование установки списка адресатов до и после ввода пользователя
 *
 * @author vv.chekurda
 * @since 7/22/2019
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class CleanSendStateOnEventRecipientsTest {

    @get:Rule
    val rxSchedulerRule = TrampolineSchedulerRule()

    @Mock
    private lateinit var recipientsList: List<UUID>

    @Mock
    private lateinit var vm: MessagePanelViewModel<*, *, *>

    @Mock
    private lateinit var liveData: MessagePanelLiveData

    private lateinit var machine: MessagePanelStateMachine<*, *, *>

    @Before
    fun setUpMachine() {
        liveData.setUpDefaultSubjects()
        whenever(vm.liveData).thenReturn(liveData)
        whenever(liveData.newDialogModeEnabled).thenReturn(Observable.just(false))
        machine = MessagePanelStateMachineImpl(vm, Schedulers.trampoline())
        whenever(vm.stateMachine).thenReturn(machine)

        machine.start()
    }

    @After
    fun tearDown() {
        machine.stop()
    }

    @Test
    fun `On recipients obtained`() {
        machine.fire(EventEnable())
        machine.fire(EventRecipients(recipientsList))

        verify(vm).loadRecipients(recipientsList)
        assertThat(machine.currentState(), instanceOf(CleanSendState::class.java))
    }

    @Test
    fun `On recipients obtained after user input`() {
        machine.fire(SimpleSendStateEvent())
        machine.fire(EventRecipients(recipientsList))
        verify(vm, never()).loadRecipients(recipientsList)
    }
}