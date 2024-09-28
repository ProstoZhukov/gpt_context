package ru.tensor.sbis.communicator.communicator_share_messages.utils

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset

/**
 * Инструмент для получения информации об оффлайн ссылках при шаринге.
 * Необходимо для корректного шаринга ссылок из гугла, т.к. у них есть фича оффлайн режима, по которому
 * при шаринге мы делимся не ссылкой, а файлом страницы.
 *
 * @author dv.baranov
 */
class OfflineLinksUtil(private val context: Context) {

    /**
     * Получить ссылку для шаринга в оффлайн.
     *
     * @param uri - uri файла.
     * @return ссылка на ресурс в виде строки.
     */
    suspend fun getLinkFromOfflineFile(uri: Uri): String {
        val stringBuilder = StringBuilder()
        val stream = context.contentResolver.openInputStream(uri)
        val bufferedReader = BufferedReader(InputStreamReader(stream, Charset.forName("UTF-8")))
        var line = ""
        while (true) {
            line = withContext(Dispatchers.IO) {
                bufferedReader.readLine()
            }
            if (line.startsWith(LINK_PREFIX)) {
                stringBuilder.appendLine(line.removePrefix(LINK_PREFIX))
                break
            }
            if (line == null) break
        }
        return stringBuilder.toString().trim()
    }
}

private const val LINK_PREFIX = "Snapshot-Content-Location:"