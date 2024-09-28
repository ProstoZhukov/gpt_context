package ru.tensor.sbis.link_opener.domain.parser

import android.content.Intent
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.robolectric.annotation.Config
import ru.tensor.sbis.link_opener.data.IncomingLinkType
import ru.tensor.sbis.link_opener.data.UriContainer

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
internal class LinkTypeDetectorTest {

    private val keywords = listOf("sbis.ru")
    private val mapper = LinkUriMapper()
    private val detector = LinkTypeDetector(keywords, mapper)

    private val sbisLinkExample = "https://online.sbis.ru/opendoc.html?guid=6e6e866b-13f5-4098-851f-acd6ccd7f348"
    private val encodedSbisLinkWithJsonExample =
        "https://fix-online.sbis.ru/opendoc.html?guid=182689c1-53d2-44e8-abab-a5b89b35bb3c&client=201700056&templateOptions=%7B%22basketData%22%3A%7B%22hasItemsAtBasket%22%3Afalse%7D%7D"
    private val decodedSbisLinkWithJsonExample =
        "https://fix-online.sbis.ru/opendoc.html?guid=182689c1-53d2-44e8-abab-a5b89b35bb3c&client=201700056&templateOptions={\"basketData\":{\"hasItemsAtBasket\":false}}"
    private val sabyLinkExample =
        "sabylink://type.subtype/details?uuid=6e6e866b-13f5-4098-851f-acd6ccd7f348&title=Title"

    @Test
    fun `Detect sbis link type from opaque Uri does not throw UnsupportedOperationException`() {
        detector.getType(UriContainer(uri = "mailto:nobody@tensor.ru"))
            .apply {
                assertEquals(IncomingLinkType.INVALID, this)
            }
    }

    @Test
    fun `Detect sbis link type from complex encoded Uri`() {
        detector.getType(UriContainer(uri = encodedSbisLinkWithJsonExample))
            .apply {
                assertEquals(IncomingLinkType.SBIS, this)
            }
    }

    @Test
    fun `Detect sbis link type from complex decoded Uri`() {
        detector.getType(UriContainer(uri = decodedSbisLinkWithJsonExample))
            .apply {
                assertEquals(IncomingLinkType.SBIS, this)
            }
    }

    @Test
    fun `Detect invalid link type from Uri`() {
        detector.getType(UriContainer()).apply {
            assertEquals(IncomingLinkType.INVALID, this)
        }
    }

    @Test
    fun `Detect invalid link type from Intent`() {
        val intent = createIntent("")
        detector.getType(UriContainer(intent)).apply {
            assertEquals(IncomingLinkType.INVALID, this)
        }
    }

    @Test
    fun `Detect foreign link type from Uri`() {
        val intent = createIntent("https://www.dom.ru/")
        detector.getType(UriContainer(intent)).apply {
            assertEquals(IncomingLinkType.FOREIGN, this)
        }
    }

    @Test
    fun `Detect foreign link type from Intent`() {
        detector.getType(UriContainer(uri = "https://www.dom.ru/")).apply {
            assertEquals(IncomingLinkType.FOREIGN, this)
        }
    }

    @Test
    fun `Detect sbis link type from Uri`() {
        val intent = createIntent(sbisLinkExample)
        detector.getType(UriContainer(intent)).apply {
            assertEquals(IncomingLinkType.SBIS, this)
        }
    }

    @Test
    fun `Detect sbis link type from Intent`() {
        detector.getType(UriContainer(uri = sbisLinkExample))
            .apply {
                assertEquals(IncomingLinkType.SBIS, this)
            }
    }

    @Test
    fun `Detect inner sabylink link type from Intent`() {
        val intent =
            createIntent(sabyLinkExample)
        detector.getType(UriContainer(intent)).apply {
            assertEquals(IncomingLinkType.SABYLINK, this)
        }
    }

    @Test
    fun `Detect inner sabylink link type from Uri`() {
        detector.getType(UriContainer(uri = sabyLinkExample))
            .apply {
                assertEquals(IncomingLinkType.SABYLINK, this)
            }
    }

    private fun createIntent(uri: String): Intent {
        val mockUir = mock<Uri> {
            on { host } doReturn uri.substringAfter("//").substringBefore("/")
            on { toString() } doReturn uri
        }
        return mock {
            on { data } doReturn mockUir
        }
    }
}