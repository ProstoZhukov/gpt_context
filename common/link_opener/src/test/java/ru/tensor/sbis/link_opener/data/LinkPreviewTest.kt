package ru.tensor.sbis.link_opener.data

import org.junit.Assert.*
import org.junit.Test
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreviewImpl
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType
import ru.tensor.sbis.toolbox_decl.linkopener.data.LinkDocSubtype

class LinkPreviewTest {

    private val testUuid = "6e6e866b-13f5-4098-851f-acd6ccd7f348"
    private val testHref = "https://online.sbis.ru/opendoc.html?guid=$testUuid"

    @Test
    fun `Detect correct Predictable state of preview`() {
        createLinkPreview().apply {
            assert(isPredictable)
        }
        createLinkPreview(docType = DocType.DOCUMENT, subType = LinkDocSubtype.TASK_OUTCOME).apply {
            assertFalse(isPredictable)
        }
        createLinkPreview(title = "title").apply {
            assertFalse(isPredictable)
        }
    }

    @Test
    @Suppress("SpellCheckingInspection")
    fun `Detect correct Redirectable state of inner preview`() {
        InnerLinkPreview.EMPTY.apply {
            assertFalse(isRedirectable())
        }
        createLinkPreview(testHref).apply {
            assertTrue(isRedirectable())
        }
        createLinkPreview(docType = DocType.DOCUMENT).apply {
            assertTrue(isRedirectable())
        }
    }

    @Test
    @Suppress("SpellCheckingInspection")
    fun `Detect correct Redirectable state of outer preview`() {
        LinkPreviewImpl(image = "image", title = "title").apply {
            assertFalse(isRedirectable())
        }
        LinkPreviewImpl(href = testHref, docType = DocType.DOCUMENT).apply {
            assertTrue(isRedirectable())
        }
        LinkPreviewImpl(docUuid = testUuid).apply {
            assertTrue(isRedirectable())
        }
    }
}

private fun createLinkPreview(
    href: String = "",
    title: String = "",
    docType: DocType = DocType.UNKNOWN,
    subType: LinkDocSubtype = LinkDocSubtype.UNKNOWN
) = InnerLinkPreview(
    url = href,
    title = title,
    docType = docType,
    docSubtype = subType
)