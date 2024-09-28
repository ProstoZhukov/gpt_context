package ru.tensor.sbis.design.message_panel.recorder_common.utils

import ru.tensor.sbis.common.util.date.DateFormatTemplate
import ru.tensor.sbis.design.message_panel.decl.record.MediaFileFactory
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Реализация по-умолчанию для [MediaFileFactory].
 *
 * @property fileNameTemplate шаблон названия файлов.
 * @property cacheDir путь к кэшу, по которому будут создаваться новые файлы.
 *
 * @author vv.chekurda
 */
class DefaultMediaFileFactory(
    private val fileNameTemplate: String,
    private val cacheDir: String,
    private val fileFormat: String = Formats.MP4.value
) : MediaFileFactory {

    enum class Formats(val value: String) {
        FLAC("flac"),
        MP4("mp4")
    }

    private val dateFormatter = SimpleDateFormat(DateFormatTemplate.ONLY_DIGITS.template, Locale.getDefault())

    override fun createFile(): File {
        val path = "$cacheDir/${getFileName(fileNameTemplate)}"
        return File(path).apply {
            if (exists() && !delete()) {
                Timber.e("Unable to delete file to record new one ($path)")
            }
        }
    }

    private fun getFileName(template: String) = String.format(template, dateFormatter.format(Date()), fileFormat)
}