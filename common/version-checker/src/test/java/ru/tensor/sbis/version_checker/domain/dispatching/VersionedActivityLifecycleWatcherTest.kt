package ru.tensor.sbis.version_checker.domain.dispatching

import android.os.Build
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import org.mockito.kotlin.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.R])
internal class VersionedActivityLifecycleWatcherTest {

    private lateinit var versionedActivityLifecycleWatcher: VersionedActivityLifecycleWatcher
    private lateinit var mockSupportFragmentManager: FragmentManager
    private lateinit var controller: ActivityController<FragmentActivity>
    private lateinit var spyActivity: FragmentActivity

    @Test
    fun `Register fragment watcher on create and unregister on destroy`() {
        buildActivityLifecycleWatcher()

        // Подписка при onCreate
        controller.create()
        verify(mockSupportFragmentManager).registerFragmentLifecycleCallbacks(any(), eq(true))

        // Отписка при onDestroy
        clearInvocations(mockSupportFragmentManager)
        controller.destroy()
        verify(mockSupportFragmentManager).unregisterFragmentLifecycleCallbacks(any())
        verify(mockSupportFragmentManager, never()).registerFragmentLifecycleCallbacks(any(), any())
    }

    private fun buildActivityLifecycleWatcher() {
        mockSupportFragmentManager = mock()
        controller = Robolectric.buildActivity(FragmentActivity::class.java)
        spyActivity = spy(controller.get()) {
            on { supportFragmentManager } doReturn mockSupportFragmentManager
        }
        versionedActivityLifecycleWatcher = VersionedActivityLifecycleWatcher(spyActivity)
    }
}