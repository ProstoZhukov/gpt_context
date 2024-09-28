package ru.tensor.sbis.marks.view.api

import android.graphics.Color
import android.widget.LinearLayout
import androidx.core.view.children
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockedConstruction
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.marks.item.SbisMarksElementView
import ru.tensor.sbis.marks.model.SbisMarksCheckboxStatus
import ru.tensor.sbis.marks.model.SbisMarksComponentType
import ru.tensor.sbis.marks.model.item.SbisMarksColorElement
import ru.tensor.sbis.marks.model.title.SbisMarksTitle
import ru.tensor.sbis.marks.utils.createImportant
import ru.tensor.sbis.marks.utils.createPlus
import ru.tensor.sbis.marks.view.SbisMarksListView

/**
 * Тестовый класс для [MarksListController].
 *
 * @author ra.geraskin
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class MarksListControllerTest {

    private val listView: SbisMarksListView = mockk()
    private val elementView: SbisMarksElementView = mockk()
    private var elementViewConstructorMock: MockedConstruction<SbisMarksElementView>? = null

    private val itemList = listOf(
        createImportant(SbisMarksCheckboxStatus.UNCHECKED),
        createPlus(SbisMarksCheckboxStatus.UNCHECKED),
        SbisMarksColorElement(
            "id1",
            SbisMarksTitle.Value("title1"),
            SbisMarksCheckboxStatus.UNCHECKED,
            SbisColor.Int(Color.BLACK)
        ),
        SbisMarksColorElement(
            "id2",
            SbisMarksTitle.Value("title2"),
            SbisMarksCheckboxStatus.UNCHECKED,
            SbisColor.Int(Color.BLACK)
        ),
        SbisMarksColorElement(
            "id3",
            SbisMarksTitle.Value("title3"),
            SbisMarksCheckboxStatus.UNCHECKED,
            SbisColor.Int(Color.BLACK)
        ),
        SbisMarksColorElement(
            "id4",
            SbisMarksTitle.Value("title4"),
            SbisMarksCheckboxStatus.UNCHECKED,
            SbisColor.Int(Color.BLACK)
        ),
        SbisMarksColorElement(
            "id5",
            SbisMarksTitle.Value("title5"),
            SbisMarksCheckboxStatus.UNCHECKED,
            SbisColor.Int(Color.BLACK)
        ),
        SbisMarksColorElement(
            "id6",
            SbisMarksTitle.Value("title6"),
            SbisMarksCheckboxStatus.UNCHECKED,
            SbisColor.Int(Color.BLACK)
        )
    )

    private val controller = MarksListController(itemList, SbisMarksComponentType.WITH_ADDITIONAL_MARKS, { }, { })

    @Before
    fun setUp() {
        elementViewConstructorMock =
            Mockito.mockConstructionWithAnswer(SbisMarksElementView::class.java, { elementView })
        every {
            listView.context
        } returns mockk()
        justRun {
            listView.addView(any(), any<LinearLayout.LayoutParams>())
        }
        controller.attachMarksListView(listView, mockk())
    }

    @After
    fun tearDown() {
        elementViewConstructorMock?.close()
    }

    @Test
    fun `When call clearAll function then function clearSelection is executed for elementView the same number of times as there are marks in listView`() {
        mockkStatic("androidx.core.view.ViewGroupKt")
        every {
            listView.children
        } returns Sequence { itemList.map { elementView }.listIterator() }
        justRun { elementView.clearSelection() }
        // act
        controller.clearAll()
        // verify
        verify(exactly = itemList.size) { elementView.clearSelection() }
    }

    @Test
    fun `When call getSelected function then get list of models whose checkbox values are only CHECKED`() {
        val selectedIndex1 = 0
        val selectedIndex2 = 4
        itemList[selectedIndex1].checkboxValue = SbisMarksCheckboxStatus.CHECKED
        itemList[selectedIndex2].checkboxValue = SbisMarksCheckboxStatus.CHECKED
        val elementViewList = List(itemList.size) {
            mockk<SbisMarksElementView> {
                every { item } returns itemList[it]
                every { getSelectionStatus() } returns itemList[it].checkboxValue
            }
        }
        mockkStatic("androidx.core.view.ViewGroupKt")
        every {
            listView.children
        } returns Sequence { elementViewList.listIterator() }

        // act
        val selectedElements = controller.getSelected()

        // verify
        assertEquals(selectedElements[0].id, itemList[selectedIndex1].id)
        assertEquals(selectedElements[1].id, itemList[selectedIndex2].id)
        assertEquals(selectedElements.size, 2)
    }
}