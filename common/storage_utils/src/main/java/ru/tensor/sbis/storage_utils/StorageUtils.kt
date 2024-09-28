package ru.tensor.sbis.storage_utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.webkit.MimeTypeMap
import android.widget.Toast
import ru.tensor.sbis.common.util.CommonUtils.getFileName
import ru.tensor.sbis.common.util.CommonUtils.hasActivitiesForProcessingIntent
import ru.tensor.sbis.common.util.FileUriUtil
import ru.tensor.sbis.common.util.FileUtil
import java.io.File
import ru.tensor.sbis.common.R as RCommon

/**
 * Файл с утилитами выделенный из [CommonUtils]
 */

/** SelfDocumented */
fun openFromInternalStorage(filePath: String, context: Context) {
    val intent: Intent = createIntentOpenFileFromInternalStorage(context, filePath)
    if (hasActivitiesForProcessingIntent(context, intent)) {
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            //ignore
        }
        return
    }

    val fileName: String = getFileName(filePath) ?: filePath
    val message = context.getString(
        RCommon.string.file_can_not_be_open,
        fileName
    )
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

/** Подготовить интент для открытия файла */
fun createIntentOpenFileFromInternalStorage(
    context: Context,
    filePath: String,
): Intent {
    val fileUri = FileUriUtil.getUriForInternalFile(context, File(filePath))

    val file = File(filePath)
    val extension = FileUtil.getFileExtension(file, false)
    val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    val intent = Intent(Intent.ACTION_VIEW)
    intent.setDataAndType(fileUri, mimeType)
    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    return intent
}