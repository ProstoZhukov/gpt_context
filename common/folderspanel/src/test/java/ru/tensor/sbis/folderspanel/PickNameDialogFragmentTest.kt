package ru.tensor.sbis.folderspanel

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.R as RDesign

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class PickNameDialogFragmentTest {
    /**
     * При изменении [PickNameDialogFragment.FOLDER_BUNDLE_NAME], [PickNameDialogFragment.TITLE_BUNDLE_NAME],
     * [PickNameDialogFragment.HINT_BUNDLE_NAME] поддержать тесты
     * */
    private val FOLDER_BUNDLE_NAME = PickNameDialogFragment::class.java.canonicalName!! + ".folderUUID"
    private val TITLE_BUNDLE_NAME = PickNameDialogFragment::class.java.canonicalName!! + ".title"
    private val HINT_BUNDLE_NAME = PickNameDialogFragment::class.java.canonicalName!! + ".hint"

    private lateinit var rootActivity: FragmentActivity
    private lateinit var rootActivityController: ActivityController<FragmentActivity>
    private lateinit var fragment: PickNameDialogFragment

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
        val title = "title"
        val previousName = "name"
        val hint = "hint"
        fragment = PickNameDialogFragment.newInstance(title, previousName, hint)
        //act
        val titleArguments = fragment.arguments?.getString(TITLE_BUNDLE_NAME)
        val previousNameArguments = fragment.arguments?.getString(FOLDER_BUNDLE_NAME)
        val hintArguments = fragment.arguments?.getString(HINT_BUNDLE_NAME)
        //verify
        assertEquals(title, titleArguments)
        assertEquals(previousName, previousNameArguments)
        assertEquals(hint, hintArguments)
    }
}