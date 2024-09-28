package ru.tensor.sbis.message_panel.attachments

import org.mockito.kotlin.*
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*
import kotlin.random.Random

/**
 * Тестирование контракта обновления статуса загрузки от контроллера
 *
 * @author vv.chekurda
 * Создан 10/9/2019
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
@Ignore("TODO починить https://online.sbis.ru/opendoc.html?guid=ba5c3817-c121-4bee-8bdb-925218413930")
class AttachmentAddingEventsHandlerTest {
/*
    private val attachmentUUID = UUID.randomUUID().toString()
    private val attachmentDiskUUID = UUID.randomUUID().toString()

    @Mock
    private lateinit var progressHandler: AttachmentProgressHandler

    @Mock
    private lateinit var errorHandler: AttachmentErrorHandler

    @InjectMocks
    private lateinit var callback: AttachmentAddingEventsHandler

    @Test
    fun `When execute on null params, then should be none interactions with handlers`() {
        callback.execute(null)

        verifyNoMoreInteractions(progressHandler)
        verifyNoMoreInteractions(errorHandler)
    }

    @Test
    fun `When execute on empty params, then should be none interactions with handlers`() {
        callback.execute(hashMapOf())

        verifyNoMoreInteractions(progressHandler)
        verifyNoMoreInteractions(errorHandler)
    }

    @Test
    fun `When uploading completed, then progress handler should be invoked with max progress`() {
        val params = hashMapOf(
            ATTACHMENT_UUID_KEY to attachmentUUID,
            ATTACHMENT_STATUS_KEY to ATTACHMENT_STATUS_UPLOADED,
            ATTACHMENT_DISK_UUID_KEY to attachmentDiskUUID
        )

        callback.execute(params)

        verify(progressHandler, only()).invoke(attachmentUUID, ATTACHMENT_FULL, attachmentDiskUUID)
        verifyNoMoreInteractions(errorHandler)
    }

    @Test
    fun `When new progress obtained, then progress handler should be invoked with this value`() {
        val progress = Random.nextInt(99)
        val params = hashMapOf(
            ATTACHMENT_UUID_KEY to attachmentUUID,
            ATTACHMENT_PROGRESS_KEY to progress.toString()
        )

        callback.execute(params)

        verify(progressHandler, only()).invoke(attachmentUUID, progress, null)
        verifyNoMoreInteractions(errorHandler)
    }

    @Test
    fun `When error occur, then error handler should be invoked`() {
        val params = hashMapOf(
            ATTACHMENT_UUID_KEY to attachmentUUID,
            ATTACHMENT_STATUS_KEY to ATTACHMENT_STATUS_ERROR
        )

        callback.execute(params)

        verify(errorHandler, only()).invoke(attachmentUUID)
        verifyNoMoreInteractions(progressHandler)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `When execute without attachment UUID, then exception should be thrown`() {
        val params: HashMap<String, String> = mock()
        whenever(params.isEmpty()).thenReturn(false)

        callback.execute(params)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `When execute without status and progress, then exception should be thrown`() {
        val params = hashMapOf(
            ATTACHMENT_UUID_KEY to attachmentUUID
        )

        callback.execute(params)
    }
 */
}