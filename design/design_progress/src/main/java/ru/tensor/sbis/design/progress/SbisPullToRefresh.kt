package ru.tensor.sbis.design.progress

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.tensor.sbis.design.utils.ThemeContextBuilder

/**
 * Стандартный индикатор прогресса с подложкой, для обновления по свайпу.
 * Обёртка над [SwipeRefreshLayout] для обеспечения темизации с использованием глобальных атрибутов.
 *
 * @see [Стандарт "Индикатор процесса"](http://axure.tensor.ru/standarts/v7/хлебные_крошки__версия_02_.html)
 *
 * @author us.bessonov
 */
class SbisPullToRefresh @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.sbisPullToRefreshTheme,
    @StyleRes defStyleRes: Int = R.style.SbisPullToRefreshDefaultTheme
) : SwipeRefreshLayout(ThemeContextBuilder(context, defStyleAttr, defStyleRes).build(), attrs) {

    init {
        getContext().withStyledAttributes(attrs, R.styleable.SbisPullToRefresh, defStyleAttr, defStyleRes) {
            val progressColor = getColor(R.styleable.SbisPullToRefresh_SbisPullToRefresh_progressColor, Color.MAGENTA)
            setColorSchemeColors(progressColor)

            val progressBackground =
                getColor(R.styleable.SbisPullToRefresh_SbisPullToRefresh_progressBackground, Color.MAGENTA)
            setProgressBackgroundColorSchemeColor(progressBackground)
        }
    }
}