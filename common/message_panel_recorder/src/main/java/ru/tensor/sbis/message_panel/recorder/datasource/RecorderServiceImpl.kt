package ru.tensor.sbis.message_panel.recorder.datasource

import android.media.MediaRecorder
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.common_attachments.Attachment
import ru.tensor.sbis.message_panel.recorder.datasource.file.RecordFileFactory
import ru.tensor.sbis.message_panel.recorder.util.createAudioAttachment
import ru.tensor.sbis.message_panel.recorder.util.init
import ru.tensor.sbis.recorder.decl.RecorderService
import timber.log.Timber
import java.io.File

/**
 * Реализация по умолчанию для [RecorderService]
 *
 * @author vv.chekurda
 * Создан 8/6/2019
 */
internal class RecorderServiceImpl(
    private val fileFactory: RecordFileFactory,
    private val errorListener: MediaRecorder.OnErrorListener,
    private val infoListener: MediaRecorder.OnInfoListener? = null,
    private val recorderFactory: () -> MediaRecorder = { MediaRecorder().init() }
) : RecorderService, MediaRecorder.OnErrorListener, MediaRecorder.OnInfoListener {

    private var isDisposed = false
    private var recorder: MediaRecorder? = null
    private var file: File? = null

    override val recordFile = PublishSubject.create<Attachment>()

    override fun startRecord() {
        recorder = recorderFactory().also { recorder ->
            file = fileFactory.createFile().also { outputFile ->
                outputFile.run(File::getAbsolutePath).let(recorder::setOutputFile)
            }

            recorder.setOnInfoListener(this)
            recorder.setOnErrorListener(this)

            recorder.prepare()
            recorder.start()
        }
    }

    override fun stopRecord() {
        try {
            recorder!!.stop()
            recordFile.onNext(createAudioAttachment(fileFactory.fileToUri(file!!)))
        } catch (e: RuntimeException) {
            Timber.w(e)
            file!!.runCatching(File::delete)
                .onFailure(Timber::w)
                .onSuccess { successfulDeleted ->
                    if (!successfulDeleted) Timber.w("File $file was not deleted")
                }
        } finally {
            releaseRecorder()
        }
    }

    override fun cancelRecord() {
        releaseRecorder()
    }

    override fun onError(mediaRecorder: MediaRecorder, what: Int, extra: Int) {
        releaseRecorder()
        errorListener.onError(mediaRecorder, what, extra)
    }

    override fun onInfo(mediaRecorder: MediaRecorder, what: Int, extra: Int) {
        if (recorder == null) {
            infoListener?.onInfo(mediaRecorder, what, extra)
        } else when (what) {
            MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED,
            MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED -> stopRecord()
            else                                                   -> infoListener?.onInfo(mediaRecorder, what, extra)
                ?: Timber.w("Unexpected info message (what: %d, extra: %d)", what, extra)
        }
    }

    override fun isDisposed(): Boolean = isDisposed

    override fun dispose() {
        isDisposed = true
        releaseRecorder()
    }

    private fun releaseRecorder() {
        recorder?.release()
        recorder = null
        file = null
    }
}