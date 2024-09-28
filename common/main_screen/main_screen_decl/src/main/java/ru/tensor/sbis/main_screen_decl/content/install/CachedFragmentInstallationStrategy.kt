package ru.tensor.sbis.main_screen_decl.content.install

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import ru.tensor.sbis.main_screen_decl.content.ContentContainer
import ru.tensor.sbis.main_screen_decl.content.ContentController
import ru.tensor.sbis.main_screen_decl.content.install.CachedFragmentInstallationStrategy.Mode

/**
 * Кеширующая реализация [FragmentInstallationStrategy]
 *
 * Если [mode] равен [Mode.AttachDetach], то использует [FragmentTransaction.attach]/[FragmentTransaction.detach]
 * Согласно документации, вью фрагментов будет пересоздаваться, но сами фрагменты (и ViewModel, в том числе) будут
 * храниться.
 * Управление жизненным циклом фрагмента полностью регулируется [androidx.fragment.app.FragmentManager]
 *
 * Если [mode] равен [Mode.AttachDetach], то использует [FragmentTransaction.show]/[FragmentTransaction.hide]
 * Согласно документации, ни фрагменты, ни их вью не будут пересоздаваться, отработает лишь [Fragment.onHiddenChanged]
 *
 * @author kv.martyshenko
 */
class CachedFragmentInstallationStrategy(private val mode: Mode = Mode.AttachDetach) : FragmentInstallationStrategy {

    sealed interface Mode {

        object AttachDetach : Mode

        object ShowHide : Mode
    }

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
            when (mode) {
                Mode.AttachDetach -> transaction.attach(existedFragment)
                Mode.ShowHide -> transaction.show(existedFragment)
            }
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

        when (mode) {
            Mode.AttachDetach -> findContent(contentContainer)?.let { transaction.detach(it) }
            Mode.ShowHide -> contentContainer.fragmentManager.fragments.forEach { transaction.hide(it) }
        }

        if (onTransactionAction != null) {
            transaction.runOnCommit { onTransactionAction() }
        }
    }

}