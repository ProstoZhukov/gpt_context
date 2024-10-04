package ru.tensor.sbis.hallscheme.v2.business.model.textconfig

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Настройки отображения текста на столе.
 *
 * @param size размер шрифта.
 * @param align выравнивание.
 * @param fontWeight насыщенность (обычный или жирный).
 * @param textDecoration оформление (подчёркнутый, перечёркнутый, всё сразу или ничего).
 * @param textStyle тип начертания (обычный или курсив).
 * @param color цвет.
 */
@Parcelize
data class HallSchemeTextConfig(
    val size: Int,
    val align: String,
    val fontWeight: HallSchemeFontWeight,
    val textDecoration: HallSchemeFontDecoration,
    val textStyle: HallSchemeFontStyle,
    val color: String?
) : Parcelable