package ru.tensor.sbis.message_panel.viewModel.livedata.attachments

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.attachments.models.property.MAX_PROGRESS
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.message_panel.attachments.MessagePanelAttachmentHelper
import ru.tensor.sbis.message_panel.viewModel.livedata.MessagePanelNotifications
import ru.tensor.sbis.message_panel.viewModel.livedata.keyboard.KeyboardEventMediator
import java.util.UUID
import kotlin.random.Random

/**
 * @author vv.chekurda
 */
@RunWith(MockitoJUnitRunner::class)
class MessagePanelAttachmentsDataImplTest {

    @Mock
    private lateinit var attachmentHelper: MessagePanelAttachmentHelper
    @Mock
    private lateinit var keyboardEventMediator: KeyboardEventMediator
    @Mock
    private lateinit var resourceProvider: ResourceProvider
    @Mock
    private lateinit var notifications: MessagePanelNotifications

    @InjectMocks
    private lateinit var attachmentsData: MessagePanelAttachmentsDataImpl

    @Test
    fun `When new attachment progress received, then it should be delivered`() {
        val progress1 = Random.nextInt(MAX_PROGRESS)
        val id1 = UUID.randomUUID()
        val progress2 = Random.nextInt(MAX_PROGRESS)
        val id2 = UUID.randomUUID()
        val progressObserver = attachmentsData.locationProgressUpdater.test()

        attachmentsData.setAttachmentProgress(id2 to progress2)
        attachmentsData.setAttachmentProgress(id1 to progress1)

        progressObserver
            .assertValueAt(0, id2 to progress2)
            .assertValueAt(1, id1 to progress1)
    }
}