package ru.tensor.sbis.our_organisations.presintation.list.interactor

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import io.mockk.junit5.MockKExtension
import junitparams.JUnitParamsRunner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import ru.tensor.sbis.our_organisations.data.OurOrgFilter
import ru.tensor.sbis.our_organisations.presentation.list.interactor.OurOrgListInteractor
import ru.tensor.sbis.ourorg.generated.ListResultOfOrganizationMapOfStringString
import ru.tensor.sbis.ourorg.generated.Organization
import ru.tensor.sbis.ourorg.generated.OurorgController
import ru.tensor.sbis.ourorg.generated.OurorgFilter
import ru.tensor.sbis.platform.generated.Subscription
import ru.tensor.sbis.platform.sync.generated.AreaStatus
import ru.tensor.sbis.platform.sync.generated.AreaSyncInformer
import ru.tensor.sbis.platform.sync.generated.AreaSyncStatusChangedCallback
import ru.tensor.sbis.platform.sync.generated.AreaSyncStatusChangedEvent
import ru.tensor.sbis.platform.sync.generated.SyncType
import java.util.UUID

@RunWith(JUnitParamsRunner::class)
@ExperimentalCoroutinesApi
@ExtendWith(MockKExtension::class)
class OurOrgListInteractorTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()
    private val testDispatcher = UnconfinedTestDispatcher()

    private var refreshCallback: OurorgController.DataRefreshedCallback? = null
    private val controller = mock<OurorgController> {
        on { dataRefreshed() } doReturn object : OurorgController.DataRefreshedEvent() {
            override fun hasSubscriptions() = true

            override fun subscribe(cb: OurorgController.DataRefreshedCallback?): Subscription {
                refreshCallback = cb
                return object : Subscription() {
                    override fun enable() = Unit
                    override fun disable() = Unit
                }
            }

            override fun subscribeUnmanaged(cb: OurorgController.DataRefreshedCallback?) {}
        }

        on { list(any(OurorgFilter::class.java)) } doAnswer {
            ListResultOfOrganizationMapOfStringString().apply {
                result = returnedList
                haveMore = false
            }
        }

        on { read(parentHeadOrgUUID) } doAnswer { org }

        on { read(any(UUID::class.java)) } doAnswer {
            readOrg?.parent =
                if (readOrg?.parent == parentHeadOrgUUID)
                    null
                else
                    parentOrgUUID
            readOrg
        }

        on { refresh(any(OurorgFilter::class.java)) } doAnswer {
            ListResultOfOrganizationMapOfStringString().apply {
                result = returnedList
                haveMore = false
            }
        }
    }
    private val controllerLazy = mock<Lazy<@JvmSuppressWildcards OurorgController>> {
        on { value } doReturn controller
    }
    private var areaSyncInformerCallback: AreaSyncStatusChangedCallback? = null
    private val areaSyncStatusChangedEvent = object : AreaSyncStatusChangedEvent() {
        override fun hasSubscriptions() = true

        override fun subscribe(cb: AreaSyncStatusChangedCallback?): Subscription {
            return object : Subscription() {
                override fun enable() = Unit
                override fun disable() = Unit
            }
        }

        override fun subscribeUnmanaged(cb: AreaSyncStatusChangedCallback?) {
            areaSyncInformerCallback = cb
        }
    }
    private val areaSyncInformerInstanceMock = mock<AreaSyncInformer> {
        on { areaSyncStatusChanged("OURORG", SyncType.PARTIAL) } doAnswer {
            syncTypePartialCalled = true
            areaSyncStatusChangedEvent
        }
        on { areaSyncStatusChanged("OURORG", SyncType.INCREMENTAL) } doAnswer {
            syncTypeIncrementalCalled = true
            areaSyncStatusChangedEvent
        }
    }

    private var syncTypePartialCalled = false
    private var syncTypeIncrementalCalled = false

    private var returnedList: ArrayList<Organization> = arrayListOf()
    private val parentHeadOrgUUID = UUID.fromString("6d711338-8211-11eb-8dcd-0242ac130003")
    private var parentOrgUUID: UUID? = null
    private var ourOrgFilter = OurOrgFilter(withEliminated = false)
    private var readOrg: Organization? = Organization().apply {
        guid = UUID.randomUUID()
    }
    private var org = Organization().apply {
        guid = UUID.randomUUID()
    }

    private val interactor = OurOrgListInteractor(controllerLazy, areaSyncInformerInstanceMock)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        isAssertOnMainThreadEnabled = false
    }

    @After
    fun tearDown() {
        isAssertOnMainThreadEnabled = true
        Dispatchers.resetMain()
    }

    @Test
    fun `init`() =
        runTest {
            verify(controller).dataRefreshed()
            assertNotEquals(null, refreshCallback)
            assertTrue(syncTypePartialCalled)
            assertTrue(syncTypeIncrementalCalled)
        }

    @Test
    fun `subscribeDataRefreshEvents - REFRESH event`() =
        runTest {
            var refreshedCalled = false
            interactor.subscribeDataRefreshEvents().doOnNext {
                refreshedCalled = it == OurOrgListInteractor.DataEvent.REFRESH
            }.subscribe()

            refreshCallback?.onEvent(hashMapOf())

            delay(100)

            assertTrue(refreshedCalled)
        }

    @Test
    fun `areaSyncInformerCallback - NETWORK_WAITING`() =
        runTest {
            var networkErrorCalled = false
            interactor.subscribeDataRefreshEvents().doOnNext {
                networkErrorCalled = it == OurOrgListInteractor.DataEvent.NETWORK_ERROR
            }.subscribe()

            areaSyncInformerCallback?.onEvent(AreaStatus.NETWORK_WAITING)

            delay(100)

            assertTrue(networkErrorCalled)
        }

    @Test
    fun `areaSyncInformerCallback - ERROR`() =
        runTest {
            var errorCalled = false
            interactor.subscribeDataRefreshEvents().doOnNext {
                errorCalled = it == OurOrgListInteractor.DataEvent.ERROR
            }.subscribe()

            areaSyncInformerCallback?.onEvent(AreaStatus.ERROR)

            delay(100)

            assertTrue(errorCalled)
        }

    @Test
    fun `areaSyncInformerCallback - NOT_RUNNING, RUNNING`() =
        runTest {
            var wasCalled = false
            interactor.subscribeDataRefreshEvents().doOnNext {
                wasCalled = true
            }.subscribe()

            areaSyncInformerCallback?.onEvent(AreaStatus.NOT_RUNNING)

            delay(100)

            assertFalse(wasCalled)

            areaSyncInformerCallback?.onEvent(AreaStatus.RUNNING)

            delay(100)

            assertFalse(wasCalled)
        }

    @Test
    fun `list - get data`() =
        runTest {
            returnedList = arrayListOf(org, org)

            val result = interactor.listRx(ourOrgFilter)

            assertEquals(2, result.result.size)
            verify(controller).list(any(OurorgFilter::class.java))
        }

    @Test
    fun `getHeadOrganisation - null organisation`() =
        runTest {
            readOrg = null
            val readUUID = UUID.randomUUID()
            val result = interactor.getHeadOrganisation(readUUID)

            verify(controller).read(readUUID)
            assertEquals(null, result)
        }

    @Test
    fun `getHeadOrganisation - null parent uuid`() =
        runTest {
            val readUUID = UUID.randomUUID()
            val result = interactor.getHeadOrganisation(readUUID)

            verify(controller).read(readUUID)
            assertEquals(null, result!!.parentUUID)
        }

    @Test
    fun `getHeadOrganisation - has parent uuid`() =
        runTest {
            val readUUID = UUID.randomUUID()
            parentOrgUUID = parentHeadOrgUUID
            val result = interactor.getHeadOrganisation(readUUID)

            verify(controller).read(readUUID)
            verify(controller).read(parentHeadOrgUUID)
            assertEquals(null, result?.parentUUID)
        }

    @Test
    fun `refreshRx - get data`() =
        runTest {
            returnedList = arrayListOf(org, org)

            val result = interactor.refreshRx(ourOrgFilter)

            assertEquals(2, result.result.size)
            verify(controller).refresh(any(OurorgFilter::class.java))
        }
}