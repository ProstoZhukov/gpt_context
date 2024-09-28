package ru.tensor.sbis.message_panel.recorder.datasource

import android.media.MediaRecorder
import org.mockito.kotlin.only
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import ru.tensor.sbis.recorder.decl.RecorderService

/**
 * Тестирование стандартного обработчика информационных сообщений
 *
 * @author vv.chekurda
 * Создан 8/6/2019
 */
@RunWith(JUnitParamsRunner::class)
class DefaultRecorderInfoListenerTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    private val extra = 0

    @Mock
    private lateinit var recorderService: RecorderService

    @Mock
    private lateinit var recorder: MediaRecorder

    @InjectMocks
    private lateinit var listener: DefaultRecorderInfoListener

    @Test
    fun `Stop record on max file size reached`() {
        listener.onInfo(recorder, MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED, extra)

        verify(recorderService, only()).stopRecord()
        verifyNoMoreInteractions(recorder)
    }

    @Test
    fun `Stop record on max duration reached`() {
        listener.onInfo(recorder, MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED, extra)

        verify(recorderService, only()).stopRecord()
        verifyNoMoreInteractions(recorder)
    }

    @Test
    @Parameters(method = "getEventSet")
    fun `No reaction on other events`(what: Int) {
        listener.onInfo(recorder, what, extra)

        verifyNoMoreInteractions(recorderService, recorder)
    }

    private fun getEventSet(): Array<Any> = arrayOf(
        MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_APPROACHING,
        MediaRecorder.MEDIA_RECORDER_INFO_NEXT_OUTPUT_FILE_STARTED,
        MediaRecorder.MEDIA_RECORDER_INFO_UNKNOWN
    )
}