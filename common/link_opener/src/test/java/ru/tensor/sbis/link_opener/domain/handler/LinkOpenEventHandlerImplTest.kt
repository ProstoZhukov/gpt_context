package ru.tensor.sbis.link_opener.domain.handler

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import ru.tensor.sbis.common.testing.params
import ru.tensor.sbis.link_opener.utils.getEventHandler
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType
import ru.tensor.sbis.toolbox_decl.linkopener.data.LinkDocSubtype
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenHandlerPriority

@RunWith(JUnitParamsRunner::class)
internal class LinkOpenEventHandlerImplTest {

    @Test
    @Parameters(method = "equalsCases")
    fun equalsTest(one: LinkOpenEventHandlerImpl, another: LinkOpenEventHandlerImpl) {
        assertEquals(one, another)
        assertEquals(one.hashCode(), another.hashCode())
    }

    @Test
    @Parameters(method = "notEqualsCases")
    fun notEqualsTest(one: LinkOpenEventHandlerImpl, another: LinkOpenEventHandlerImpl) {
        assertNotEquals(one, another)
        assertNotEquals(one.hashCode(), another.hashCode())
    }

    @Suppress("unused")
    private fun equalsCases() = params {
        add(
            getEventHandler(DocType.DOCUMENT, LinkDocSubtype.DISK_DOC),
            getEventHandler(DocType.DOCUMENT, LinkDocSubtype.DISK_DOC)
        )
        add(
            getEventHandler(DocType.EVENT, LinkDocSubtype.UNKNOWN, LinkOpenHandlerPriority.HIGH),
            getEventHandler(DocType.EVENT, LinkDocSubtype.UNKNOWN, LinkOpenHandlerPriority.HIGH)
        )
        add(
            getEventHandler(),
            getEventHandler()
        )
    }

    @Suppress("unused")
    private fun notEqualsCases() = params {
        add( // Отличается тип
            getEventHandler(DocType.DOCUMENT, LinkDocSubtype.DISK_DOC),
            getEventHandler(DocType.ARTICLE, LinkDocSubtype.DISK_DOC)
        )
        add( // Отличается подтип
            getEventHandler(DocType.EVENT, LinkDocSubtype.UNKNOWN, LinkOpenHandlerPriority.HIGH),
            getEventHandler(DocType.EVENT, LinkDocSubtype.BUSINESS_TRIP, LinkOpenHandlerPriority.HIGH)
        )
        add( // Отличается приоритет
            getEventHandler(DocType.EVENT, LinkDocSubtype.UNKNOWN, LinkOpenHandlerPriority.LOW),
            getEventHandler(DocType.EVENT, LinkDocSubtype.BUSINESS_TRIP, LinkOpenHandlerPriority.NORMAL)
        )
    }
}