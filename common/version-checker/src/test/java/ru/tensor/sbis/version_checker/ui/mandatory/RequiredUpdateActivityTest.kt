package ru.tensor.sbis.version_checker.ui.mandatory

import android.content.Intent
import android.os.Build
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.R])
internal class RequiredUpdateActivityTest {

    @Test
    fun `Create intent activity with right flags`() {
        val expectedIntent = Intent()
        expectedIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        expectedIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

        val intent = RequiredUpdateActivity.createIntent()
        assertEquals(expectedIntent.flags, intent.flags)
    }
}