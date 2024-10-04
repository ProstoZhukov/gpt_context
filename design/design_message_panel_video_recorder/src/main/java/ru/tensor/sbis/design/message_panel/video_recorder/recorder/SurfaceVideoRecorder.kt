package ru.tensor.sbis.design.message_panel.video_recorder.recorder

import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.view.Surface
import ru.tensor.sbis.design.message_panel.recorder_common.audio_record.AudioRecordListener
import ru.tensor.sbis.design.message_panel.recorder_common.audio_record.AudioRecorder
import ru.tensor.sbis.design.message_panel.recorder_common.utils.MediaMuxerWrapper
import ru.tensor.sbis.design.message_panel.video_recorder.recorder.codec.SurfaceVideoCodec
import ru.tensor.sbis.design.message_panel.video_recorder.recorder.codec.SurfaceVideoCodecListener
import ru.tensor.sbis.design.message_panel.video_recorder.recorder.codec.surface.CodecSurfaceDrawer
import ru.tensor.sbis.design.message_panel.video_recorder.recorder.listener.SurfaceVideoRecordListener
import java.io.File

/**
 * Компонент записи видео с [Surface] посредством OpenGL.
 *
 * @author vv.chekurda
 */
internal class SurfaceVideoRecorder {

    private var mediaMuxer: MediaMuxerWrapper? = null
    private val audioRecorder = AudioRecorder(
        config = AudioRecorder.AudioRecordConfig(
            maxRecordDurationSec = Int.MAX_VALUE,
            audioMimeType = MediaFormat.MIMETYPE_AUDIO_AAC
        )
    )
    private val videoCodec = SurfaceVideoCodec().apply {
        processListener = object : SurfaceVideoCodecListener {
            override fun onPrepared(surfaceDrawer: CodecSurfaceDrawer) {
                recordListener?.onPrepared(surfaceDrawer)
            }

            override fun onStarted() {
                recordListener?.onStarted()
            }

            override fun onStopped() {
                recordListener?.apply {
                    val resultFile = mediaMuxer?.resultFile
                    if (resultFile != null) {
                        onFinished(resultFile, audioRecorder.durationSeconds)
                    } else {
                        onCancelled()
                    }
                }
                mediaMuxer = null
            }

            override fun onError(error: Exception) {
                mediaMuxer = null
                recordListener?.onError(error)
            }
        }
    }

    /**
     * Слушатель процесса записи видео с [SurfaceVideoRecorder].
     */
    var recordListener: SurfaceVideoRecordListener? = null
        set(value) {
            field = value
            audioRecorder.recordListener = value?.let {
                object : AudioRecordListener {
                    override fun onVolumeAmplitudeChanged(amplitude: Float) {
                        recordListener?.onVolumeAmplitudeChanged(amplitude)
                    }
                    override fun onRecordCompleted(audioFile: File, duration: Int, waveform: ByteArray) = Unit
                }
            }
        }

    /**
     * Подготовить [SurfaceVideoRecorder] к записи видео.
     */
    fun prepare(muxer: MediaMuxerWrapper) {
        mediaMuxer = muxer
        val videoFormat = MediaFormat.createVideoFormat(VIDEO_MIME_TYPE, VIDEO_RESOLUTION, VIDEO_RESOLUTION).apply {
            setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
            setInteger(MediaFormat.KEY_BIT_RATE, VIDEO_BITRATE)
            setInteger(MediaFormat.KEY_FRAME_RATE, VIDEO_FRAME_RATE)
            setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, I_FRAME_INTERVAL)
        }
        videoCodec.prepare(muxer, videoFormat)
        audioRecorder.prepare(muxer)
    }

    /**
     * Начать запись видео.
     */
    fun startRecording() {
        videoCodec.start()
        audioRecorder.startRecording()
    }

    /**
     * Остановить запись видео.
     */
    fun stopRecording() {
        videoCodec.stop()
        audioRecorder.stopRecording()
    }

    /**
     * Отменить запись видео.
     */
    fun cancelRecording() {
        videoCodec.stop()
        audioRecorder.cancelRecording()
    }

    /**
     * Высвобождение ресурсов.
     */
    fun release() {
        videoCodec.release()
        audioRecorder.release()
    }
}

private const val VIDEO_MIME_TYPE = "video/avc"
private const val VIDEO_RESOLUTION = 384
private const val VIDEO_BITRATE = 1024 * 1000
private const val VIDEO_FRAME_RATE = 30
private const val I_FRAME_INTERVAL = 1