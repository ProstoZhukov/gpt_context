package ru.tensor.sbis.link_opener.domain.router

import androidx.test.core.app.ApplicationProvider
import org.mockito.kotlin.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.link_opener.analytics.Analytics
import ru.tensor.sbis.link_opener.contract.LinkOpenerFeatureConfiguration
import ru.tensor.sbis.link_opener.domain.router.producer.*
import ru.tensor.sbis.link_opener.utils.ShadowUri
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreviewImpl
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType
import ru.tensor.sbis.toolbox_decl.linkopener.data.LinkDocSubtype
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenEventHandler
import javax.inject.Provider

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], shadows = [ShadowUri::class])
internal class LinkOpenHandlerFactoryTest {

    private val testUuid = "6e6e866b-13f5-4098-851f-acd6ccd7f348"
    private val testHref = "https://online.sbis.ru/opendoc.html?guid=$testUuid"
    private val testOurHref = "https://online.sabyget.ru/opendoc.html?guid=$testUuid"
    private val testForeignHref = "https://ru.wikipedia.org/wiki"
    private var testKeywords = listOf("sbis.ru", "sabyget.ru")

    private var inAppHandler: LinkOpenEventHandler? = null
    private var otherAppHandler: LinkOpenEventHandler? = null
    private var stubHandler = mock<LinkOpenEventHandler>()
    private val mockInAppProducer = mock<InAppLinkOpenHandlerProducer> {
        on { produce(any()) } doAnswer { inAppHandler }
    }
    private val mockOtherInAppProducer = mock<OtherInAppLinkOpenHandlerProducer> {
        on { produce(any()) } doAnswer { otherAppHandler }
    }
    private val mockOurLinkWebViewProducer = mock<OurLinkOpenHandlerProducer> {
        on { produce(any()) } doAnswer { stubHandler }
    }
    private val mockForeignLinkAppProducer = mock<ForeignLinkOpenHandlerProducer> {
        on { produce(any()) } doAnswer { stubHandler }
    }
    private val mockTabsLinkAppProducer = mock<ForeignLinkOpenHandlerProducer> {
        on { produce(any()) } doAnswer { stubHandler }
    }
    private val mockLinkOpenerFeatureConfiguration = mock<LinkOpenerFeatureConfiguration> {
        on { areCustomTabsAllowed } doReturn true
        on { useSabylinkAppRedirect } doReturn true
    }

    //TODO https://online.sbis.ru/opendoc.html?guid=69f15e97-948b-422c-a670-c864a6874a25&client=3 тестирование отправки аналитики
    private val spyAnalytics = spy(Analytics())

    private lateinit var factory: LinkOpenHandlerFactory

    @Before
    fun setUp() {
        factory = LinkOpenHandlerFactory(
            producers = mapOf(
                InAppLinkOpenHandlerProducer::class.java to Provider { mockInAppProducer },
                OtherInAppLinkOpenHandlerProducer::class.java to Provider { mockOtherInAppProducer },
                OurLinkOpenHandlerProducer::class.java to Provider { mockOurLinkWebViewProducer },
                ForeignLinkOpenHandlerProducer::class.java to Provider { mockForeignLinkAppProducer },
                TabsLinkOpenHandlerProducer::class.java to Provider { mockTabsLinkAppProducer }
            ),
            keywords = testKeywords,
            context = ApplicationProvider.getApplicationContext(),
            configuration = mockLinkOpenerFeatureConfiguration,
            analytics = spyAnalytics
        )
    }

    @After
    fun tearDown() {
        inAppHandler = null
    }

    @Test
    fun `Determine the target producer for our link with handler`() {
        inAppHandler = mock()
        val preview = LinkPreviewImpl(testHref, docType = DocType.PERSON)

        factory.create(preview)

        verify(mockInAppProducer).produce(eq(preview))
        verify(mockOurLinkWebViewProducer, never()).produce(anyOrNull())
        verify(mockForeignLinkAppProducer, never()).produce(anyOrNull())
        verify(mockTabsLinkAppProducer, never()).produce(anyOrNull())
    }

    @Test
    fun `Determine the target producer for our link without handler`() {
        val preview = LinkPreviewImpl(testHref, docType = DocType.PERSON)

        factory.create(preview)

        verify(mockInAppProducer).produce(eq(preview))
        verify(mockOurLinkWebViewProducer).produce(eq(preview))
        verify(mockForeignLinkAppProducer, never()).produce(anyOrNull())
    }

    @Test
    fun `Determine the target producer without handler`() {
        val preview = LinkPreviewImpl(testOurHref, docType = DocType.DOCUMENT)

        factory.create(preview)

        verify(mockInAppProducer).produce(eq(preview))
        verify(mockOurLinkWebViewProducer).produce(eq(preview))
        verify(mockForeignLinkAppProducer, never()).produce(anyOrNull())
    }

    @Test
    fun `Determine the target producer for foreign link`() {
        val preview = LinkPreviewImpl(testForeignHref, docType = DocType.UNKNOWN)

        factory.create(preview)

        verify(mockInAppProducer).produce(eq(preview))
        verify(mockOurLinkWebViewProducer, never()).produce(anyOrNull())
        verify(mockForeignLinkAppProducer).produce(eq(preview))
    }

    @Test
    fun `Determine target in-app producers based on link subtype (choose InApp)`() {
        inAppHandler = mock {
            on { types } doReturn listOf(DocType.DOCUMENT)
            on { subtypes } doReturn listOf(LinkDocSubtype.UNKNOWN, LinkDocSubtype.INCOMING_PAYMENT)
        }
        otherAppHandler = mock {
            on { types } doReturn listOf(DocType.DOCUMENT)
            on { subtypes } doReturn listOf(LinkDocSubtype.TASK_ORDER, LinkDocSubtype.TASK_NOTE)
        }
        val preview =
            LinkPreviewImpl(testOurHref, docType = DocType.DOCUMENT, docSubtype = LinkDocSubtype.TASK_ORDER)

        val targetHandler = factory.create(preview)

        verify(mockInAppProducer).produce(eq(preview))
        verify(mockOtherInAppProducer).produce(eq(preview))

        assertEquals(otherAppHandler, targetHandler)
    }

    @Test
    fun `Determine target in-app producers based on link subtype (choose OtherInApp)`() {
        inAppHandler = mock {
            on { types } doReturn listOf(DocType.DOCUMENT)
            on { subtypes } doReturn listOf(LinkDocSubtype.UNKNOWN)
        }
        otherAppHandler = mock {
            on { types } doReturn listOf(DocType.DOCUMENT)
            on { subtypes } doReturn listOf(LinkDocSubtype.UNKNOWN)
        }
        val preview =
            LinkPreviewImpl(testOurHref, docType = DocType.DOCUMENT, docSubtype = LinkDocSubtype.TASK_NOTE)

        val targetHandler = factory.create(preview)

        verify(mockInAppProducer).produce(eq(preview))
        verify(mockOtherInAppProducer).produce(eq(preview))

        assertEquals(inAppHandler, targetHandler)
    }

    @Test
    fun `Ignore other in-app producers based on link state`() {
        inAppHandler = mock {
            on { types } doReturn listOf(DocType.DOCUMENT)
            on { subtypes } doReturn listOf(LinkDocSubtype.UNKNOWN, LinkDocSubtype.INCOMING_PAYMENT)
        }
        otherAppHandler = mock {
            on { types } doReturn listOf(DocType.DOCUMENT)
            on { subtypes } doReturn listOf(LinkDocSubtype.TASK_ORDER, LinkDocSubtype.TASK_NOTE)
        }
        val preview = LinkPreviewImpl(
            testOurHref,
            docType = DocType.DOCUMENT,
            docSubtype = LinkDocSubtype.TASK_ORDER,
            isSabylink = true
        )

        val targetHandler = factory.create(preview)

        verify(mockInAppProducer).produce(eq(preview))
        verify(mockOtherInAppProducer, never()).produce(any())

        assertEquals(inAppHandler, targetHandler)
    }
}