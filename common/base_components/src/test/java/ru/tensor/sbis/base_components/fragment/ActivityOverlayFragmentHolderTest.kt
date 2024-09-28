package ru.tensor.sbis.base_components.fragment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import ru.tensor.sbis.android_ext_decl.viewprovider.OverlayFragmentHolder
import ru.tensor.sbis.common.R as RCommon

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
internal class ActivityOverlayFragmentHolderTest {

    @Test
    fun `Given nothing, when check has fragment, then return  false`() {
        createScenario().onActivity {
            assertFalse(it.hasFragment())
        }
    }

    @Test
    fun `When set fragment swipeable, then the fragment is added to swipe back fragment`() {
        createScenario().onActivity {
            it.showFragmentWithCommit(TestFragment(), true)
            val swipeBackFragment = it.supportFragmentManager.findFragmentById(RCommon.id.overlay_container)

            assertTrue(swipeBackFragment!!.childFragmentManager.fragments[0] is TestFragment)
            assertTrue(it.hasFragment())
        }
    }

    @Test
    fun `When set fragment not swipeable, then the fragment is added`() {
        createScenario().onActivity {
            it.showFragmentWithCommit(TestFragment(), false)

            assertHasFragment(it)
        }
    }

    @Test
    fun `Given swipeable fragment, when remove fragment, then the fragment is removed`() {
        createScenario().onActivity {
            it.showFragmentWithCommit(TestFragment(), true)
            it.removeFragmentWithCommit()

            assertHasNoFragment(it)
        }
    }

    @Test
    fun `Given not swipeable fragment, when remove fragment, then the fragment is removed`() {
        createScenario().onActivity {
            it.showFragmentWithCommit(TestFragment(), false)
            it.removeFragmentWithCommit()

            assertHasNoFragment(it)
        }
    }

    @Test
    fun `Given handling backpress fragment, when back press, then the fragment is not removed but handled back press`() {
        createScenario().onActivity {
            it.showFragmentWithCommit(TestFragmentBackPress(), false)

            assertTrue(it.handlePressed())
            assertHasFragment(it)
        }
    }

    @Test
    fun `Given not handling backpress fragment, when back press, then the fragment is removed and return true`() {
        createScenario().onActivity {
            it.showFragmentWithCommit(TestFragment(), false)

            assertTrue(it.handlePressed())
            assertHasNoFragment(it)
        }
    }

    @Test
    fun `Given nothing, handlePressed return false`() {
        createScenario().onActivity {
            assertFalse(it.handlePressed())
        }
    }

    @Test
    fun `Added press callbacks`() {
        createScenario().onActivity {
            assertTrue(it.spyActivityOverlayFragmentHolder.needAddBackPressCallback)
        }
    }

    @Test
    fun `Not added press callbacks`() {
        createScenarioWithoutBackPressCallback().onActivity {
            assertFalse(it.spyActivityOverlayFragmentHolder.needAddBackPressCallback)
        }
    }

    private fun TestOverlayFragmentHolderActivity.showFragmentWithCommit(
        fragment: Fragment, swipeable: Boolean
    ) {
        setFragment(fragment, swipeable)
        supportFragmentManager.executePendingTransactions()
    }

    private fun TestOverlayFragmentHolderActivity.removeFragmentWithCommit() {
        removeFragment()
        supportFragmentManager.executePendingTransactions()
    }

    private fun assertHasFragment(activity: TestOverlayFragmentHolderActivity) {
        activity.supportFragmentManager.executePendingTransactions()
        assertTrue(activity.supportFragmentManager.findFragmentById(RCommon.id.overlay_container) != null)
        assertTrue(activity.hasFragment())
    }

    private fun assertHasNoFragment(activity: TestOverlayFragmentHolderActivity) {
        activity.supportFragmentManager.executePendingTransactions()
        assertNull(activity.supportFragmentManager.findFragmentById(RCommon.id.overlay_container))
        assertFalse(activity.hasFragment())
    }

    private fun createScenario() = ActivityScenario.launch(TestOverlayFragmentHolderActivity::class.java).apply {
        moveToState(Lifecycle.State.RESUMED)
    }

    private fun createScenarioWithoutBackPressCallback() =
        ActivityScenario.launch(TestOverlayFragmentHolderActivityWithoutBackPressCallback::class.java).apply {
            moveToState(Lifecycle.State.RESUMED)
        }
}

internal open class TestOverlayFragmentHolderActivity : AppCompatActivity(), OverlayFragmentHolder {

    open val spyActivityOverlayFragmentHolder = ActivityOverlayFragmentHolder(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ru.tensor.sbis.base_components.test.R.layout.base_components_activity_main)
    }

    override fun setFragment(fragment: Fragment, swipeable: Boolean) {
        spyActivityOverlayFragmentHolder.setFragment(fragment, swipeable)
    }

    override fun hasFragment() = spyActivityOverlayFragmentHolder.hasFragment()

    override fun removeFragment() {
        spyActivityOverlayFragmentHolder.removeFragment()
    }

    override fun handlePressed() = spyActivityOverlayFragmentHolder.handlePressed()

}
internal class TestOverlayFragmentHolderActivityWithoutBackPressCallback : TestOverlayFragmentHolderActivity() {

    override val spyActivityOverlayFragmentHolder = ActivityOverlayFragmentHolder().apply {
        init(this@TestOverlayFragmentHolderActivityWithoutBackPressCallback, false)
    }
}

internal class TestFragment : Fragment()

internal class TestFragmentBackPress : Fragment(), FragmentBackPress {
    override fun onBackPressed() = true
}