package ru.tensor.sbis.design.selection.ui.fragment.single

import android.os.Bundle
import android.view.View
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.databinding.SelectionFragmentSingleSubContentBinding
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.parentItemIdArg
import ru.tensor.sbis.design.selection.ui.utils.parentItemTitleArg
import ru.tensor.sbis.design.selection.ui.utils.performGoBack
import ru.tensor.sbis.design.swipeback.SwipeBackLayout.DragEdge.LEFT
import ru.tensor.sbis.design.swipeback.SwipeBackLayout.DragEdge.NONE

/**
 * Фрагмент для одиночного выбора элемента при переходе на один внутренний уровень иерархии
 *
 * @author us.bessonov
 */
internal class SingleSubSelectorContentFragment : BaseSingleSelectorContentFragment(
    R.layout.selection_fragment_single_sub_content
) {

    override val lowerHeaderView: View
        get() = viewBinding.goBackPanel

    private var viewBindingNullable: SelectionFragmentSingleSubContentBinding? = null
    private val viewBinding get() = viewBindingNullable!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBindingNullable = SelectionFragmentSingleSubContentBinding.bind(view)

        viewBinding.goBackPanel.setOnClickListener { goBack() }
        viewBinding.goBackPanel.setTitle(requireArguments().parentItemTitleArg)
    }

    override fun onStart() {
        super.onStart()

        disposable.add(listViewModel.itemClicked.subscribe(::completeSelection))

        setHostSwipeBackEnabledIfAvailable(false)
    }

    override fun onStop() {
        super.onStop()
        setHostSwipeBackEnabledIfAvailable(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBindingNullable = null
    }

    override fun swipeBackEnabled() = singleSelectorFragment.isSwipeBackEnabled

    private fun setHostSwipeBackEnabledIfAvailable(isEnabled: Boolean) {
        if (swipeBackEnabled()) {
            singleSelectorFragment.setDragEdge(if (isEnabled) LEFT else NONE)
        }
    }

    private fun completeSelection(data: SelectorItemModel) {
        selectionViewModel.complete(data)
    }

    private fun goBack() {
        performGoBack(singleSelectorFragment.childFragmentManager, searchViewModel)
    }

    companion object {

        /** @SelfDocumented */
        fun newInstance(parentItem: SelectorItemModel) = SingleSubSelectorContentFragment().apply {
            arguments = Bundle().apply {
                parentItemIdArg = parentItem.id
                parentItemTitleArg = parentItem.title
            }
        }
    }
}