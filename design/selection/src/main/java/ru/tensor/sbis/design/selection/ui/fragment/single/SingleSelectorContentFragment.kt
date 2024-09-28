package ru.tensor.sbis.design.selection.ui.fragment.single

import android.os.Bundle
import android.view.View
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.bl.contract.listener.ClickHandleStrategy
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.addTopFragment
import ru.tensor.sbis.design.selection.ui.utils.isHierarchicalData
import timber.log.Timber

/**
 * Фрагмент со списком для одиночного выбора с возможностью проваливания
 *
 * @author us.bessonov
 */
internal class SingleSelectorContentFragment : BaseSingleSelectorContentFragment(
    R.layout.selection_fragment_single_content
) {
    override val lowerHeaderView: View
        get() = singleSelectorFragment.requireView().findViewById(R.id.headerContent)

    override fun onStart() {
        super.onStart()

        disposable.addAll(
            selectionViewModel.selection.map(::listOf).subscribe(listViewModel::onSelectionChanged),
            listViewModel.itemClicked.subscribe(::onItemClicked),
            // т.к. фрагменты не останавливаются подписку достаточно добавить только на корневой фрагмент
            selectionViewModel.result.subscribe(
                { /* важна только отмена. Выбор применяется при нажатии на элемент вместе с завершением поиска */ },
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

    private fun onItemClicked(data: SelectorItemModel) {
        // TODO: 4/23/2020 подключить переходы по иерархии https://online.sbis.ru/opendoc.html?guid=96e89e26-fed5-4ec0-9baa-567ce5f8d6b7
        if (data.meta.handleStrategy == ClickHandleStrategy.DEFAULT && false) {
            openHierarchy(data)
        } else {
            selectionViewModel.complete(data)
        }
    }

    private fun openHierarchy(data: SelectorItemModel) {
        singleSelectorFragment.childFragmentManager
            .addTopFragment(this, SingleSubSelectorContentFragment.newInstance(data))
    }

    companion object {

        /** @SelfDocumented */
        fun newInstance() = SingleSelectorContentFragment().apply {
            arguments = Bundle().apply {
                // TODO: 4/14/2020 иерархия для одиночного выбора ещё не поддерживается https://online.sbis.ru/opendoc.html?guid=96e89e26-fed5-4ec0-9baa-567ce5f8d6b7
                isHierarchicalData = false
            }
        }
    }
}