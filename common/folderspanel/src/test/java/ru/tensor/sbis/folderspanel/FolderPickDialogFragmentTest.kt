package ru.tensor.sbis.folderspanel

import androidx.fragment.app.FragmentActivity
import android.view.ViewGroup
import android.widget.FrameLayout
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import java.util.*
import kotlin.collections.ArrayList
import ru.tensor.sbis.design.R as RDesign

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class FolderPickDialogFragmentTest {
    /**
     * При изменении [FolderPickDialogFragment.FOLDERS] поддержать тесты
     * */
    private val FOLDERS = FolderPickDialogFragment::class.java.canonicalName + ".FOLDERS"

    private lateinit var rootActivity: FragmentActivity
    private lateinit var rootActivityController: ActivityController<FragmentActivity>
    private lateinit var fragment: FolderPickDialogFragment

    @Before
    fun setupApplication() {
        rootActivityController = Robolectric.buildActivity(FragmentActivity::class.java)
            .create()
            .start()
            .resume()
            .visible()
        rootActivity = rootActivityController.get() as FragmentActivity
        rootActivity.setTheme(RDesign.style.AppTheme_Swipe_Back)

        val view = FrameLayout(rootActivity.applicationContext)
        view.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        rootActivity.setContentView(view)
    }

    @Test
    fun newInstance() {
        val foldersList = createFolderViewModelList()
        fragment = FolderPickDialogFragment.newInstance(foldersList)
        //act
        val foldersFromArguments = fragment.arguments?.getParcelableArrayList<FolderViewModel>(FOLDERS)
        //verify
        assertEquals(foldersList.size, foldersFromArguments?.size)
        for (i in foldersList.indices) assertEquals(foldersList[i], foldersFromArguments?.get(i))
    }

    private fun createFolderViewModelList(): List<FolderViewModel> {
        val result = ArrayList<FolderViewModel>()
        for (i in 0..9) result.add(createFolderViewModel())
        return result
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
}