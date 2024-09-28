package ru.tensor.sbis.design.theme.res

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.DimenRes
import androidx.annotation.Dimension
import ru.tensor.sbis.design.theme.utils.getDimen
import ru.tensor.sbis.design.theme.utils.getDimenPx
import ru.tensor.sbis.design.util.dpToDimension
import ru.tensor.sbis.design.util.dpToPx

/**
 * Модель представления размера.
 *
 * @author da.zolotarev
 */
sealed interface SbisDimen {

    /**
     * Получить размер.
     */
    fun getDimen(context: Context): Float

    /**
     * Получить округленный до пикселей.
     */
    fun getDimenPx(context: Context): Int

    /**
     * Размер, заданный значением в пикселах.
     */
    class Px(
        @androidx.annotation.Px val dimenPx: Int
    ) : SbisDimen {
        override fun getDimen(context: Context) = dimenPx.toFloat()
        override fun getDimenPx(context: Context) = dimenPx

    }

    /**
     * Размер, заданный значением в dp.
     */
    class Dp(
        @Dimension(unit = Dimension.DP) val dimenDp: Int
    ) : SbisDimen {
        override fun getDimen(context: Context) = context.dpToDimension(dimenDp)
        override fun getDimenPx(context: Context) = context.dpToPx(dimenDp)
    }

    /**
     * Размер, заданный ресурсом.
     */
    class Res(
        @DimenRes
        val stringRes: Int,
    ) : SbisDimen {
        override fun getDimen(context: Context) = context.resources.getDimension(stringRes)
        override fun getDimenPx(context: Context) = context.resources.getDimensionPixelSize(stringRes)
    }

    /**
     * Размер, заданный атрибутом.
     */
    class Attr(
        @AttrRes
        val attr: Int,
    ) : SbisDimen {
        override fun getDimen(context: Context) = context.getDimen(attr)
        override fun getDimenPx(context: Context) = context.getDimenPx(attr)
    }

}
