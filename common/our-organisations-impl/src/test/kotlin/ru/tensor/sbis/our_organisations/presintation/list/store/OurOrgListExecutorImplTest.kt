package ru.tensor.sbis.our_organisations.presintation.list.store

import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.content.res.ResourcesCompat
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import io.mockk.junit5.MockKExtension
import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.Disposable
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.internal.schedulers.ImmediateThinScheduler
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.subjects.BehaviorSubject
import junitparams.JUnitParamsRunner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.MockedConstruction
import org.mockito.Mockito
import org.mockito.Mockito.mockConstruction
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import ru.tensor.sbis.design.stubview.ImageStubContent
import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.design.stubview.StubViewImageType
import ru.tensor.sbis.our_organisations.data.OurOrgFilter
import ru.tensor.sbis.our_organisations.feature.data.Organisation
import ru.tensor.sbis.our_organisations.feature.data.OurOrgParams
import ru.tensor.sbis.our_organisations.presentation.list.interactor.OurOrgListInteractor
import ru.tensor.sbis.our_organisations.presentation.list.store.OurOrgListExecutorImpl
import ru.tensor.sbis.our_organisations.presentation.list.store.OurOrgListStore
import ru.tensor.sbis.our_organisations.presentation.list.store.OurOrgListStore.Intent
import ru.tensor.sbis.our_organisations.presentation.list.store.OurOrgListStore.Label
import ru.tensor.sbis.our_organisations.presentation.list.store.OurOrgListStore.State
import ru.tensor.sbis.our_organisations.presentation.list.store.OurOrgListStoreFactory.Action
import ru.tensor.sbis.our_organisations.presentation.list.store.OurOrgListStoreFactory.Message
import ru.tensor.sbis.our_organisations.presentation.list.ui.ListResultWrapper
import ru.tensor.sbis.our_organisations.presentation.list.ui.OurOrgListStateController
import ru.tensor.sbis.our_organisations.presentation.list.ui.adapter.OrganisationVM
import java.util.UUID
import java.util.concurrent.TimeUnit

@RunWith(JUnitParamsRunner::class)
@ExperimentalCoroutinesApi
@ExtendWith(MockKExtension::class)
class OurOrgListExecutorImplTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()
    private val testDispatcher = UnconfinedTestDispatcher()

    private val dataRefreshSubj = BehaviorSubject.create<OurOrgListInteractor.DataEvent>()
    private val ourOrgListInteractor = mock<OurOrgListInteractor> {
        onBlocking { listRx(any(OurOrgFilter::class.java)) } doAnswer {
            ListResultWrapper(mutableListOf(organisation, organisation2), false)
        }
        on { subscribeDataRefreshEvents() } doReturn dataRefreshSubj

    }
    private val state = mock<State> {
        on { data } doAnswer { showDataState }
        on { selectedOrganisations } doAnswer { selectedOrganisations }
    }
    private val callbacks: Executor.Callbacks<State, Message, Label> = mock {
        on { state } doReturn state
    }

    private var initParams = OurOrgParams(listOf())

    private var calledControllerRelease = false
    private var calledControllerRefresh = false
    private var calledControllerRefreshAllPage = false
    private var calledControllerLoadNewPage = false
    private var controllerOurOrgFilter: OurOrgFilter? = null

    private var organisation = Organisation(
        1,
        UUID.randomUUID(),
        "name",
        "",
        null,
        false,
        true,
        null,
        null,
        true,
        true,
        null
    )

    private var organisationVM = OrganisationVM(organisation, null)

    private var organisation2 = Organisation(
        2,
        UUID.randomUUID(),
        "name2",
        "",
        null,
        false,
        true,
        null,
        null,
        true,
        true,
        null
    )

    private var organisationVM2 = OrganisationVM(organisation2, null)

    private val showDataState = OurOrgListStore.ShowData(listOf(organisationVM, organisationVM2), false, false)

    private val selectedOrganisations: MutableList<Organisation> = mutableListOf()

    private lateinit var ourOrgListStateControllerMock: MockedConstruction<OurOrgListStateController>

    private fun setUpRxSchedulers() {
        val immediate: Scheduler = object : Scheduler() {
            override fun scheduleDirect(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
                return super.scheduleDirect(run, 0, unit)
            }

            override fun createWorker(): Worker {
                return ExecutorScheduler.ExecutorWorker({ run -> run?.run() }, true)
            }
        }
        RxJavaPlugins.setInitIoSchedulerHandler { immediate }
        RxJavaPlugins.setInitComputationSchedulerHandler { immediate }
        RxJavaPlugins.setInitNewThreadSchedulerHandler { immediate }
        RxJavaPlugins.setInitSingleSchedulerHandler { immediate }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { immediate }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        isAssertOnMainThreadEnabled = false

        ourOrgListStateControllerMock = mockConstruction(OurOrgListStateController::class.java) { mock, context ->
            Mockito.`when`(mock.release()).thenAnswer {
                calledControllerRelease = true
                Unit
            }
            Mockito.`when`(mock.refresh(any())).thenAnswer {
                controllerOurOrgFilter = it.arguments[0] as? OurOrgFilter
                calledControllerRefresh = true
                Unit
            }
            Mockito.`when`(mock.refreshAllPage(any())).thenAnswer {
                controllerOurOrgFilter = it.arguments[0] as? OurOrgFilter
                calledControllerRefreshAllPage = true
                Unit
            }
            Mockito.`when`(mock.loadNewPage(any())).thenAnswer {
                controllerOurOrgFilter = it.arguments[0] as? OurOrgFilter
                calledControllerLoadNewPage = true
                Unit
            }
        }

        setUpRxSchedulers()

        // Нужно для AndroidSchedulers.mainThread()
        RxAndroidPlugins.setInitMainThreadSchedulerHandler {
            ImmediateThinScheduler.INSTANCE
        }
        RxAndroidPlugins.setMainThreadSchedulerHandler {
            ImmediateThinScheduler.INSTANCE
        }
    }

    @After
    fun tearDown() {
        isAssertOnMainThreadEnabled = true
        Dispatchers.resetMain()
        ourOrgListStateControllerMock.close()
    }

    @Test
    fun `init`() =
        runTest {
            OurOrgListExecutorImpl(ourOrgListInteractor, initParams)

            assertEquals(1, ourOrgListStateControllerMock.constructed().size)
        }

    // Test Intent

    @Test
    fun `On Request Page`() =
        runTest {
            val executor = OurOrgListExecutorImpl(ourOrgListInteractor, initParams)
            executor.init(callbacks)

            executor.executeIntent(Intent.RequestPage)

            assertTrue(calledControllerLoadNewPage)
            assertTrue(controllerOurOrgFilter?.requestNewData == true)
        }

    @Test
    fun `On Refresh`() =
        runTest {
            val executor = OurOrgListExecutorImpl(ourOrgListInteractor, initParams)
            executor.init(callbacks)

            executor.executeIntent(Intent.Refresh)

            assertTrue(calledControllerRefresh)
            assertTrue(controllerOurOrgFilter?.requestNewData == true)
        }

    @Test
    fun `On Cleared`() =
        runTest {
            val executor = OurOrgListExecutorImpl(ourOrgListInteractor, initParams)
            executor.init(callbacks)

            executor.executeIntent(Intent.OnCleared)

            assertTrue(calledControllerRelease)
        }

    @Test
    fun `Open Filter`() =
        runTest {
            val executor = OurOrgListExecutorImpl(ourOrgListInteractor, initParams)
            executor.init(callbacks)

            val mockView = mock<View>()

            executor.executeIntent(Intent.OpenFilter(anchor = mockView))

            verify(callbacks).onLabel(Label.OpenFilter(mockView, any()))
        }

    @Test
    fun `On Apply`() =
        runTest {
            val executor = OurOrgListExecutorImpl(ourOrgListInteractor, initParams)
            executor.init(callbacks)

            executor.executeIntent(Intent.OnApply)

            verify(callbacks).onLabel(Label.ClickApply(listOf()))
        }

    @Test
    fun `On Reset`() =
        runTest {
            val executor = OurOrgListExecutorImpl(ourOrgListInteractor, initParams)
            executor.init(callbacks)

            organisationVM.isSelected.set(true)

            executor.executeIntent(Intent.OnReset)

            assertFalse(organisationVM.isSelected.get())
        }

    @Test
    fun `Organisation Item Clicked - select, multiply choice, no need close`() =
        runTest {
            initParams = initParams.copy(isMultipleChoice = true)
            organisationVM.isSelected.set(false)
            organisationVM2.isSelected.set(false)
            selectedOrganisations.clear()

            val executor = OurOrgListExecutorImpl(ourOrgListInteractor, initParams)
            executor.init(callbacks)

            executor.executeIntent(Intent.OrganisationItemClicked(organisationVM, false))

            verify(callbacks).onMessage(Message.UpdateSelectedOrganisations(listOf(organisation)))
            selectedOrganisations.add(organisationVM.organisation)

            assertTrue(organisationVM.isSelected.get())
            assertFalse(organisationVM2.isSelected.get())
            verify(callbacks).onLabel(Label.ClickOrganisation(listOf(organisation)))

            executor.executeIntent(Intent.OrganisationItemClicked(organisationVM2, false))

            verify(callbacks).onMessage(Message.UpdateSelectedOrganisations(listOf(organisation, organisation2)))
            selectedOrganisations.add(organisationVM2.organisation)

            assertTrue(organisationVM.isSelected.get())
            assertTrue(organisationVM2.isSelected.get())
            verify(callbacks).onLabel(Label.ClickOrganisation(listOf(organisation, organisation2)))
        }

    @Test
    fun `Organisation Item Clicked - unselect, multiply choice, no need close`() =
        runTest {
            initParams = initParams.copy(isMultipleChoice = true)
            selectedOrganisations.clear()

            val executor = OurOrgListExecutorImpl(ourOrgListInteractor, initParams)
            executor.init(callbacks)

            executor.executeIntent(Intent.OrganisationItemClicked(organisationVM, false))
            selectedOrganisations.add(organisationVM.organisation)

            executor.executeIntent(Intent.OrganisationItemClicked(organisationVM2, false))
            selectedOrganisations.add(organisationVM2.organisation)

            verify(callbacks).onMessage(Message.UpdateSelectedOrganisations(listOf(organisation, organisation2)))

            assertTrue(organisationVM.isSelected.get())
            assertTrue(organisationVM2.isSelected.get())

            executor.executeIntent(Intent.OrganisationItemClicked(organisationVM, false))

            verify(callbacks).onMessage(Message.UpdateSelectedOrganisations(listOf(organisation2)))
            selectedOrganisations.remove(organisationVM.organisation)

            assertFalse(organisationVM.isSelected.get())
            assertTrue(organisationVM2.isSelected.get())
            verify(callbacks).onLabel(Label.ClickOrganisation(listOf(organisation2)))

            executor.executeIntent(Intent.OrganisationItemClicked(organisationVM2, false))

            verify(callbacks).onMessage(Message.UpdateSelectedOrganisations(emptyList()))
            selectedOrganisations.remove(organisationVM.organisation)

            assertFalse(organisationVM.isSelected.get())
            assertFalse(organisationVM2.isSelected.get())
            verify(callbacks).onLabel(Label.ClickOrganisation(listOf()))
        }

    @Test
    fun `Organisation Item Clicked - select, multiply choice, need close`() =
        runTest {
            initParams = initParams.copy(isMultipleChoice = true)
            organisationVM.isSelected.set(false)

            val executor = OurOrgListExecutorImpl(ourOrgListInteractor, initParams)
            executor.init(callbacks)

            executor.executeIntent(Intent.OrganisationItemClicked(organisationVM, true))

            verify(callbacks).onLabel(Label.ClickOrganisation(listOf(organisationVM.organisation)))
            verify(callbacks).onLabel(Label.ClickApply(listOf(organisation)))
        }

    @Test
    fun `Organisation Item Clicked - unselect, multiply choice, need close`() =
        runTest {
            initParams = initParams.copy(isMultipleChoice = true)
            organisationVM.isSelected.set(true)
            selectedOrganisations.clear()

            val executor = OurOrgListExecutorImpl(ourOrgListInteractor, initParams)
            executor.init(callbacks)

            executor.executeIntent(Intent.OrganisationItemClicked(organisationVM, true))

            verify(callbacks).onLabel(Label.ClickOrganisation(listOf()))
            verify(callbacks).onLabel(Label.ClickApply(listOf()))
        }

    @Test
    fun `Organisation Item Clicked - select, single choice`() =
        runTest {
            initParams = initParams.copy(isMultipleChoice = false)
            organisationVM.isSelected.set(false)
            organisationVM2.isSelected.set(false)
            selectedOrganisations.clear()

            val executor = OurOrgListExecutorImpl(ourOrgListInteractor, initParams)
            executor.init(callbacks)

            executor.executeIntent(Intent.OrganisationItemClicked(organisationVM))

            verify(callbacks).onMessage(Message.UpdateSelectedOrganisations(listOf(organisation)))
            selectedOrganisations.add(organisationVM.organisation)

            assertTrue(organisationVM.isSelected.get())
            assertFalse(organisationVM2.isSelected.get())
            verify(callbacks).onLabel(Label.ClickOrganisation(listOf(organisation)))

            executor.executeIntent(Intent.OrganisationItemClicked(organisationVM2))

            verify(callbacks).onMessage(Message.UpdateSelectedOrganisations(listOf(organisation2)))
            selectedOrganisations.remove(organisationVM.organisation)
            selectedOrganisations.add(organisationVM2.organisation)

            assertFalse(organisationVM.isSelected.get())
            assertTrue(organisationVM2.isSelected.get())
            verify(callbacks).onLabel(Label.ClickOrganisation(listOf(organisation2)))
        }

    @Test
    fun `On Show Content`() =
        runTest {
            val executor = OurOrgListExecutorImpl(ourOrgListInteractor, initParams)
            executor.init(callbacks)

            executor.executeIntent(Intent.OnShowContent)

            verify(callbacks).onLabel(Label.OnShowContent)
        }

    @Test
    fun `Subscribe Data Event`() =
        runTest {
            val executor = OurOrgListExecutorImpl(ourOrgListInteractor, initParams)
            executor.init(callbacks)

            executor.executeIntent(Intent.SubscribeDataEvent)

            verify(ourOrgListInteractor).subscribeDataRefreshEvents()
            assertTrue(calledControllerRefreshAllPage)
            assertTrue(controllerOurOrgFilter?.requestNewData == true)
            assertTrue(dataRefreshSubj.hasObservers())
        }

    @Test
    fun `UnSubscribe Data Event`() =
        runTest {
            val executor = OurOrgListExecutorImpl(ourOrgListInteractor, initParams)
            executor.init(callbacks)

            executor.executeIntent(Intent.SubscribeDataEvent)
            executor.executeIntent(Intent.UnSubscribeDataEvent)

            assertFalse(dataRefreshSubj.hasObservers())
        }

    @Test
    fun `Search Text Changed`() =
        runTest {
            val executor = OurOrgListExecutorImpl(ourOrgListInteractor, initParams)
            executor.init(callbacks)

            executor.executeIntent(Intent.SearchTextChanged("searchText"))

            assertTrue(calledControllerRefresh)
            assertTrue(controllerOurOrgFilter?.requestNewData == true)
            assertTrue(controllerOurOrgFilter?.searchString == "searchText")
        }

    @Test
    fun `Search Text Changed - the same text`() =
        runTest {
            val executor = OurOrgListExecutorImpl(ourOrgListInteractor, initParams)
            executor.init(callbacks)

            executor.executeIntent(Intent.SearchTextChanged("searchText"))
            calledControllerRefresh = false

            executor.executeIntent(Intent.SearchTextChanged("searchText"))

            assertFalse(calledControllerRefresh)
        }

    @Test
    fun `Loaded Intent`() =
        runTest {
            val executor = OurOrgListExecutorImpl(ourOrgListInteractor, initParams)
            executor.init(callbacks)

            executor.executeIntent(Intent.Loaded(listOf(organisation), true, false))

            verify(callbacks).onMessage(
                Message.Loaded(
                    OurOrgListStore.ShowData(
                        listOf(OrganisationVM(organisation, null)),
                        true,
                        false
                    )
                )
            )
        }

    @Test
    fun `Show Empty View - is not search`() =
        runTest {
            val executor = OurOrgListExecutorImpl(ourOrgListInteractor, initParams)
            executor.init(callbacks)

            executor.executeIntent(Intent.ShowEmptyView)

            verify(callbacks).onMessage(
                Message.Empty(
                    ImageStubContent(
                        imageType = StubViewImageType.EMPTY,
                        messageRes = ru.tensor.sbis.our_organisations.R.string.our_org_empty_title,
                        detailsRes = ResourcesCompat.ID_NULL
                    ),
                    false
                )
            )
        }

    @Test
    fun `Show Empty View - is search`() =
        runTest {
            val executor = OurOrgListExecutorImpl(ourOrgListInteractor, initParams)
            executor.init(callbacks)

            executor.executeIntent(Intent.SearchTextChanged("searchText"))
            executor.executeIntent(Intent.ShowEmptyView)

            verify(callbacks).onMessage(
                Message.Empty(
                    StubViewCase.NO_SEARCH_RESULTS.getContent(),
                    true
                )
            )
        }

    // Test Action

    @Test
    fun `Init Action - empty list`() =
        runTest {
            val executor = OurOrgListExecutorImpl(ourOrgListInteractor, initParams)
            executor.init(callbacks)

            executor.executeAction(Action.Init)
            executor.executeIntent(Intent.OnApply)

            verify(callbacks, never()).onMessage(Message.Progress(emptyProgress = true))
            verify(callbacks).onLabel(Label.ClickApply(listOf()))
            verify(ourOrgListInteractor).subscribeDataRefreshEvents()
        }

    @Test
    fun `Init Action - not empty list`() =
        runTest {
            initParams = initParams.copy(selectedOrganisations = listOf(1, 2))

            val executor = OurOrgListExecutorImpl(ourOrgListInteractor, initParams)
            executor.init(callbacks)

            executor.executeAction(Action.Init)

            verify(callbacks).onMessage(Message.Progress(emptyProgress = true))
            verify(callbacks).onMessage(Message.Progress(emptyProgress = false))
            verify(callbacks).onMessage(Message.UpdateSelectedOrganisations(listOf(organisation, organisation2)))
            verify(ourOrgListInteractor).subscribeDataRefreshEvents()
        }

    @Test
    fun `Load Data`() =
        runTest {
            val executor = OurOrgListExecutorImpl(ourOrgListInteractor, initParams)
            executor.init(callbacks)

            executor.executeAction(Action.LoadData)

            assertTrue(calledControllerRefreshAllPage)
            assertTrue(controllerOurOrgFilter?.requestNewData == true)
        }

    @Test
    fun `Refresh All Data`() =
        runTest {
            val executor = OurOrgListExecutorImpl(ourOrgListInteractor, initParams)
            executor.init(callbacks)

            executor.executeAction(Action.RefreshAllData)

            assertTrue(calledControllerRefreshAllPage)
            assertTrue(controllerOurOrgFilter?.requestNewData == false)
        }

    @Test
    fun `Refresh Action`() =
        runTest {
            val executor = OurOrgListExecutorImpl(ourOrgListInteractor, initParams)
            executor.init(callbacks)

            executor.executeAction(Action.Refresh)

            assertTrue(calledControllerRefresh)
            assertTrue(controllerOurOrgFilter?.requestNewData == true)
        }

    @Test
    fun `Request Page`() =
        runTest {
            val executor = OurOrgListExecutorImpl(ourOrgListInteractor, initParams)
            executor.init(callbacks)

            executor.executeAction(Action.RequestPage)

            assertTrue(calledControllerLoadNewPage)
            assertTrue(controllerOurOrgFilter?.requestNewData == true)
        }
}