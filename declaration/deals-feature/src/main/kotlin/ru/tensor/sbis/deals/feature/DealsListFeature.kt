package ru.tensor.sbis.deals.feature

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Описывает внешний API реестра сделок.
 *
 * @author aa.sviridov
 */
interface DealsListFeature : Feature {

    /**
     * Создаёт фрагмент реестра сделок.
     * @param args аргументы реестра сделок, см. [DealsListArgs].
     * @return новый экземпляр фрагмента реестра сделок.
     */
    fun createDealsListFragment(
        args: DealsListArgs,
    ): Fragment
}