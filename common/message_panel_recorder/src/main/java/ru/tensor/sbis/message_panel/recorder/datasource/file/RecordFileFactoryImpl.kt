package ru.tensor.sbis.message_panel.recorder.datasource.file

import android.net.Uri
import ru.tensor.sbis.common.util.date.DateFormatTemplate
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author vv.chekurda
 * Создан 8/8/2019
 */
internal class RecordFileFactoryImpl(
    private val fileNameTemplate: String,
    private val cacheDir: String
) : RecordFileFactory {

    private val dateFormatter = SimpleDateFormat(DateFormatTemplate.ONLY_DIGITS.template, Locale.getDefault())

    override fun createFile(): File {
        val path = "$cacheDir/${getFileName(fileNameTemplate)}"
        return File(path).apply {
            if (exists() && !delete()) {
                Timber.w("Unable to delete file to record new one ($path)")
            }
        }
    }

    override fun fileToUri(file: File): Uri = Uri.fromFile(file)

    private fun getFileName(template: String) = String.format(template, dateFormatter.format(Date()))
}