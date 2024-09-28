package ru.tensor.sbis.deals.feature

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import ru.tensor.sbis.android_ext_decl.FragmentTransactionArgs
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Описывает внешний API мастера создания сделок.
 *
 * @author ki.zhdanov
 */
interface DealsCreateFeature : Feature {

    /**
     * Создать экран мастера создания сделок.
     * @param args аргументы мастера создания сделок, см. [DealsCreateArgs].
     * @param fragmentManager фрагмент менеджер, на котором нужно выполнить транзакцию.
     * @param fragmentTransactionArgs аргументы для построения транзакции фрагмента, см. [FragmentTransactionArgs].
     * @return новый экземпляр фрагмента мастера создания сделки.
     */
    fun createDealsCreateMasterFragmentTransaction(
        args: DealsCreateArgs,
        fragmentManager: FragmentManager,
        fragmentTransactionArgs: FragmentTransactionArgs,
    ): FragmentTransaction

    /**
     * Создать экран редактирования сделки.
     * @param args аргументы для открытия сделки в режиме редактирования, см. [DealOpenArgs].
     */
    fun createDealEditFragment(
        args: DealOpenArgs,
    ): Fragment
}