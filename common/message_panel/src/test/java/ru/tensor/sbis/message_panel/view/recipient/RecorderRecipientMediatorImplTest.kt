package ru.tensor.sbis.message_panel.view.recipient

import org.mockito.kotlin.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.message_panel.viewModel.livedata.recipients.MessagePanelRecipientsData

/**
 * @author vv.chekurda
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class RecorderRecipientMediatorImplTest {
    @Mock
    private lateinit var recipientData : MessagePanelRecipientsData

    @Test
    fun `When recipients not require, then mediator call block and not call onRecipientButtonClick`() {
        val block: () -> Unit = mock()
        whenever(recipientData.requireRecipients).thenReturn(false)
        val mediator = RecorderRecipientMediatorImpl(recipientData)

        mediator.withRecipient(block)

        verify(recipientData,times(0)).onRecipientButtonClick()
        verify(block).invoke()
    }

    @Test
    fun `When require recipients, then mediator call onRecipientButtonClick and not call block`() {
        val block: () -> Unit = mock()
        whenever(recipientData.requireRecipients).thenReturn(true)
        val mediator = RecorderRecipientMediatorImpl(recipientData)

        mediator.withRecipient(block)

        verify(recipientData).onRecipientButtonClick()
        verifyNoMoreInteractions(block)
    }

}