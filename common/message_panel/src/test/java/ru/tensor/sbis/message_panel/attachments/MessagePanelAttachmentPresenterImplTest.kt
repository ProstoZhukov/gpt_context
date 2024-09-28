package ru.tensor.sbis.message_panel.attachments

import org.junit.Ignore
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.message_panel.viewModel.livedata.MessagePanelLiveDataImpl

private const val FILE_NAME = "test_file.txt"
private const val FILE_URI = "file:///test/path/$FILE_NAME"
private const val UPLOAD_ERROR = "Test message: Error uploading attachments"
private const val TOO_BIG = "Test message: File size exceeds the limit"

/**
 * Тест использует связку из классов [MessagePanelAttachmentPresenterImpl] и [MessagePanelLiveDataImpl] так как
 * последний выступает в роли подписки на разные события. Для снижения нагрузки по реализации моков используется
 * реальный объект [MessagePanelLiveDataImpl]
 *
 * @author vv.chekurda
 * @since 12/9/2019
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
@Ignore("TODO починить https://online.sbis.ru/opendoc.html?guid=ba5c3817-c121-4bee-8bdb-925218413930")
class MessagePanelAttachmentPresenterImplTest {
/*
    private val conversationUuid: UUID = UUID.randomUUID()
    private val attachmentUuid: UUID = UUID.randomUUID()
    private val fileSize = Random.nextLong(1L, MAX_MESSAGE_ATTACHMENT_SIZE_MB.toLong())

    @Mock
    private lateinit var viewModel: MessagePanelViewModel
    @Mock
    private lateinit var interactor: MessagePanelRecipientsInteractor
    @Mock
    private lateinit var fileUriUtil: FileUriUtil
    @Mock
    private lateinit var resourceProvider: ResourceProvider

    /**
     * Реальный объект для подписок на получение вложений
     */
    private lateinit var liveData: MessagePanelLiveData
    private lateinit var presenter: MessagePanelAttachmentPresenterImpl

    @Before
    fun setUp() {
        whenever(interactor.events(any())).thenReturn(Observable.empty())

        liveData = initLiveData(viewModel, info = CoreConversationInfo(conversationUuid = conversationUuid))
        whenever(viewModel.liveData).thenReturn(liveData)
        liveData.setConversationUuid(conversationUuid)

        whenever(viewModel.resourceProvider).thenReturn(resourceProvider)

        presenter = MessagePanelAttachmentPresenterImpl(
            viewModel,
            interactor,
            fileUriUtil,
            resourceProvider,
            Schedulers.single()
        )
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=def70c4f-0892-4362-916b-60fb54249452
     */
    @Test
    fun `When file attach requested, then upload dialog should not be shown`() {
        val attachmentsObserver = mockPositiveAsyncUploadingScenario()
        val dialogObserver = liveData.progressDialog.test()

        presenter.addAttachment(FILE_URI)

        attachmentsObserver.awaitCount(1).assertValueCount(1)
        dialogObserver.assertNoValues()
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=d0cae5eb-6f59-420c-8902-17058e7c91af
     */
    @Test
    fun `When file info is not obtained, then error message should be shown`() {
        whenever(resourceProvider.getString(R.string.message_panel_attachment_upload_error)).thenReturn(UPLOAD_ERROR)
        val toastObserver = liveData.toast.test()

        presenter.addAttachment(FILE_URI)

        toastObserver.awaitCount(1).assertValue(UPLOAD_ERROR)
    }

    @Test
    fun `When file size more than limit, then error message should be shown`() {
        val fileInfo = FileUriUtil.FileInfo(FILE_NAME, Long.MAX_VALUE)
        whenever(resourceProvider.getString(R.string.message_panel_attachment_files_size_limit)).thenReturn(TOO_BIG)
        whenever(fileUriUtil.getFileInfo(FILE_URI, true, true)).thenReturn(fileInfo)
        val toastObserver = liveData.toast.test()

        presenter.addAttachment(FILE_URI)

        toastObserver.awaitCount(1).assertValue(TOO_BIG)
    }

    /**
     * fix https://online.sbis.ru/opendoc.html?guid=b1a5facc-63f3-47c8-a518-a94dc96e6ad2
     */
    @Test
    fun `When uri string, file info with small size are given and method addAttachment is called, then method fileUriUtil#getFileAsyncWithName will be invoked`() {
        val attachmentObserver = mockPositiveAsyncUploadingScenario(fileSize)

        presenter.addAttachment(FILE_URI)

        attachmentObserver.awaitCount(1).assertValueCount(1)
        verify(fileUriUtil).getFileAsyncWithName(eq(FILE_URI), any())
    }

    /**
     * Не запускаем пкопирование, если файл уже выпадает за допустимые ограничения на загрузку
     */
    @Test
    fun `When uri string, file info with too big size are given and method addAttachment is called, then method fileUriUtil#getFileAsyncWithName will not be invoked`() {
        val fileInfo = FileUriUtil.FileInfo(FILE_NAME, Long.MAX_VALUE)
        whenever(fileUriUtil.getFileInfo(FILE_URI, true, true)).thenReturn(fileInfo)
        whenever(resourceProvider.getString(R.string.message_panel_attachment_files_size_limit)).thenReturn(TOO_BIG)
        val toastObserver = liveData.toast.test()

        presenter.addAttachment(FILE_URI)

        toastObserver.awaitCount(1).assertValue(TOO_BIG)
        verify(fileUriUtil, never()).getFileAsyncWithName(eq(FILE_URI), any())
    }

    /**
     * Подготовка окружения для положительного сценария загрузки. В конце теста нужно проверять, что доставлено одно
     * вложение
     */
    @CheckResult
    private fun mockPositiveUploadingScenario(@IntRange(from = 1L, to = Long.MAX_VALUE) size: Long = 1L): TestObserver<*> {
        val fileInfo = FileUriUtil.FileInfo(FILE_NAME, size)
        whenever(fileUriUtil.getFileInfo(FILE_URI, true, true)).thenReturn(fileInfo)

        val messageAttachment: MessageAttachment = mock()
        whenever(messageAttachment.uuid).thenReturn(attachmentUuid)
        whenever(messageAttachment.encryptionStatus).thenReturn(mock())
        whenever(interactor.createAttachment(conversationUuid, FILE_URI, FILE_URI, FILE_NAME, size)).thenReturn(messageAttachment)

        return liveData.attachments.skip(1).test()
    }

    @CheckResult
    private fun mockPositiveAsyncUploadingScenario(@IntRange(from = 1L, to = Long.MAX_VALUE) size: Long = 1L): TestObserver<*> {
        val file: File = mock()
        val uri: URI = mock { on { toString() } doReturn FILE_URI }
        whenever(file.toURI()).thenReturn(uri)
        whenever(file.name).thenReturn(FILE_NAME)
        whenever(file.length()).thenReturn(size)

        whenever(fileUriUtil.getFileAsyncWithName(eq(FILE_URI), any())).thenReturn(Single.just(Pair(file, FILE_NAME)))

        return mockPositiveUploadingScenario(size)
    }
 */
}