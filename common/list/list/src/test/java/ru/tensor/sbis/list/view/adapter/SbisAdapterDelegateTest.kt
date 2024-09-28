package ru.tensor.sbis.list.view.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.only
import org.mockito.kotlin.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito
import ru.tensor.sbis.list.utils.IntViewHolder
import ru.tensor.sbis.list.utils.LongViewHolder
import ru.tensor.sbis.list.utils.StringViewHolder
import ru.tensor.sbis.list.view.container.ListContainerViewModel
import ru.tensor.sbis.list.view.item.Item
import ru.tensor.sbis.list.view.item.Options
import ru.tensor.sbis.list.view.item.ViewHolderHelper
import ru.tensor.sbis.list.view.utils.Plain

internal class SbisAdapterDelegateTest {

    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var delegate: SbisAdapterDelegate

    private val item = "item"
    private val item2 = "item2"
    private val item4 = "item" //Чтобы проверить на дубли
    private val item5 = 33L //Чтобы проверить на дубли
    private val stringViewHolder = mock<StringViewHolder> {
        on { itemViewType } doReturn itemViewTypeForStringViewHolder
    }
    private val intViewHolder = mock<IntViewHolder> {
        on { itemViewType } doReturn itemViewTypeForIntViewHolder
    }
    private val longViewHolder = mock<LongViewHolder> {
        on { itemViewType } doReturn itemViewTypeForLongViewHolder
    }
    private val mockContext = mock<Context>()
    private val mockParent = mock<ViewGroup> {
        on { context } doReturn mockContext
    }
    private val stringViewHolderProviderBinder = mock<ViewHolderHelper<String, StringViewHolder>> {
        on { createViewHolder(mockParent) } doReturn stringViewHolder
        on { getViewHolderType() } doReturn itemViewTypeForStringViewHolder
    }
    private val intViewHolderProviderBinder = mock<ViewHolderHelper<Int, IntViewHolder>> {
        on { createViewHolder(mockParent) } doReturn intViewHolder
        on { getViewHolderType() } doReturn itemViewTypeForIntViewHolder
    }
    private val longViewHolderProviderBinder = mock<ViewHolderHelper<Long, LongViewHolder>> {
        on { createViewHolder(mockParent) } doReturn longViewHolder
        on { getViewHolderType() } doReturn itemViewTypeForLongViewHolder
    }

    private val clickAction0 = {}
    private val clickAction2 = {}
    private val longClickAction0 = {}
    private val longClickAction2 = {}
    private val list = listOf(
        Item(
            item,
            stringViewHolderProviderBinder,
            options = Options(level = 0, clickAction = clickAction0, longClickAction = longClickAction0)
        ),
        Item(item2, stringViewHolderProviderBinder, options = Options(level = 3)),
        Item(
            222,
            intViewHolderProviderBinder,
            options = Options(level = 2, clickAction = clickAction2, longClickAction = longClickAction2)
        ),
        Item(item4, stringViewHolderProviderBinder, options = Options(customSidePadding = true)),
        Item(333, intViewHolderProviderBinder),
        Item(222, intViewHolderProviderBinder, options = Options(customSidePadding = true))
    )

    @Before
    fun setUp() {
        delegate = SbisAdapterDelegate()
        /** Чтобы проверить корректную работу с кешируемыми типа ViewHolder, которые должно сохраняться,
         * добавляем сперва список, с которым работать не будем.
         * см. [SbisAdapterDelegate.removeItems]
         */
        val data = Plain(list)
        delegate.setItemsAndNotify(data, mock())
        delegate.setItemsAndNotify(
            data,
            mock()
        )
    }

    @Test
    fun getItemViewType() {
        assertEquals(itemViewTypeForStringViewHolder, delegate.getItemViewType(0))
        assertEquals(itemViewTypeForStringViewHolder, delegate.getItemViewType(1))
        assertEquals(itemViewTypeForIntViewHolder, delegate.getItemViewType(2))
        assertEquals(itemViewTypeForStringViewHolder, delegate.getItemViewType(3))
        assertEquals(itemViewTypeForIntViewHolder, delegate.getItemViewType(4))
        assertEquals(itemViewTypeForIntViewHolder, delegate.getItemViewType(5))
    }

    @Test
    fun onCreateViewHolder() {
        assertEquals(stringViewHolder, delegate.onCreateViewHolder(mockParent, itemViewTypeForStringViewHolder))
        assertEquals(stringViewHolder, delegate.onCreateViewHolder(mockParent, itemViewTypeForStringViewHolder))
        assertEquals(intViewHolder, delegate.onCreateViewHolder(mockParent, itemViewTypeForIntViewHolder))
        assertEquals(stringViewHolder, delegate.onCreateViewHolder(mockParent, itemViewTypeForStringViewHolder))
    }

    @Test
    fun onBindViewHolder() {
        delegate.onBindViewHolder(stringViewHolder, 0)
        delegate.onBindViewHolder(stringViewHolder, 1)
        delegate.onBindViewHolder(intViewHolder, 2)
        delegate.onBindViewHolder(stringViewHolder, 3)
        delegate.onBindViewHolder(intViewHolder, 4)
        delegate.onBindViewHolder(intViewHolder, 5)

        val orderVerifier = Mockito.inOrder(stringViewHolderProviderBinder, intViewHolderProviderBinder)
        // These lines will PASS
        orderVerifier.verify(stringViewHolderProviderBinder).bindToViewHolder(item, stringViewHolder)
        orderVerifier.verify(stringViewHolderProviderBinder).bindToViewHolder(item2, stringViewHolder)
        orderVerifier.verify(intViewHolderProviderBinder).bindToViewHolder(222, intViewHolder)
        orderVerifier.verify(stringViewHolderProviderBinder).bindToViewHolder(item4, stringViewHolder)
        orderVerifier.verify(intViewHolderProviderBinder).bindToViewHolder(333, intViewHolder)
        orderVerifier.verify(intViewHolderProviderBinder).bindToViewHolder(222, intViewHolder)
    }

    @Test
    fun addLast() {
        val (_, mockViewHolder) = addLastItem()
        //verify
        val lastItemIndex = list.size
        /**У уже имеющимся добавлен один для индикатора прогресса*/
        assertEquals(mockViewHolder, delegate.onCreateViewHolder(mock(), delegate.getItemViewType(lastItemIndex)))
    }

    @Test
    fun removeIfLast() {
        val list = listOf(
            Item(item4, stringViewHolderProviderBinder),
            Item("333", stringViewHolderProviderBinder),
            Item(222, intViewHolderProviderBinder)
        )
        delegate.setItemsAndNotify(Plain(list), mock())
        //act
        delegate.removeIfLast(Item(222, intViewHolderProviderBinder)) {}
        //verify
        val newListSize = list.size - 1
        assertEquals(newListSize, delegate.getItemCount())
        assertEquals(itemViewTypeForStringViewHolder, delegate.getItemViewType(newListSize - 1))
    }

    @Test
    fun `Should not remove last if wrong type`() {
        val list = listOf(
            Item(item4, stringViewHolderProviderBinder),
            Item("333", stringViewHolderProviderBinder),
            Item(222, intViewHolderProviderBinder)
        )
        delegate.setItemsAndNotify(Plain(list), mock())
        //act
        delegate.removeIfLast(Item("333", stringViewHolderProviderBinder)) {}
        //verify
        assertEquals(list.size, delegate.getItemCount())
        assertEquals(itemViewTypeForIntViewHolder, delegate.getItemViewType(list.size - 1))
    }

    @Test
    fun getItemCount() {
        assertEquals(list.size, delegate.getItemCount())
    }

    @Test
    fun getLevel() {
        assertEquals(0, delegate.getLevel(0))
        assertEquals(3, delegate.getLevel(1))
        assertEquals(2, delegate.getLevel(2))
    }

    @Test
    fun isClickable() {
        assertTrue(delegate.isClickable(0))
        assertFalse(delegate.isClickable(1))
        assertTrue(delegate.isClickable(2))
    }

    @Test
    fun hasCustomSidePadding() {
        assertFalse(delegate.hasCustomSidePadding(0))
        assertTrue(delegate.hasCustomSidePadding(3))
        assertFalse(delegate.hasCustomSidePadding(4))
        assertTrue(delegate.hasCustomSidePadding(5))
    }

    @Test
    fun clickAction() {
        assertSame(clickAction0, delegate.clickAction(0))
        assertSame(clickAction2, delegate.clickAction(2))
    }

    @Test
    fun longClickAction() {
        assertSame(longClickAction0, delegate.longClickAction(0))
        assertSame(longClickAction2, delegate.longClickAction(2))
    }

    @Test
    fun getItem() {
        assertTrue(delegate.isClickable(0))
        assertFalse(delegate.isClickable(1))
        assertTrue(delegate.isClickable(2))
    }

    @Test
    fun getStickyHeaderPos() {
        val delegate = SbisAdapterDelegate()
        delegate.setItemsAndNotify(
            Plain(getList()),
            mock()
        )

        assertEquals(0, delegate.getStickyHeaderPos(0))
        assertEquals(0, delegate.getStickyHeaderPos(1))
        assertEquals(2, delegate.getStickyHeaderPos(2))
        assertEquals(2, delegate.getStickyHeaderPos(3))
    }

    @Test
    fun isSticky() {
        val delegate = SbisAdapterDelegate()
        delegate.setItemsAndNotify(
            Plain(getList()),
            mock()
        )

        assertTrue(delegate.isSticky(0))
        assertFalse(delegate.isSticky(1))
        assertTrue(delegate.isSticky(2))
        assertFalse(delegate.isSticky(3))

        //Неправильные индексы.
        assertFalse(delegate.isSticky(-1))
        assertFalse(delegate.isSticky(4))
    }

    @Test
    fun `getHeaderPos when no header`() {
        val delegate = SbisAdapterDelegate()
        delegate.setItemsAndNotify(
            Plain(
                listOf(
                    Item(item5, longViewHolderProviderBinder),
                    Item(item4, stringViewHolderProviderBinder)
                )
            ),
            mock()
        )

        assertEquals(NO_HEADER, delegate.getStickyHeaderPos(0))
        assertEquals(NO_HEADER, delegate.getStickyHeaderPos(1))
    }

    private fun getList() = listOf(
        Item(item5, longViewHolderProviderBinder, options = Options(isSticky = true)),
        Item(item4, stringViewHolderProviderBinder),
        Item(333, intViewHolderProviderBinder, options = Options(isSticky = true)),
        Item(222, intViewHolderProviderBinder)
    )

    private fun addLastItem(): Pair<SbisAdapter, RecyclerView.ViewHolder> {
        val mockAdapter = mock<SbisAdapter>()
        val mockViewHolder = mock<RecyclerView.ViewHolder>()
        val mockViewHolderProviderAndBinder = mock<ViewHolderHelper<Any, RecyclerView.ViewHolder>> {
            on { createViewHolder(any()) } doReturn mockViewHolder
        }
        val itemAndViewHolderProvider = Item(
            true,
            mockViewHolderProviderAndBinder
        )

        delegate.addLast(itemAndViewHolderProvider)

        return Pair(mockAdapter, mockViewHolder)
    }

    /**
     * Потребовалась особая обработка очистки списка т.к. DiffUtil не успевает отобразить анимацию скрытия элементов в
     * момент скрытия самого списка из-за переключения на показ заглушки [ListContainerViewModel.showOnlyStub].
     * В результате при обратном переключении на показ списка промелькивает "скрытие прошлого списка" и только после
     * этого показываются актуальные данные
     *
     * Fix https://online.sbis.ru/opendoc.html?guid=f9731598-cd28-4c35-bdb8-70768f8b9cbd
     */
    @Test
    fun `When item list is empty, then items should be removed without diff utils`() {
        val adapter = mock<SbisAdapter>()

        delegate.setItemsAndNotify(Plain(emptyList()), adapter)

        verify(adapter, only()).notifyDataSetChanged()
    }
}

private const val itemViewTypeForStringViewHolder = 0
private const val itemViewTypeForIntViewHolder = 1
private const val itemViewTypeForLongViewHolder = 2