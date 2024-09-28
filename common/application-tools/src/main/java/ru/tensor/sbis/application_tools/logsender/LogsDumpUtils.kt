package ru.tensor.sbis.application_tools.logsender

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Build.MANUFACTURER
import android.os.Build.MODEL
import android.provider.Settings
import com.google.firebase.FirebaseApp
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import ru.tensor.sbis.application_tools.R
import ru.tensor.sbis.application_tools.logsender.model.JsonHeader
import ru.tensor.sbis.application_tools.logsender.model.LogsData
import ru.tensor.sbis.common.util.date.DateFormatUtils
import ru.tensor.sbis.common.util.date.DateParseTemplate
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Date
import java.util.Scanner
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

private const val BUFFER_SIZE = 1024
private const val UTF8_BOM = "\uFEFF"

/**
 * Является ли текущий процесс служебным для отправки файлов дампов логов
 */
fun isLogsDumpSenderProcess(
    context: Context,
    getProcessName: (Context) -> String? = ::getProcessName
): Boolean = getProcessName(context)
    ?.endsWith(context.getString(R.string.application_tools_logs_dump_sender_process_name))
    ?: false

/**
 * Возвращает список файлов дампов логов
 *
 * @author us.bessonov
 */
internal fun getLogFiles(context: Context): List<File> {
    val crashLogsDir = context.cacheDir.listFiles { _, name -> name == "crashlogs" }?.firstOrNull()
        ?: return emptyList()
    return crashLogsDir.listFiles { _, name -> name.endsWith(".log") }?.toList().orEmpty()
}

/**
 * Создаёт на основе файлов дампов логов данные для формирования запросов по их отправке на облако
 */
internal fun getLogsData(context: Context, files: List<File>): List<LogsData> {
    val gson = GsonBuilder().create()
    return files.mapNotNull { file ->
        val scanner = Scanner(file).useDelimiter(UTF8_BOM)
        val headerJson = scanner.next()
        extractHeader(gson, headerJson)?.let {
            LogsData(it, getCreationDateString(file), context.packageName, getProjectId(), file, createZip(file))
        }
    }
}

/**
 * Позволяет формировать имя устройства при его отсутствии в заголовке файла логов.
 * Логика формирования заимствована из [https://git.sbis.ru/sbis/offline/-/blob/rc-22.4100/src/sbis-device-identification/java/ru/tensor/sbis/desktop/device_identification/DeviceInfoProviderImpl.java#L26-32]
 */
internal fun getDeviceName() = if (MODEL.lowercase().startsWith(MANUFACTURER.lowercase())) {
    capitalize(MODEL)
} else {
    "${capitalize(MANUFACTURER)} $MODEL"
}

/**
 * Позволяет формировать идентификатор устройства при его отсутствии в заголовке файла логов.
 * Логика формирования заимствована из [https://git.sbis.ru/sbis/offline/-/blob/rc-22.4100/src/sbis-device-identification/java/ru/tensor/sbis/desktop/device_identification/DeviceInfoProviderImpl.java#L21-24]
 */
@SuppressLint("HardwareIds")
internal fun getDeviceId(context: Context): String {
    val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

    @Suppress("DEPRECATION")
    val deviceId = UUID(androidId.hashCode().toLong(), Build.SERIAL.hashCode().toLong().shl(32))
    return deviceId.toString()
}

private fun extractHeader(gson: Gson, json: String) = try {
    gson.fromJson(json, JsonHeader::class.java)
} catch (ex: Exception) {
    Timber.e("Cannot create JSON header model")
    null
}

private fun getCreationDateString(file: File) =
    DateFormatUtils.format(Date(file.lastModified()), DateParseTemplate.WITH_LONG_MILLISECONDS_NO_TIMEZONE.pattern)

private fun getProjectId() = FirebaseApp.getInstance().options.projectId.orEmpty()

private fun capitalize(string: String) = StringBuilder().apply {
    var capitalizeNext = true
    string.forEach {
        when {
            capitalizeNext && it.isLetter() -> {
                capitalizeNext = false
                append(it.uppercaseChar())
                return@forEach
            }
            it.isWhitespace() -> capitalizeNext = true
        }
        append(it)
    }
}.toString()

private fun createZip(logs: File): File {
    val path = "${logs.parent}/${logs.nameWithoutExtension}.zip"
    if (File(path).exists()) return File(path)

    val fileOutputStream = FileOutputStream(path)
    val zipOutputStream = ZipOutputStream(fileOutputStream)
    val fileInputStream = FileInputStream(logs)
    zipOutputStream.putNextEntry(ZipEntry(logs.name))

    val bytes = ByteArray(BUFFER_SIZE)
    var length: Int
    do {
        length = fileInputStream.read(bytes)
        if (length < 0) break
        zipOutputStream.write(bytes, 0, length)
    } while (true)

    zipOutputStream.close()
    fileInputStream.close()
    fileOutputStream.close()
    return File(path)
}