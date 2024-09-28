package ru.tensor.sbis.link_opener.domain

import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.tensor.sbis.common.testing.params
import ru.tensor.sbis.link_opener.domain.handler.LinkOpenEventHandlerImpl
import ru.tensor.sbis.link_opener.domain.handler.LinkOpenHandlerImpl
import ru.tensor.sbis.toolbox_decl.linkopener.LinkOpenerRegistrar
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenHandler
import ru.tensor.sbis.link_opener.domain.router.LinkHandlersHolder
import ru.tensor.sbis.link_opener.utils.getEventHandler
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType.*
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenHandlerPriority
import ru.tensor.sbis.toolbox_decl.linkopener.data.LinkDocSubtype as DocSubtype

@RunWith(JUnitParamsRunner::class)
internal class LinkOpenerRegistrarImplTest {

    private val mockAppHandlerMap = spy(LinkHandlersHolder())
    private val testHandler1 = mock<LinkOpenHandler>()
    private val testHandler2 = mock<LinkOpenHandler>()

    private lateinit var registrar: LinkOpenerRegistrar

    @Before
    fun setUp() {
        registrar = LinkOpenerRegistrarImpl { mockAppHandlerMap }
    }

    @Test
    fun `On call register add one handler to ApplicationHandlerMap`() {
        registrar.register(testHandler1)

        verify(mockAppHandlerMap).addHandler(testHandler1)
    }

    @Test
    fun `On call multi argument register add few handlers to ApplicationHandlerMap`() {
        registrar.register(testHandler1, testHandler2)

        verify(mockAppHandlerMap).addHandler(testHandler1, testHandler2)
    }

    @Test
    fun `On register providers add few handlers to ApplicationHandlerMap`() {
        val provider1 = object : LinkOpenHandler.Provider {
            override fun getLinkOpenHandler(): LinkOpenHandler = testHandler1
        }
        val provider2 = object : LinkOpenHandler.Provider {
            override fun getLinkOpenHandler(): LinkOpenHandler = testHandler2
        }
        registrar.registerProvider(provider1, provider2)

        verify(mockAppHandlerMap).addHandler(testHandler1, testHandler2)
    }

    @Test
    @Parameters(method = "shouldBeReplacedCases")
    fun `On register replace existing handler with new`(providers: Array<LinkOpenHandler.Provider>) {
        registrar.registerProvider(*providers)

        val actualHandlers = mockAppHandlerMap.getAllHandlers()
        val providedHandlers = providers.last().getLinkOpenHandler().getEventHandlers()
        assertEquals(1, actualHandlers.count())
        assertEquals(providedHandlers, actualHandlers)

    }

    @Test
    @Parameters(method = "shouldNotBeReplacedCases")
    fun `On register do not replace handler, if type, subtype or priority differs`(
        providers: Array<LinkOpenHandler.Provider>
    ) {
        registrar.registerProvider(*providers)

        val actualHandlers = mockAppHandlerMap.getAllHandlers()
        val providedHandlers = providers.map { it.getLinkOpenHandler().getEventHandlers() }.flatten()
        assertEquals(providedHandlers.count(), actualHandlers.count())
        assertEquals(providedHandlers, actualHandlers)
    }

    private fun getProvider(vararg eventHandlers: LinkOpenEventHandlerImpl): LinkOpenHandler.Provider {
        val testHandler = LinkOpenHandlerImpl(
            defaultAction = null,
            priorityLevel = LinkOpenHandlerPriority.HIGH,
            eventHandlers = eventHandlers.toList()
        )
        return object : LinkOpenHandler.Provider {
            override fun getLinkOpenHandler(): LinkOpenHandler = testHandler
        }
    }

    @Suppress("unused")
    private fun shouldBeReplacedCases() =
        params {
            add(arrayOf(
                getProvider(getEventHandler(DOCUMENT, DocSubtype.DISK_DOC)),
                getProvider(getEventHandler(DOCUMENT, DocSubtype.DISK_DOC))
            ))
            add(arrayOf(
                getProvider(
                    getEventHandler(ARTICLE, DocSubtype.DISK_DOC),
                    getEventHandler(ARTICLE, DocSubtype.DISK_DOC)
                ),
                getProvider(getEventHandler(ARTICLE, DocSubtype.DISK_DOC))
            ))
            add(arrayOf(
                getProvider(getEventHandler(ARTICLE, DocSubtype.BUSINESS_TRIP, LinkOpenHandlerPriority.NORMAL)),
                getProvider(getEventHandler(ARTICLE, DocSubtype.BUSINESS_TRIP, LinkOpenHandlerPriority.NORMAL))
            ))
        }

    @Suppress("unused")
    private fun shouldNotBeReplacedCases() =
        params {
            add(arrayOf( // Отличается тип
                getProvider(getEventHandler(DOCUMENT, DocSubtype.DISK_DOC)),
                getProvider(getEventHandler(ARTICLE, DocSubtype.DISK_DOC))
            ))
            add(arrayOf( // Отличается подтип
                getProvider(getEventHandler(DOCUMENT, DocSubtype.DISK_DOC)),
                getProvider(getEventHandler(DOCUMENT, DocSubtype.AGREEMENT))
            ))
            add(arrayOf( // Отличается приоритет
                getProvider(getEventHandler(DOCUMENT, DocSubtype.DISK_DOC, LinkOpenHandlerPriority.LOW)),
                getProvider(getEventHandler(DOCUMENT, DocSubtype.DISK_DOC, LinkOpenHandlerPriority.HIGH))
            ))
        }
}