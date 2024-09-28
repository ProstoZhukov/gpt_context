package ru.tensor.sbis.design.selection.ui.utils

import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.design.selection.ui.utils.vm.SearchViewModel

/**
 * Обработчик кнопки "Назад" при работе с хост фрагментом. Автоматически активируется при появлении фрагментов в
 * back stack за счёт подписки на [FragmentManager.addOnBackStackChangedListener]
 *
 * @author ma.kolpakov
 */
internal class SelectorHostBackPressedCallback(
    private val fragmentManager: FragmentManager,
    private val searchViewModel: SearchViewModel
) : OnBackPressedCallback(fragmentManager.isBackStackNotEmpty), FragmentManager.OnBackStackChangedListener {

    init {
        fragmentManager.addOnBackStackChangedListener(this)
    }

    override fun handleOnBackPressed() {
        performGoBack(fragmentManager, searchViewModel)
    }

    override fun onBackStackChanged() {
        isEnabled = fragmentManager.isBackStackNotEmpty
    }
}