package ru.tensor.sbis.design.tabs.util

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import ru.tensor.sbis.design.tabs.api.SbisTabsViewItem
import ru.tensor.sbis.design.tabs.tabItem.SbisTabItemStyleHolder.Factory.STATES
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.global_variables.MarkerColor
import ru.tensor.sbis.design.theme.global_variables.SelectedItemColor
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.design.theme.models.BaseModel
import ru.tensor.sbis.design.theme.res.SbisColor

/**
 * Стили вкладок.
 *
 * @da.zolotarev
 */
internal enum class SbisTabInternalStyle(
    private val textColor: BaseModel<*>,
    private val selectedTextColor: BaseModel<*>,
    private val selectedTextSize: FontSize,
    private val markerColor: BaseModel<*>
) {
    /** @SelfDocumented */
    ACCENTED(
        StyleColor.SECONDARY,
        StyleColor.PRIMARY,
        FontSize.L,
        MarkerColor.DEFAULT
    ),

    /** @SelfDocumented */
    UNACCENTED(
        TextColor.LABEL,
        TextColor.DEFAULT,
        FontSize.XS,
        SelectedItemColor.DEFAULT
    );

    /**
     * Получить размер текста.
     */
    fun getTextSize(context: Context) = selectedTextSize.getScaleOffDimen(context)

    /**
     * Получить цвет маркера.
     */
    fun getMarkerColor(context: Context) = when (markerColor) {
        is MarkerColor -> markerColor.globalVar.getValue(context)
        is SelectedItemColor -> markerColor.globalVar.getValue(context)
        else -> Color.MAGENTA
    }

    /**
     * Получить набор цветов текста, соответствующих состоянию выбранности.
     *
     * Принимает кастомные цвета текста для ВСЕХ вкладок (они менее приоритетны чем [SbisTabsViewItem.customTitleColor])
     */
    fun getTextColorStateList(
        context: Context,
        customSelectedTitleColor: SbisColor?,
        customUnselectedTitleColor: SbisColor?
    ) = when {
        textColor is TextColor && selectedTextColor is TextColor -> ColorStateList(
            STATES,
            intArrayOf(
                customUnselectedTitleColor?.getColor(context) ?: textColor.getValue(context),
                customSelectedTitleColor?.getColor(context) ?: selectedTextColor.getValue(context)
            )
        )

        textColor is StyleColor && selectedTextColor is StyleColor -> ColorStateList(
            STATES,
            intArrayOf(
                customUnselectedTitleColor?.getColor(context) ?: textColor.getTextColor(context),
                customSelectedTitleColor?.getColor(context) ?: selectedTextColor.getTextColor(context)
            )
        )

        else -> ColorStateList.valueOf(Color.MAGENTA)
    }
}