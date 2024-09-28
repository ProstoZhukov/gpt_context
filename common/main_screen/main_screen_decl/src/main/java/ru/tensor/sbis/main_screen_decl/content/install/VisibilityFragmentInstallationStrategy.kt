package ru.tensor.sbis.main_screen_decl.content.install

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import ru.tensor.sbis.main_screen_decl.content.ContentContainer
import ru.tensor.sbis.main_screen_decl.content.ContentController

/**
 * Реализация [FragmentInstallationStrategy], построенная на базе [FragmentTransaction.show]/[FragmentTransaction.hide].
 * View и ViewModel фрагментов не будут пересоздаваться, за исключением случая, когда он уже находится на экране
 * и [FragmentInstallationStrategy.FragmentDiffCallback] посчитал, что его нужно обновить.
 * При скрытии у целевого фрагмента будет вызван [Fragment.onPause], при появлении - [Fragment.onResume].
 *
 * @author am.boldinov
 */
@Suppress("unused")
class VisibilityFragmentInstallationStrategy : FragmentInstallationStrategy {

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
        if (existedFragment == null) {
            transaction.add(contentContainer.containerId, fragment, tag)
        } else if (!fragmentDiffCallback.checkIfFragmentsInterchangeable(existedFragment, fragment, selectionInfo)) {
            transaction.remove(existedFragment)
            transaction.add(contentContainer.containerId, fragment, tag)
        } else {
            transaction.setMaxLifecycle(existedFragment, Lifecycle.State.RESUMED)
            transaction.show(existedFragment)
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
            transaction.setMaxLifecycle(it, Lifecycle.State.STARTED)
            transaction.hide(it)
        }

        if (onTransactionAction != null) {
            transaction.runOnCommit { onTransactionAction() }
        }
    }

    override fun findContent(container: ContentContainer): Fragment? = with(container) {
        fragmentManager.fragments.find {
            it.isVisible && it.id == containerId
        } ?: fragmentManager.findFragmentById(containerId)
    }
}