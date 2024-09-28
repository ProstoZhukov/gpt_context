package ru.tensor.sbis.marks.utils.checker

import android.graphics.Color
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.marks.item.SbisMarksElementView
import ru.tensor.sbis.marks.model.SbisMarksCheckboxStatus
import ru.tensor.sbis.marks.model.item.SbisMarksColorElement
import ru.tensor.sbis.marks.model.title.SbisMarksTitle
import ru.tensor.sbis.marks.utils.createImportant
import ru.tensor.sbis.marks.utils.createPlus

/**
 * Тестовый класс для [ColorSingleSelectionChecker].
 *
 * @author ra.geraskin
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class ColorSingleSelectionCheckerTest {

    private val itemList = getItemList()

    private val elementViewSeq = Sequence {
        List(itemList.size) { index ->
            mockk<SbisMarksElementView> {
                every { item } returns itemList[index]
                every { clearSelection() } answers {
                    itemList[index].checkboxValue = SbisMarksCheckboxStatus.UNCHECKED
                }
            }
        }.listIterator()
    }

    @Test
    fun `When click on platform marks, then selection from previous platform mark is not reset o UNCHECKED`() {
        val checkedIndex1 = 0
        val checkedIndex2 = 1

        // act
        itemList[checkedIndex1].checkboxValue = SbisMarksCheckboxStatus.CHECKED
        ColorSingleSelectionChecker().checkSelectionRules(itemList[checkedIndex1], elementViewSeq)
        itemList[checkedIndex2].checkboxValue = SbisMarksCheckboxStatus.CHECKED
        ColorSingleSelectionChecker().checkSelectionRules(itemList[checkedIndex2], elementViewSeq)

        // verify
        assertEquals(elementViewSeq.toList()[checkedIndex1].item.checkboxValue, SbisMarksCheckboxStatus.CHECKED)
        assertEquals(elementViewSeq.toList()[checkedIndex2].item.checkboxValue, SbisMarksCheckboxStatus.CHECKED)
    }

    @Test
    fun `When click on the colored marks, then selection from previous colored mark is reset to UNCHECKED`() {
        val checkedIndex1 = 5
        val checkedIndex2 = 6

        // act
        itemList[checkedIndex1].checkboxValue = SbisMarksCheckboxStatus.CHECKED
        ColorSingleSelectionChecker().checkSelectionRules(itemList[checkedIndex1], elementViewSeq)
        itemList[checkedIndex2].checkboxValue = SbisMarksCheckboxStatus.CHECKED
        ColorSingleSelectionChecker().checkSelectionRules(itemList[checkedIndex2], elementViewSeq)

        // verify
        assertEquals(elementViewSeq.toList()[checkedIndex1].item.checkboxValue, SbisMarksCheckboxStatus.UNCHECKED)
        assertEquals(elementViewSeq.toList()[checkedIndex2].item.checkboxValue, SbisMarksCheckboxStatus.CHECKED)
    }

    @Test
    fun `When click on colored marks and then on platform marks, then selection from previous colored mark is not reset to UNCHECKED`() {
        val checkedPlatformIndex1 = 0
        val checkedPlatformIndex2 = 1
        val checkedColorIndex = 5

        // act
        itemList[checkedColorIndex].checkboxValue = SbisMarksCheckboxStatus.CHECKED
        ColorSingleSelectionChecker().checkSelectionRules(itemList[checkedColorIndex], elementViewSeq)
        itemList[checkedPlatformIndex1].checkboxValue = SbisMarksCheckboxStatus.CHECKED
        ColorSingleSelectionChecker().checkSelectionRules(itemList[checkedPlatformIndex1], elementViewSeq)
        itemList[checkedPlatformIndex2].checkboxValue = SbisMarksCheckboxStatus.CHECKED
        ColorSingleSelectionChecker().checkSelectionRules(itemList[checkedPlatformIndex2], elementViewSeq)

        // verify
        assertEquals(elementViewSeq.toList()[checkedPlatformIndex1].item.checkboxValue, SbisMarksCheckboxStatus.CHECKED)
        assertEquals(elementViewSeq.toList()[checkedPlatformIndex2].item.checkboxValue, SbisMarksCheckboxStatus.CHECKED)
        assertEquals(elementViewSeq.toList()[checkedColorIndex].item.checkboxValue, SbisMarksCheckboxStatus.CHECKED)
    }

    private fun getItemList() = mutableListOf(
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

}