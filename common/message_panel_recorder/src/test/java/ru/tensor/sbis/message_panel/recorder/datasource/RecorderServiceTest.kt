package ru.tensor.sbis.message_panel.recorder.datasource

import android.media.MediaRecorder
import android.net.Uri
import org.mockito.kotlin.*
import io.reactivex.observers.TestObserver
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import ru.tensor.sbis.common_attachments.Attachment
import ru.tensor.sbis.common.util.FileUtil
import ru.tensor.sbis.message_panel.recorder.datasource.file.RecordFileFactory
import java.io.File

/**
 * @author vv.chekurda
 * Создан 8/6/2019
 */
@RunWith(JUnitParamsRunner::class)
class RecorderServiceTest {

    private val fileName = "file_name"
    private val path = "absolute/path/to/$fileName"

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    private lateinit var recordObserver: TestObserver<Attachment>

    private val anyInt = -1

    @Mock
    private lateinit var fileFactory: RecordFileFactory

    @Mock
    private lateinit var uri: Uri

    @Mock
    private lateinit var file: File

    @Mock
    private lateinit var errorListener: MediaRecorder.OnErrorListener

    @Mock
    private lateinit var infoListener: MediaRecorder.OnInfoListener

    @Mock
    private lateinit var recorderFactory: () -> MediaRecorder

    @Mock
    private lateinit var recorder: MediaRecorder

    private lateinit var service: RecorderServiceImpl

    @Before
    fun setUp() {
        service = RecorderServiceImpl(fileFactory, errorListener, infoListener, recorderFactory)

        recordObserver = TestObserver()
        service.recordFile.subscribe(recordObserver)
    }

    @Test
    fun `Start record`() {
        setUpFactories()

        service.startRecord()

        verify(recorderFactory).invoke()
        verify(fileFactory).createFile()
        verify(recorder).setOnErrorListener(service)
        verify(recorder).setOnInfoListener(service)
        verify(recorder).setOutputFile(file.absolutePath)
        verify(recorder).prepare()
        verify(recorder).start()

        recordObserver.assertNoValues()

        verifyNoMoreInteractions(errorListener)
        verifyNoMoreInteractions(infoListener)
    }

    @Test
    fun `Stop record`() {
        setUpFactories()
        setUpUriProperties()

        service.startRecord()
        service.stopRecord()

        verifyStop(recorder)

        // проверим, что записалось
        recordObserver.assertValue(createPredicate())

        verifyNoMoreInteractions(errorListener)
        verifyNoMoreInteractions(infoListener)
    }

    @Test
    fun `Cancel record`() {
        setUpFactories()

        service.startRecord()
        service.cancelRecord()

        verify(recorder).release()

        recordObserver.assertNoValues()

        verifyNoMoreInteractions(errorListener)
        verifyNoMoreInteractions(infoListener)
    }

    @Test
    fun `Handle error on started record`() {
        setUpFactories()

        service.startRecord()
        service.onError(recorder, anyInt, anyInt)

        recordObserver.assertNoValues()

        verifyTerminate(recorder)
        verify(errorListener).onError(recorder, anyInt, anyInt)

        verifyNoMoreInteractions(infoListener)
    }

    @Test
    fun `Handle error when record wasn't started`() {
        service.onError(recorder, anyInt, anyInt)

        recordObserver.assertNoValues()

        // делегация без дополнительной обработки
        verify(errorListener).onError(recorder, anyInt, anyInt)

        verifyNoMoreInteractions(recorder)
        verifyNoMoreInteractions(infoListener)
    }

    @Test
    @Parameters(method = "getAutoStopInfo")
    fun `Verify auto stop on max file size or max duration reached`(what: Int) {
        setUpFactories()
        setUpUriProperties()

        with(spy(service)) {
            startRecord()
            onInfo(recorder, what, anyInt)

            verify(this).stopRecord()
        }

        verify(infoListener, never()).onInfo(recorder, what, anyInt)
        verifyNoMoreInteractions(errorListener)
    }

    @Test
    @Parameters(method = "getAllInfo")
    fun `Verify info delegation without auto stop if record not started`(what: Int) {
        with(spy(service)) {
            onInfo(recorder, what, anyInt)

            verify(this, never()).stopRecord()
        }

        verify(infoListener).onInfo(recorder, what, anyInt)
        verifyNoMoreInteractions(errorListener)
    }

    @Test
    @Parameters(method = "getIgnoredInfo")
    fun `Verify info delegation without auto stop`(what: Int) {
        setUpFactories()

        with(spy(service)) {
            startRecord()
            onInfo(recorder, what, anyInt)

            verify(this, never()).stopRecord()
        }

        verify(infoListener).onInfo(recorder, what, anyInt)
        verifyNoMoreInteractions(errorListener)
    }

    @Test
    fun `Dispose test`() {
        assertFalse(service.isDisposed)

        service.dispose()

        assertTrue(service.isDisposed)

        recordObserver.assertEmpty()

        verifyNoMoreInteractions(recorder)
        verifyNoMoreInteractions(errorListener)
        verifyNoMoreInteractions(infoListener)
    }

    @Test
    fun `Dispose on active record`() {
        setUpFactories()

        service.startRecord()
        service.dispose()

        recordObserver.assertEmpty()

        verifyTerminate(recorder)

        verifyNoMoreInteractions(errorListener)
        verifyNoMoreInteractions(infoListener)
    }

    /**
     * Тест случая, когда запрошена остановка записи, но [MediaRecorder] ещё не успел перейти в состояние записи
     * Fix https://online.sbis.ru/opendoc.html?guid=56f16430-51a6-4f8f-af6d-915718e6e783
     */
    @Test
    fun `Given recorder in non started state, when stop invoked, then file should be removed and attachment shouldn't be delivered`() {
        setUpFactories()
        whenever(recorder.stop()).thenThrow(RuntimeException::class.java)

        service.startRecord()
        service.stopRecord()

        verify(file).delete()
        // самостоятельно не вызываем подписку на ошибки, только MediaRecorder
        verifyNoMoreInteractions(errorListener)
        verifyNoMoreInteractions(infoListener)
        verifyStop(recorder)
        recordObserver.assertNoValues()
    }

    private fun setUpFactories() {
        whenever(recorderFactory.invoke()).thenReturn(recorder)
        whenever(fileFactory.createFile()).thenReturn(file)
        whenever(file.absolutePath).thenReturn(path)
    }

    private fun setUpUriProperties() {
        whenever(uri.scheme).thenReturn("file")
        whenever(uri.lastPathSegment).thenReturn(fileName)
        whenever(fileFactory.fileToUri(file)).thenReturn(uri)
    }

    private fun verifyStop(recorder: MediaRecorder) {
        verify(recorder).stop()
        verify(recorder).release()
    }

    private fun verifyTerminate(recorder: MediaRecorder) {
        verify(recorder).release()
    }

    private fun createPredicate(): (Attachment) -> Boolean = {
        // путь до файла корректный
        it.uri == uri.toString()
                // это звукозапись
                && it.fileType == FileUtil.FileType.AUDIO
                // новая, ещё не подписана
                && it.signaturesCount == 0
    }

    private fun getAutoStopInfo(): Array<Any> = arrayOf(
        MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED,
        MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED
    )

    private fun getIgnoredInfo(): Array<Any> = arrayOf(
        MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_APPROACHING,
        MediaRecorder.MEDIA_RECORDER_INFO_NEXT_OUTPUT_FILE_STARTED,
        MediaRecorder.MEDIA_RECORDER_INFO_UNKNOWN
    )

    private fun getAllInfo(): Array<Any> = arrayOf(
        *getAutoStopInfo(),
        *getIgnoredInfo()
    )
}