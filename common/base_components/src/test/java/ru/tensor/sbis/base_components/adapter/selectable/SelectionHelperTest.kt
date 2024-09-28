package ru.tensor.sbis.base_components.adapter.selectable

import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import io.reactivex.functions.Predicate
import io.reactivex.subjects.PublishSubject
import junit.framework.TestCase
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(JUnitParamsRunner::class)
class SelectionHelperTest : TestCase() {

    private val adapterStubItem = 444
    private val someItem = 111
    private val anotherItem = 222
    private val itemComparator = ItemComparator { item1: Int, item2: Int ->
        item1 == item2
    }
    private val spyItemSelectionSubject = spy<PublishSubject<Int>>()
    private val mockPredicate = mock<ThrottleUntilChangedPredicate<Int>>()

    @Test
    fun `When reset selection then should not be throttling for item selection event`() {
        val throttleUntilChangedPredicate = mock<ThrottleUntilChangedPredicate<Int>>()
        val selectionHelper = SelectionHelper(
            true, itemComparator, spyItemSelectionSubject, throttleUntilChangedPredicate
        )
        attachAdapter(selectionHelper, true)
        Mockito.clearInvocations(throttleUntilChangedPredicate)
        selectionHelper.changeSelection(someItem)
        val inOrder = inOrder(spyItemSelectionSubject, throttleUntilChangedPredicate)
        //act
        selectionHelper.resetSelection()
        //verify
        inOrder.verify(throttleUntilChangedPredicate).ignoreSelectionThrottling = true
        inOrder.verify(spyItemSelectionSubject).onNext(adapterStubItem)
        inOrder.verify(throttleUntilChangedPredicate).ignoreSelectionThrottling = false
    }

    @Test
    fun `Given not tablet, when attach adapter then don't pass selection event and turn on throttling`() {
        val isTablet = false
        val selectionHelper = SelectionHelper(
            isTablet, itemComparator, spyItemSelectionSubject, mockPredicate
        )
        Mockito.clearInvocations(mockPredicate)
        //act
        val adapter = attachAdapter(selectionHelper, isTablet)
        //verify
        verify(mockPredicate).shouldThrottle = true
        verify(adapter).attachSelectionHelper(selectionHelper)
        verify(adapter, never()).onItemSelected(any(), any())
    }

    @Test
    fun `Given not tablet, when attach adapter then turn on throttling and pass selection`() {
        val isTablet = true
        val selectionHelper = SelectionHelper(
            isTablet,
            itemComparator = { _, _ -> true },
            itemSelectionSubject = PublishSubject.create(),
            throttleUntilChangedPredicate = mockPredicate
        )
        Mockito.clearInvocations(mockPredicate)
        selectionHelper.changeSelection(someItem)
        //act
        val adapter = attachAdapter(selectionHelper, isTablet)
        //verify
        verify(mockPredicate).shouldThrottle = false
        verify(adapter).attachSelectionHelper(selectionHelper)
        verify(adapter).onItemSelected(adapterStubItem, someItem)
    }

    @Suppress("JUnitMalformedDeclaration", "JUnitMalformedDeclaration")
    @Test
    @Parameters(value = ["true", "false"])
    fun testDetachAdapter(isTablet: Boolean) {
        val (adapter, selectionHelper) = createSelectionHelper(isTablet)
        //act
        selectionHelper.detachAdapter()
        //verify
        verify(adapter).detachSelectionHelper(selectionHelper)
    }

    @Test
    fun `SelectItem twice rapidly on tablet`() {
        val selectionHelper = SelectionHelper<Int>(true)
        val test = selectionHelper.itemSelectionObservable.test()
        //act
        selectionHelper.selectItem(someItem)
        selectionHelper.selectItem(anotherItem)
        //verify
        assertEquals(someItem, test.values()[0])
        assertEquals(anotherItem, test.values()[1])
    }

    @Test
    fun getItemSelectionObservable() {
        val publishSubjectFiler = PublishSubject.create<Int>()
        val itemSelectionSubject = spy(PublishSubject.create<Int>()) {
            onGeneric { filter(any<Predicate<Int>>()) } doReturn publishSubjectFiler
        }
        //act
        val selectionHelper = SelectionHelper(false, { _, _ -> true }, itemSelectionSubject)
        //verify
        assertEquals(publishSubjectFiler, selectionHelper.itemSelectionObservable)
    }

    @Test
    fun `Given tablet, when change selection then pass event and return selected item`() {
        val (adapter, selectionHelper) = createSelectionHelper(true)
        //act
        selectionHelper.changeSelection(someItem)
        //verify
        verify(adapter).onItemSelected(someItem, adapterStubItem)
        assertEquals(someItem, selectionHelper.selectedItem)
    }

    @Test
    fun `Given not tablet, when change selection then don't pass event and return selected item`() {
        val (adapter, selectionHelper) = createSelectionHelper(false)
        //act
        selectionHelper.changeSelection(someItem)
        //verify
        verify(adapter, never()).onItemSelected(any(), any())
        assertEquals(someItem, selectionHelper.selectedItem)
    }

    @Suppress("JUnitMalformedDeclaration")
    @Test
    @Parameters(value = ["true", "false"])
    fun testGetSelectedItem(isTablet: Boolean) {
        val selectionHelper = SelectionHelper<Int>(isTablet)
        //act
        selectionHelper.selectItem(someItem)
        //verify
        assertEquals(someItem, selectionHelper.selectedItem)
    }

    @Suppress("JUnitMalformedDeclaration")
    @Test
    @Parameters(value = ["true", "false"])
    fun testResetSelection(isTablet: Boolean) {
        val (_, selectionHelper) = createSelectionHelper(isTablet)
        selectionHelper.changeSelection(someItem)
        //act
        selectionHelper.resetSelection()
        //verify
        assertEquals(adapterStubItem, selectionHelper.selectedItem)
    }

    @Suppress("JUnitMalformedDeclaration")
    @Test
    @Parameters(value = ["true", "false"])
    fun testIsTablet(isTablet: Boolean) {
        assertEquals(isTablet, SelectionHelper<Int>(isTablet).isTablet)
    }

    private fun createSelectionHelper(
        isTablet: Boolean
    ): Pair<SelectableListAdapter<Int>, SelectionHelper<Int>> {
        val selectionHelper = SelectionHelper<Int>(isTablet)
        val adapter = attachAdapter(selectionHelper, isTablet)
        return Pair(adapter, selectionHelper)
    }

    private fun attachAdapter(selectionHelper: SelectionHelper<Int>, isTablet: Boolean): SelectableListAdapter<Int> {
        val adapter = mock<SelectableListAdapter<Int>> {
            on { provideStubItem() } doReturn adapterStubItem
        }

        selectionHelper.attachAdapter(adapter, isTablet)
        return adapter
    }
}