/**
 * Helper для Samsung устройств
 *
 * @author ma.kolpakov
 * @since 01/26/2020
 */
@file:JvmName("SamsungHelper")

package ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.ResourcesCompat.ID_NULL
import ru.tensor.sbis.design.TypefaceManager
import timber.log.Timber
import java.io.File
import java.util.Locale

private const val SAMSUNG_KEY = "samsung"

/**
 * Получение шрифта на Samsung устройствах. По-другому на Samsung устройствах по пути к файлу со
 * шрифтом (например /res/font/roboto_medium.ttf) нельзя получить Typeface
 */
internal fun getTypefaceByFontFilePathForSamsung(fontFilePath: String, context: Context): Typeface? {
    val fontPath = fontFilePath.split(File.separator.toRegex())

    if (fontPath.size > 1) {
        val fileFullName = fontPath[fontPath.size - 1]
        val fileName = fileFullName.substring(0, fileFullName.lastIndexOf("."))
        val folderName = fontPath[fontPath.size - 2]
        val resId = context.resources.getIdentifier(fileName, folderName, context.packageName)
        return if (resId != ID_NULL) {
            ResourcesCompat.getFont(context, resId)
        } else {
            Timber.e("Cannot get font resource by path $fontFilePath")
            // Typeface по fontFilePath получить не удастся, используем шрифт по умолчанию
            TypefaceManager.getRobotoRegularFont(context)
        }
    }

    return Typeface.create(fontFilePath, Typeface.NORMAL)
}

/**
 * Взято из TelecomConnectionService
 *
 * @return true - если запущенно устройство Samsung
 */
@SuppressLint("DefaultLocale")
internal fun isSamsungDevice(): Boolean {
    return Build.MANUFACTURER.lowercase(Locale.getDefault()).contains(SAMSUNG_KEY)
}