package ru.tensor.sbis.link_opener.domain.data

import org.junit.Assert
import org.junit.Test
import ru.tensor.sbis.link_opener.data.IncomingLinkType

class IncomingLinkTypeTest {

    @Test
    fun `correct sbis link classifier state`() {
        IncomingLinkType.SBIS.apply {
            Assert.assertTrue(isSbis)
            Assert.assertTrue(isValid)
            Assert.assertFalse(isForeign)
        }
    }

    @Test
    fun `correct foreign link classifier state`() {
        IncomingLinkType.FOREIGN.apply {
            Assert.assertTrue(isForeign)
            Assert.assertTrue(isValid)
            Assert.assertFalse(isSbis)
        }
    }

    @Test
    fun `correct no link classifier state`() {
        IncomingLinkType.INVALID.apply {
            Assert.assertFalse(isValid)
            Assert.assertFalse(isForeign)
            Assert.assertFalse(isSbis)
        }
    }
}