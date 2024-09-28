package ru.tensor.sbis.design.theme.global_variables

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.ThemeTokensProvider
import ru.tensor.sbis.design.theme.models.GraphsBaseColorModel

/**
 * Линейка цветов графика из глобальных переменных.
 *
 * Реализует [GraphsBaseColorModel].
 *
 * @author mb.kruglova
 */
enum class GraphsBaseColor(
    @AttrRes private val colorAttrRes: Int
) : GraphsBaseColorModel {

    // region Цвет графика для пустого представления (базового графика без данных)
    GRAPHS_BASE_0(R.attr.graphs_base_color_0),
    // endregion

    // region Цвета графика для типового представления для базового графика.
    GRAPHS_BASE_1(R.attr.graphs_base_color_1),
    GRAPHS_BASE_2(R.attr.graphs_base_color_2),
    GRAPHS_BASE_3(R.attr.graphs_base_color_3),
    GRAPHS_BASE_4(R.attr.graphs_base_color_4),
    GRAPHS_BASE_5(R.attr.graphs_base_color_5),
    GRAPHS_BASE_6(R.attr.graphs_base_color_6),
    GRAPHS_BASE_7(R.attr.graphs_base_color_7),
    GRAPHS_BASE_8(R.attr.graphs_base_color_8),
    GRAPHS_BASE_9(R.attr.graphs_base_color_9),
    GRAPHS_BASE_10(R.attr.graphs_base_color_10),
    GRAPHS_BASE_11(R.attr.graphs_base_color_11),
    GRAPHS_BASE_12(R.attr.graphs_base_color_12),
    // endregion

    // region Цвета для графиков сравнения - прошлый период.
    // Цвета данных
    GRAPHS_EXTRA_0(R.attr.graphs_extra_color_0),
    // Цвета заливок графика (градиент 50-20%)
    GRAPHS_BASE_0_OPACITY_50(R.attr.graphs_base_color_0_opacity_50),
    GRAPHS_BASE_0_OPACITY_20(R.attr.graphs_base_color_0_opacity_20),
    // endregion

    // region Цвета для графиков сравнения - текущий период.
    // Цвета данных
    GRAPHS_EXTRA_1(R.attr.graphs_extra_color_1),
    // Цвета заливок графика (градиент 50-20%)
    GRAPHS_BASE_1_OPACITY_50(R.attr.graphs_base_color_1_opacity_50),
    GRAPHS_BASE_1_OPACITY_20(R.attr.graphs_base_color_1_opacity_20),
    GRAPHS_EXTRA_3(R.attr.graphs_extra_color_3);
    // endregion

    override val globalVar = this

    /**
     * @SelfDocumented
     */
    @ColorInt
    fun getValue(context: Context) = ThemeTokensProvider.getColorInt(context, colorAttrRes)
}