package ru.tensor.sbis.main_screen.widget

import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.tensor.sbis.common.navigation.NavxId
import ru.tensor.sbis.design.navigation.view.view.NavigationView
import ru.tensor.sbis.main_screen_decl.navigation.service.ItemType
import ru.tensor.sbis.main_screen_decl.navigation.service.NavigationService
import ru.tensor.sbis.main_screen_decl.navigation.service.NavigationServiceItem
import ru.tensor.sbis.main_screen_decl.navigation.service.NavigationServiceNode

/**
 * Тесты инструмента по управлению видимостью пунктов навигации.
 *
 * @author us.bessonov
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class NavigationItemsManagerTest {

    private val testMainDispatcher = TestCoroutineDispatcher()

    private val testIoDispatcher = StandardTestDispatcher(TestCoroutineScheduler())

    @Mock
    private lateinit var mockSideNavView: NavigationView

    @Mock
    private lateinit var mockBottomNavView: NavigationView

    @Mock
    private lateinit var mockNavigationService: NavigationService

    private lateinit var navVisibilityManager: NavigationItemsManager

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    @Before
    fun setUp() {
        Dispatchers.setMain(testMainDispatcher)
        navVisibilityManager = NavigationItemsManager(
            mockSideNavView,
            mockBottomNavView,
            mock(),
            mock(),
            mock(),
            mock(),
            mock(),
            mock(),
            mock(),
            mock(),
            mock(),
            mockNavigationService,
            ioDispatcher = testIoDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `When item is added, then observer to its visibility is added`() {
        val mockRecord = mockRecord()

        navVisibilityManager.onItemAdded(mockRecord)

        verify(mockRecord).observeVisibility(any())
    }

    @Test
    fun `When item is added, item is visible, it is available, and it is installed in menu, then item is shown`() {
        val navxId = NavxId.SETTINGS
        val mockRecord = mockRecord(navxId)
        val mockNavigationNode = mockNavigationNode(visible = true, id = navxId.ids.first())

        whenever(mockRecord.isVisible).thenReturn(true)
        whenever(mockRecord.isInstalledInBottomMenu).thenReturn(true)
        whenever(mockRecord.isInstalledInSideMenu).thenReturn(true)
        whenever(mockNavigationService.getNavigationHierarchyFlow()).thenReturn(flowOf(mockNavigationNode))

        navVisibilityManager.init(coroutineScope)
        advanceTime()
        navVisibilityManager.onItemAdded(mockRecord)

        verify(mockSideNavView).showItem(mockRecord.item)
        verify(mockBottomNavView).showItem(mockRecord.item)
    }

    @Test
    fun `When item is added, item is visible, it is installed in menu, and available items list is empty, then item is shown`() {
        val mockRecord = mockRecord()

        whenever(mockRecord.isVisible).thenReturn(true)
        whenever(mockRecord.isInstalledInBottomMenu).thenReturn(true)
        whenever(mockRecord.isInstalledInSideMenu).thenReturn(true)
        whenever(mockNavigationService.getNavigationHierarchyFlow()).thenReturn(flowOf(null))

        navVisibilityManager.init(coroutineScope)
        advanceTime()
        navVisibilityManager.onItemAdded(mockRecord)

        verify(mockSideNavView).showItem(mockRecord.item)
        verify(mockBottomNavView).showItem(mockRecord.item)
    }

    @Test
    fun `When item is added, but item is not visible, then it is hidden`() {
        val mockRecord = mockRecord()

        whenever(mockRecord.isVisible).thenReturn(false)
        whenever(mockRecord.isInstalledInBottomMenu).thenReturn(true)
        whenever(mockRecord.isInstalledInSideMenu).thenReturn(true)
        whenever(mockNavigationService.getNavigationHierarchyFlow()).thenReturn(flowOf(null))

        navVisibilityManager.init(coroutineScope)
        advanceTime()
        navVisibilityManager.onItemAdded(mockRecord)

        verify(mockSideNavView).hideItem(mockRecord.item)
        verify(mockBottomNavView).hideItem(mockRecord.item)
    }

    @Test
    fun `When item is added, but not available, then item is hidden`() {
        val navxId = NavxId.SETTINGS
        val mockRecord = mockRecord(navxId)
        val mockNavigationNode = mockNavigationNode(id = navxId.ids.first())

        whenever(mockRecord.isVisible).thenReturn(true)
        whenever(mockRecord.isInstalledInBottomMenu).thenReturn(true)
        whenever(mockRecord.isInstalledInSideMenu).thenReturn(true)
        whenever(mockNavigationService.getNavigationHierarchyFlow()).thenReturn(flowOf(mockNavigationNode))

        navVisibilityManager.init(coroutineScope)
        advanceTime()
        navVisibilityManager.onItemAdded(mockRecord)

        verify(mockSideNavView).hideItem(mockRecord.item)
        verify(mockBottomNavView).hideItem(mockRecord.item)
    }

    @Test
    fun `When item is not available according to service, but is available locally, then it is shown`() {
        val navxId = "localItemNavxId"
        val mockRecord = mockRecord(NavxId.of(navxId))
        val mockNavigationNode = mockNavigationNode(id = navxId)

        whenever(mockRecord.isVisible).thenReturn(true)
        whenever(mockRecord.isInstalledInBottomMenu).thenReturn(true)
        whenever(mockRecord.isInstalledInSideMenu).thenReturn(true)
        whenever(mockNavigationService.getNavigationHierarchyFlow()).thenReturn(flowOf(mockNavigationNode))

        navVisibilityManager.init(coroutineScope)
        advanceTime()
        navVisibilityManager.onItemAdded(mockRecord)

        verify(mockSideNavView).showItem(mockRecord.item)
        verify(mockBottomNavView).showItem(mockRecord.item)
    }

    @Test
    fun `When item is added, but it is not installed in menu, then item is hidden`() {
        val mockRecord = mockRecord()

        whenever(mockRecord.isVisible).thenReturn(true)
        whenever(mockRecord.isInstalledInBottomMenu).thenReturn(false)
        whenever(mockRecord.isInstalledInSideMenu).thenReturn(false)
        whenever(mockNavigationService.getNavigationHierarchyFlow()).thenReturn(flowOf(null))

        navVisibilityManager.init(coroutineScope)
        advanceTime()
        navVisibilityManager.onItemAdded(mockRecord)

        verify(mockSideNavView).hideItem(mockRecord.item)
        verify(mockBottomNavView).hideItem(mockRecord.item)
    }

    @Test
    fun `When item visibility is checked, and it is shown, then corresponding result is returned`() {
        val navxId = NavxId.SETTINGS
        val mockRecord = mockRecord(navxId)
        val mockNavigationNode = mockNavigationNode(visible = true, id = navxId.ids.first())

        whenever(mockRecord.isVisible).thenReturn(true)
        whenever(mockNavigationService.getNavigationHierarchyFlow()).thenReturn(flowOf(mockNavigationNode))

        navVisibilityManager.init(coroutineScope)
        advanceTime()
        navVisibilityManager.onItemAdded(mockRecord)

        assertTrue(navVisibilityManager.isItemVisible(mockRecord.item))
    }

    @Test
    fun `When item is removed, then visibility observer is removed, and it is no longer shown`() {
        val navxId = NavxId.SETTINGS
        val mockRecord = mockRecord(navxId)
        val mockNavigationNode = mockNavigationNode(visible = true, id = navxId.ids.first())
        val mockObserver = mock<Observer<Boolean>>()

        whenever(mockRecord.isVisible).thenReturn(true)
        whenever(mockNavigationService.getNavigationHierarchyFlow()).thenReturn(flowOf(mockNavigationNode))
        whenever(mockRecord.observeVisibility(any())).thenReturn(mockObserver)

        navVisibilityManager.init(coroutineScope)
        advanceTime()
        navVisibilityManager.onItemAdded(mockRecord)
        navVisibilityManager.onItemRemoved(mockRecord)

        verify(mockRecord).removeVisibilityObserver(mockObserver)
        assertFalse(navVisibilityManager.isItemVisible(mockRecord.item))
    }

    private fun mockRecord(navxId: NavxId? = null) = mock<MenuItemRecord> {
        on { item } doReturn mock()
        on { navxIdentifier } doReturn navxId
    }

    private fun mockNavigationNode(visible: Boolean = false, id: String = "ItemId"): NavigationServiceNode {
        val serviceItem = mock<NavigationServiceItem> {
            on { itemType } doReturn ItemType.MAIN
            on { itemId } doReturn id
            on { isVisible } doReturn visible
        }
        return mock<NavigationServiceNode> {
            on { data } doReturn serviceItem
        }
    }

    private fun advanceTime() = testIoDispatcher.scheduler.advanceTimeBy(IO_TIME_ADVANCE_AMOUNT)
}

private const val IO_TIME_ADVANCE_AMOUNT = 300L