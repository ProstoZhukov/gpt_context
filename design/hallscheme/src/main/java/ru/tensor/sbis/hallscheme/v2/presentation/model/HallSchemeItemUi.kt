package ru.tensor.sbis.hallscheme.v2.presentation.model

import android.content.Context
import android.graphics.BitmapShader
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import ru.tensor.sbis.hallscheme.R
import ru.tensor.sbis.hallscheme.v2.HallSchemeV2
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.HallSchemeItem
import java.lang.ref.WeakReference

/**
 * Минимальный слой для рисования эелемента схемы.
 */
internal const val MIN_DRAWING_LAYER_FOR_SCHEME_ITEM = 2F

/**
 * Абстракнтый класс для отображения элемента схемы зала.
 * @author aa.gulevskiy
 */
internal abstract class HallSchemeItemUi(val schemeItem: HallSchemeItem) {
    companion object {
        private val colors = mapOf(
            "white" to R.color.hall_scheme_white,
            "yellow" to R.color.hall_scheme_yellow,
            "green" to R.color.hall_scheme_green,
            "red" to R.color.hall_scheme_red,
            "grey" to R.color.hall_scheme_grey,
            "blue" to R.color.hall_scheme_blue,
            "black" to R.color.hall_scheme_black,
            "emerald" to R.color.hall_scheme_emerald,
            "violet" to R.color.hall_scheme_violet,
            "khaki" to R.color.hall_scheme_khaki,
            "lime" to R.color.hall_scheme_lime,
            "aquamarine" to R.color.hall_scheme_aquamarine,
            "turquoise" to R.color.hall_scheme_turquoise,
            "lightblue" to R.color.hall_scheme_lightblue,
            "purple" to R.color.hall_scheme_purple,
            "plum" to R.color.hall_scheme_plum,
            "rose" to R.color.hall_scheme_rose,
            "peach" to R.color.hall_scheme_peach
        )
    }

    /**@SelfDocumented*/
    protected var viewReference: WeakReference<View>? = null

    /**
     * Возвращает цвет по его названию или hex-представлению.
     */
    @ColorInt
    fun getHallSchemeColor(context: Context, colorName: String?): Int? {
        return when {
            colorName == null -> null
            colorName.startsWith('#') -> {
                var colorString = colorName
                if (colorString.length == 9) { // Цвет с прозрачностью '#XXXXXXXX'
                    /**
                     * Значение прозрачности в веб-цвете стоит в конце, поэтому переставляем его в начало.
                     */
                    colorString = colorString.slice(listOf(0, 7, 8, 1, 2, 3, 4, 5, 6))
                }
                try {
                    colorString.toColorInt()
                } catch(e: Exception) {
                    null
                }
            }
            else -> {
                val colorRes = colors[colorName]
                if (colorRes != null) ContextCompat.getColor(context, colorRes)
                else null
            }
        }
    }

    /**
     * Создаёт вью для плоской схемы и добавляет её в родительский лэйаут.
     */
    open fun draw(
        viewGroup: ViewGroup,
        onItemClickListener: HallSchemeV2.OnHallSchemeItemClickListener? = null
    ) {
        val view = getView(viewGroup)
        viewReference = WeakReference(view)
        setElementZ(view)
        viewGroup.addView(view)
    }

    /**
     * Установка Z-координаты (вертикальный слой).
     */
    protected fun setElementZ(view: View) {
        // Минимальный слой для элементов, получаемый с контроллера - 0.
        // увеличиваем на 2, чтобы элементы размещались по слоям следующим образом:
        // 0  - фон
        // 1  - обводка для столов
        // 2+ - все остальные элементы
        view.z = schemeItem.z + MIN_DRAWING_LAYER_FOR_SCHEME_ITEM
    }

    /**
     * Получает вью для объекта в плоской схеме.
     */
    abstract fun getView(viewGroup: ViewGroup): View

    /**
     * Создаёт вью для объёмной схемы и добавляет её в родительский лэйаут.
     */
    open fun draw3D(
        viewGroup: ViewGroup,
        pressedShader: BitmapShader,
        unpressedShader: BitmapShader,
        onItemClickListener: HallSchemeV2.OnHallSchemeItemClickListener? = null
    ) {
        val view = get3dView(viewGroup)
        viewReference = WeakReference(view)
        setElementZ(view)
        viewGroup.addView(view)
    }

    /**
     * Получает вью для объекта в объёмной схеме.
     */
    abstract fun get3dView(viewGroup: ViewGroup): View

    /**
     * Удаление связанного view.
     */
    @CallSuper
    open fun removeView() {
        viewReference?.run {
            get()?.let { (it.parent as ViewGroup).removeView(it) }
            clear()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HallSchemeItemUi

        if (schemeItem != other.schemeItem) return false

        return true
    }

    override fun hashCode(): Int {
        return schemeItem.hashCode()
    }
}