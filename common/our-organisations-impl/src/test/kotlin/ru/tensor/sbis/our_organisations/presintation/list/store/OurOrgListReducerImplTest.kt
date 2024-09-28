package ru.tensor.sbis.our_organisations.presintation.list.store

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import io.mockk.junit5.MockKExtension
import junitparams.JUnitParamsRunner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import ru.tensor.sbis.design.stubview.ResourceAttributeStubContent
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.our_organisations.feature.data.Organisation
import ru.tensor.sbis.our_organisations.presentation.list.store.OurOrgListReducerImpl
import ru.tensor.sbis.our_organisations.presentation.list.store.OurOrgListStore
import ru.tensor.sbis.our_organisations.presentation.list.store.OurOrgListStore.State
import ru.tensor.sbis.our_organisations.presentation.list.store.OurOrgListStoreFactory.Message
import ru.tensor.sbis.our_organisations.presentation.list.ui.adapter.OrganisationVM
import java.util.UUID

@RunWith(JUnitParamsRunner::class)
@ExperimentalCoroutinesApi
@ExtendWith(MockKExtension::class)
class OurOrgListReducerImplTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val reducer = OurOrgListReducerImpl()

    private var state: State = State(
        emptyProgress = false,
        showPageProgress = false,
        swipeRefreshing = false,
        stubContent = null,
        data = null,
        needShowSearch = true,
        setSelectedFilter = null,
        selectedOrganisations = emptyList(),
        discardResultAfterClick = false
    )

    private val stub = ResourceAttributeStubContent(1, null, null)

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
    fun `On Loaded`() =
        runTest {
            state = state.copy(
                stubContent = stub,
                emptyProgress = true,
                needShowSearch = false
            )
            val data = OurOrgListStore.ShowData(
                listOf(organisationVM),
                true,
                false
            )

            state = reducer.run { state.reduce(Message.Loaded(data)) }

            Assert.assertEquals(null, state.stubContent)
            Assert.assertEquals(false, state.emptyProgress)
            Assert.assertEquals(true, state.needShowSearch)
            Assert.assertEquals(data, state.data)
        }

    @Test
    fun `On Progress - Page progress`() =
        runTest {
            state = state.copy(
                needShowSearch = false,
                showPageProgress = false,
            )

            state = reducer.run { state.reduce(Message.Progress(showPageProgress = true)) }

            Assert.assertEquals(true, state.showPageProgress)
        }

    @Test
    fun `On Progress - Swipe progress`() =
        runTest {
            state = state.copy(
                needShowSearch = false,
                swipeRefreshing = false,
            )

            state = reducer.run { state.reduce(Message.Progress(swipeRefreshing = true)) }

            Assert.assertEquals(true, state.swipeRefreshing)
        }

    @Test
    fun `On Progress - Empty progress`() =
        runTest {
            state = state.copy(
                needShowSearch = false,
                emptyProgress = false,
            )

            state = reducer.run { state.reduce(Message.Progress(emptyProgress = true)) }

            Assert.assertEquals(true, state.emptyProgress)
        }

    @Test
    fun `On Empty - need search false`() =
        runTest {
            state = state.copy(
                stubContent = null,
                needShowSearch = true,
            )

            state = reducer.run { state.reduce(Message.Empty(stubContent = stub, needShowSearch = false)) }

            Assert.assertEquals(stub, state.stubContent)
            Assert.assertEquals(false, state.needShowSearch)
        }

    @Test
    fun `On Empty - need search true`() =
        runTest {
            state = state.copy(
                stubContent = null,
                needShowSearch = false,
            )

            state = reducer.run { state.reduce(Message.Empty(stubContent = stub, needShowSearch = true)) }

            Assert.assertEquals(stub, state.stubContent)
            Assert.assertEquals(true, state.needShowSearch)
        }

    @Test
    fun `On Apply Filter`() =
        runTest {
            state = state.copy(
                setSelectedFilter = null
            )

            val orgName = PlatformSbisString.Value("org name")
            state = reducer.run { state.reduce(Message.ApplyFilter(orgName)) }

            Assert.assertEquals(orgName, state.setSelectedFilter)
        }

    @Test
    fun `Update Selected Organisation`() =
        runTest {
            state = state.copy(
                selectedOrganisations = emptyList()
            )

            val newSelectedOrganisations = listOf(organisation)
            state = reducer.run { state.reduce(Message.UpdateSelectedOrganisations(newSelectedOrganisations)) }

            Assert.assertEquals(newSelectedOrganisations, state.selectedOrganisations)
        }
}