package ru.tensor.sbis.design_selection.ui.main.utils

import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.design_selection.databinding.DesignSelectionHostFragmentBinding
import ru.tensor.sbis.design_selection.R

/**
 * Делегат для работы с тенями при скроллировании контента компонента выбора.
 *
 * @author vv.chekurda
 */
internal class SelectionHeaderShadowDelegate(
    private val childFragmentManager: FragmentManager,
    private val binding: DesignSelectionHostFragmentBinding
) {

    /**
     * Интерфейс скроллируемого контента.
     */
    interface ScrollableContent {

        /**
         * Установить [view] из хост разметки, на которую нужно наложить тень при скроллировании.
         */
        fun setShadowView(view: View?)
    }

    private var hasSelectedData: Boolean = false

    private val shadowView: View
        get() {
            val headerButton = binding.root.findViewById<View>(R.id.selection_header_button)
            return when {
                headerButton?.isVisible == true -> binding.selectionHeaderContainer
                hasSelectedData -> binding.selectionPanel
                binding.selectionHeaderContent.selectionToolbar.isGone -> {
                    binding.root.findViewById(R.id.selection_app_bar_layout)
                }
                else -> binding.selectionHeaderContent.selectionToolbar
            }
        }

    private val backStackChangedListener = FragmentManager.OnBackStackChangedListener {
        updateShadowView()
    }

    init {
        childFragmentManager.addOnBackStackChangedListener(backStackChangedListener)
    }

    /**
     * Изменилось состояние отображения выбранных элементов.
     */
    fun onSelectedDataChanged(hasSelectedData: Boolean) {
        if (this.hasSelectedData == hasSelectedData) return
        this.hasSelectedData = hasSelectedData
        updateShadowView()
    }

    fun updateShadowView() {
        (childFragmentManager.fragments.lastOrNull() as? ScrollableContent)
            ?.setShadowView(shadowView)
    }

    /**
     * Очистка.
     */
    fun clear() {
        childFragmentManager.removeOnBackStackChangedListener(backStackChangedListener)
    }
}