package ru.tensor.sbis.design.selection.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.openHierarchy
import ru.tensor.sbis.design.selection.ui.utils.parentItemIdArg
import ru.tensor.sbis.design.selection.ui.utils.parentItemTitleArg
import ru.tensor.sbis.design.selection.ui.utils.performGoBack
import ru.tensor.sbis.design.breadcrumbs.CurrentFolderView
import ru.tensor.sbis.design.swipeback.SwipeBackLayout.DragEdge.LEFT
import ru.tensor.sbis.design.swipeback.SwipeBackLayout.DragEdge.NONE

/**
 * Фрагмент для выбора элемента при переходе на один внутренний уровень иерархии
 *
 * @author ma.kolpakov
 */
internal class SubSelectorContentFragment : SelectorContentFragment(R.layout.selection_fragment_multi_sub_content) {

    override val lowerHeaderView: View
        get() = goBackPanel

    private lateinit var goBackPanel: CurrentFolderView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        goBackPanel = view.findViewById(R.id.goBackPanel)

        goBackPanel.setOnClickListener { goBack() }
        goBackPanel.setTitle(requireArguments().parentItemTitleArg)
        goBackPanel.isVisible = true
    }

    override fun onStart() {
        super.onStart()

        disposable.add(listViewModel.itemClicked.subscribe(::openHierarchy))
        setHostSwipeBackEnabledIfAvailable(false)
    }

    override fun onStop() {
        super.onStop()
        setHostSwipeBackEnabledIfAvailable(true)
    }

    override fun swipeBackEnabled() = multiSelectorFragment.isSwipeBackEnabled

    private fun setHostSwipeBackEnabledIfAvailable(isEnabled: Boolean) {
        if (swipeBackEnabled()) {
            multiSelectorFragment.setDragEdge(if (isEnabled) LEFT else NONE)
        }
    }

    private fun goBack() {
        performGoBack(multiSelectorFragment.childFragmentManager, searchViewModel)
    }

    companion object {

        fun newInstance(parentItem: SelectorItemModel) = SubSelectorContentFragment().apply {
            arguments = Bundle().apply {
                parentItemIdArg = parentItem.id
                parentItemTitleArg = parentItem.title
            }
        }
    }
}