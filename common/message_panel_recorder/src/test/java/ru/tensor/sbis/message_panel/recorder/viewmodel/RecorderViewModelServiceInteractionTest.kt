package ru.tensor.sbis.message_panel.recorder.viewmodel

import org.mockito.kotlin.only
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.message_panel.recorder.viewmodel.listener.RecordViewModelListener
import ru.tensor.sbis.recorder.decl.RecorderService

/**
 * Тестирование заимодействия с сервисом записи
 *
 * @author vv.chekurda
 * Создан 8/5/2019
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class RecorderViewModelServiceInteractionTest {

    private val permissionMediator = RecordPermissionMediatorMock()

    private val recipientMediator = RecordRecipientMediatorMock()

    @Mock
    private lateinit var service: RecorderService

    @Mock
    private lateinit var listener: RecordViewModelListener

    private lateinit var vm: RecorderViewModel

    @Before
    fun setUp() {
        vm = RecorderViewModelImpl(service, permissionMediator, recipientMediator, listener, Schedulers.single())
    }

    @Test
    fun `Nothing on click in DEFAULT state`() {
        vm.onIconClick()

        verifyNoMoreInteractions(service)
    }

    @Test
    fun `Start record on long press (RECORD state)`() {
        vm.onIconLongClick()

        verify(service, only()).startRecord()
    }

    @Test
    fun `Stop record on icon released in RECORD state`() {
        vm.onIconLongClick()
        vm.onIconReleased()

        verify(service).stopRecord()
    }

    @Test
    fun `Cancel record on icon released in CANCEL state`() {
        vm.onIconLongClick()
        vm.onOutOfIcon(true)
        vm.onIconReleased()

        verify(service).cancelRecord()
    }
}