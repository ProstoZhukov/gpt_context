package ru.tensor.sbis.communicator.communicator_navigation.navigation.utils

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import ru.tensor.sbis.common.util.addArgs
import ru.tensor.sbis.communicator.common.util.share.ConversationUtils
import ru.tensor.sbis.communicator.common.util.doIfNotNull
import java.util.*

/**
 * Добавить к намерению контент для шаринга
 */
internal fun Intent.mapShareContent(incomingIntent: Intent?, filesToShare: ArrayList<Uri>? = null) {
    val text = ConversationUtils.getTextToShare(incomingIntent)
    val files = filesToShare ?: ConversationUtils.getFilesToShare(incomingIntent)
    doIfNotNull(files) { putExtra(Intent.EXTRA_STREAM, files) }
    doIfNotNull(text) { putExtra(Intent.EXTRA_TEXT, text) }
}

internal fun Fragment.mapShareContent(incomingIntent: Intent?, filesToShare: ArrayList<Uri>? = null) {
    val text = ConversationUtils.getTextToShare(incomingIntent)
    val files = filesToShare ?: ConversationUtils.getFilesToShare(incomingIntent)
    addArgs {
        doIfNotNull(files) { putSerializable(Intent.EXTRA_STREAM, files) }
        doIfNotNull(text) { putSerializable(Intent.EXTRA_TEXT, text) }
    }
}
