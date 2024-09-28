package ru.tensor.sbis.link_opener.domain

import org.mockito.kotlin.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.MockedStatic
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule
import ru.tensor.sbis.common.testing.doReturn
import ru.tensor.sbis.common.util.AppConfig
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType
import ru.tensor.sbis.toolbox_decl.linkopener.data.LinkDocSubtype
import ru.tensor.sbis.link_opener.data.InnerLinkPreview
import ru.tensor.sbis.link_opener.data.IncomingLinkType
import ru.tensor.sbis.link_opener.data.UriContainer
import ru.tensor.sbis.link_opener.domain.parser.DeeplinkParser
import ru.tensor.sbis.link_opener.domain.parser.LinkTypeDetector
import ru.tensor.sbis.link_opener.domain.parser.ScalableParser
import ru.tensor.sbis.link_opener.ui.LinkOpenerProgressDispatcher
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreview
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreviewImpl
import ru.tensor.sbis.toolbox_decl.linkopener.service.LinkDecoratorServiceRepository
import ru.tensor.sbis.toolbox_decl.linkopener.service.Subscription

@RunWith(MockitoJUnitRunner::class)
class DeeplinkParserTest {

    @get:Rule
    @Suppress("RedundantVisibilityModifier")
    public var rule = TrampolineSchedulerRule()

    private val mockNetworkAssistant = mock<NetworkUtils>()
    private val mockLinkOpenerProgressDispatcher = mock<LinkOpenerProgressDispatcher>()
    private val spyScalableParser: ScalableParser = spy(ScalableParser())
    private val mockLinkTypeDetector = mock<LinkTypeDetector> {
        on { getType(any()) } doAnswer { testedLinkType }
    }
    private val spyOnParse: (InnerLinkPreview) -> Unit = spy(fun(res: InnerLinkPreview) {
        testedParseResult = res
    })

    private val mockSubscription = mock<Subscription> {
        on { enable() } doAnswer {}
        on { disable() } doAnswer {}
    }

    private val mockServiceRepository = mock<LinkDecoratorServiceRepository> {
        on { getDecoratedLinkWithoutDetection(anyOrNull()) } doAnswer { linkPreview }
        on { subscribe(anyOrNull()) } doAnswer { invocationOnMock ->
            eventCallback = invocationOnMock.arguments.first() as LinkDecoratorServiceRepository.DataRefreshedCallback
            mockSubscription
        }
    }
    private var eventCallback: LinkDecoratorServiceRepository.DataRefreshedCallback? = null
    private var linkPreview: LinkPreview = getLinkPreview()
    private var isNotConnected: Boolean = false

    private lateinit var parser: DeeplinkParser
    private var testedLinkType = IncomingLinkType.SBIS
    private var testedParseResult: InnerLinkPreview? = null
    private var testArgs = UriContainer(uri = "test uri")

    @Before
    fun setUp() {
        parser = DeeplinkParser(
            linkDecoratorServiceRepository = mock {
                on { get() } doReturn mockServiceRepository
            },
            scalableParser = spyScalableParser,
            linkTypeDetector = mockLinkTypeDetector,
            mapper = mock(),
            networkUtils = mockNetworkAssistant,
            progressDispatcher = mockLinkOpenerProgressDispatcher,
            configuration = mock()
        )
    }

    @After
    fun tearDown() {
        eventCallback = null
        linkPreview = getLinkPreview()
        isNotConnected = false
        testedLinkType = IncomingLinkType.SBIS
        testedParseResult = null
        clearInvocations(mockSubscription)
    }

    @Test
    fun `While initialization does NOT create cpp callback`() {
        verify(mockServiceRepository, never()).subscribe(anyOrNull())
        assertNull(eventCallback)
    }

    @Test
    fun `Subscribe ON cpp service when client invoke Observable parseIntent`() {
        val testObservable = parser.observeParsing(testArgs).test(false)

        testObservable.assertNoErrors()
        verify(mockServiceRepository).subscribe(any())
        verify(mockSubscription).enable()
        verify(mockSubscription, never()).disable()
        assertNotNull(eventCallback)
    }

    @Test
    fun `Return link preview when client invoke Observable parseIntent`() {
        val testObservable = parser.observeParsing(testArgs).test()

        testObservable.apply {
            assertTrue(valueCount() == 1)
            assertTrue(values()?.first()?.href == linkPreview.href)
        }
    }

    @Test
    fun `Unsubscribe of cpp service when client dispose Observable parseIntent`() {
        val testObservable = parser.observeParsing(mock()).test()
        testObservable.dispose()

        assert(testObservable.isDisposed)
        verify(mockSubscription).disable()
    }

    @Test
    fun `Subscribe ON cpp service when client invoke Callback parseIntent and then disposes of itself`() {
        parser.executeOnParsing(mock()) {}

        verify(mockServiceRepository).subscribe(anyOrNull())
        verify(mockSubscription).enable()
        verify(mockSubscription).disable()
        assertNotNull(eventCallback)
    }

    @Test
    fun `Invoke link handler when client invoke Callback parseIntent for link`() {
        linkPreview = getLinkPreview(false)

        parser.executeOnParsing(testArgs) {
            assertNotNull(it)
        }
        verify(mockServiceRepository).getDecoratedLinkWithoutDetection(anyOrNull())
    }

    @Test
    fun `On parse foreign link handler through extendable parser`() {
        testedLinkType = IncomingLinkType.FOREIGN

        parser.executeOnParsing(UriContainer(uri = "")) {}
        verify(spyScalableParser).parseNoSbisUrlToLinkPreview(any())
    }

    @Test
    fun `On parse sbis link handler without extendable parser`() {
        testedLinkType = IncomingLinkType.SBIS

        parser.executeOnParsing(UriContainer(uri = "")) {}
        verify(spyScalableParser, never()).parseNoSbisUrlToLinkPreview(any())
    }

    @Test
    fun `Use extendable parser in correct way when link (uri) is just foreign `() {
        testedLinkType = IncomingLinkType.FOREIGN
        val foreignLink = "https://www.drom.ru/"

        parser.executeOnParsing(UriContainer(uri = foreignLink), onParse = spyOnParse)
        verify(spyScalableParser).parseNoSbisUrlToLinkPreview(foreignLink)
        verify(spyOnParse).invoke(any())

        assertNotNull(testedParseResult)
        assertTrue(testedParseResult!!.href.isNotBlank())
        assertTrue(testedParseResult!!.docType == DocType.UNKNOWN)
    }

    @Test
    fun `Use extendable parser in correct way when link (uri) is UNKNOWN_ONLINE_DOC`() {
        testedLinkType = IncomingLinkType.FOREIGN
        val foreignLink = "https://online.sbis.ru/something further"

        parser.executeOnParsing(UriContainer(uri = foreignLink), onParse = spyOnParse)
        verify(spyScalableParser).parseNoSbisUrlToLinkPreview(foreignLink)
        verify(spyOnParse).invoke(any())

        assertNotNull(testedParseResult)
        assertTrue(testedParseResult!!.href == foreignLink)
        assertTrue(testedParseResult!!.docType == DocType.UNKNOWN_ONLINE_DOC)
    }

    @Test
    fun `Do nothing on parse invalid link`() {
        testedLinkType = IncomingLinkType.INVALID

        parser.executeOnParsing(UriContainer(uri = "")) {}
        verify(mockServiceRepository, never()).getDecoratedLinkWithoutDetection(anyOrNull())
        verify(spyScalableParser, never()).parseNoSbisUrlToLinkPreview(any())
    }

    @Test
    fun `Synchronously return true if parser can process url`() {
        testedLinkType = IncomingLinkType.FOREIGN

        assertTrue(parser.executeOnParsing(UriContainer(uri = "")) {})
    }

    @Test
    fun `Synchronously return false if parser can not process url`() {
        testedLinkType = IncomingLinkType.INVALID

        assertFalse(parser.executeOnParsing(UriContainer(uri = "")) {})
    }

    companion object {
        private var mockedAppConfig: MockedStatic<AppConfig>? = null

        @BeforeClass
        @JvmStatic
        fun mockStatic() {
            mockedAppConfig = ru.tensor.sbis.common.testing.mockStatic {
                on<Boolean> { AppConfig.isDebug() } doReturn true
            }
        }

        @AfterClass
        @JvmStatic
        fun closeMocks() {
            mockedAppConfig?.close()
        }
    }
}

private const val testHref = "https://online.sbis.ru/opendoc.html?guid=6e6e866b-13f5-4098-851f-acd6ccd7f348"
private fun getLinkPreview(isPredictable: Boolean = false): LinkPreview =
    LinkPreviewImpl(
        href = if (isPredictable) "" else testHref,
        title = if (isPredictable) "" else "title",
        docSubtype = if (isPredictable) LinkDocSubtype.UNKNOWN else LinkDocSubtype.TASK_ORDER,
    )