package ru.tensor.sbis.design.selection.ui.fragment

import android.os.Bundle
import android.view.View
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.utils.openHierarchy
import timber.log.Timber

/**
 * Фрагмент области списка и выбранных элементов в [MultiSelectorFragment]
 *
 * @author ma.kolpakov
 */
internal class MultiSelectorContentFragment : SelectorContentFragment(R.layout.selection_fragment_multi_content) {

    override val lowerHeaderView: View
        get() = multiSelectorFragment.requireView().findViewById(R.id.toolbar)

    override fun onStart() {
        super.onStart()

        disposable.addAll(
            listViewModel.itemClicked.subscribe(::openHierarchy),
            // т.к. фрагменты не останавливаются подписку достаточно добавить только на корневой фрагмент
            selectionViewModel.result.subscribe(
                {
                    searchViewModel.finishEditingSearchQuery()
                    searchViewModel.isEnabled = false
                },
                Timber::e,
                searchViewModel::finishEditingSearchQuery
            )
        )
    }

    override fun onResume() {
        super.onResume()
        searchViewModel.isEnabled = true
    }

    override fun onPause() {
        super.onPause()
        searchViewModel.isEnabled = false
    }

    companion object {

        fun newInstance() = MultiSelectorContentFragment().apply {
            arguments = Bundle()
        }
    }
}