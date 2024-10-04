package ru.tensor.sbis.design.message_panel.recorder_common.audio_record

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaRecorder
import android.net.Uri
import android.os.Handler
import android.os.Looper
import ru.tensor.sbis.design.message_panel.decl.record.AudioWaveformHelper
import ru.tensor.sbis.design.message_panel.recorder_common.audio_record.AudioRecordError.PreparingError
import ru.tensor.sbis.design.message_panel.recorder_common.audio_record.AudioRecordError.RecordingError
import ru.tensor.sbis.design.message_panel.recorder_common.utils.DispatchQueue
import ru.tensor.sbis.design.message_panel.recorder_common.utils.MediaMuxerWrapper
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.sqrt

/**
 * Компонент записи аудио.
 *
 * @property config настройки записи аудио.
 * @property waveformHelper вспомогательная реализация для построения осциллограммы.
 *
 * @author vv.chekurda
 */
class AudioRecorder(
    private val config: AudioRecordConfig = AudioRecordConfig(),
    private val waveformHelper: AudioWaveformHelper? = null
) {

    data class AudioRecordConfig(
        val audioSource: Int = MediaRecorder.AudioSource.DEFAULT,
        val sampleRateHz: Int = AUDIO_FORMAT_SAMPLE_RATE_HZ,
        val channelConfig: Int = AudioFormat.CHANNEL_IN_MONO,
        val channelsCount: Int = AUDIO_FORMAT_CHANNELS_COUNT,
        val audioMimeType: String = MediaFormat.MIMETYPE_AUDIO_FLAC,
        val dataFormat: Int = AudioFormat.ENCODING_PCM_16BIT,
        val formatProfileType: Int = MediaCodecInfo.CodecProfileLevel.AACObjectHE,
        val formatBitRate: Int = AUDIO_FORMAT_BITRATE,
        val minRecordDurationSec: Int = DEFAULT_MIN_RECORD_DURATION_SECONDS,
        val maxRecordDurationSec: Int = DEFAULT_MAX_RECORD_DURATION_SECONDS
    )

    private var mediaMuxer: MediaMuxerWrapper? = null
    private var trackIndex: Int = -1

    private var mediaCodec: MediaCodec? = null
    @Volatile
    private var codecBufferInfo = MediaCodec.BufferInfo()
    private var recordStartTime: Long = -1L
    val durationSeconds: Int
        get() = ((System.currentTimeMillis() - recordStartTime) / 1000).toInt()

    private var audioRecord: AudioRecord? = null

    private val minBufferSize = with(config) {
        AudioRecord.getMinBufferSize(sampleRateHz, channelConfig, dataFormat)
            .takeIf { it > 0 }
    } ?: AUDIO_RECORD_DEFAULT_BUFFER_SIZE
    private val recordBufferSize = minBufferSize / 2
    private var recordBuffers = ConcurrentLinkedQueue<ByteBuffer>()
    private val recordBuffer: ByteBuffer
        get() = recordBuffers.poll()
            ?: createRecordBuffer(recordBufferSize)

    private val recordQueue = DispatchQueue(AUDIO_RECORD_QUEUE_THREAD_NAME)
        .apply { priority = Thread.MAX_PRIORITY }
    private val encodeQueue = DispatchQueue(AUDIO_ENCODE_QUEUE_THREAD_NAME)
        .apply { priority = Thread.MAX_PRIORITY }
    private val mainHandler: Handler by lazy { Handler(Looper.getMainLooper()) }

    private var waveformFile: File? = null
    private var waveformStream: FileOutputStream? = null
    private val waveformDataByteArray = ByteArray(recordBufferSize)

    /**
     * Признак готовности компонента к началу записи.
     */
    @Volatile
    var isPrepared: Boolean = false
        private set

    /**
     * Признак процесса записи.
     */
    @Volatile
    var isRecording: Boolean = false
        private set

    /**
     * Слушатель процесса записи.
     */
    var recordListener: AudioRecordListener? = null

    /**
     * Подготовить компонент к началу записи.
     */
    fun prepare(muxer: MediaMuxerWrapper) {
        recordQueue.cleanupQueue()
        encodeQueue.cleanupQueue()
        isRecording = false
        isPrepared = false

        recordQueue.post {
            audioRecord?.stop()
            try {
                prepareRecorder(muxer)
                waveformFile = waveformHelper?.createTempFile(WAVEFORM_FILE_EXTENSION)?.also { file ->
                    waveformStream = FileOutputStream(file)
                }
                isPrepared = true
            } catch (e: Exception) {
                Timber.e(e)
                finishEncoder()
                clearWaveform()
                runOnUiThread { recordListener?.onError(PreparingError(e)) }
            }
        }
    }

    /**
     * Начать запись.
     */
    fun startRecording() {
        recordQueue.post {
            if (isRecording) return@post
            isRecording = true

            if (!isPrepared) {
                cancelRecording()
                Timber.e("${javaClass.simpleName} is not prepared")
            }
            try {
                checkNotNull(audioRecord).startRecording()
                checkNotNull(mediaCodec).start()

                recordStartTime = System.currentTimeMillis()
                runOnUiThread { recordListener?.onRecordStarted() }
                recordAudio()
            } catch (e: Exception) {
                isPrepared = false
                isRecording = false
                recordStartTime = -1L
                finishEncoder()
                Timber.e(e)
                runOnUiThread { recordListener?.onError(RecordingError(e)) }
            }
        }
    }

    /**
     * Остановить запись.
     */
    fun stopRecording() {
        when {
            !isRecording -> {
                clearWaveform()
                recordListener?.onRecordCanceled()
            }
            durationSeconds < config.minRecordDurationSec -> {
                cancelRecording()
            }
            else -> {
                recordQueue.postDelayed(STOP_RECORDER_DELAY_MS) {
                    try {
                        audioRecord?.stop()
                    } catch (ex: IllegalStateException) {
                        // Recorder is not initialized
                        Timber.e(ex)
                    }
                    isRecording = false
                    isPrepared = false
                }
            }
        }
    }

    /**
     * Отменить запись.
     */
    fun cancelRecording() {
        if (isRecording) {
            isRecording = false
            isPrepared = false
            recordStartTime = -1L

            val muxer = mediaMuxer
            audioRecord?.release()
            audioRecord = null
            finishEncoder()
            clearWaveform()
            muxer?.resultFile?.delete()
        }
        recordListener?.onRecordCanceled()
    }

    /**
     * Очистить компонент.
     */
    fun release() {
        recordListener = null
        cancelRecording()
        recordQueue.recycle()
        encodeQueue.recycle()
    }

    @SuppressLint("MissingPermission")
    private fun prepareRecorder(muxer: MediaMuxerWrapper) {
        mediaMuxer = muxer
        with(config) {
            val audioFormat = MediaFormat.createAudioFormat(audioMimeType, sampleRateHz, channelsCount).apply {
                setInteger(MediaFormat.KEY_AAC_PROFILE, formatProfileType)
                setInteger(MediaFormat.KEY_BIT_RATE, formatBitRate)
                setInteger(MediaFormat.KEY_CHANNEL_MASK, channelConfig)
            }
            mediaCodec = MediaCodec.createEncoderByType(audioMimeType).apply {
                codecBufferInfo = MediaCodec.BufferInfo()
                trackIndex = -1
                configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            }

            audioRecord = AudioRecord(audioSource, sampleRateHz, channelConfig, dataFormat, minBufferSize)

            recordBuffers.clear()
            repeat(RECORD_BUFFERS_START_COUNT) {
                recordBuffers.add(createRecordBuffer(recordBufferSize))
            }
        }
    }

    private fun recordAudio() {
        val audioRecord = audioRecord ?: return
        if (isRecording && durationSeconds >= config.maxRecordDurationSec) {
            stopRecording()
        }
        val buffer = recordBuffer
        buffer.clear()
        val length = audioRecord.read(buffer, buffer.capacity())

        if (length > 0) {
            val presentationTimeUs = System.nanoTime() / 1000
            encodeQueue.post {
                try {
                    encode(buffer, length, presentationTimeUs)
                    writeWaveformData(buffer, length)
                } catch (e: IllegalStateException) {
                    Timber.w(e)
                }
                recordQueue.post { recordBuffers.add(buffer) }
            }
            updateVolumeAmplitude(buffer, length)
            recordQueue.post(::recordAudio)
        } else {
            recordBuffers.add(buffer)
            encodeQueue.post { finishRecording() }
        }
    }

    private fun updateVolumeAmplitude(buffer: ByteBuffer, length: Int) {
        recordListener?.let { listener ->
            var sum = 0.0f
            try {
                repeat(length / 2) {
                    val peak = buffer.short
                    sum += peak * peak
                }
            } catch (ignore: BufferUnderflowException) {}

            val amplitude = sqrt(sum / length / 2)
            runOnUiThread { listener.onVolumeAmplitudeChanged(amplitude / AMPLITUDE_SCALE) }
        }
    }

    private fun encode(buffer: ByteBuffer, length: Int, presentationTimeUs: Long) {
        val mediaMuxer = mediaMuxer ?: return
        val mediaCodec = mediaCodec ?: return

        val inputBufferIndex = mediaCodec.dequeueInputBuffer(MEDIA_CODEC_TIMEOUT_US)
        mediaCodec.getInputBuffer(inputBufferIndex)?.apply {
            clear()
            buffer.limit(length)
            buffer.position(0)
            put(buffer)
        } ?: return
        mediaCodec.queueInputBuffer(inputBufferIndex, 0, length, presentationTimeUs, 0)

        while (true) {
            val status = mediaCodec.dequeueOutputBuffer(codecBufferInfo, MEDIA_CODEC_TIMEOUT_US)
            when {
                status == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                    trackIndex = mediaMuxer.addTrack(mediaCodec.outputFormat)
                    mediaMuxer.start()
                }
                status >= 0 -> {
                    val outputData = mediaCodec.getOutputBuffer(status)?.apply {
                        position(codecBufferInfo.offset)
                        limit(codecBufferInfo.offset + codecBufferInfo.size)
                    }

                    if (codecBufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0) {
                        codecBufferInfo.size = 0
                    }

                    if (outputData != null) {
                        mediaMuxer.writeSampleData(trackIndex, outputData, codecBufferInfo)
                    }

                    mediaCodec.releaseOutputBuffer(status, false)
                }
                status == MediaCodec.INFO_TRY_AGAIN_LATER -> break
                else -> continue
            }
        }
    }

    private fun finishRecording() {
        isRecording = false
        isPrepared = false
        val muxer = mediaMuxer
        finishEncoder()
        val waveformData = if (muxer?.resultFile != null) {
            val recordDuration = durationSeconds
            val waveform = getWaveform()
            clearWaveform()
            recordDuration to waveform
        } else {
            0 to ByteArray(0)
        }
        runOnUiThread {
            muxer?.resultFile?.let { file ->
                recordListener?.onRecordCompleted(file, waveformData.first, waveformData.second)
            } ?: recordListener?.onRecordCanceled()
        }
    }

    private fun finishEncoder() {
        mediaMuxer?.stop()
        mediaMuxer = null
        try {
            mediaCodec?.stop()
            mediaCodec = null
        } catch (ignore: Exception) {}
        audioRecord?.release()
        audioRecord = null
        trackIndex = -1
    }

    private fun writeWaveformData(buffer: ByteBuffer, length: Int) {
        waveformStream?.apply {
            try {
                buffer.position(0)
                buffer.limit(length)
                if (length >= buffer.remaining()) {
                    buffer.get(waveformDataByteArray, 0, length)
                    write(waveformDataByteArray, 0, length)
                } else {
                    Timber.e("WTF, length = $length, position = ${buffer.position()}, limit = ${buffer.limit()}")
                }
            } catch (ex: Exception) {
                Timber.e(ex)
            }
        }
    }

    private fun getWaveform(): ByteArray {
        val helper = waveformHelper ?: return byteArrayOf()
        val file = waveformFile ?: return byteArrayOf()
        return helper.getWaveform(Uri.fromFile(file))
    }

    private fun clearWaveform() {
        waveformFile?.delete()
        waveformFile = null
        try {
            waveformStream?.close()
        } catch (ignore: IOException) { }
        waveformStream = null
    }

    private fun createRecordBuffer(size: Int): ByteBuffer =
        ByteBuffer.allocateDirect(size).apply {
            order(ByteOrder.nativeOrder())
            MediaFormat.KEY_BIT_RATE
        }

    private fun runOnUiThread(action: () -> Unit) {
        mainHandler.post(action)
    }
}

private const val AUDIO_RECORD_QUEUE_THREAD_NAME = "audioRecordQueue"
private const val AUDIO_ENCODE_QUEUE_THREAD_NAME = "audioEncodeQueue"
private const val AUDIO_FORMAT_SAMPLE_RATE_HZ = 24_000
private const val AUDIO_FORMAT_BITRATE = 32 * 1024
private const val AUDIO_FORMAT_CHANNELS_COUNT = 1
private const val AUDIO_RECORD_DEFAULT_BUFFER_SIZE = 1280
private const val MEDIA_CODEC_TIMEOUT_US = 10000L
private const val RECORD_BUFFERS_START_COUNT = 5
private const val WAVEFORM_FILE_EXTENSION = "pcm"
private const val DEFAULT_MIN_RECORD_DURATION_SECONDS = 1
private const val DEFAULT_MAX_RECORD_DURATION_SECONDS = 3 * 60

/**
 * Эмперическая величина отложенной остановки записи, чтобы дозаписывать окончание последней буквы слова.
 * Остановка [AudioRecorder] происходит примерно через 60 мс после вызова окончания записи c UI трэда,
 * чего иногда не хватает для записи послезвучия последней буквы последнего слова.
 */
private const val STOP_RECORDER_DELAY_MS = 50L

/**
 * Масштаб амплитуды громкости для кнопки записи.
 */
private const val AMPLITUDE_SCALE = 1800