package ru.tensor.sbis.onboarding.ui.utils

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.onboarding.ui.host.OnboardingHostFragmentImpl
import ru.tensor.sbis.onboarding.ui.host.OnboardingContract

/**
 * Выполняет транзакцию вывода фрагмента на передний план
 *
 * @param tag тег фрагмента
 * @param containerId идентификатор контейнера для фрагмента
 * @param getInstanceAction лямбда создания нового экземпляра
 */
internal fun FragmentManager.showScreen(
    tag: String,
    getInstanceAction: () -> Fragment,
    @IdRes containerId: Int
): Boolean {
    findFragmentByTag(tag)?.let {
        if (!it.isRemoving) {
            return false
        }
    }

    beginTransaction()
        .setCustomAnimations(0, 0, 0, 0)
        .add(containerId, getInstanceAction(), tag)
        .apply { addToBackStack(tag) }
        .commit()
    return true
}

/**
 * Выполняет скрытие фрагмента
 *
 * @param tag тег фрагмента
 */
internal fun FragmentManager.hideScreen(
    tag: String
) {
    val allowPopBackStack = !isStateSaved && backStackEntryCount >= MIN_BACK_STACK_ENTRY_COUNT
    if (allowPopBackStack) {
        popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}

private const val MIN_BACK_STACK_ENTRY_COUNT = 1