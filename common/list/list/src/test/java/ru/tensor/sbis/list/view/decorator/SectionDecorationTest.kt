package ru.tensor.sbis.list.view.decorator

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.list.view.ListDataHolder
import ru.tensor.sbis.list.view.utils.layout_manager.SbisGridLayoutManager

/**
 * @author ma.kolpakov
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [28])
@Ignore
class SectionDecorationTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    private val itemPosition = 1

    @Mock
    private lateinit var resources: Resources

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var layoutManager: SbisGridLayoutManager

    @Mock
    private lateinit var sectionHolder: ListDataHolder

    @Mock
    private lateinit var itemView: View

    @Mock
    private lateinit var recyclerView: RecyclerView

    private lateinit var decoration: SectionDecoration

    @Before
    fun setUp() {
        val displayMetrics = DisplayMetrics().apply {
            density = DisplayMetrics.DENSITY_DEFAULT.toFloat()
        }
        whenever(resources.displayMetrics).thenReturn(displayMetrics)
        whenever(itemView.resources).thenReturn(resources)
        whenever(itemView.context).thenReturn(context)

        decoration = SectionDecoration(layoutManager, sectionHolder, 0, mock())
    }

    //region Fix https://online.sbis.ru/opendoc.html?guid=cefedfb7-0caf-45f6-a898-b154a3e4754c
    @Test
    fun `When card is not last in the group, then bottom margin should be zero`() {
        val rect = Rect()
        whenever(itemView.layoutParams).thenReturn(mock<RecyclerView.LayoutParams>())
        whenever(recyclerView.getChildAdapterPosition(itemView)).thenReturn(itemPosition)
        /*
        TODO: 2/25/2021 https://online.sbis.ru/opendoc.html?guid=7a38d118-8f69-4a33-a634-2859f7e526b7

        whenever(sectionHolder.isCard(itemPosition)).thenReturn(true)
         */

        decoration.getItemOffsets(rect, itemView, recyclerView, mock())

        assertEquals(0, rect.bottom)
    }

    /**
     * Опциональным должен быть отступ снизу. Отступ сверху должен быть статичным, чтобы при добавлении элементов в
     * начало, не происходило перестроения со сдвигом *прежнего верхнего* элемента. Проблемный сценарий:
     * - у *верхнего* элемента отступ сверху и он полностью виден
     * - добавляется элемент над *верхним*
     * - удаляется отступ сверху у *прежнего верхнего* элемента
     * - *прежний верхний* элемент поднимается выше, чтобы прижаться к *новому верхнему*
     */
    @Test
    fun `When card is last in the group, then bottom margin should not be zero`() {
        val rect = Rect()
        whenever(itemView.layoutParams).thenReturn(mock<RecyclerView.LayoutParams>())
        whenever(recyclerView.getChildAdapterPosition(itemView)).thenReturn(itemPosition)
        /*
        TODO: 2/25/2021 https://online.sbis.ru/opendoc.html?guid=7a38d118-8f69-4a33-a634-2859f7e526b7

        whenever(sectionHolder.isCard(itemPosition)).thenReturn(true)
        whenever(layoutManager.isInLastGroup(itemPosition)).thenReturn(true)
        whenever(sectionHolder.getCardMarginDp()).thenReturn(1)
        */

        decoration.getItemOffsets(rect, itemView, recyclerView, mock())

        assertEquals(DisplayMetrics.DENSITY_DEFAULT, rect.bottom)
    }
    //endregion
}