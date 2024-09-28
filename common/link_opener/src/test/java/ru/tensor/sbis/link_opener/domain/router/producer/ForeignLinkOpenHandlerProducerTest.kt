package ru.tensor.sbis.link_opener.domain.router.producer

import android.content.Context
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreviewImpl
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType

internal class ForeignLinkOpenHandlerProducerTest {

    private val testForeignHref = "https://ru.wikipedia.org/wiki"
    private val mockContext = mock<Context>()

    private lateinit var producer: ForeignLinkOpenHandlerProducer

    @Before
    fun setUp() {
        producer = ForeignLinkOpenHandlerProducer()
    }

    @Test
    fun `Return workable handler to open the browser`() {
        val preview = LinkPreviewImpl(href = testForeignHref, docType = DocType.UNKNOWN)
        val handler = producer.produce(preview)

        assertNotNull(handler.action)

        handler.action!!.onOpen(preview, mockContext)

        verify(mockContext).startActivity(anyOrNull())
    }
}