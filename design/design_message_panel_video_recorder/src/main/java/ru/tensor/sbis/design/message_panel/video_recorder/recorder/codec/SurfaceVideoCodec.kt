package ru.tensor.sbis.design.message_panel.video_recorder.recorder.codec

import android.media.MediaCodec
import android.media.MediaCodec.BufferInfo
import android.media.MediaFormat
import android.os.Handler
import android.os.Looper
import android.view.Surface
import ru.tensor.sbis.common.util.safeThrow
import ru.tensor.sbis.design.message_panel.recorder_common.utils.DispatchQueue
import ru.tensor.sbis.design.message_panel.recorder_common.utils.MediaMuxerWrapper
import ru.tensor.sbis.design.message_panel.video_recorder.recorder.codec.surface.CodecSurfaceChangeListener
import ru.tensor.sbis.design.message_panel.video_recorder.recorder.codec.surface.CodecInputSurface

/**
 * Кодек для записи видео с [Surface] в файл при помощи [MediaMuxerWrapper].
 *
 * @author vv.chekurda
 */
internal class SurfaceVideoCodec {

    private val codecQueue = DispatchQueue(VIDEO_CODEC_QUEUE_THREAD_NAME)
        .apply { priority = Thread.MAX_PRIORITY }
    private val mainHandler: Handler by lazy { Handler(Looper.getMainLooper()) }

    private var mediaCodec: MediaCodec? = null
    private var codecInputSurface: CodecInputSurface? = null
    private var codecBufferInfo = BufferInfo()
    private var mediaMuxer: MediaMuxerWrapper? = null
    private var trackIndex = -1

    private var lastPresentationTimeUs = EMPTY_TIME
    private var lastSurfaceTimestampNanos = EMPTY_TIME

    /**
     * Признак готовности кодека для записи видео.
     */
    @Volatile
    var isPrepared: Boolean = false
        private set

    /**
     * Признак состояния кодека.
     */
    @Volatile
    var isRunning: Boolean = false
        private set

    /**
     * Слушатель кодека.
     */
    var processListener: SurfaceVideoCodecListener? = null

    /**
     * Подготовить кодек к записи видео.
     */
    fun prepare(muxer: MediaMuxerWrapper, format: MediaFormat) {
        codecQueue.cleanupQueue()
        finishEncoder()
        isRunning = false
        isPrepared = false

        codecQueue.post {
            mediaMuxer = muxer
            val codec = MediaCodec.createEncoderByType(VIDEO_MIME_TYPE).apply {
                configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            }
            val inputSurface = CodecInputSurface(codec.createInputSurface(), codecQueue).apply {
                surfaceChangeListener = CodecSurfaceChangeListener(::drainEncoder)
            }
            mediaCodec = codec
            codecInputSurface = inputSurface
            codecBufferInfo = BufferInfo()

            isPrepared = true
            runOnUiThread { processListener?.onPrepared(inputSurface) }
        }
    }

    /**
     * Начать запись видео.
     */
    fun start() {
        codecQueue.post {
            if (isRunning) return@post
            isRunning = true

            check(isPrepared) { "${javaClass.simpleName} is not prepared" }
            try {
                clearPresentationTime()
                checkNotNull(mediaCodec).start()
                checkNotNull(codecInputSurface).isEnabled = true
                runOnUiThread { processListener?.onStarted() }
            } catch (e: Exception) {
                isPrepared = false
                isRunning = false
                finishEncoder()

                runOnUiThread { processListener?.onError(e) }
                safeThrow(e)
            }
            processListener
        }
    }

    /**
     * Остановить запись видео.
     */
    fun stop() {
        if (!isRunning) return
        isRunning = false
        isPrepared = false
        codecQueue.post {
            finishEncoder()
            runOnUiThread { processListener?.onStopped() }
        }
    }

    /**
     * Высвобождение ресурсов.
     */
    fun release() {
        finishEncoder()
        codecQueue.recycle()
    }

    private fun drainEncoder(surfacePresentationTimeNanos: Long) {
        val mediaMuxer = mediaMuxer ?: return
        val mediaCodec = mediaCodec ?: return
        val presentationTimeUs = getEncoderPresentationTimeUs(surfacePresentationTimeNanos)

        val outputIndex = mediaCodec.dequeueOutputBuffer(codecBufferInfo, MEDIA_CODEC_TIMEOUT_US)
        when {
            outputIndex == MediaCodec.INFO_TRY_AGAIN_LATER -> Unit
            outputIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                trackIndex = mediaMuxer.addTrack(mediaCodec.outputFormat)
                mediaMuxer.start()
            }
            outputIndex >= 0 -> {
                val outputData = mediaCodec.getOutputBuffer(outputIndex)
                if (codecBufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0) {
                    codecBufferInfo.size = 0
                }
                if (codecBufferInfo.size != 0 && outputData != null) {
                    codecBufferInfo.presentationTimeUs = presentationTimeUs
                    mediaMuxer.writeSampleData(trackIndex, outputData, codecBufferInfo)
                }
                mediaCodec.releaseOutputBuffer(outputIndex, false)
            }
        }
    }

    private fun getEncoderPresentationTimeUs(surfaceTimestampNanos: Long): Long {
        if (lastPresentationTimeUs == EMPTY_TIME) {
            lastPresentationTimeUs = System.nanoTime() / 1000
            lastSurfaceTimestampNanos = surfaceTimestampNanos
        }
        val deltaSurfaceTimestampUs = (surfaceTimestampNanos - lastSurfaceTimestampNanos) / 1000
        lastSurfaceTimestampNanos = surfaceTimestampNanos
        lastPresentationTimeUs += deltaSurfaceTimestampUs
        return lastPresentationTimeUs
    }

    private fun finishEncoder() {
        mediaCodec?.stop()
        mediaCodec?.release()
        mediaCodec = null

        mediaMuxer?.stop()
        mediaMuxer = null

        codecInputSurface?.release()
        codecInputSurface = null
        trackIndex = -1
    }

    private fun clearPresentationTime() {
        lastPresentationTimeUs = EMPTY_TIME
        lastSurfaceTimestampNanos = EMPTY_TIME
    }

    private fun runOnUiThread(action: () -> Unit) {
        mainHandler.post(action)
    }
}

private const val VIDEO_CODEC_QUEUE_THREAD_NAME = "videoCodecQueue"
private const val VIDEO_MIME_TYPE = "video/avc"
private const val MEDIA_CODEC_TIMEOUT_US = 10_000L
private const val EMPTY_TIME = -1L