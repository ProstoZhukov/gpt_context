package ru.tensor.sbis.message_panel.viewModel.livedata.attachments

import org.mockito.kotlin.whenever
import io.reactivex.BackpressureStrategy.ERROR
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Test

import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel
import ru.tensor.sbis.message_panel.viewModel.stateMachine.MessagePanelStateMachine

/**
 * @author vv.chekurda
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class MessagePanelAttachmentsControlsImplTest {

    private val isEnableSubject = PublishSubject.create<Boolean>()

    @Mock
    private lateinit var stateMachine: MessagePanelStateMachine<*, *, *>

    @Mock
    private lateinit var viewModel: MessagePanelViewModel<*, *, *>

    private lateinit var attachmentControls: MessagePanelAttachmentsControlsImpl

    @Before
    fun setUp() {
        whenever(viewModel.stateMachine).thenReturn(stateMachine)
        whenever(stateMachine.isEnabled).thenReturn(isEnableSubject.toFlowable(ERROR))

        attachmentControls = MessagePanelAttachmentsControlsImpl(viewModel)
    }

    @Test
    fun `When message panel enabled, then attach control should be enabled`() {
        val attachmentButtonEnabled = attachmentControls.attachmentsButtonEnabled.test()

        isEnableSubject.onNext(true)

        attachmentButtonEnabled.assertValue(true)
    }

    @Test
    fun `When message panel disabled, then attach control should be disabled`() {
        val attachmentButtonEnabled = attachmentControls.attachmentsButtonEnabled.test()

        isEnableSubject.onNext(false)

        attachmentButtonEnabled.assertValue(false)
    }
}