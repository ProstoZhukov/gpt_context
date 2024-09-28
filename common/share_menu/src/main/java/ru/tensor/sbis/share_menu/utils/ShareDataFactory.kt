package ru.tensor.sbis.share_menu.utils

import android.content.Intent
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.common.util.FileUriUtil
import ru.tensor.sbis.toolbox_decl.share.QUICK_SHARE_KEY
import ru.tensor.sbis.toolbox_decl.share.ShareData

/**
 * Фабрика для создания модели с данными для "поделиться".
 * @see ShareData
 *
 * @author vv.chekurda
 */
internal object ShareDataFactory {

    /**
     * Получить ключ для быстрого шаринга.
     */
    val Intent.quickShareKey: String?
        get() = getStringExtra(QUICK_SHARE_KEY)

    /**
     * Создать [ShareData] по [intent].
     */
    fun createShareData(intent: Intent?): ShareData? =
        intent
            ?.takeIf(ShareDataFactory::isActionSend)
            ?.let {
                val files = getFilesFromIntent(intent)
                val text = getTextFromIntent(intent)
                return@let when {
                    files.isEmpty() && text.isEmpty() -> null
                    files.firstOrNull()?.contains(OFFLINE_CONTENT) == true -> {
                        ShareData.OfflineLink(
                            files = files,
                            text = text
                        )
                    }
                    files.firstOrNull()?.contains(CONTACT_CONTENT) == true -> {
                        ShareData.Contacts(
                            files = files,
                            text = text
                        )
                    }
                    files.isNotEmpty() -> {
                        ShareData.Files(
                            files = files,
                            text = text
                        )
                    }
                    else -> {
                        ShareData.Text(text = text)
                    }
                }
            }

    private fun isActionSend(intent: Intent): Boolean =
        intent.action == Intent.ACTION_SEND || intent.action == Intent.ACTION_SEND_MULTIPLE

    private fun getTextFromIntent(intent: Intent): CharSequence {
        var text: CharSequence? = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (text.isNullOrEmpty()) {
            if (intent.clipData?.itemCount != 0) {
                text = intent.clipData?.getItemAt(0)?.text
            }
        }
        return text ?: StringUtils.EMPTY
    }

    private fun getFilesFromIntent(intent: Intent): List<String> =
        FileUriUtil.getUrisFromIntent(intent).map { uri -> uri.toString() }
}

private const val CONTACT_CONTENT = "android.contacts"
private const val OFFLINE_CONTENT = "offline-cache"