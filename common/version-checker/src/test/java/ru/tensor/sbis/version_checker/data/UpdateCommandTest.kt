package ru.tensor.sbis.version_checker.data

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import org.mockito.kotlin.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.version_checker.domain.source.UpdateCommandFactory.Companion.UPDATE_SOURCE_KEY
import ru.tensor.sbis.version_checker_decl.data.UpdateSource

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.R])
internal class UpdateCommandTest {

    private val mockContext = mock<Context> {
        on { startActivity(any()) } doAnswer {
            if (doThrowActivityNotFoundException) {
                throw ActivityNotFoundException()
            }
        }
    }
    private var doThrowActivityNotFoundException = false

    @After
    fun tearDown() {
        doThrowActivityNotFoundException = false
    }

    @Test
    fun `All intents were tried to launch`() {
        doThrowActivityNotFoundException = true
        val intents = listOf(Intent(), Intent(), Intent(), Intent())
        val command = UpdateCommand(intents)
        command.run(mockContext)

        verify(mockContext, times(4)).startActivity(any())
    }

    @Test
    fun `On successful attempt return update source for analytics`() {
        val testUpdateSource = UpdateSource.GOOGLE_PLAY_STORE.name
        val intents = listOf(Intent().apply { putExtra(UPDATE_SOURCE_KEY, testUpdateSource) })

        val command = UpdateCommand(intents)
        assertEquals(testUpdateSource, command.run(mockContext))
    }
}
