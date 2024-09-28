package ru.tensor.sbis.viper.helper.recyclerviewitemdecoration.headeritems

import android.view.View

/**
 * Интерфейс для реагирования на клики по stickyHeader
 */
interface HeaderClickEventHook {

    /**
     * Отдать views из header для обработки кликов
     */
    fun onBindHeaderViews(header: View): List<View> = listOf()

    /**@SelfDocumented*/
    fun onHeaderViewClick(header: View, view: View, itemPosition: Int) = Unit

    /**@SelfDocumented*/
    fun onHeaderClick(header: View, itemPosition: Int) = Unit
}