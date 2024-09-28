package ru.tensor.sbis.link_opener.domain.router.producer

import android.content.Context
import android.graphics.Color
import android.net.Uri
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import ru.tensor.sbis.common.testing.doReturn
import ru.tensor.sbis.common.testing.mockStatic
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreviewImpl
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType

internal class TabsLinkOpenHandlerProducerTest {

    private val testForeignHref = "https://ru.wikipedia.org/wiki"
    private val testColor = Color.RED
    private val mockContext = mock<Context>()
    private val mockUri = mock<Uri> {
        on { normalizeScheme() } doReturn mock
        on { scheme } doReturn HTTPS
    }

    private lateinit var producer: TabsLinkOpenHandlerProducer

    @Before
    fun setUp() {
        producer = TabsLinkOpenHandlerProducer(testColor)
    }

    @Test
    fun `Return workable handler to open the custom tabs`() {
        mockStatic<Uri> {
            on<Uri> { Uri.parse(ArgumentMatchers.anyString()) } doReturn mockUri
        }

        val preview = LinkPreviewImpl(href = testForeignHref, docType = DocType.UNKNOWN)
        val handler = producer.produce(preview)

        assertNotNull(handler.action)

        handler.action!!.onOpen(preview, mockContext)

        verify(mockContext).startActivity(anyOrNull())
    }
}

const val HTTPS = "https://"