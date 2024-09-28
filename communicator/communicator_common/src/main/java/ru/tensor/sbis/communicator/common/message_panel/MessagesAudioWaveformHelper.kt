package ru.tensor.sbis.communicator.common.message_panel

import android.net.Uri
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.util.date.DateFormatTemplate
import ru.tensor.sbis.communicator.common.ControllerHelper.checkExecutionTime
import ru.tensor.sbis.communicator.generated.MessageController
import ru.tensor.sbis.design.message_panel.decl.record.AudioWaveformHelper
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Реализация вспомогательного класса для получения осциллограм аудиофайлов.
 *
 * @author vv.chekurda
 */
internal class MessagesAudioWaveformHelper(
    private val cacheDir: String,
    private val messageController: DependencyProvider<MessageController>
) : AudioWaveformHelper {

    private val dateFormatter = SimpleDateFormat(DateFormatTemplate.ONLY_DIGITS.template, Locale.getDefault())
    private val fileName: String
        get() = String.format(AUDIO_WAVE_TEMP_FILE_NAME, dateFormatter.format(Date()))

    override fun createTempFile(fileExtension: String): File {
        val path = "$cacheDir/$fileName.$fileExtension"
        return File(path).apply {
            if (exists() && !delete()) {
                Timber.e("Unable to delete file to record waveform ($path)")
            }
        }
    }

    override fun getWaveform(fileUri: Uri): ByteArray {
        val file = fileUri.path?.let { File(it) }
        val fileSize = file?.length() ?: 0
        val nOut = WAVEFORM_SIZE
        // Во всех этих случаях контроллер выкинет эксепшен. Возвращаем пустую осциллограмму
        if (file == null || !file.exists() || fileSize == 0L || fileSize % 2 != 0L || nOut > fileSize / 2) {
            Timber.e("Invalid input data ${file?.absoluteFile}")
            return ByteArray(nOut)
        }
        return try {
            checkExecutionTime("MessageController.downsampleAudioDataFromFile") {
                messageController.get().downsampleAudioDataFromFile(file.absolutePath, nOut, true)
            }
        } catch (e: Throwable) {
            Timber.e(e)
            ByteArray(nOut)
        }
    }
}

private const val AUDIO_WAVE_TEMP_FILE_NAME = "audio_message_waveform - %s"
private const val WAVEFORM_SIZE = 340