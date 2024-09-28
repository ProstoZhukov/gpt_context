package ru.tensor.sbis.message_panel.viewModel.stateMachine

import org.mockito.kotlin.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule
import ru.tensor.sbis.message_panel.setUpDefaultSubjects
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel
import ru.tensor.sbis.message_panel.viewModel.livedata.MessagePanelLiveData

/**
 * @author vv.chekurda
 * Создан 8/16/2019
 */
@RunWith(JUnitParamsRunner::class)
class SimpleSendStateTest {

    @get:Rule
    val rxSchedulerRule = TrampolineSchedulerRule()

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private lateinit var vm: MessagePanelViewModel<*, *, *>

    @Mock
    private lateinit var liveData: MessagePanelLiveData

    private lateinit var machine: MessagePanelStateMachine<*, *, *>

    @Before
    fun setUp() {
        liveData.setUpDefaultSubjects()
        whenever(vm.liveData).thenReturn(liveData)
        machine = MessagePanelStateMachineImpl(vm, Schedulers.trampoline())
        whenever(vm.stateMachine).thenReturn(machine)
    }

    @Test
    @Parameters(
        "true,true,true",
        "true,false,true",
        "false,true,true",
        "false,false,false"
    )
    fun `Test recipients loading in SimpleSendState`(byUser: Boolean, newMessageMode: Boolean, shouldReload: Boolean) {
        // Fix https://online.sbis.ru/opendoc.html?guid=6e79d304-cfc7-43d3-887c-548100fd897d
        whenever(liveData.newDialogModeEnabled).thenReturn(Observable.just(newMessageMode))

        machine.setState(SimpleSendState(vm))
        machine.fire(EventRecipients(emptyList(), byUser))

        verify(vm, if (shouldReload) times(1) else never()).loadRecipients(any(), eq(byUser), any())
    }
}