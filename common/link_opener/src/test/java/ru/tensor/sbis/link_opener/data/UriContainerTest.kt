package ru.tensor.sbis.link_opener.data

import android.content.Intent
import android.net.Uri
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.junit.Assert.*
import org.junit.Test

internal class UriContainerTest {

    @Test
    fun `Check positive case of isIntentSource`() {
        UriContainer(Intent(), "").apply {
            assertTrue(isIntentSource)
        }
    }

    @Test
    fun `Check negative case of isIntentSource`() {
        UriContainer(null, "https://any.site.com").apply {
            assertFalse(isIntentSource)
        }
    }

    @Test
    fun `Build uriString from uri parameter`() {
        val uri = "https://any.site.com"
        UriContainer(null, uri).apply {
            assertEquals(uri, uriString)
        }
    }

    @Test
    fun `Build uriString from intent parameter`() {
        val expectedUri = "https://any.site.com"
        val mockUir = mock<Uri> {
            on { toString() } doReturn expectedUri
        }
        val mockIntent = mock<Intent> {
            on { data } doReturn mockUir
        }
        UriContainer(mockIntent, "http://test.com").apply {
            assertEquals(expectedUri, uriString)
        }
    }
}