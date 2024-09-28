package ru.tensor.sbis.message_panel.helper

import junitparams.JUnitParamsRunner
import junitparams.custom.combined.CombinedParameters
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import ru.tensor.sbis.common.testing.dataListParamMapper
import ru.tensor.sbis.message_panel.model.CoreConversationInfo
import ru.tensor.sbis.common.testing.stringParamMapper

/**
 * Тестирование получения контента из [CoreConversationInfo]
 *
 * @author vv.chekurda
 * Создан 8/10/2019
 */
@RunWith(JUnitParamsRunner::class)
class GetSharedContentTest {

    @Test
    @CombinedParameters("0,1", "0,1")
    fun `No shared content test`(sharedTextCode: Int, sharedFilesCode: Int) {
        val info = createInfo(sharedTextCode, sharedFilesCode)

        assertNull(info.getSharedContent())
    }

    @Test
    @CombinedParameters("2", "0,1")
    fun `Test shared text without attachments`(sharedTextCode: Int, sharedFilesCode: Int) {
        val info = createInfo(sharedTextCode, sharedFilesCode)

        val content = info.getSharedContent()!!

        assertTrue(content.text.isNotEmpty())
        assertTrue(content.fileUriList.isEmpty())
    }

    @Test
    @CombinedParameters("0,1", "2")
    fun `Test shared attachments without text`(sharedTextCode: Int, sharedFilesCode: Int) {
        val info = createInfo(sharedTextCode, sharedFilesCode)

        val content = info.getSharedContent()!!

        assertTrue(content.text.isEmpty())
        assertTrue(content.fileUriList.isNotEmpty())
    }

    @Test
    fun `Test shared text and attachments`() {
        val info = createInfo(2, 2)

        val content = info.getSharedContent()!!

        assertTrue(content.text.isNotEmpty())
        assertTrue(content.fileUriList.isNotEmpty())
    }

    private fun createInfo(sharedTextCode: Int, sharedFilesCode: Int) = CoreConversationInfo(
        sharedText = stringParamMapper(sharedTextCode),
        sharedAttachments = dataListParamMapper(sharedFilesCode, "File uri")
    )
}