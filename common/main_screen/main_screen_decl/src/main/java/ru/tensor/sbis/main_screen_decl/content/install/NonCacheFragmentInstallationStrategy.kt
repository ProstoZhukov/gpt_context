package ru.tensor.sbis.main_screen_decl.content.install

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import ru.tensor.sbis.main_screen_decl.content.ContentContainer
import ru.tensor.sbis.main_screen_decl.content.ContentController

/**
 * Реализация [FragmentInstallationStrategy], построенная на базе [FragmentTransaction.replace].
 * Фрагмент будет создаваться каждый раз заново, за исключением случая, когда он уже находится на экране
 * и [FragmentInstallationStrategy.FragmentDiffCallback] посчитал, что его нужно обновить.
 *
 * @author kv.martyshenko
 */
class NonCacheFragmentInstallationStrategy : FragmentInstallationStrategy {

    override fun show(
        fragment: Fragment,
        tag: String,
        selectionInfo: ContentController.SelectionInfo,
        contentContainer: ContentContainer,
        transaction: FragmentTransaction,
        beforeTransactionAction: Action?,
        onTransactionAction: Action?,
        fragmentDiffCallback: FragmentInstallationStrategy.FragmentDiffCallback
    ) {
        beforeTransactionAction?.invoke()

        val existedFragment = contentContainer.fragmentManager.findFragmentByTag(tag)
        if (existedFragment == null ||
            !fragmentDiffCallback.checkIfFragmentsInterchangeable(existedFragment, fragment, selectionInfo)) {
            /*
            Быстрофикс для уничтожения existedFragment даже если он и fragment являются экземплярами одного и того же
            класса и теги одинаковые, но аргументы разные. Вызов transaction.replace(...) приводит только к
            existedFragment.onStop, но не existedFragment.onDestroy, что для прикладной стороны неприемлемо (грозит
            крашами, возможно даже утечками т.к. onDestroy тоже занимается освобождениями ресурсов и окружений).
            Комбинации attach/detach/add/remove на transaction также не заставляют existedFragment уничтожаться.
            TODO: https://online.sbis.ru/opendoc.html?guid=230c7b07-83f2-460b-a3dd-ca22cad3ec6e&client=3
            */
            if (existedFragment != null) {
                contentContainer.fragmentManager.beginTransaction()
                    .remove(existedFragment)
                    .commitNowAllowingStateLoss()
            }
            transaction.replace(contentContainer.containerId, fragment, tag)
        } else {
            fragmentDiffCallback.update(existedFragment, selectionInfo, transaction)
        }

        if (onTransactionAction != null) {
            transaction.runOnCommit { onTransactionAction() }
        }
    }

    override fun hide(
        contentContainer: ContentContainer,
        transaction: FragmentTransaction,
        beforeTransactionAction: Action?,
        onTransactionAction: Action?
    ) {
        beforeTransactionAction?.invoke()

        findContent(contentContainer)?.let {
            transaction.remove(it)
        }

        if (onTransactionAction != null) {
            transaction.runOnCommit { onTransactionAction() }
        }
    }

}