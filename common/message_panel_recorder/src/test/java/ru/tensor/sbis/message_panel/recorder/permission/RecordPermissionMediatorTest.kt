package ru.tensor.sbis.message_panel.recorder.permission

import android.app.Activity
import android.content.pm.PackageManager
import org.mockito.kotlin.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.recorder.decl.RecordPermissionMediator

/**
 * @author vv.chekurda
 * Создан 8/7/2019
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
@Ignore("Не удаётся шпионить за final методами. Решение будет определено по обсуждению " +
                "https://online.sbis.ru/forum/b9027e38-44cc-4efc-88b4-315681698673?tc=c8d203af-617e-4a7f-933f-1ed25c50114a")
class RecordPermissionMediatorTest {

    private lateinit var activity: Activity

    private lateinit var mediator: RecordPermissionMediator

    @Before
    fun setUp() {
        activity = spy(Robolectric.buildActivity(Activity::class.java).get())
        mediator = RecordPermissionMediatorImpl(activity)
    }

    @Test
    fun `Code block is not called without permission`() {
        val block: () -> Unit = mock()
        permissionGranted(false)

        mediator.withPermission(block)

        verifyNoMoreInteractions(block)
    }

    @Test
    fun `Code block called if permission granted`() {
        val block: () -> Unit = mock()
        permissionGranted(true)

        mediator.withPermission(block)

        verify(block).invoke()
    }

    @Test
    fun `Request permission if not granted`() {
        permissionGranted(false)

        mediator.withPermission {  }

        verify(activity).requestPermissions(arrayOf(RECORDER_VIEW_PERMISSION), RECORDER_VIEW_PERMISSION_REQUEST_CODE)
    }

    private fun permissionGranted(granted: Boolean) {
        doReturn(if (granted) PackageManager.PERMISSION_GRANTED else PackageManager.PERMISSION_DENIED)
            .whenever(activity).checkSelfPermission(RECORDER_VIEW_PERMISSION)
    }
}