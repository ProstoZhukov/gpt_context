package ru.tensor.sbis.design.theme.zen

import android.graphics.Color
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.theme.res.SbisColor

/**
 * Цвета Дзен Темы
 *
 * https://n.sbis.ru/article/fecfb715-c8d6-4616-a7a8-ce8cc27c0f52#toc_9d3a2e69-dda2-41f1-a14c-fd93a0f75271
 * @author da.zolotarev
 */
enum class ZenThemeElementsColors(
    val defaultColor: SbisColor,
    val secondaryColor: SbisColor,
    val linkColor: SbisColor,
    val labelColor: SbisColor,
    val unaccentedColor: SbisColor,
    val readonlyColor: SbisColor,
    val contrastColor: SbisColor,
    val translucentButtonColor: SbisColor,
    val translucentActiveButtonColor: SbisColor,
    val flatIndicatorColor: SbisColor,
    val flatIndicatorActiveColor: SbisColor,
) {
    LIGHT(
        defaultColor = SbisColor(Color.WHITE),
        secondaryColor = SbisColor(Color.WHITE),
        linkColor = SbisColor(Color.WHITE),
        labelColor = SbisColor("#CCFFFFFF"),
        unaccentedColor = SbisColor("#CCFFFFFF"),
        readonlyColor = SbisColor("#4DFFFFFF"),
        contrastColor = SbisColor(Color.BLACK),
        translucentButtonColor = SbisColor("#B3FFFFFF"),
        translucentActiveButtonColor = SbisColor("#7FFFFFFF"),
        flatIndicatorColor = SbisColor("#1AFFFFFF"),
        flatIndicatorActiveColor = SbisColor("#99FFFFFF"),
    ),
    DARK(
        defaultColor = SbisColor(Color.BLACK),
        secondaryColor = SbisColor(Color.BLACK),
        linkColor = SbisColor(Color.BLACK),
        labelColor = SbisColor("#CC000000"),
        unaccentedColor = SbisColor("#CC000000"),
        readonlyColor = SbisColor("#4D000000"),
        contrastColor = SbisColor(Color.WHITE),
        translucentButtonColor = SbisColor("#7F000000"),
        translucentActiveButtonColor = SbisColor("#4D000000"),
        flatIndicatorColor = SbisColor("#1A000000"),
        flatIndicatorActiveColor = SbisColor("#99000000"),
    );

    companion object {
        /** @SelfDocumented */
        fun getColors(@ColorInt dominantColor: Int): ZenThemeElementsColors {
            // Если изображение темное, значит элементы светлые, и наоборот.
            return if (isDarkImage(dominantColor)) LIGHT else DARK
        }

        /**
         * Вычисление яркости цвета на основе алгоритма веба.
         * https://git.sbis.ru/socnet/colorimeter/-/blob/rc-24.5100/colorimeter/implementation/colorimeter.cpp#L81
         */
        private fun isDarkImage(dominantColor: Int): Boolean {
            val red = Color.red(dominantColor)
            val green = Color.green(dominantColor)
            val blue = Color.blue(dominantColor)

            return (0.299 * red + 0.587 * green + 0.114 * blue) < 255 * 0.5
        }
    }
}