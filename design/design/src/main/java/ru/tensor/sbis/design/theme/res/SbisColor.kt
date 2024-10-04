package ru.tensor.sbis.design.theme.res

import android.content.Context
import android.graphics.Color
import android.os.Parcelable
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.theme.global_variables.IconColor
import ru.tensor.sbis.design.theme.utils.getThemeColorInt

/**
 * Модель представления цветов.
 *
 * @author da.zolotarev
 */
@Parcelize
sealed interface SbisColor : Parcelable {

    /**
     * Получить цвет.
     */
    @ColorInt
    fun getColor(context: Context): kotlin.Int

    /**
     * Цвет, заданный кодом.
     */
    @Parcelize
    data class Int(
        @ColorInt
        val colorInt: kotlin.Int
    ) : SbisColor {
        override fun getColor(context: Context) = colorInt
    }

    /**
     * Цвет, заданный ресурсом.
     */
    @Parcelize
    data class Res(
        @ColorRes
        val colorRes: kotlin.Int,
    ) : SbisColor {
        override fun getColor(context: Context) = if (colorRes != ResourcesCompat.ID_NULL)
            ResourcesCompat.getColor(context.resources, colorRes, context.theme) else Color.MAGENTA
    }

    /**
     * Цвет, заданный глобальной переменной IconColor.
     */
    @Parcelize
    data class Icon(
        val color: IconColor,
    ) : SbisColor {
        override fun getColor(context: Context): kotlin.Int = color.getValue(context)
    }

    /**
     * Цвет, заданный кодом.
     */
    @Parcelize
    data class Attr(
        @AttrRes
        val colorAttr: kotlin.Int
    ) : SbisColor {
        override fun getColor(context: Context) = context.getThemeColorInt(colorAttr)
    }

    /**
     * Цвет отсутствует.
     */
    @Parcelize
    object NotSpecified : SbisColor {
        override fun getColor(context: Context) = Color.MAGENTA
    }
}

/** @SelfDocumented */
fun SbisColor(@ColorInt colorHex: Int) = SbisColor.Int(colorHex)

/** @SelfDocumented */
fun SbisColor(colorHex: String) = SbisColor.Int(Color.parseColor(colorHex))

/** @SelfDocumented */
fun createColor(@ColorInt colorHex: Int) = SbisColor.Int(colorHex)
