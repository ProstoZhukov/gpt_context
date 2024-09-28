package ru.tensor.sbis.common.util

import androidx.fragment.app.FragmentManager
import javax.inject.Inject

/**
 * Класс предназначен для поиска по тегу фрагмента, уже имеющегося в [FragmentManager], либо находящего
 * в отложенной транзацкции.
 * Используется для того чтобы предотвратить повторное добавление фрагмента в [FragmentManager].
 * (!)В реализации использует рефлексию для поиска в отложенной транзацкции
 */
class FragmentByTagFinder @Inject constructor() {

    /**
     * Выполняется поиск фрагмента с указанным тегом в [FragmentManager] или в отложенной транзацкции
     * @param fragmentManager [FragmentManager] в котором выполняется поиск уже добалвенного фрагмента
     * @param tag [String] тег по которому выполняется поиск
     * @return Boolean если фрагмент найден в [FragmentManager] или в отложенной транзацкции - true,
     * иначе - false
     */
    fun hasAlreadyFragmentOrPendingTransactionWithTag(
        fragmentManager: FragmentManager,
        tag: String
    ): Boolean  = fragmentManager.hasFragmentOrPendingTransaction(tag)

}
