/**
 * Набор утилит для формирования дзен темы.
 *
 * @author da.zolotarev
 */
package ru.tensor.sbis.design.theme.zen

import android.graphics.Bitmap
import android.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Получить [ZenThemeModel] из [bitmap].
 *
 * Необходимо вызывать на бэкграунд потоке, так как внутри считается цвет циклом по битмапе.
 */
suspend fun getZenTheme(bitmap: Bitmap): ZenThemeModel {
    return withContext(Dispatchers.Default) {
        val (dominantColor, complementaryColor) = getDominantAndComplementaryColor(bitmap)
        ZenThemeModel(
            ZenThemeElementsColors.getColors(dominantColor),
            dominantColor,
            complementaryColor
        )
    }
}

/**
 * Метод нахождения "среднего" цвета изображения + комплиментарный цвет.
 * https://stackoverflow.com/questions/12408431/how-can-i-get-the-average-color-of-an-image
 */
private fun getDominantAndComplementaryColor(bitmap: Bitmap): Pair<Int, Int> {

    val resizedBitmap = bitmap

    var redBucket = 0
    var greenBucket = 0
    var blueBucket = 0
    var alphaBucket = 0

    val hasAlpha = resizedBitmap.hasAlpha()
    val pixelCount = resizedBitmap.width * resizedBitmap.height
    val pixels = IntArray(pixelCount)
    resizedBitmap.getPixels(pixels, 0, resizedBitmap.width, 0, 0, resizedBitmap.width, resizedBitmap.height)

    var y = 0
    val h = resizedBitmap.height
    while (y < h) {
        var x = 0
        val w = resizedBitmap.width
        while (x < w) {
            val color = pixels[x + y * w] // x + y * width
            redBucket += (color shr 16) and 0xFF // Color.red
            greenBucket += (color shr 8) and 0xFF // Color.greed
            blueBucket += (color and 0xFF) // Color.blue
            if (hasAlpha) alphaBucket += (color ushr 24) // Color.alpha
            x++
        }
        y++
    }

    return Color.argb(
        if ((hasAlpha)) (alphaBucket / pixelCount) else 255,
        redBucket / pixelCount,
        greenBucket / pixelCount,
        blueBucket / pixelCount
    ) to Color.argb(
        if ((hasAlpha)) (alphaBucket / pixelCount) else 255,
        255 - (redBucket / pixelCount),
        255 - (greenBucket / pixelCount),
        255 - (blueBucket / pixelCount)
    )
}