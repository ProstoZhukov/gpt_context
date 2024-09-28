package ru.tensor.sbis.link_opener.utils

import android.net.Uri
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

@Implements(Uri::class)
internal class ShadowUri {

    companion object {

        @JvmStatic
        @Implementation
        @Suppress("unused")
        fun parse(uriString: String): Uri =
            mock {
                on { host } doAnswer {
                    uriString.substringAfter("//").substringBefore("/")
                }
                on { normalizeScheme() } doAnswer {
                    mock
                }
            }
    }
}