package ru.tensor.sbis.design.breadcrumbs.folderpath.util

import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.breadcrumbs.folderpath.FolderPathView

/**
 * Тесты распределения доступного пространства между содержимым [FolderPathView]
 *
 * @author us.bessonov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class FolderPathViewsWidthUtilsTest {

    @Test
    fun `When current folder and breadcrumbs require less space than available, then their width is not restricted`() {
        val result = resolveFolderPathViewsWidth(30, 40, 10, 100)

        assertEquals(
            FolderPathViewsWidth(
                WRAP_CONTENT,
                WRAP_CONTENT
            ), result
        )
    }

    @Test
    fun `When current folder and breadcrumbs don't fit completely, but breadcrumbs require less than 50% of available space, then current folder will occupy remaining space`() {
        val result = resolveFolderPathViewsWidth(70, 30, 10, 100)

        assertEquals(FolderPathViewsWidth(60, WRAP_CONTENT), result)
    }

    @Test
    fun `When current folder and breadcrumbs don't fit completely, but current folder requires less than 50% of available space, then breadcrumbs will occupy remaining space`() {
        val result = resolveFolderPathViewsWidth(40, 70, 10, 100)

        assertEquals(
            FolderPathViewsWidth(WRAP_CONTENT, 50),
            result
        )
    }

    @Test
    fun `When current folder and breadcrumbs don't fit completely, and each of them requires more than 50% of available space, then current folder will occupy 50% of space, and breadcrumbs will occupy remaining`() {
        val result = resolveFolderPathViewsWidth(70, 70, 10, 100)

        assertEquals(FolderPathViewsWidth(50, 40), result)
    }

}