package ru.tensor.sbis.richtext.util

import android.content.Context
import ru.tensor.sbis.common.util.FileUriUtil
import java.io.File
import java.util.regex.Pattern

/**
 * Утилита по работе с локальными файлами богатого текста на диске.
 *
 * @author am.boldinov
 */
object FileUtil {

    private val BASE64_PATTERN = Pattern.compile("^data:image/.*;base64")

    @Volatile
    private var rootCachePath: String? = null

    /**
     * Возвращает корневую дирректорию приложения для хранения кеша.
     */
    @JvmStatic
    fun getRootCachePath(context: Context): String {
        return rootCachePath ?: context.cacheDir.absolutePath.also {
            rootCachePath = it
        }
    }

    /**
     * Возвращает существующую или создает новую дирректорую с названием [folderName] для хранения файлов.
     * Рекомендуется для каждого вида файла использовать отдельные дирректории.
     */
    @JvmStatic
    fun getFolderCachePath(context: Context, folderName: String): String {
        val folder = File(getRootCachePath(context) + "/$folderName").apply {
            mkdirs()
        }
        return folder.absolutePath
    }

    /**
     * Создает путь до файла со схемой "file://".
     * Необходимо использовать для формирования ссылки на превью локального изображения.
     */
    @JvmStatic
    fun buildFileSchemePath(path: String): String {
        return FileUriUtil.SCHEME_FILE + path
    }

    /**
     * Является ли содержимое [source] base64 изображением.
     */
    @JvmStatic
    fun isBase64Image(source: String): Boolean {
        return BASE64_PATTERN.matcher(source).find()
    }
}