package ru.tensor.sbis.design.folders.view.compact

import org.mockito.kotlin.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.folders.data.model.FolderButton
import ru.tensor.sbis.design.folders.test_utils.folder
import ru.tensor.sbis.design.folders.view.compact.adapter.FoldersCompactAdapter

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class FolderCompactViewControllerImplTest {

    @Mock
    private lateinit var mockAdapter: FoldersCompactAdapter

    private fun getController(withAdapter: Boolean = true) = FolderCompactViewControllerImpl().apply {
        if (withAdapter) {
            setAdapter(mockAdapter)
        }
    }

    @Test
    fun `When setAdapter called, the set adapter listeners`() {
        val controller = getController(false)

        controller.setAdapter(mockAdapter)

        verify(mockAdapter).onFolderClick(any())
    }

    @Test
    fun `When showFolderIcon(true) called and list is not empty, then button added`() {
        val controller = getController()

        controller.setFolders(listOf(folder()))
        controller.showFolderIcon(true)

        verify(mockAdapter).submitList(listOf(FolderButton, folder()))
    }

    @Test
    fun `When showFolderIcon(false) called and list is not empty, then button not added`() {
        val controller = getController()

        controller.setFolders(listOf(folder()))
        controller.showFolderIcon(false)

        verify(mockAdapter).submitList(listOf(folder()))
    }

    @Test
    fun `When showFolderIcon(true) called with empty list, then submit null`() {
        val controller = getController()

        controller.setFolders(emptyList())
        controller.showFolderIcon(true)

        verify(mockAdapter).submitList(null)
    }

    @Test
    fun `When showFolderIcon(false) called with empty list, then submit null`() {
        val controller = getController()

        controller.setFolders(emptyList())
        controller.showFolderIcon(false)

        verify(mockAdapter).submitList(null)
    }

    @Test
    fun `When setFolders() called, then don't update list`() {
        val controller = getController()

        controller.setFolders(listOf(folder()))

        verify(mockAdapter, never()).submitList(listOf(folder()))
    }

    @Test
    fun `When setFolders() called and showFolderIcon(true) called, then update list`() {
        val controller = getController()

        controller.setFolders(listOf(folder()))
        clearInvocations(mockAdapter)
        controller.showFolderIcon(true)

        verify(mockAdapter).submitList(listOf(FolderButton, folder()))
    }

    @Test
    fun `When setFolders() called and showFolderIcon(false) called, then update list`() {
        val controller = getController()

        controller.setFolders(listOf(folder()))
        controller.showFolderIcon(false)

        verify(mockAdapter).submitList(listOf(folder()))
    }
}
