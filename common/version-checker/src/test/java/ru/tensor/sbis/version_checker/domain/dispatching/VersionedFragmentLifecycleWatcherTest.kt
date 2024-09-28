package ru.tensor.sbis.version_checker.domain.dispatching

import android.os.Build
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import org.mockito.kotlin.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import ru.tensor.sbis.version_checker.testUtils.ShadowVersionCheckerPlugin
import ru.tensor.sbis.version_checker.testUtils.ShadowVersionCheckerPlugin.Companion.shadowState
import ru.tensor.sbis.version_checker.testUtils.ShadowVersionCheckerPlugin.Companion.shadowVersionManager
import ru.tensor.sbis.version_checker_decl.VersionedComponent
import ru.tensor.sbis.version_checker_decl.data.UpdateStatus

@RunWith(RobolectricTestRunner::class)
@Config(
    shadows = [ShadowVersionCheckerPlugin::class],
    sdk = [Build.VERSION_CODES.R]
)
@Ignore("Падает в ci, нужно попробовать обновить robolectric 4.9")
internal class VersionedFragmentLifecycleWatcherTest {

    private lateinit var fragmentLifecycleWatcher: VersionedFragmentLifecycleWatcher
    private lateinit var controller: ActivityController<FragmentActivity>
    private lateinit var testActivity: FragmentActivity

    @Test
    fun `Receive recommended update status and propagate to version manager`() {
        buildFragmentLifecycleWatcher()

        controller.resume()
        verify(shadowVersionManager).showRecommendedFragment(any())
    }

    @Test
    fun `Do not subscribe on DialogFragment`() {
        buildFragmentLifecycleWatcher(DialogFragment())

        controller.resume()
        verify(shadowVersionManager, never()).showRecommendedFragment(any())
    }

    @Test
    fun `Do not subscribe on Fragment with skip versioning strategy`() {
        buildFragmentLifecycleWatcher(FragmentSkipStrategy())

        controller.resume()
        verify(shadowVersionManager, never()).showRecommendedFragment(any())
    }

    @Test
    fun `Do not subscribe on Fragment with check critical versioning strategy`() {
        buildFragmentLifecycleWatcher(FragmentCheckCriticalStrategy())

        controller.resume()
        verify(shadowVersionManager, never()).showRecommendedFragment(any())
    }

    @Test
    fun `Do subscribe on Fragment with check recommended versioning strategy`() {
        buildFragmentLifecycleWatcher(FragmentCheckRecommendedStrategy())

        controller.resume()
        verify(shadowVersionManager).showRecommendedFragment(any())
    }

    @Test
    fun `Do nothing when update status is not recommended`() {
        buildFragmentLifecycleWatcher()

        val listStatus = listOf(UpdateStatus.Empty, UpdateStatus.Mandatory)
        for (inappropriateStatus in listStatus) {
            shadowState.value = inappropriateStatus
            controller.resume()
            verify(shadowVersionManager, never()).showRecommendedFragment(any())
            controller.pause()
        }
    }

    class FragmentSkipStrategy : Fragment(), VersionedComponent

    class FragmentCheckCriticalStrategy : Fragment(), VersionedComponent {
        override val versioningStrategy = VersionedComponent.Strategy.CHECK_CRITICAL
    }

    class FragmentCheckRecommendedStrategy : Fragment(), VersionedComponent {
        override val versioningStrategy = VersionedComponent.Strategy.CHECK_RECOMMENDED
    }

    private fun buildFragmentLifecycleWatcher(testFragment: Fragment = Fragment()) {
        shadowState = MutableStateFlow(UpdateStatus.Empty)
        shadowState.value = UpdateStatus.Recommended
        fragmentLifecycleWatcher = spy(VersionedFragmentLifecycleWatcher())

        clearInvocations(shadowVersionManager)
        controller = Robolectric.buildActivity(FragmentActivity::class.java).create()
        testActivity = controller.get()
        testActivity.supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleWatcher, true)
        testActivity.supportFragmentManager.beginTransaction().add(testFragment, "").commit()
    }
}