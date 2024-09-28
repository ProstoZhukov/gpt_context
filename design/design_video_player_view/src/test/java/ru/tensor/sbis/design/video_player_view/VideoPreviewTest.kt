package ru.tensor.sbis.design.video_player_view

import android.view.ContextThemeWrapper
import androidx.test.core.app.ApplicationProvider
import com.facebook.drawee.backends.pipeline.DraweeConfig
import com.facebook.drawee.backends.pipeline.Fresco
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.video_message_view.preview.VideoPreview
import ru.tensor.sbis.design.video_message_view.preview.data.VideoPreviewData

/**
 * Тесты компонента [VideoPreview].
 *
 * @author dv.baranov
 */
@RunWith(RobolectricTestRunner::class)
class VideoPreviewTest {

    private val context = ContextThemeWrapper(ApplicationProvider.getApplicationContext(), R.style.AppGlobalTheme)

    @Before
    fun before() {
        Fresco.initialize(
            ApplicationProvider.getApplicationContext(),
            null,
            DraweeConfig.newBuilder().build()
        )
    }

    @Test
    fun `Set view data`() {
        val view = VideoPreview(context)
        val viewData: VideoPreviewData = mock()
        view.data = viewData
        assertEquals(view.data, viewData)
    }
}