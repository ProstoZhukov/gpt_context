package ru.tensor.sbis.deals.feature

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Предоставляет внешний API модуля карточки сделки.
 *
 * @author ki.zhdanov
 */
interface DealsCardFeature : Feature {

    /**
     * Создаёт фрагмент карточки документа.
     * @param args аргументы для чтения карточки документа, см. [DealOpenArgs].
     *
     * @return новый экземпляр фрагмента карточки документа, см. [Fragment].
     */
    fun createDealCardFragment(
        args: DealOpenArgs,
    ): Fragment
}