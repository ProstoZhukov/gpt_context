package ru.tensor.sbis.design.message_panel.decl.record

import android.net.Uri
import androidx.annotation.WorkerThread
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.io.File

/**
 * Интерфейс помощника для получения осциллограмм аудиофайлов.
 *
 * @author vv.chekurda
 */
interface AudioWaveformHelper {

    /**
     * Создать временный файл расширения [fileExtension], чтобы писать в него данные для построения осциллограммы.
     */
    fun createTempFile(fileExtension: String): File

    /**
     * Получить осциллограмму для аудиофайла.
     */
    @WorkerThread
    fun getWaveform(fileUri: Uri): ByteArray

    /**
     * Поставщик [AudioWaveformHelper].
     */
    fun interface Provider : Feature {

        fun provideAudioWaveformHelper(): AudioWaveformHelper
    }
}