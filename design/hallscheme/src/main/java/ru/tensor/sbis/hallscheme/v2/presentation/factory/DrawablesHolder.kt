package ru.tensor.sbis.hallscheme.v2.presentation.factory

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import ru.tensor.sbis.design.utils.getThemeColor
import ru.tensor.sbis.hallscheme.R
import ru.tensor.sbis.hallscheme.v2.business.ChairType
import ru.tensor.sbis.hallscheme.v2.business.SofaPartType
import ru.tensor.sbis.hallscheme.v2.util.unsafeLazy

/**
 * Холдер для изображений стульев, частей диванов и и.п.
 */
internal class DrawablesHolder(private val context: Context) {

    // region Chairs
    private val chairFlatDrawablesMap: MutableMap<Pair<ChairType, Int>, Drawable?> = mutableMapOf()

    /**
     * Возвращает изображение стула для плоской темы.
     */
    fun getChairFlatDrawable(chairType: ChairType, color: Int) =
        chairFlatDrawablesMap.getOrPut(chairType to color) {
            AppCompatResources.getDrawable(context, chairType.iconResFlatName)?.mutate()?.apply { setTint(color) }
        }

    /**
     * Содержит изображения стульев объёмной темы.
     */
    val chair3dDrawablesMap: Map<ChairType, Drawable?> by unsafeLazy {
        ChairType.values().associateWith { AppCompatResources.getDrawable(context, it.iconRes3dName) }
    }

    private val sofaFlatDrawablesMap: MutableMap<Pair<SofaPartType, Int>, Drawable?> = mutableMapOf()

    /**
     * Возвращает изображение части дивана для плоской темы.
     */
    fun getSofaFlatDrawable(sofaPartType: SofaPartType, color: Int) =
        sofaFlatDrawablesMap.getOrPut(sofaPartType to color) {
            AppCompatResources.getDrawable(context, sofaPartType.iconResName)?.mutate()?.apply { setTint(color) }
        }

    /**
     * Содержит изображения частей дивана в объёмной теме.
     */
    val sofa3dDrawablesMap: Map<SofaPartType, Drawable?> by unsafeLazy {
        SofaPartType.values().associateWith { AppCompatResources.getDrawable(context, it.icon3dResName) }
    }


    // region Place
    private val placeColor: Int by unsafeLazy {
        ContextCompat.getColor(context, context.getThemeColor(R.attr.hall_scheme_place_default))
    }

    /**
     * Содержит изображение места в ряду.
     */
    val placeChairDrawable: Drawable? by unsafeLazy {
        AppCompatResources.getDrawable(context, R.drawable.hall_scheme_place_chair)?.apply { setTint(placeColor) }
    }

    /**
     * Содержит изображение дивана в ряду.
     */
    val placeSofaDrawable: Drawable? by unsafeLazy {
        AppCompatResources.getDrawable(context, R.drawable.hall_scheme_place_sofa)?.apply { setTint(placeColor) }
    }


    // region Decor
    private val decorFlatDrawables: MutableMap<Int, Drawable?> = mutableMapOf()
    private val decor3dDrawables: MutableMap<Int, Drawable?> = mutableMapOf()

    private val decorColor: Int by unsafeLazy {
        ContextCompat.getColor(context, context.getThemeColor(R.attr.hall_scheme_table_empty_contour))
    }

    /**
     * Возвращает изображение декора для плоской темы.
     */
    fun getDecorFlatDrawable(@DrawableRes drawableRes: Int): Drawable? =
        decorFlatDrawables.getOrPut(drawableRes) {
            AppCompatResources.getDrawable(context, drawableRes)?.apply { setTint(decorColor) }
        }

    /**
     * Возвращает изображение декора для объёмной темы.
     */
    fun getDecor3dDrawable(@DrawableRes drawableRes: Int): Drawable? =
        decor3dDrawables.getOrPut(drawableRes) {
            AppCompatResources.getDrawable(context, drawableRes)
        }


    // region Other
    /**
     * Содержит изображение фона для названия стола с готовыми блюдами.
     */
    val tableNameReadyBackground: Drawable? by unsafeLazy {
        AppCompatResources.getDrawable(context, R.drawable.hall_scheme_bg_table_name_ready)
    }

    /**
     * Содержит изображение иконки счёта.
     */
    val billDrawable: Drawable? by unsafeLazy {
        AppCompatResources.getDrawable(context, R.drawable.hall_scheme_bill_table)
    }
}