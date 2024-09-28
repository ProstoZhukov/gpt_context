package ru.tensor.sbis.main_screen_decl.content.install

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import ru.tensor.sbis.main_screen_decl.content.ContentContainer
import ru.tensor.sbis.main_screen_decl.content.ContentController

typealias Action = () -> Unit

/**
 * Стратегия по установке фрагмента.
 *
 * @author kv.martyshenko
 */
interface FragmentInstallationStrategy {

    /**
     * Метод для показа фрагмента в контентной области.
     *
     * @param fragment
     * @param tag
     * @param selectionInfo доп.информация о выбранном элементе.
     * @param contentContainer контейнер контента.
     * @param transaction активная транзакция.
     * @param beforeTransactionAction действие, которое будет выполнено до транзакции.
     * @param onTransactionAction действие, которое будет выполнено во время транзакции.
     * @param fragmentDiffCallback реализация [FragmentDiffCallback], необходимая для корректного обновления существующего фрагмента.
     */
    fun show(
        fragment: Fragment,
        tag: String,
        selectionInfo: ContentController.SelectionInfo,
        contentContainer: ContentContainer,
        transaction: FragmentTransaction,
        beforeTransactionAction: Action? = null,
        onTransactionAction: Action? = null,
        fragmentDiffCallback: FragmentDiffCallback
    )

    /**
     * Метод для скрытия фрагмента из контентной области.
     *
     * @param contentContainer контейнер контента.
     * @param transaction активная транзакция.
     * @param beforeTransactionAction действие, которое будет выполнено до транзакции.
     * @param onTransactionAction действие, которое будет выполнено во время транзакции.
     *
     */
    fun hide(
        contentContainer: ContentContainer,
        transaction: FragmentTransaction,
        beforeTransactionAction: Action? = null,
        onTransactionAction: Action? = null
    )

    /**
     * Метод для поиска текущего фрагмента в контентной области
     *
     * @param container контейнер контента.
     */
    fun findContent(container: ContentContainer): Fragment? = with(container) {
        fragmentManager.findFragmentById(containerId)
    }

    /**
     * Интерфейс для обновлений фрагментов.
     *
     * @author kv.martyshenko
     */
    interface FragmentDiffCallback {

        /**
         * Метод для проверки эквивалентности/взаимозаменяемости фрагментов.
         *
         * @param existedFragment существующий фрагмент.
         * @param newFragment новый фрагмент.
         * @param selectionInfo доп.информация о выбранном элементе.
         */
        fun checkIfFragmentsInterchangeable(
            existedFragment: Fragment,
            newFragment: Fragment,
            selectionInfo: ContentController.SelectionInfo
        ): Boolean

        /**
         * Метод для проведения обновления существующего фрагмента новыми данными.
         *
         * @param existedFragment существующий фрагмент.
         * @param selectionInfo доп.информация о выбранном элементе.
         * @param transaction текущая транзакция.
         */
        fun update(
            existedFragment: Fragment,
            selectionInfo: ContentController.SelectionInfo,
            transaction: FragmentTransaction
        )
    }

    /**
     * Реализация [FragmentDiffCallback].
     *
     * @property fragmentVerifier функция для проверки эквивалентности/взаимозаменяемости фрагментов.
     * @property fragmentUpdater функция для обновления существующего фрагмента новыми данными.
     *
     * @author kv.martyshenko
     */
    class DefaultFragmentDiffCallback(
        private val fragmentVerifier: (existedFragment: Fragment, newFragment: Fragment, selectionInfo: ContentController.SelectionInfo) -> Boolean = { existed, new, _ ->
            existed::class.java == new::class.java && equalBundles(existed.arguments, new.arguments)
        },
        private val fragmentUpdater: (existedFragment: Fragment, selectionInfo: ContentController.SelectionInfo) -> Unit
    ) : FragmentDiffCallback {

        override fun checkIfFragmentsInterchangeable(
            existedFragment: Fragment,
            newFragment: Fragment,
            selectionInfo: ContentController.SelectionInfo
        ): Boolean {
            return fragmentVerifier(existedFragment, newFragment, selectionInfo)
        }

        override fun update(
            existedFragment: Fragment,
            selectionInfo: ContentController.SelectionInfo,
            transaction: FragmentTransaction
        ) {
            transaction.runOnCommit {
                fragmentUpdater(existedFragment, selectionInfo)
            }
        }

        private companion object {
            fun equalBundles(one: Bundle?, two: Bundle?): Boolean {
                one == two && return true
                (one == null || two == null) && return false
                one!!.size() != two!!.size() && return false
                val keySet = HashSet(one.keySet() + two.keySet())
                var valuePair: Pair<Any?, Any?>
                for (key in keySet) {
                    (!one.containsKey(key) || !two.containsKey(key)) && return false
                    valuePair = Pair(one[key], two[key])
                    if (valuePair.first == null) {
                        valuePair.second != null && return false
                    } else if (valuePair.first is Bundle && valuePair.second is Bundle) {
                        if (!equalBundles(valuePair.first as Bundle, valuePair.second as Bundle)) {
                            return false
                        }
                    } else if (valuePair.first != valuePair.second) {
                        return false
                    }
                }
                return true
            }
        }
    }
}