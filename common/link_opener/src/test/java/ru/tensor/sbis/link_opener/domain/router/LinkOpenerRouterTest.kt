package ru.tensor.sbis.link_opener.domain.router

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import org.mockito.kotlin.*
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule
import ru.tensor.sbis.common.testing.doReturn
import ru.tensor.sbis.link_opener.data.InnerLinkPreview
import ru.tensor.sbis.link_opener.domain.LinkOpenHandlerCreatorImpl
import ru.tensor.sbis.link_opener.ui.LinkOpenerProgressDispatcher
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenEventHandler

class LinkOpenerRouterTest {

    @get:Rule
    val rxRule = TrampolineSchedulerRule()

    private val mockEditor = mock<SharedPreferences.Editor> {
        on { remove(any()) } doReturn this.mock
    }

    private val mockPrefs = mock<SharedPreferences> {
        on { edit() } doReturn mockEditor
    }
    private val mockContext = mock<Context> {
        on { applicationContext } doReturn mock()
        on { getSharedPreferences(any(), any()) } doReturn mockPrefs
    }

    private lateinit var testEventHandler: LinkOpenEventHandler
    private lateinit var testWebEventHandler: LinkOpenEventHandler
    private val mockFactory = mock<LinkOpenHandlerFactory> {
        on { create(anyOrNull()) } doAnswer { testEventHandler }
        on { createWebViewHandler(anyOrNull()) } doAnswer { testWebEventHandler }
    }
    private val mockLinkOpenerProgressDispatcher = mock<LinkOpenerProgressDispatcher>()


    private lateinit var router: LinkOpenerRouter

    @Before
    fun setUp() {
        router = LinkOpenerRouter(
            context = mockContext,
            factory = mockFactory,
            progressDispatcher = mockLinkOpenerProgressDispatcher
        )
    }

    @Test
    fun `Return completed state from handler on invoke registered link`() {
        var handlerCompleted = false
        testEventHandler = LinkOpenHandlerCreatorImpl().createSingle(DocType.PERSON) { _, _ ->
            handlerCompleted = true
        }.getEventHandlers().first()

        val observer = router.navigate(
            InnerLinkPreview(docType = DocType.PERSON)
        ).test()

        observer
            .assertValueCount(1)
            .assertValues(true)
        assertTrue(handlerCompleted)
    }

    @Test
    fun `Return completed state from handler through routing with intent`() {
        val mockIntent = mock<Intent>()
        testEventHandler = LinkOpenHandlerCreatorImpl().createSingleForRouter(DocType.DOCUMENT) { _, _ -> mockIntent }
            .getEventHandlers().first()

        val observer = router.navigate(
            InnerLinkPreview(docType = DocType.DOCUMENT)
        ).test()

        observer
            .assertValueCount(1)
            .assertValue(true)
        verify(mockContext).startActivity(mockIntent)
    }

    @Test
    fun `Return completed state from handler through routing with no intent`() {
        testEventHandler = LinkOpenHandlerCreatorImpl().createSingleForRouter(DocType.DOCUMENT) { _, _ -> null }
            .getEventHandlers().first()
        var handlerCompleted = false
        testWebEventHandler = LinkOpenHandlerCreatorImpl().createSingle(DocType.UNKNOWN) { _, _ ->
            handlerCompleted = true
        }.getEventHandlers().first()

        val preview = InnerLinkPreview(fullUrl = "fullUrl", docType = DocType.DOCUMENT)
        val observer = router.navigate(preview).test()

        observer
            .assertValueCount(1)
            .assertValue(true)
        verify(mockFactory).createWebViewHandler(eq(preview))
        assertTrue(handlerCompleted)
    }

    @Test
    fun `Return uncompleted state from handler through routing with no intent and no href`() {
        testEventHandler = LinkOpenHandlerCreatorImpl().createSingleForRouter(DocType.DOCUMENT) { _, _ -> null }
            .getEventHandlers().first()

        val observer = router.navigate(
            InnerLinkPreview(docType = DocType.DOCUMENT)
        ).test()

        observer
            .assertValueCount(1)
            .assertValue(false)
        verify(mockFactory, never()).createWebViewHandler(anyOrNull())
    }
}