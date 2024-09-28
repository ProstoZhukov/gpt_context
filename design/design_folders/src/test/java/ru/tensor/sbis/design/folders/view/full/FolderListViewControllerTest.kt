package ru.tensor.sbis.design.folders.view.full

import android.view.View
import org.mockito.kotlin.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.folders.data.FolderActionHandler
import ru.tensor.sbis.design.folders.data.model.AdditionalCommand
import ru.tensor.sbis.design.folders.data.model.Folder
import ru.tensor.sbis.design.folders.data.model.FolderItem
import ru.tensor.sbis.design.folders.data.model.MoreButton
import ru.tensor.sbis.design.folders.test_utils.command
import ru.tensor.sbis.design.folders.test_utils.folder
import ru.tensor.sbis.design.folders.view.full.adapter.FoldersFullAdapter
import androidx.core.view.isVisible

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class FolderListViewControllerTest {

    @Mock
    private lateinit var mockAdapter: FoldersFullAdapter

    @Mock
    private lateinit var mockFolderIcon: View

    private val testFolders: List<Folder> = getFolders(3)
    private val testCommand = command("some command")

    private fun getController() = FolderListViewController(mockAdapter, mockFolderIcon)

    private fun getFolders(count: Int) = mutableListOf<Folder>().apply {
        repeat(count) {
            add(folder(it.toString(), it.toString()))
        }
    }

    @Test
    fun `When setAdditionalCommand() called, then call adapter submitList() with command`() {
        getController().setAdditionalCommand(testCommand)

        verify(mockAdapter).submitList(listOf(testCommand))
    }

    @Test
    fun `When setFolders() called, then call adapter submitList() with folders`() {
        getController().setFolders(testFolders, true)

        verify(mockAdapter).submitList(testFolders)
    }

    @Test
    fun `When setAdditionalCommand() and setFolders() called, then call adapter submitList() with command and folders`() {
        val controller = getController()

        controller.setAdditionalCommand(testCommand)
        clearInvocations(mockAdapter)
        controller.setFolders(testFolders, true)

        verify(mockAdapter).submitList(eq(listOf(testCommand) + testFolders))
    }

    @Test
    fun `When setFolders() and setAdditionalCommand() called, then call adapter submitList() with command and folders`() {
        val controller = getController()

        controller.setFolders(testFolders, true)
        clearInvocations(mockAdapter)
        controller.setAdditionalCommand(testCommand)

        verify(mockAdapter).submitList(eq(listOf(testCommand) + testFolders))
    }

    @Test
    fun `When setActionHandler() called, then call adapter setActionHandler()`() {
        val handler: FolderActionHandler = mock()

        getController().setActionHandler(handler)

        verify(mockAdapter).setActionHandler(handler)
    }

    @Test
    fun `When onMoreClicked() called, then call adapter onMoreClicked()`() {
        getController().onMoreClicked(mock())

        verify(mockAdapter).onMoreClicked(any())
    }

    @Test
    fun `When onFold() called, then call folderIcon setOnClickListener()`() {
        getController().onFold(mock())

        verify(mockFolderIcon).setOnClickListener(any())
    }

    @Test
    fun `Given isFolderIconVisible true, when setFolders() called, then show folderIcon`() {
        getController().setFolders(emptyList(), true)

        verify(mockFolderIcon).isVisible = true
    }

    @Test
    fun `Given isFolderIconVisible false, when setFolders() called, then hide folderIcon`() {
        getController().setFolders(emptyList(), false)

        verify(mockFolderIcon).isVisible = false
    }

    @Test
    fun `Given isFolderIconVisible true and more than 20 folders, then show only 20`() {
        val testFolders = getFolders(30)
        val controller = getController()

        val testList = mutableListOf<FolderItem>().apply {
            add(command())
            addAll(testFolders.take(19))
            add(MoreButton)
        }

        controller.setAdditionalCommand(command())
        clearInvocations(mockAdapter)
        controller.setFolders(testFolders, true)

        verify(mockAdapter).submitList(testList)
    }

    @Test
    fun `Given isFolderIconVisible false and more than 20 folders, then show all folders without command`() {
        val testFolders = getFolders(30)
        val controller = getController()

        controller.setAdditionalCommand(command())
        clearInvocations(mockAdapter)
        controller.setFolders(testFolders, false)

        verify(mockAdapter).submitList(testFolders as List<FolderItem>)
    }

    @Test
    fun `Given existing data with additional command, when setAdditionalCommand() called with null, then remove command `() {
        val testFolders = getFolders(12)
        val controller = getController()

        controller.setFolders(testFolders, false)
        controller.setAdditionalCommand(command())
        clearInvocations(mockAdapter)
        controller.setAdditionalCommand(null)

        verify(mockAdapter).submitList(testFolders as List<FolderItem>)
    }

    @Test
    fun `Given existing data with additional command, when setAdditionalCommand() called with EMPTY, then remove command `() {
        val testFolders = getFolders(12)
        val controller = getController()

        controller.setFolders(testFolders, false)
        controller.setAdditionalCommand(command())
        clearInvocations(mockAdapter)
        controller.setAdditionalCommand(AdditionalCommand.EMPTY)

        verify(mockAdapter).submitList(testFolders as List<FolderItem>)
    }
}
