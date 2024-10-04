package ru.tensor.sbis.design.selection.ui.factories

import org.mockito.kotlin.KArgumentCaptor
import org.mockito.kotlin.only
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.selection.bl.contract.listener.ClickHandleStrategy
import ru.tensor.sbis.design.selection.bl.contract.listener.SelectorItemHandleStrategy
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemMeta
import ru.tensor.sbis.design.selection.bl.vm.TestData
import ru.tensor.sbis.design.selection.ui.list.items.SelectorCustomisation
import ru.tensor.sbis.design.selection.ui.utils.CounterFormat
import kotlin.random.Random

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class ItemMetaFactoryImplTest {

    private val counter = Random.nextInt(0, Int.MAX_VALUE)
    private val formattedCounter = "Formatted counter $counter"
    private val viewHolderType = "View holder type"

    /**
     * Мета объект для проверки взаимодействий за пределами конструктора
     */
    @Mock
    private lateinit var meta: SelectorItemMeta
    @Mock
    private lateinit var data: TestData
    @Mock
    private lateinit var handleStrategy: ClickHandleStrategy
    @Mock
    private lateinit var selectorItemHandleStrategy: SelectorItemHandleStrategy<TestData>
    @Mock
    private lateinit var counterFormat: CounterFormat
    @Mock
    private lateinit var customisation: SelectorCustomisation

    @Captor
    private lateinit var metaCaptor: ArgumentCaptor<SelectorItemMeta>
    private lateinit var kMetaCaptor: KArgumentCaptor<SelectorItemMeta>

    @InjectMocks
    private lateinit var factory: ItemMetaFactoryImpl

    @Before
    fun setUp() {
        whenever(data.counter).thenReturn(counter)
        whenever(selectorItemHandleStrategy.onItemClick(data)).thenReturn(handleStrategy)
        whenever(counterFormat.format(counter)).thenReturn(formattedCounter)
        whenever(customisation.getViewHolderType(data)).thenReturn(viewHolderType)
        whenever(data.meta).thenReturn(meta)

        kMetaCaptor = KArgumentCaptor(metaCaptor, SelectorItemMeta::class)
    }

    @Test
    fun `When factory produces new meta, then it should contain handle strategy`() {
        factory.attachItemMeta(data)

        verify(data).meta = kMetaCaptor.capture()
        verify(selectorItemHandleStrategy, only()).onItemClick(data)
        assertEquals(handleStrategy, metaCaptor.value.handleStrategy)
    }

    @Test
    fun `When factory produces new meta, then it should contain formatted counter`() {
        factory.attachItemMeta(data)

        verify(data).meta = kMetaCaptor.capture()
        verify(counterFormat, only()).format(data.counter)
        assertEquals(formattedCounter, metaCaptor.value.formattedCounter)
    }

    @Test
    fun `When factory produces new meta, then it should contain view holder type`() {
        factory.attachItemMeta(data)

        verify(meta).viewHolderType = viewHolderType
    }

    /**
     * Фабрика создаётся многократно -> она может инициализировать только неизменяемые атрибуты
     *
     * Fix https://online.sbis.ru/opendoc.html?guid=34ab2c28-ee53-45b0-af1c-1e89bf14ef21
     */
    @Test
    fun `When factory produces new meta, then it variable properties should be equal to default values`() {
        val defaultMeta = SelectorItemMeta()

        factory.attachItemMeta(data)

        verify(data).meta = kMetaCaptor.capture()
        assertEquals(defaultMeta.isSelected, metaCaptor.value.isSelected)
        assertEquals(defaultMeta.queryRanges, metaCaptor.value.queryRanges)
    }
}