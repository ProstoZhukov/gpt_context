package ru.tensor.sbis.list.view.section

import android.graphics.Color
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.TextLayoutMode
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.list.view.utils.ListData
import ru.tensor.sbis.list.view.utils.Plain
import ru.tensor.sbis.list.view.utils.Sections

class SectionsHolderTest {

    @Test
    fun isEmpty() {
        val sectionsWithNoItems = SectionsHolder()
        val sectionsWithItems =
            SectionsHolder(listOf(Section(emptyList()), Section(emptyList())))

        assertTrue(sectionsWithNoItems.isEmpty())
        assertFalse(sectionsWithItems.isEmpty())
    }

    @Test
    fun hasMoreThanOne() {
        val zeroSections = SectionsHolder()
        val oneSections = SectionsHolder(getListOfOneSections())
        val twoSections = SectionsHolder(getListOfTwoSections())
        val threeSections = SectionsHolder(getListThreeTwoSections())

        assertFalse(zeroSections.hasMoreThanOneSection())
        assertFalse(oneSections.hasMoreThanOneSection())
        assertTrue(twoSections.hasMoreThanOneSection())
        assertTrue(threeSections.hasMoreThanOneSection())
    }

    @Test
    fun moreThanOne() {
        val zeroSections = SectionsHolder()
        val oneSections = SectionsHolder(getListOfOneSections())
        val twoSections = SectionsHolder(getListOfTwoSections())
        val threeSections = SectionsHolder(getListThreeTwoSections())

        assertFalse(zeroSections.hasMoreThanOneSection())
        assertFalse(oneSections.hasMoreThanOneSection())
        assertTrue(twoSections.hasMoreThanOneSection())
        assertTrue(threeSections.hasMoreThanOneSection())
    }

    @Test
    fun isLastItemInSection() {
        val sectionHolder = getSectionHolder()

        assertFalse(sectionHolder.isLastItemInSection(0))
        assertTrue(sectionHolder.isLastItemInSection(1))
        assertFalse(sectionHolder.isLastItemInSection(2))
        assertTrue(sectionHolder.isLastItemInSection(3))
        assertFalse(SectionsHolder().isLastItemInSection(0))
        assertFalse(SectionsHolder().isLastItemInSection(1))
    }

    @Test
    fun isMovable() {
        val sectionHolder = getSectionHolder()

        assertFalse(sectionHolder.isMovable(0))
        assertTrue(sectionHolder.isMovable(1))
    }

    @Test
    fun hasCollapsibleItems() {
        val sectionHolderOne = SectionsHolder(getListOfOneSections())
        val sectionHolderTwo = SectionsHolder(getListThreeTwoSections())

        assertFalse(sectionHolderOne.hasCollapsibleItems())
        assertTrue(sectionHolderTwo.hasCollapsibleItems())
    }

    @Test
    fun hasDividers() {
        val sectionHolder = getSectionHolder()

        assertTrue(sectionHolder.hasDividers(0))
        assertFalse(sectionHolder.hasDividers(1))
        assertTrue(sectionHolder.hasDividers(2))
        assertFalse(sectionHolder.hasDividers(3))
        assertFalse(SectionsHolder().hasDividers(0))
        assertFalse(SectionsHolder().hasDividers(1))
    }

    @Test
    fun isFirstItemInSectionAndHasLine() {
        val sectionsHolder = getSectionHolder()
        val mockFun0 = mock<(color: Int) -> Unit>()
        val mockFun1 = mock<(color: Int) -> Unit>()
        val mockFun2 = mock<(color: Int) -> Unit>()
        val mockFun3 = mock<(color: Int) -> Unit>()
        sectionsHolder.runIfIsFirstItemInSectionAndHasLine(0, mockFun0)
        sectionsHolder.runIfIsFirstItemInSectionAndHasLine(1, mockFun1)
        sectionsHolder.runIfIsFirstItemInSectionAndHasLine(2, mockFun2)
        sectionsHolder.runIfIsFirstItemInSectionAndHasLine(3, mockFun3)

        verify(mockFun0).invoke(headerColorBLUE)
        verify(mockFun2).invoke(headerColorMAGENTA)
    }


    @Test
    fun isFirstInSection() {
        val sectionsHolder = getSectionHolder()

        assertTrue(sectionsHolder.isFirstInSection(0))
        assertFalse(sectionsHolder.isFirstInSection(1))
        assertTrue(sectionsHolder.isFirstInSection(2))
        assertFalse(sectionsHolder.isFirstInSection(3))
        assertFalse(sectionsHolder.isFirstInSection(33))
    }

    @Test
    fun isFirstItemATitle() {
        val sectionsHolder = SectionsHolder(getListThreeTwoSections())

        assertFalse(sectionsHolder.needDrawDividerUnderFirst(0))
        assertFalse(sectionsHolder.needDrawDividerUnderFirst(1))
        assertFalse(sectionsHolder.needDrawDividerUnderFirst(2))
        assertFalse(sectionsHolder.needDrawDividerUnderFirst(3))
        assertTrue(sectionsHolder.needDrawDividerUnderFirst(4))
        assertTrue(sectionsHolder.needDrawDividerUnderFirst(5))
    }

    @Test
    fun getTotal() {
        assertEquals(4, getSectionHolder().getItemsTotal())
    }

    @Test
    fun equalsAndHashCode() {
        EqualsVerifier.forClass(SectionsHolder::class.java)
            .suppress(Warning.NONFINAL_FIELDS)
            .withNonnullFields("sections")
            .withNonnullFields("forcedBackgroundColor")
            .withNonnullFields("items")
            .verify()
    }

    @Test
    fun reorderOnPlainListData() {
        val item1 = mock<AnyItem>()
        val item2 = mock<AnyItem>()
        val item3 = mock<AnyItem>()

        val originalList = listOf(
            item1,
            item2,
            item3
        )
        val plainListData = Plain(originalList)
        val modifiedData = plainListData.reorder(plainListData, 0, 2)
        val expectedResult = listOf(item3, item2, item1)
        assertEquals(expectedResult, modifiedData!!.getItems())
    }

    @Test
    fun `reorder items in same section in sections list data`() {
        val item1 = mock<AnyItem>()
        val item2 = mock<AnyItem>()
        val item3 = mock<AnyItem>()
        val item4 = mock<AnyItem>()
        val item5 = mock<AnyItem>()
        val section1 = Section(items = listOf(item1, item2, item3))
        val section2 = Section(items = listOf(item4, item5))
        val sectionsListData = Sections(data = listOf(section1, section2))
        val modifiedData = sectionsListData.reorder(sectionsListData, 0, 2)
        val expectedResult = listOf(item3, item2, item1, item4, item5)
        assertEquals(expectedResult, modifiedData!!.getItems())
    }

    @Test
    fun `reorder items in different section in sections list data`() {
        val item1 = mock<AnyItem>()
        val item2 = mock<AnyItem>()
        val item3 = mock<AnyItem>()
        val item4 = mock<AnyItem>()
        val item5 = mock<AnyItem>()
        val section1 = Section(items = listOf(item1, item2, item3))
        val section2 = Section(items = listOf(item4, item5))
        val sectionsListData = Sections(data = listOf(section1, section2))
        val modifiedData = sectionsListData.reorder(sectionsListData, 0, 3)
        assertNull(modifiedData)
    }

    private fun getSectionHolder(): SectionsHolder {
        return SectionsHolder(getListOfTwoSections())
    }

    private fun getListThreeTwoSections(): List<Section> {
        val section = Section(
            listOf(
                mock {
                    on { isMovable } doReturn false
                },
                mock {
                    on { isMovable } doReturn false
                }
            ),
            options = Options(
                indicatorColor = headerColorBLUE,
                needDrawDividerUnderFirst = true
            )
        )
        return listOf(getListOfTwoSections(), (listOf(section))).flatten()
    }

    private fun getListOfTwoSections(): List<Section> {
        val section = Section(
            listOf(
                mock(),
                mock {
                    on { isCollapsible } doReturn true
                }
            ),
            options = Options(
                indicatorColor = headerColorMAGENTA,
                hasDividers = true,
                needDrawDividerUnderFirst = false
            )
        )

        return listOf(getListOfOneSections(), (listOf(section))).flatten()
    }

    private fun getListOfOneSections(): List<Section> {
        val section = Section(
            listOf(
                mock(),
                mock {
                    on { isMovable } doReturn true
                }
            ),
            options = Options(
                indicatorColor = headerColorBLUE,
                hasDividers = true,
                needDrawDividerUnderFirst = false
            )
        )

        return listOf(section)
    }
}

private const val headerColorMAGENTA = Color.MAGENTA
private const val headerColorBLUE = Color.BLUE