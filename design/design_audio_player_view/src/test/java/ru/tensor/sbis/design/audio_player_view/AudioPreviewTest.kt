package ru.tensor.sbis.design.audio_player_view

import android.view.ContextThemeWrapper
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.audio_player_view.view.preview.AudioPreview
import ru.tensor.sbis.design.audio_player_view.view.preview.data.AudioPreviewData

/**
 * Тесты компонента [AudioPreview].
 *
 * @author dv.baranov
 */
@RunWith(RobolectricTestRunner::class)
class AudioPreviewTest {

    private val context = ContextThemeWrapper(ApplicationProvider.getApplicationContext(), R.style.AppGlobalTheme)

    @Test
    fun `Set view data`() {
        val view = AudioPreview(context)
        val viewData: AudioPreviewData = mock()
        view.data = viewData
        Assert.assertEquals(view.data, viewData)
    }
}