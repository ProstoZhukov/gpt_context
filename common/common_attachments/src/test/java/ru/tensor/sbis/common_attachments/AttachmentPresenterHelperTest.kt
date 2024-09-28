package ru.tensor.sbis.common_attachments

import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.common.util.FileUriUtil
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.disk.decl.attach_helper.MediaOption

private const val CURRENT_ATTACHMENTS_COUNT = 1

/**
 * Тест для [AttachmentPresenterHelper] проверяет, есть ли вызов запроса разрешений на чтение с диска при добавлении
 * вложений в панель сообщений
 *
 * @author sa.nikitin
 * @since 12/16/2019
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class AttachmentPresenterHelperTest {
    private val requiredPermissionsList = listOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)

    @Mock
    private lateinit var fileUriUtil: FileUriUtil

    @Mock
    private lateinit var resourceProvider: ResourceProvider

    @Mock
    private lateinit var attachmentView: AttachmentView

    @InjectMocks
    private lateinit var attachmentPresenterHelper: AttachmentPresenterHelperImpl

    @Before
    fun setUp() {
        attachmentPresenterHelper.setView(attachmentView)
    }

    /**
     * fix https://online.sbis.ru/opendoc.html?guid=b1a5facc-63f3-47c8-a518-a94dc96e6ad2
     */
    @Test
    fun `When files' uris list and current attachments count are given and read external storage permissions are not granted then requestPermissions will be invoked`() {
        whenever(attachmentView.getNotGrantedPermissions(requiredPermissionsList)).thenReturn(
            requiredPermissionsList
        )

        attachmentPresenterHelper.onFilesAttached(mock(), CURRENT_ATTACHMENTS_COUNT)

        verify(attachmentView).requestPermissions(requiredPermissionsList)
    }

    @Test
    fun `When files' uris list and current attachments count are given and read external storage permissions are granted then requestPermissions will be invoked`() {
        whenever(attachmentView.getNotGrantedPermissions(requiredPermissionsList)).thenReturn(
            emptyList()
        )

        attachmentPresenterHelper.onFilesAttached(mock(), CURRENT_ATTACHMENTS_COUNT)

        verify(attachmentView, never()).requestPermissions(requiredPermissionsList)
    }

    private class AttachmentPresenterHelperImpl(
        fileUriUtil: FileUriUtil,
        resourceProvider: ResourceProvider
    ) : AttachmentPresenterHelper<AttachmentView>(fileUriUtil, resourceProvider) {
        override fun getMediaOptionsItems(): MutableList<MediaOption> {
            throw AssertionError("Unexpected method call")
        }
    }
}