package ru.tensor.sbis.link_opener.domain.router.producer

import android.content.Context
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import ru.tensor.sbis.link_opener.contract.LinkOpenerDependency
import ru.tensor.sbis.link_opener.data.InnerLinkPreview
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType

internal class OurLinkOpenHandlerProducerTest {

    private val mockDependency = mock<LinkOpenerDependency>()
    private val mockContext = mock<Context>()

    private lateinit var producer: OurLinkOpenHandlerProducer

    @Before
    fun setUp() {
        producer = OurLinkOpenHandlerProducer(mockDependency)
    }

    @Test
    fun `Return workable handler to open the webView`() {
        val preview = InnerLinkPreview(docType = DocType.ORDER)
        val handler = producer.produce(preview)

        verify(mockDependency, never()).showDocumentLink(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

        assertNotNull(handler.action)
        handler.action!!.onOpen(preview, mockContext)

        verify(mockDependency)
            .showDocumentLink(mockContext, preview.title, preview.href, null)
    }

    @Test
    fun `Return workable handler to open preview with doc uuid in the webView`() {
        val preview = InnerLinkPreview(docType = DocType.DOCUMENT, docUuid = "docUUID")
        val handler = producer.produce(preview)

        handler.action!!.onOpen(preview, mockContext)

        verify(mockDependency)
            .showDocumentLink(mockContext, preview.title, preview.href, preview.docUuid)
    }
}