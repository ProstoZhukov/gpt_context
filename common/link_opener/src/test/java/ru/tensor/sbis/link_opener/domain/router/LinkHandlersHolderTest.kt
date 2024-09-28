package ru.tensor.sbis.link_opener.domain.router

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import ru.tensor.sbis.link_opener.domain.LinkOpenHandlerCreatorImpl
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType


internal class LinkHandlersHolderTest {

    private lateinit var testHandlersMap: LinkHandlersHolder

    @Before
    fun setUp() {
        testHandlersMap = LinkHandlersHolder()
    }

    @Test
    fun `Nothing is given if there were no registrations`() {
        assertEquals(0, testHandlersMap.getAllHandlers().size)
        assertEquals(0, testHandlersMap.getDefaultHandlers().size)
    }

    @Test
    fun `Specific handlers are given if they have been registered`() {
        testHandlersMap.addHandler(
            LinkOpenHandlerCreatorImpl().createSingle(DocType.DOCUMENT) { _, _ -> },
            LinkOpenHandlerCreatorImpl().createSingle(DocType.CONTRACTOR) { _, _ -> },
            LinkOpenHandlerCreatorImpl().createSingle(DocType.ARTICLE) { _, _ -> }
        )
        assertEquals(3, testHandlersMap.getAllHandlers().size)
        assertEquals(0, testHandlersMap.getDefaultHandlers().size)
    }

    @Test
    fun `Default handlers are given if they have been registered`() {
        repeat(2) { id ->
            LinkOpenHandlerCreatorImpl().create {
                on {
                    type = DocType.values()[id]
                    accomplish { _, _ -> }
                }
                default { }
                default { }
            }.run {
                testHandlersMap.addHandler(this)
            }
        }

        assertEquals(2, testHandlersMap.getAllHandlers().size)
        assertEquals(2, testHandlersMap.getDefaultHandlers().size)
    }
}