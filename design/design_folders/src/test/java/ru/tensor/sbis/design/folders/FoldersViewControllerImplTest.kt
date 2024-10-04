package ru.tensor.sbis.design.folders

import android.view.View
import androidx.core.view.isVisible
import org.mockito.kotlin.*
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.breadcrumbs.CurrentFolderView
import ru.tensor.sbis.design.folders.data.FolderActionHandler
import ru.tensor.sbis.design.folders.data.MoreClickHandler
import ru.tensor.sbis.design.folders.data.model.AdditionalCommand
import ru.tensor.sbis.design.folders.test_utils.command
import ru.tensor.sbis.design.folders.test_utils.folder
import ru.tensor.sbis.design.folders.view.compact.FoldersCompactView
import ru.tensor.sbis.design.folders.view.full.FolderListView
import kotlin.LazyThreadSafetyMode.NONE

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class FoldersViewControllerImplTest {

    @Mock
    private lateinit var mockFoldersCompact: FoldersCompactView

    @Mock
    private lateinit var mockFolderFull: FolderListView

    @Mock
    private lateinit var foldersRootViewGroup: View

    @Mock
    private lateinit var mockCurrentFolder: CurrentFolderView

    private val controller: FoldersViewController by lazy(NONE) { getController() }

    private fun getController() = FoldersViewControllerImpl().apply {
        setViews(foldersRootViewGroup, mockFoldersCompact, mockFolderFull, mockCurrentFolder)
    }

    @Test
    fun `When setViews() called, then set fold and unfold listeners`() {
        val mockFoldersCompact: FoldersCompactView = mock()
        val mockFolderFull: FolderListView = mock()
        val controller = FoldersViewControllerImpl()

        controller.setViews(foldersRootViewGroup, mockFoldersCompact, mockFolderFull, mockCurrentFolder)

        verify(mockFoldersCompact).onUnfold(any())
        verify(mockFolderFull).onFold(any())
    }

    @Test
    fun `When setAdditionalCommand() called, then call foldersFull setAdditionalCommand()`() {
        val testCommand = command()

        controller.setAdditionalCommand(testCommand)

        verify(mockFolderFull).setAdditionalCommand(testCommand)
    }

    @Test
    fun `When setFolders() called, then call foldersCompact and foldersFull setFolders()`() {
        val testFolders = listOf(folder("12", "123"), folder("13", "345"), folder("14", "889"))

        controller.setFolders(testFolders)

        verify(mockFoldersCompact).setFolders(testFolders)
        verify(mockFolderFull).setFolders(testFolders, true)
    }

    @Test
    fun `When setActionHandler() called, then call foldersCompact and foldersFull setActionHandler()`() {
        val handler: FolderActionHandler = mock()

        controller.setActionHandler(handler)

        verify(mockFoldersCompact).setActionHandler(handler)
        verify(mockFolderFull).setActionHandler(handler)
    }

    @Test
    fun `When onMoreClicked() called, then call foldersFull onMoreClicked()`() {
        val moreClickHandler: MoreClickHandler = mock()

        controller.onMoreClicked(moreClickHandler)

        verify(mockFolderFull).onMoreClicked(moreClickHandler)
    }

    @Test
    fun `When showCurrentFolder() called, then setTitle() called`() {
        val folderName = "test folder name"
        val controller = getController()

        controller.showCurrentFolder(folderName)

        verify(mockCurrentFolder).setTitle(folderName)
    }

    @Test
    fun `When showCurrentFolder() called, show current folder and hide folders views`() {
        val controller = getController()
        controller.showCurrentFolder("test")

        verify(mockFolderFull).isVisible = false
        verify(mockFoldersCompact).isVisible = false
        verify(mockCurrentFolder).isVisible = true
    }

    @Test
    fun `Given no folder and null command, when setAdditionalCommand() called, then don't change visibility`() {
        val controller = getController()

        controller.setAdditionalCommand(null)

        verify(mockFolderFull, never()).isVisible = any()
        verify(mockFoldersCompact, never()).isVisible = any()
        verify(mockCurrentFolder, never()).isVisible = any()
    }

    @Test
    fun `Given no folder and EMPTY command, when setAdditionalCommand() called, then don't change visibility`() {
        val controller = getController()

        controller.setAdditionalCommand(AdditionalCommand.EMPTY)

        verify(mockFolderFull, never()).isVisible = any()
        verify(mockFoldersCompact, never()).isVisible = any()
        verify(mockCurrentFolder, never()).isVisible = any()
    }

    @Test
    fun `When controller attached to view, then compact folders icon should be invisible by default`() {
        getController()

        verify(mockFoldersCompact).showFolderIcon(false)
        verify(mockFoldersCompact, never()).showFolderIcon(true)
    }

    @Test
    fun `Given first level folders and command, when setFolders called on expandable view, then compact folders icon should be visible`() {
        val controller = getController()

        controller.setAdditionalCommand(command())
        controller.setFolders(listOf(folder(depthLevel = 0), folder(depthLevel = 0), folder(depthLevel = 0)))

        verify(mockFoldersCompact).showFolderIcon(true)
    }

    @Test
    fun `Given first level folders and command, when setFolders called on non expandable view, then compact folders icon should be visible`() {
        val controller = getController()
        controller.isExpandable = false

        controller.setAdditionalCommand(command())
        controller.setFolders(listOf(folder(depthLevel = 0), folder(depthLevel = 0), folder(depthLevel = 0)))

        verify(mockFoldersCompact).showFolderIcon(true)
    }

    @Test
    fun `Given first level folders only, when setFolders called on expandable view, then compact folders icon should be visible`() {
        val controller = getController()

        controller.setFolders(listOf(folder(depthLevel = 0), folder(depthLevel = 0), folder(depthLevel = 0)))

        verify(mockFoldersCompact).showFolderIcon(true)
    }

    @Test
    fun `Given first level folders only, when setFolders called on non expandable view, then hide compact folders icon`() {
        val controller = getController()
        controller.isExpandable = false

        controller.setFolders(listOf(folder(depthLevel = 0), folder(depthLevel = 0), folder(depthLevel = 0)))

        // кнопка по умочланию скрыта
        verify(mockFoldersCompact).showFolderIcon(false)
        verify(mockFoldersCompact, never()).showFolderIcon(true)
    }

    @Test
    fun `Given folders with different levels, when setFolders called on expandable view, then compact folders icon should be visible`() {
        val controller = getController()

        controller.setFolders(listOf(folder(depthLevel = 0), folder(depthLevel = 1), folder(depthLevel = 2)))

        verify(mockFoldersCompact).showFolderIcon(true)
    }

    @Test
    fun `Given folders with different levels, when setFolders called on non expandable view, then compact folders icon should be visible`() {
        val controller = getController()
        controller.isExpandable = false

        controller.setFolders(listOf(folder(depthLevel = 0), folder(depthLevel = 1), folder(depthLevel = 2)))

        verify(mockFoldersCompact).showFolderIcon(true)
    }

    @Test
    fun `Given non expandable view, when setFolder called with empty list, then compact folders icon should be invisible`() {
        val controller = getController()
        controller.isExpandable = false

        controller.setFolders(emptyList())

        // кнопка по умочланию скрыта
        verify(mockFoldersCompact).showFolderIcon(false)
        verify(mockFoldersCompact, never()).showFolderIcon(true)
    }

    @Test
    fun `Given first level folders only, when isExpandable changed to false, then compact folders icon should be hidden`() {
        val controller = getController()
        val captor = argumentCaptor<Boolean>()
        controller.setFolders(listOf(folder(depthLevel = 0), folder(depthLevel = 0), folder(depthLevel = 0)))

        controller.isExpandable = false

        verify(mockFoldersCompact, times(3)).showFolderIcon(captor.capture())
        assertEquals(listOf(false, true, false), captor.allValues)
    }

    @Test
    fun `Given first level folders only, when isExpandable changed to false, then view should be compacted`() {
        val controller = getController()
        controller.setFolders(listOf(folder(depthLevel = 0), folder(depthLevel = 0), folder(depthLevel = 0)))

        controller.isExpandable = false

        verify(mockFolderFull).isVisible = false
        verify(mockFoldersCompact).isVisible = true
    }

    @Test
    fun `Given first level folders only and non expandable view, when isExpandable changed to true, then compact folders icon should be visible`() {
        val controller = getController()
        controller.isExpandable = false
        val captor = argumentCaptor<Boolean>()
        controller.setFolders(listOf(folder(depthLevel = 0), folder(depthLevel = 0), folder(depthLevel = 0)))

        controller.isExpandable = true

        verify(mockFoldersCompact, times(2)).showFolderIcon(captor.capture())
        assertEquals(listOf(false, true), captor.allValues)
    }
}
