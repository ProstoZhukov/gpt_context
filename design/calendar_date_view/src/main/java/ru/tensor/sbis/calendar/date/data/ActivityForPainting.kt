package ru.tensor.sbis.calendar.date.data

import android.graphics.Color
import android.os.Parcelable
import androidx.annotation.ColorInt
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.theme.res.SbisColor

/**
 * Схема раскрашивания блока дня.
 *
 * @param percentStart начальный процент раскрашиваниия
 * @param percentEnd конечный процент раскрашивания
 * @param color цвет раскрашивания
 *
 * @author ae.noskov
 */
@Parcelize
data class ActivityForPainting(
    val percentStart: Int,
    val percentEnd: Int,
    @Deprecated(
        "Будет удалено по https://dev.saby.ru/doc/f6fd193c-397a-4d1b-bbd2-d63ad7e3b75a",
        ReplaceWith("sbisColor"),
    )
    @ColorInt val color: Int = Color.MAGENTA,
    val sbisColor: SbisColor = SbisColor.Int(color),
) : Parcelable