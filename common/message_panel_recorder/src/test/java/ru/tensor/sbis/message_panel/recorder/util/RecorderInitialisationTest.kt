package ru.tensor.sbis.message_panel.recorder.util

import android.media.MediaRecorder
import org.mockito.kotlin.*
import org.junit.Test
import ru.tensor.sbis.message_panel.recorder.RECORDER_MAX_DURATION
import ru.tensor.sbis.message_panel.recorder.RECORDER_MAX_FILE_SIZE

/**
 * @author vv.chekurda
 * Создан 8/7/2019
 */
class RecorderInitialisationTest {

    private val recorder: MediaRecorder = mock()

    @Test
    fun `Required parameters test`() {
        recorder.init()

        verify(recorder).setAudioSource(any())
        verify(recorder).setOutputFormat(any())
        verify(recorder).setAudioEncoder(any())

        verify(recorder).setMaxFileSize(RECORDER_MAX_FILE_SIZE)
        verify(recorder).setMaxDuration(RECORDER_MAX_DURATION)

        verify(recorder, atMost(1)).setPreferredMicrophoneDirection(any())

        // остальные атрибуты устанавливаются пользователем
        verifyNoMoreInteractions(recorder)
    }
}