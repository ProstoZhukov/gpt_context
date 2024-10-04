package ru.tensor.sbis.design_selection_common.controller

import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import ru.tensor.sbis.communication_decl.selection.SelectionConfig
import ru.tensor.sbis.communication_decl.selection.SelectionItemId
import ru.tensor.sbis.design_selection.contract.data.SelectionFolderItem
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.contract.data.SelectionItemMapper
import ru.tensor.sbis.recipients.generated.CollectionObserverOfRecipientViewModel
import ru.tensor.sbis.recipients.generated.CollectionOfRecipientViewModel
import ru.tensor.sbis.recipients.generated.ItemWithIndexOfRecipientViewModel
import ru.tensor.sbis.recipients.generated.PaginationOfRecipientAnchor
import ru.tensor.sbis.recipients.generated.RecipientFilter
import ru.tensor.sbis.recipients.generated.RecipientId
import ru.tensor.sbis.recipients.generated.RecipientViewModel
import ru.tensor.sbis.service.generated.DirectionType

/**
 * Тесты обертки над коллекцией контроллера компонента выбора [SelectionCollectionWrapperImpl].
 *
 * @author vv.chekurda
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class SelectionCollectionWrapperImplTest {

    @Test
    fun `When create empty filter, then search string is empty and folder uuid is equals current folder`() {
        val collectionWrapper = prepareCollectionWrapper(folderItem = mock())

        val filter = collectionWrapper.createEmptyFilter()

        assertTrue(filter.searchString.isEmpty())
    }

    @Test
    fun `When create pagination filter with unlimited items config, then anchor pageSize is equals itemsOnPage`() {
        val config = mock<SelectionConfig> {
            on { itemsLimit } doReturn Int.MAX_VALUE
        }
        val collectionWrapper = prepareCollectionWrapper(config = config)
        val itemsOnPage = 20L
        val directionType = mock<DirectionType>()

        val anchor = collectionWrapper.createPaginationAnchor(itemsOnPage, directionType)

        assertEquals(itemsOnPage, anchor.pageSize)
        assertEquals(directionType, anchor.direction)
        assertNull(anchor.anchor)
    }

    @Test
    fun `When call createCollection, then delegate to adapter`() {
        val filter = mock<RecipientFilter>()
        val anchor = mock<PaginationOfRecipientAnchor>()
        val adapter = mock<SelectionControllerAdapter<SelectionItem>>()
        val collectionWrapper = prepareCollectionWrapper(adapter = adapter)

        collectionWrapper.createCollection(filter, anchor)

        verify(adapter).createCollection(filter, anchor)
    }

    @Test
    fun `When call setObserver then set observer to collection`() {
        val observer = mock<CollectionObserverOfRecipientViewModel>()
        val collection = mock<CollectionOfRecipientViewModel>()
        val collectionWrapper = prepareCollectionWrapper()

        collectionWrapper.setObserver(observer, collection)

        verify(collection).setObserver(observer)
    }

    @Test
    fun `When call goNext, then call next on collection`() {
        val config: SelectionConfig = mock()
        whenever(config.itemsLimit).thenReturn(null)
        val collectionWrapper = prepareCollectionWrapper(config = config)
        val collection = mock<CollectionOfRecipientViewModel>()
        val var1 = 50L

        collectionWrapper.goNext(collection, var1)

        verify(collection).next(var1)
    }

    @Test
    fun `When call goPrev, then call prev on collection`() {
        val config: SelectionConfig = mock()
        whenever(config.itemsLimit).thenReturn(null)
        val collection = mock<CollectionOfRecipientViewModel>()
        val var1 = 50L
        val collectionWrapper = prepareCollectionWrapper(config = config)

        collectionWrapper.goPrev(collection, var1)

        verify(collection).prev(var1)
    }

    @Test
    fun `When call refresh, then call refresh on collection`() {
        val collection = mock<CollectionOfRecipientViewModel>()
        val collectionWrapper = prepareCollectionWrapper()

        collectionWrapper.refresh(collection)

        verify(collection).refresh()
    }

    @Test
    fun `When call dispose, then call dispose on collection`() {
        val collection = mock<CollectionOfRecipientViewModel>()
        val collectionWrapper = prepareCollectionWrapper()

        collectionWrapper.dispose(collection)

        verify(collection).dispose()
    }

    @Test
    fun `When call getIndex, then return item's index`() {
        val index = 1000L
        val itemWithIndex = mock<ItemWithIndexOfRecipientViewModel> {
            on { this.index } doReturn index
        }
        val collectionWrapper = prepareCollectionWrapper()

        val result = collectionWrapper.getIndex(itemWithIndex)

        assertEquals(index, result)
        verify(itemWithIndex).index
    }

    @Test
    fun `When call getItem, then map controller item`() {
        val item = mock<RecipientViewModel>()
        val itemWithIndex = mock<ItemWithIndexOfRecipientViewModel> {
            on { this.item } doReturn item
        }
        val mappedItem = mock<SelectionItem>()
        val mapper = mock<SelectionItemMapper<RecipientViewModel, RecipientId, SelectionItem, SelectionItemId>> {
            on { map(any()) } doReturn mappedItem
        }
        val collectionWrapper = prepareCollectionWrapper(mapper = mapper)

        val result = collectionWrapper.getItem(itemWithIndex)

        assertEquals(mappedItem, result)
        verify(mapper).map(item)
    }

    @Test
    fun `When call createCollectionObserver, then delegate to adapter`() {
        val adapter = mock<SelectionControllerAdapter<SelectionItem>>()
        val collectionWrapper = prepareCollectionWrapper(adapter = adapter)

        collectionWrapper.createCollectionObserver(mock())

        verify(adapter).createCollectionObserver(any())
    }

    private fun prepareCollectionWrapper(
        adapter: SelectionControllerAdapter<SelectionItem> = mock(),
        config: SelectionConfig = mock(),
        mapper: SelectionItemMapper<RecipientViewModel, RecipientId, SelectionItem, SelectionItemId> = mock(),
        folderItem: SelectionFolderItem? = mock()
    ): SelectionCollectionWrapperImpl<SelectionItem> =
        SelectionCollectionWrapperImpl(
            adapter = adapter,
            config = config,
            mapper = mapper,
            folderItem = folderItem,
            enableLogs = false
        )
}