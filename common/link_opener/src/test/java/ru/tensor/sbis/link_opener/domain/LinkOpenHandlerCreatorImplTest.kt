package ru.tensor.sbis.link_opener.domain

import android.content.Context
import org.mockito.kotlin.mock
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import ru.tensor.sbis.common.testing.doReturn
import ru.tensor.sbis.toolbox_decl.linkopener.builder.LinkOpenHandlerCreator
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType
import ru.tensor.sbis.toolbox_decl.linkopener.data.LinkDocSubtype
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenHandler
import ru.tensor.sbis.link_opener.data.InnerLinkPreview
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenHandlerPriority

class LinkOpenHandlerCreatorImplTest {

    private val mockContext = mock<Context> {
        on { applicationContext } doReturn mock()
    }
    private val testAnyLink = InnerLinkPreview(docType = DocType.DOCUMENT)
    private var testHandlerCompleted = false
    private var testPriority: LinkOpenHandlerPriority = LinkOpenHandlerPriority.NORMAL

    private lateinit var creator: LinkOpenHandlerCreator

    @Before
    fun setUp() {
        testHandlerCompleted = false
        creator = LinkOpenHandlerCreatorImpl()
    }

    @Test
    fun `Check createSingle method with alone type`() {
        val handler = creator.createSingle(DocType.PERSON) { _, _ ->
            testHandlerCompleted = true
        }
        val eventHandlers = handler.getEventHandlers()

        assertNull(handler.getDefaultHandler())
        assertEquals(handler.getPriority(), LinkOpenHandlerPriority.NORMAL)

        assertTrue(eventHandlers.isNotEmpty())
        assertEquals(eventHandlers.size, 1)
        val eventHandler = eventHandlers[0]
        assertEquals(eventHandler.types, listOf(DocType.PERSON))
        assertTrue(eventHandler.subtypes.isEmpty())
        assertNull(eventHandler.actionRouter)
        assertNotNull(eventHandler.action)
        assertFalse(testHandlerCompleted)
        eventHandler.action!!.onOpen(testAnyLink, mockContext)
        assertTrue(testHandlerCompleted)
    }

    @Test
    fun `Check createSingle method with subtypes`() {
        val handler = creator.createSingle(
            DocType.DISC,
            LinkDocSubtype.DISK_ARCHIVE,
            LinkDocSubtype.DISK_DOC
        ) { _, _ -> }
        val eventHandlers = handler.getEventHandlers()

        assertNull(handler.getDefaultHandler())
        assertEquals(handler.getPriority(), LinkOpenHandlerPriority.NORMAL)

        assertTrue(eventHandlers.isNotEmpty())
        assertEquals(eventHandlers.size, 1)
        assertEquals(eventHandlers[0].types, listOf(DocType.DISC))
        assertTrue(eventHandlers[0].subtypes.isNotEmpty())
        assertEquals(
            eventHandlers[0].subtypes,
            listOf(LinkDocSubtype.DISK_ARCHIVE, LinkDocSubtype.DISK_DOC)
        )
        assertNull(eventHandlers[0].actionRouter)
        assertNotNull(eventHandlers[0].action)
    }

    @Test
    fun `Check createSingleForRouter method with alone type`() {
        val handler = creator.createSingleForRouter(DocType.DOCUMENT) { _, _ ->
            testHandlerCompleted = true
            null
        }
        val eventHandlers = handler.getEventHandlers()

        assertNull(handler.getDefaultHandler())
        assertEquals(handler.getPriority(), LinkOpenHandlerPriority.NORMAL)

        assertTrue(eventHandlers.isNotEmpty())
        val eventHandler = eventHandlers[0]
        assertEquals(eventHandler.types, listOf(DocType.DOCUMENT))
        assertTrue(eventHandler.subtypes.isEmpty())
        assertNotNull(eventHandler.actionRouter)
        assertNull(eventHandler.action)
        assertFalse(testHandlerCompleted)
        eventHandler.actionRouter!!.onOpenIntent(testAnyLink, mockContext)
        assertTrue(testHandlerCompleted)
    }

    @Test
    fun `Check createSingleForRouter method with subtypes`() {
        val handler = creator.createSingleForRouter(
            DocType.DOCUMENT,
            LinkDocSubtype.TASK_VACATION,
            LinkDocSubtype.TASK_ORDER
        ) { _, _ ->
            testHandlerCompleted = true
            null
        }
        val eventHandlers = handler.getEventHandlers()

        assertNull(handler.getDefaultHandler())
        assertEquals(handler.getPriority(), LinkOpenHandlerPriority.NORMAL)

        assertTrue(eventHandlers.isNotEmpty())
        val eventHandler = eventHandlers[0]
        assertTrue(eventHandler.subtypes.isNotEmpty())
        assertEquals(eventHandler.types, listOf(DocType.DOCUMENT))
        assertEquals(
            eventHandler.subtypes,
            listOf(LinkDocSubtype.TASK_VACATION, LinkDocSubtype.TASK_ORDER)
        )
        assertNotNull(eventHandler.actionRouter)
        assertNull(eventHandler.action)
        assertFalse(testHandlerCompleted)
        eventHandler.actionRouter!!.onOpenIntent(testAnyLink, mockContext)
        assertTrue(testHandlerCompleted)
    }

    @Test
    fun `Check createSingleForRouter method with multiple types`() {
        val handler = creator.createSingleForRouter(
            DocType.DOCUMENT,
            DocType.INSTRUCTDOC
        ) { _, _ ->
            testHandlerCompleted = true
            null
        }
        val eventHandlers = handler.getEventHandlers()

        assertTrue(eventHandlers.isNotEmpty())
        assertEquals(eventHandlers.size, 1)

        val eventHandler = eventHandlers[0]
        assertEquals(
            eventHandler.types,
            listOf(DocType.DOCUMENT, DocType.INSTRUCTDOC)
        )
        assertTrue(eventHandler.subtypes.isEmpty())
        assertNotNull(eventHandler.actionRouter)
        assertFalse(testHandlerCompleted)
        eventHandler.actionRouter!!.onOpenIntent(testAnyLink, mockContext)
        assertTrue(testHandlerCompleted)
    }

    @Test
    fun `Check the creation of a complex handler`() {
        var testCompleted1 = false
        var testCompleted2 = false
        val handler = creator.create {
            on {
                type = DocType.UNKNOWN_ONLINE_DOC
                subtype = LinkDocSubtype.OUTGOING_PAYMENT
                accomplish { _, _ -> testCompleted1 = true }
            }
            on {
                type = DocType.DOCUMENT
                subtypes(LinkDocSubtype.TASK_WORK_PLAN, LinkDocSubtype.TASK_PROJECT)
                accomplishStart { _, _ ->
                    testCompleted2 = true
                    null
                }
            }
            default {
                testHandlerCompleted = true
            }
            priority(testPriority)
        }

        val defaultHandler = handler.getDefaultHandler()

        assertNotNull(defaultHandler)
        assertFalse(testHandlerCompleted)
        defaultHandler!!.onOpen(testAnyLink, mockContext)
        assertTrue(testHandlerCompleted)

        val eventHandlers = handler.getEventHandlers()
        assertTrue(eventHandlers.isNotEmpty())
        assertEquals(eventHandlers.size, 2)
        assertEquals(handler.getPriority(), testPriority)

        val (eventHandler1, eventHandler2) = eventHandlers[0] to eventHandlers[1]
        assertEquals(eventHandler1.types, listOf(DocType.UNKNOWN_ONLINE_DOC))
        assertEquals(eventHandler1.subtypes, listOf(LinkDocSubtype.OUTGOING_PAYMENT))
        assertEquals(eventHandler2.types, listOf(DocType.DOCUMENT))
        assertEquals(eventHandler2.subtypes, listOf(LinkDocSubtype.TASK_WORK_PLAN, LinkDocSubtype.TASK_PROJECT))
        assertNotNull(eventHandler1.action)
        assertNull(eventHandler1.actionRouter)
        assertNull(eventHandler2.action)
        assertNotNull(eventHandler2.actionRouter)

        assertFalse(testCompleted1)
        assertFalse(testCompleted2)
        eventHandler1.action!!.onOpen(testAnyLink, mockContext)
        assertTrue(testCompleted1)
        assertFalse(testCompleted2)
        eventHandler2.actionRouter!!.onOpenIntent(testAnyLink, mockContext)
        assertTrue(testCompleted2)
    }
}