package ru.tensor.sbis.common.util

import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.webkit.MimeTypeMap
import timber.log.Timber
import java.io.File

/**
 * Вспомогательный класс, помогающий в сканировании файлов
 *
 * @author kv.martyshenko
 */
object ScanUtil {

    /**
     * Функция позволяет запустить сканирование файла.
     *
     * @param context
     * @param file целевой файл
     */
    @JvmStatic
    fun scanFile(context: Context, file: File) {
        if (!file.absolutePath.isNullOrEmpty()) {
            val extension = FileUtil.getFileExtension(file, false)
            val mimeType = MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(extension)
                ?.takeIf { it.isNotEmpty() } ?: "*/*"

            MediaScannerConnection
                .scanFile(context, arrayOf(file.absolutePath), arrayOf(mimeType), null)

            Uri.fromFile(file)?.let {
                sendMediaScanBroadcast(context, it)
            }
        }
    }

    private fun sendMediaScanBroadcast(context: Context, uri: Uri) {
        try {
            val mediaScanIntent =
                Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            mediaScanIntent.data = uri
            context.applicationContext.sendBroadcast(mediaScanIntent)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

}