package ru.tensor.sbis.link_opener.domain.router.producer

import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule
import ru.tensor.sbis.link_opener.data.InnerLinkPreview
import ru.tensor.sbis.link_opener.domain.LinkOpenHandlerCreatorImpl
import ru.tensor.sbis.link_opener.domain.router.LinkHandlersHolder
import ru.tensor.sbis.toolbox_decl.linkopener.action.OnDocumentOpenListener
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType
import ru.tensor.sbis.toolbox_decl.linkopener.data.LinkDocSubtype
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenEventHandler
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenHandler
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenHandlerPriority

@RunWith(MockitoJUnitRunner::class)
internal class InAppLinkOpenHandlerProducerTest {

    @get:Rule
    val rxRule = TrampolineSchedulerRule()

    private lateinit var handlersHolder: LinkHandlersHolder
    private lateinit var producer: InAppLinkOpenHandlerProducer
    private val LinkOpenHandler.internalHandler get() = getEventHandlers().first()

    @Before
    fun setUp() {
        handlersHolder = LinkHandlersHolder()
        producer = InAppLinkOpenHandlerProducer { handlersHolder }
    }

    @Test
    fun `Returning target handler from among the registered ones`() {
        val handler1 = LinkOpenHandlerCreatorImpl().createSingle(DocType.DOCUMENT) { _, _ -> }
        val handler2 = LinkOpenHandlerCreatorImpl().createSingle(DocType.PERSON) { _, _ -> }
        val handler3 = LinkOpenHandlerCreatorImpl().createSingle(DocType.ORDER) { _, _ -> }
        handlersHolder.addHandler(handler1, handler2, handler3)

        val receivedHandler = producer.produce(link(DocType.PERSON))

        assertNotNull(receivedHandler)
        assertEquals(handler2.internalHandler, receivedHandler)
    }

    @Test
    fun `The details of the target handler match the incoming data`() {
        val testDocHandler = handlersHolder.registerHandler(DocType.DOCUMENT, DocType.INSTRUCTDOC)
        val testOrderHandler = handlersHolder.registerHandler(DocType.ORDER)
        val testDiscHandler = handlersHolder.registerHandler(
            DocType.DISC,
            subtype = arrayOf(LinkDocSubtype.DISK_FILE, LinkDocSubtype.DISK_DOC)
        )

        val docEventHandler = producer.produce(link(DocType.DOCUMENT))
        val orderEventHandler = producer.produce(link(DocType.ORDER))
        val discEventHandler = producer.produce(
            InnerLinkPreview(docType = DocType.DISC, docSubtype = LinkDocSubtype.DISK_FILE)
        )
        val missedEventHandler = producer.produce(
            InnerLinkPreview(docType = DocType.DISC, docSubtype = LinkDocSubtype.DISK_AUDIO)
        )

        docEventHandler.verify(DocType.DOCUMENT, testDocHandler.internalHandler.action)
        orderEventHandler.verify(DocType.ORDER, testOrderHandler.internalHandler.action)
        discEventHandler.verify(DocType.DISC, testDiscHandler.internalHandler.action)
        assertNull(missedEventHandler)
    }

    @Test
    fun `Returning higher priority handler from among those registered by the same type (deprecated way)`() {
        val handlers = mutableListOf<LinkOpenHandler>().apply {
            for (priority in 4 downTo 0) {
                add(handlersHolder.registerHandler(DocType.PERSON, priority = priority))
            }
        }

        val receivedHandler = producer.produce(link(DocType.PERSON))

        assertNotNull(receivedHandler)
        assertEquals(handlers.first().internalHandler, receivedHandler)
    }

    @Test
    fun `Returning higher priority handler from among those registered by the same type`() {
        handlersHolder.registerHandler(DocType.PERSON, priorityType = LinkOpenHandlerPriority.NORMAL)
        handlersHolder.registerHandler(DocType.PERSON, priorityType = LinkOpenHandlerPriority.HIGH)
        handlersHolder.registerHandler(DocType.PERSON, priorityType = LinkOpenHandlerPriority.LOW)

        val receivedHandler = producer.produce(link(DocType.PERSON))

        assertNotNull(receivedHandler)
        assertEquals(LinkOpenHandlerPriority.HIGH, receivedHandler!!.priority)
    }

    @Test
    fun `Returning normal priority handler from among those registered by the same type`() {
        handlersHolder.registerHandler(DocType.PERSON, priorityType = LinkOpenHandlerPriority.LOW)
        handlersHolder.registerHandler(DocType.PERSON, priorityType = LinkOpenHandlerPriority.NORMAL)
        handlersHolder.registerHandler(DocType.PERSON, priorityType = LinkOpenHandlerPriority.LOW)

        val receivedHandler = producer.produce(link(DocType.PERSON))

        assertNotNull(receivedHandler)
        assertEquals(LinkOpenHandlerPriority.NORMAL, receivedHandler!!.priority)
    }

    @Test
    fun `Return nothing if there are no required registered handlers`() {
        val handler1 = LinkOpenHandlerCreatorImpl().createSingle(DocType.DOCUMENT) { _, _ -> }
        val handler2 = LinkOpenHandlerCreatorImpl().createSingle(DocType.PERSON) { _, _ -> }
        handlersHolder.addHandler(handler1, handler2)

        val receivedHandler = producer.produce(link(DocType.ARTICLE))

        assertNull(receivedHandler)
    }

    @Test
    fun `Return nothing if there are no required registered handlers by subtype`() {
        handlersHolder.addHandler(
            LinkOpenHandlerCreatorImpl().createSingle(DocType.DOCUMENT, LinkDocSubtype.TASK_ORDER) { _, _ -> },
            LinkOpenHandlerCreatorImpl().createSingle(DocType.DOCUMENT, LinkDocSubtype.TASK_INVOICE) { _, _ -> }
        )

        var receivedHandler = producer.produce(
            InnerLinkPreview(docType = DocType.DOCUMENT, docSubtype = LinkDocSubtype.TASK_PROJECT)
        )
        assertNull(receivedHandler)

        receivedHandler = producer.produce(link(DocType.DOCUMENT))
        assertNull(receivedHandler)
    }

    @Test
    fun `Returning default handler from among the registered if there are no required registered ones`() {
        val defaultHandler = handlersHolder.registerHandler(DocType.PERSON, withDefault = true)
            .getDefaultHandler()

        val receivedHandler1 = producer.produce(link(DocType.ARTICLE))
        assertNotNull(receivedHandler1)
        assertEquals(defaultHandler, receivedHandler1!!.action)

        val receivedHandler2 = producer.produce(link(DocType.UNKNOWN))
        assertNotNull(receivedHandler2)
        assertEquals(receivedHandler1, receivedHandler2)
    }

    @Test
    fun `Returning higher priority default handler from among those registered`() {
        val handlers = mutableListOf<LinkOpenHandler>().apply {
            for (priority in 0..4) {
                add(handlersHolder.registerHandler(DocType.PERSON, priority = priority, withDefault = true))
            }
        }

        val receivedHandler = producer.produce(link(DocType.UNKNOWN))

        assertNotNull(receivedHandler)
        assertEquals(handlers.last().getDefaultHandler(), receivedHandler!!.action)
    }

    private fun link(docType: DocType) = InnerLinkPreview(docType = docType)

    private fun LinkHandlersHolder.registerHandler(
        vararg type: DocType,
        subtype: Array<LinkDocSubtype> = emptyArray(),
        priority: Int = LinkOpenHandler.MID_PRIORITY,
        priorityType: LinkOpenHandlerPriority? = null,
        withDefault: Boolean = false
    ): LinkOpenHandler = LinkOpenHandlerCreatorImpl().create {
        on {
            types(*type)
            subtypes(*subtype)
            accomplish { _, _ -> }
            if (priorityType != null) {
                priority(priorityType)
            } else {
                priority(priority)
            }
        }
        if (withDefault) {
            default { }
        }
    }.apply {
        addHandler(this)
    }

    private fun LinkOpenEventHandler?.verify(
        expectedType: DocType,
        expectedAction: OnDocumentOpenListener?,
    ) {
        assertNotNull(this)
        this!!
        assertThat(types, CoreMatchers.hasItem(expectedType))
        assertNotNull(action)
        assertNotNull(expectedAction)
        assertEquals(action, expectedAction)
    }
}