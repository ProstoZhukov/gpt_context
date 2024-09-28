package ru.tensor.sbis.design.toolbar.appbar.color

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.StyleRes
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.ColorUtils
import com.mikepenz.iconics.IconicsDrawable
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.toolbar.R
import ru.tensor.sbis.design.toolbar.appbar.model.ColorModel
import androidx.appcompat.R as AndroidXR

/**
 * @author ma.kolpakov
 * Создан 9/30/2019
 */
internal class ToolbarColorUpdateFunction(
    private val getIconicsDrawable: (Context) -> IconicsDrawable =
        { IconicsDrawable(it, SbisMobileIcon.Icon.smi_arrowBack) }
) : ColorUpdateFunction<Toolbar> {

    override fun updateColorModel(view: Toolbar, model: ColorModel?) {
        /*
        Установка типа слоя необходима для отрисовки цветной тени под иконкой. Если иконка будет получаться иным
        способом, нужно рассмотреть вариант отказа от этого вызова.
        https://developer.android.com/reference/android/graphics/Paint#setShadowLayer(float,%20float,%20float,%20int)
         */
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        view.navigationIcon = when {
            model == null || model.darkText -> createIcon(view.context, R.style.SbisAppBar_Toolbar_Icon_Dark)
            else -> createIcon(view.context, R.style.SbisAppBar_Toolbar_Icon)
        }
    }

    /**
     * Генерация иконок для навигационной кнопки из шрифта с учётом темы
     */
    @SuppressLint("PrivateResource")
    private fun createIcon(context: Context, @StyleRes style: Int): Drawable {
        val attributes = intArrayOf(android.R.attr.ambientShadowAlpha)
        val typedArray = context.theme.obtainStyledAttributes(style, attributes)
        val ambientShadowAlpha = typedArray.getFloat(0, 1f)
        typedArray.recycle()

        val drawable = getIconicsDrawable(context)

        // стиль иконки
        context.obtainStyledAttributes(style, AndroidXR.styleable.TextAppearance).apply {
            drawable.sizeRes(getResourceId(AndroidXR.styleable.TextAppearance_android_textSize, 0))
            drawable.colorRes(getResourceId(AndroidXR.styleable.TextAppearance_android_textColor, 0))

            // цвет тени с прозрачностью
            val shadowColor = ColorUtils.setAlphaComponent(
                getInt(AndroidXR.styleable.TextAppearance_android_shadowColor, 0),
                (drawable.compatAlpha * ambientShadowAlpha).toInt()
            )
            // установка тени
            drawable.shadowPx(
                getFloat(AndroidXR.styleable.TextAppearance_android_shadowRadius, 0f),
                getFloat(AndroidXR.styleable.TextAppearance_android_shadowDx, 0f),
                getFloat(AndroidXR.styleable.TextAppearance_android_shadowDy, 0f),
                shadowColor
            )
            drawable.respectFontBounds(true)
            recycle()
        }

        return drawable
    }
}