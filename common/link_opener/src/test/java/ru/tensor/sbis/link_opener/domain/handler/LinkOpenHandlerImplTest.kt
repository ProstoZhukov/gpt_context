package ru.tensor.sbis.link_opener.domain.handler

import org.mockito.kotlin.mock
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith
import ru.tensor.sbis.common.testing.params
import ru.tensor.sbis.link_opener.utils.getEventHandler
import ru.tensor.sbis.toolbox_decl.linkopener.action.OnDocumentOpenListener
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType
import ru.tensor.sbis.toolbox_decl.linkopener.data.LinkDocSubtype
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenHandlerPriority

@RunWith(JUnitParamsRunner::class)
internal class LinkOpenHandlerImplTest {

    @Test
    @Parameters(method = "equalsCases")
    fun equalsTest(one: LinkOpenHandlerImpl, another: LinkOpenHandlerImpl) {
        assertEquals(one, another)
        if (one.getEventHandlers().count() == another.getEventHandlers().count()) {
            assertEquals(one.hashCode(), another.hashCode())
        }
    }

    @Test
    @Parameters(method = "notEqualsCases")
    fun notEqualsTest(one: LinkOpenHandlerImpl, another: LinkOpenHandlerImpl) {
        assertNotEquals(one, another)
        assertNotEquals(one.hashCode(), another.hashCode())
    }

    private fun getLinkOpenHandler(
        events: List<LinkOpenEventHandlerImpl>,
        priority: LinkOpenHandlerPriority = LinkOpenHandlerPriority.NORMAL,
        defaultAction: OnDocumentOpenListener? = mock()
    ) = LinkOpenHandlerImpl(
            eventHandlers = events,
            defaultAction = defaultAction,
            priorityLevel = priority
        )

    @Suppress("unused")
    private fun equalsCases() = params {
        add(
            getLinkOpenHandler(
                listOf(getEventHandler(DocType.DOCUMENT, LinkDocSubtype.DISK_DOC, LinkOpenHandlerPriority.HIGH))
            ),
            getLinkOpenHandler(
                listOf(getEventHandler(DocType.DOCUMENT, LinkDocSubtype.DISK_DOC, LinkOpenHandlerPriority.HIGH))
            )
        )
        add( // С разным кол-вом, но одинаковыми обработчиками - все равно одинаковые
            getLinkOpenHandler(
                listOf(
                    getEventHandler(DocType.ARTICLE, LinkDocSubtype.DISK_DOC),
                    getEventHandler(DocType.ARTICLE, LinkDocSubtype.DISK_DOC)
                )
            ),
            getLinkOpenHandler(
                listOf(getEventHandler(DocType.ARTICLE, LinkDocSubtype.DISK_DOC))
            )
        )
        add( // Действие разное, но все равно одинаковые
            getLinkOpenHandler(
                events = listOf(getEventHandler(DocType.DOCUMENT, LinkDocSubtype.DISK_DOC, LinkOpenHandlerPriority.HIGH))
            ),
            getLinkOpenHandler(
                events = listOf(getEventHandler(DocType.DOCUMENT, LinkDocSubtype.DISK_DOC, LinkOpenHandlerPriority.HIGH)),
                defaultAction = null
            )
        )
    }

    @Suppress("unused")
    private fun notEqualsCases() = params {
        add( // Разные обработчики событий
            getLinkOpenHandler(
                listOf(
                    getEventHandler(DocType.DOCUMENT, LinkDocSubtype.DISK_DOC),
                    getEventHandler(DocType.DOCUMENT, LinkDocSubtype.BUSINESS_TRIP)
                )
            ),
            getLinkOpenHandler(
                listOf(getEventHandler(DocType.DOCUMENT, LinkDocSubtype.DISK_DOC))
            )
        )
        add( // Разный приоритет
            getLinkOpenHandler(
                events = listOf(getEventHandler(DocType.ARTICLE, LinkDocSubtype.DISK_DOC)),
                priority = LinkOpenHandlerPriority.NORMAL
            ),
            getLinkOpenHandler(
                events = listOf(getEventHandler(DocType.DOCUMENT, LinkDocSubtype.DISK_DOC)),
                priority = LinkOpenHandlerPriority.HIGH
            )
        )
    }

}