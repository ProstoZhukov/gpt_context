package ru.tensor.sbis.common.util

import android.app.Application
import android.content.Context

/**
 * Подсчитывает объем занимаемого места приложением на диске. В подсчете учитывается кеш и содержимое папки data приложения
 * @receiver Application класс приложения
 * @return Long объем занимаемого места приложением на диске в байтах
 */
fun calculateDiskSpaceUsage(application: Application): String {
    val spaceUsed = (sizeOfExternalCacheDirs(application)
            + sizeOfDataDir(application))
    return convertMegabytesToInterval(spaceUsed / 1024 / 1024)
}

/**
 * Переводит размер в мегабайтах в строковое представление интервала в мегабайтах, например 132 = 0-199Mb, 1320 = 1250-1499Mb
 * @param mb размер в мегабайтах
 * @return строковое представление интервала, в которое попадает размер
 */
fun convertMegabytesToInterval(mb: Long): String {
    val intervalSize = when {
        mb < 200 -> 200
        mb < 1000 -> 100
        else -> 250
    }
    val from = (mb / intervalSize) * intervalSize
    val to = from + intervalSize - 1
    return "$from-${to}MB"
}

private fun sizeOfDataDir(context: Context): Long {
    val dataDir = context.filesDir.parentFile ?: return 0
    return FileUtil.getFolderSize(dataDir)
}

private fun sizeOfExternalCacheDirs(context: Context): Long {
    return context
        .externalCacheDirs
        .filterNotNull()
        .sumOf { FileUtil.getFolderSize(it) }
}