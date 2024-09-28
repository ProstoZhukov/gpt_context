package ru.tensor.sbis.folderspanel

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.ArgumentMatchers.anyBoolean
import ru.tensor.sbis.common.testing.assertObservableValueTrue
import ru.tensor.sbis.swipeablelayout.DefaultSwipeMenu
import ru.tensor.sbis.swipeablelayout.api.SwipeEvent
import ru.tensor.sbis.swipeablelayout.swipeablevm.SwipeableVm
import java.util.*
import kotlin.collections.ArrayList

class FolderViewModelTest : AbstractParcelableTest<FolderViewModel>() {

    override val parcelableCreator: Parcelable.Creator<FolderViewModel> get() = FolderViewModel.CREATOR

    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(FolderViewModel::class.java)
            .suppress(Warning.NONFINAL_FIELDS)
            .withPrefabValues(MutableLiveData::class.java, MutableLiveData<SwipeEvent>(), MutableLiveData<SwipeEvent>())
            .verify()
    }

    @Test
    fun getItemUuid() {
        val uuid = UUID.randomUUID().toString()
        val folder = createFolderViewModel(uuid)
        //verify
        assertEquals(uuid, folder.uuid)
    }

    @Test
    fun setSwipeMenuIsDragLockedTrue() = setSwipeMenu(true)

    @Test
    fun setSwipeMenuIsDragLockedFalse() = setSwipeMenu(false)

    @Test
    fun getSwipeMenu() {
        val folder = createFolderViewModel()
        val swipeMenu = createDefaultSwipeMenu()
        val swipeableVm = SwipeableVm(folder.uuid, swipeMenu, isDragLocked = anyBoolean())
        folder.swipeableVm = swipeableVm
        //verify
        assertEquals(swipeMenu, folder.getSwipeMenu())
    }

    @Test
    fun areItemsTheSame() {
        val uuid = UUID.randomUUID().toString()
        val firstFolder = createFolderViewModel(uuid)
        val secondFolder = createFolderViewModel(uuid)
        //verify
        assertTrue(FolderViewModel.areItemsTheSame(firstFolder, secondFolder))
    }

    @Test
    fun newArray() {
        val size = 5
        //verify
        assertEquals(size, FolderViewModel.newArray(size).size)
    }

    @Test
    fun parcelableConstructor() {
        val folder = createFolderViewModel()
        //act
        val restoredModel = saveAndRestore(folder)
        //verify
        checkAfterRestore(folder, restoredModel)
    }

    @Test
    fun onClickAction() {
        val mockOnClick = mock<(FolderViewModel) -> Unit>()
        val folder = createFolderViewModel()
        folder.onClick = mockOnClick
        //act
        folder.onClick()
        //verify
        assertObservableValueTrue(folder.selectableVm.selected)
        verify(mockOnClick).invoke(folder)
    }

    private fun createFolderViewModel(uuid: String? = null): FolderViewModel = FolderViewModel(
        uuid?.let { it } ?: run { UUID.randomUUID().toString() },
        0,
        "title",
        0,
        0,
        false,
        0,
        true,
        swipeEnabled = true
    )

    private fun setSwipeMenu(isDragLocked: Boolean) {
        val folder = createFolderViewModel()
        val swipeableVm = SwipeableVm(folder.uuid, createDefaultSwipeMenu(), isDragLocked = isDragLocked)
        folder.swipeableVm = swipeableVm
        //verify
        assertEquals(swipeableVm, folder.swipeableVm)
    }

    //TODO: 12/11/20 https://online.sbis.ru/opendoc.html?guid=a79bc08e-5197-4996-8e0a-742bbfaad3de
    private fun createDefaultSwipeMenu() = DefaultSwipeMenu(ArrayList(), itemBindingId = 0)
}