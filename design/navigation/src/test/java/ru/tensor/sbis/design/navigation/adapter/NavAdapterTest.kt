package ru.tensor.sbis.design.navigation.adapter

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.reactivex.Observable
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule
import ru.tensor.sbis.design.navigation.view.adapter.NavAdapter
import ru.tensor.sbis.design.navigation.view.model.ItemSelected
import ru.tensor.sbis.design.navigation.view.model.NavigationCounter
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.design.navigation.view.model.SelectedState
import ru.tensor.sbis.design.navigation.view.model.UnselectedState

/**
 * Тест проверки сценариев работы адпатера, который хранит элементы меню и обеспечивает их синхронизацию.
 */
@Suppress("DEPRECATION")
class NavAdapterTest {

    private lateinit var navAdapter: NavAdapter<NavigationItem>

    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val rxSchedulerRule = TrampolineSchedulerRule()

    @Before
    fun setUp() {
        navAdapter = NavAdapter(mock(), mock())
    }

    @Test
    fun `Given a navAdapter, when the navigationItems' map was added, then navAdapter would be able to remove a navigationItem`() {

        val navigationItem = mock<NavigationItem>()
        whenever(navigationItem.iconObservable).thenReturn(Observable.empty())
        whenever(navigationItem.labelObservable).thenReturn(Observable.empty())

        val navigationItem2 = mock<NavigationItem>()
        whenever(navigationItem2.iconObservable).thenReturn(Observable.empty())
        whenever(navigationItem2.labelObservable).thenReturn(Observable.empty())

        val navigationCounter = mock<NavigationCounter>()
        whenever(navigationCounter.newCounter).thenReturn(Observable.empty())
        whenever(navigationCounter.totalCounter).thenReturn(Observable.empty())

        navAdapter.add(
            mapOf(
                navigationItem to navigationCounter,
                navigationItem2 to navigationCounter
            )
        )

        Assert.assertTrue(navAdapter.remove(navigationItem))
        Assert.assertTrue(navAdapter.remove(navigationItem2))
    }

    @Test
    fun `When item select, then parent NOT selected too in navigation events`() {
        val childItem = mock<NavigationItem>()
        whenever(childItem.iconObservable).thenReturn(Observable.empty())
        whenever(childItem.labelObservable).thenReturn(Observable.empty())

        val parentItem = mock<NavigationItem>()
        whenever(parentItem.iconObservable).thenReturn(Observable.empty())
        whenever(parentItem.labelObservable).thenReturn(Observable.empty())

        val observer: Observer<Any> = mock()
        navAdapter.navigationEvents.observeForever(observer)
        navAdapter.add(parentItem, null)
        navAdapter.add(childItem, null, parentItem)

        navAdapter.setSelected(childItem)

        val captor = argumentCaptor<Any>()

        verify(observer, times(1)).onChanged(captor.capture())

        Assert.assertEquals(1, captor.allValues.size)
        Assert.assertEquals(childItem, (captor.firstValue as ItemSelected<*>).selectedItem)
    }

    @Test
    fun `When parent item select, then first child selected too in navigation events`() {
        val childItem = mock<NavigationItem>()
        whenever(childItem.iconObservable).thenReturn(Observable.empty())
        whenever(childItem.labelObservable).thenReturn(Observable.empty())

        val parentItem = mock<NavigationItem>()
        whenever(parentItem.iconObservable).thenReturn(Observable.empty())
        whenever(parentItem.labelObservable).thenReturn(Observable.empty())

        val observer: Observer<Any> = mock()
        navAdapter.navigationEvents.observeForever(observer)
        navAdapter.add(parentItem, null)
        navAdapter.add(childItem, null, parentItem)

        navAdapter.setSelected(parentItem)

        val captor = argumentCaptor<Any>()

        verify(observer, times(2)).onChanged(captor.capture())

        Assert.assertEquals(2, captor.allValues.size)
        Assert.assertEquals(parentItem, (captor.firstValue as ItemSelected<*>).selectedItem)
        Assert.assertEquals(childItem, (captor.secondValue as ItemSelected<*>).selectedItem)
    }

    @Test
    fun `When item select, then parent selected too`() {
        val childItem = mock<NavigationItem>()
        whenever(childItem.iconObservable).thenReturn(Observable.empty())
        whenever(childItem.labelObservable).thenReturn(Observable.empty())

        val parentItem = mock<NavigationItem>()
        whenever(parentItem.iconObservable).thenReturn(Observable.empty())
        whenever(parentItem.labelObservable).thenReturn(Observable.empty())

        navAdapter.add(parentItem, null)
        navAdapter.add(childItem, null, parentItem)

        val childValues = navAdapter.itemsMap[childItem]!!.state.test().values()
        val parentValues = navAdapter.itemsMap[parentItem]!!.state.test().values()

        navAdapter.setSelected(childItem)

        Assert.assertEquals(SelectedState, parentValues.last())
        Assert.assertEquals(SelectedState, childValues.last())

    }

    @Test
    fun `When parent item select, then first child selected too`() {
        val childItem = mock<NavigationItem>()
        whenever(childItem.iconObservable).thenReturn(Observable.empty())
        whenever(childItem.labelObservable).thenReturn(Observable.empty())

        val parentItem = mock<NavigationItem>()
        whenever(parentItem.iconObservable).thenReturn(Observable.empty())
        whenever(parentItem.labelObservable).thenReturn(Observable.empty())

        navAdapter.add(parentItem, null)
        navAdapter.add(childItem, null, parentItem)

        val childValues = navAdapter.itemsMap[childItem]!!.state.test().values()
        val parentValues = navAdapter.itemsMap[parentItem]!!.state.test().values()

        navAdapter.setSelected(parentItem)

        Assert.assertEquals(SelectedState, parentValues.last())
        Assert.assertEquals(SelectedState, childValues.last())
    }

    @Test
    fun `When item select, other item unselect`() {
        val item1 = mock<NavigationItem>()
        whenever(item1.iconObservable).thenReturn(Observable.empty())
        whenever(item1.labelObservable).thenReturn(Observable.empty())

        val item2 = mock<NavigationItem>()
        whenever(item2.iconObservable).thenReturn(Observable.empty())
        whenever(item2.labelObservable).thenReturn(Observable.empty())

        val item3 = mock<NavigationItem>()
        whenever(item3.iconObservable).thenReturn(Observable.empty())
        whenever(item3.labelObservable).thenReturn(Observable.empty())

        navAdapter.add(item1, null)
        navAdapter.add(item2, null)
        navAdapter.add(item3, null)

        val item1Values = navAdapter.itemsMap[item1]!!.state.test().values()
        val item2Values = navAdapter.itemsMap[item2]!!.state.test().values()
        val item3Values = navAdapter.itemsMap[item3]!!.state.test().values()

        navAdapter.setSelected(item3)
        navAdapter.setSelected(item2)

        Assert.assertEquals(UnselectedState, item1Values.last())
        Assert.assertEquals(SelectedState, item2Values.last())
        Assert.assertEquals(UnselectedState, item3Values.last())
    }
}