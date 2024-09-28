package ru.tensor.sbis.onboarding_tour.ui.views

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import ru.tensor.sbis.onboarding_tour.R
import ru.tensor.sbis.onboarding_tour.testUtils.SOURCE_TERM_CAPTION

internal class TermsConverterTest {

    private val mockContext: Context = mock {
        on { getString(R.string.onboarding_tour_terms_caption) } doReturn SOURCE_TERM_CAPTION
    }
    private val termsConverter = TermsConverter()

    @Test
    fun `Proper creation of unsorted complex tour content`() {
        val (caption, linkedText) = termsConverter.calculateCaptionAndLinkedText(
            mockContext,
            R.string.onboarding_tour_terms_caption,
            listOf(LINK_1, LINK_2)
        )

        assertEquals(MODIFIED_TERM_CAPTION, caption)
        assertEquals(2, linkedText.size)
        assertEquals(LINK_1, linkedText[SOURCE_TERM_1])
        assertEquals(LINK_2, linkedText[SOURCE_TERM_2])
    }

    companion object {
        const val SOURCE_TERM_1 = "the terms of service"
        const val SOURCE_TERM_2 = "privacy policy"
        const val LINK_1 = "link1"
        const val LINK_2 = "link2"
        val MODIFIED_TERM_CAPTION = SOURCE_TERM_CAPTION.replace("%%", "")
    }
}