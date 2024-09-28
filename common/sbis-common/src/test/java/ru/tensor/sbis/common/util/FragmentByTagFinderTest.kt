package ru.tensor.sbis.common.util

import android.os.Build.VERSION_CODES.O
import android.os.Build.VERSION_CODES.P
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.shadow.api.Shadow
import org.robolectric.shadows.ShadowLooper
import ru.tensor.sbis.common.R
import ru.tensor.sbis.common.testing.ActivityWithContainer

@Ignore("https://online.sbis.ru/doc/b5533ad9-737c-4ae2-a9a5-29c98487f198")
@Config(
    sdk = [
        //M,
        /**
         * с верисей M(23) часто рандомно падает в ci, при изменении в файле стоит запустить
         * тест с этой версией руками
         */
        O, P]
)
@RunWith(AndroidJUnit4::class)
class FragmentByTagFinderTest {

    private lateinit var fragment: Fragment
    private val containerId = 1

    @Before
    fun setUp() {
        fragment = object : Fragment() {
            override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
            ): View {
                return FrameLayout(context!!).apply { id = containerId }
            }
        }

        launchFragmentInContainer { fragment }
    }

    @Test
    fun `Can find fragment if added`() {
        addFragmentsWithTags()

        assertHasBothFragments()
    }

    @Test
    fun `Can NOT find fragment if NOT exist`() {
        assertHasNoFragmentsWithTags()
    }

    @Test
    fun `Can NOT find fragment if exist with another tag`() {
        Shadow.extract<ShadowLooper>(Looper.getMainLooper())
            .pause()
        addFragmentWithTag("tag 3")

        assertHasNoFragmentsWithTags()
    }

    @Test
    fun `Can find fragment if in pending transaction`() {
        addFragmentsWithTags()

        assertHasBothFragments()
    }

    @Test
    fun `Can NOT find fragment if NOT in pending transaction`() {
        Shadow.extract<ShadowLooper>(Looper.getMainLooper())
            .pause()
        addFragmentWithTag("some tag1")
        addFragmentWithTag("some tag2")

        assertHasNoFragmentsWithTags()
    }

    private fun addFragmentsWithTags() {
        addFragmentWithTag(tag1)
        addFragmentWithTag(tag2)
    }

    private fun addFragmentWithTag(tag: String) {
        fragment.childFragmentManager
            .beginTransaction()
            .add(
                containerId,
                Fragment(),
                tag
            )
            .addToBackStack(tag)
            .commit()
    }

    private fun assertHasBothFragments() {
        assertTrue(
            FragmentByTagFinder().hasAlreadyFragmentOrPendingTransactionWithTag(
                fragment.childFragmentManager,
                tag1
            )
        )
        assertTrue(
            FragmentByTagFinder().hasAlreadyFragmentOrPendingTransactionWithTag(
                fragment.childFragmentManager,
                tag2
            )
        )
    }

    private fun assertHasNoFragmentsWithTags() {
        assertFalse(
            FragmentByTagFinder().hasAlreadyFragmentOrPendingTransactionWithTag(
                fragment.childFragmentManager,
                tag1
            )
        )
        assertFalse(
            FragmentByTagFinder().hasAlreadyFragmentOrPendingTransactionWithTag(
                fragment.childFragmentManager,
                tag2
            )
        )
    }
}

private const val tag1 = "tag1"
private const val tag2 = "tag1"

private class ThemeActivityWithContainer : ActivityWithContainer() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(androidx.appcompat.R.style.Theme_AppCompat)
        super.onCreate(savedInstanceState)
    }
}