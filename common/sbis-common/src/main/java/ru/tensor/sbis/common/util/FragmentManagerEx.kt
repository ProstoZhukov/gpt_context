package ru.tensor.sbis.common.util

import androidx.fragment.app.FragmentManager
import java.lang.reflect.Field

private const val PENDING_ACTIONS_FIELD_NAME = "mPendingActions"

/**
 * Выполняет поиск фрагмента или отложенной транзакции по тегу [tag].
 *
 * @param tag Тег, по которому выполняется поиск.
 *
 * @return Если фрагмент найден в [FragmentManager] или в отложенной транзакции - true, иначе - false.
 */
fun FragmentManager.hasFragmentOrPendingTransaction(tag: String): Boolean =
    findFragmentByTag(tag) != null || pendingBackStackEntries().any { it.name == tag }

/**
 * Получить значение [FragmentManager.mPendingActions] через рефлексию.
 */
fun FragmentManager.getPendingActions(): List<Any> {
    val pendingActionsField: Field =
        //Ищем в реализации FragmentManagerImpl
        this::class.java.declaredFields.find { it.name == PENDING_ACTIONS_FIELD_NAME }
            ?: kotlin.run {
                //Ищем в базовом классе FragmentManager, с некоторой версии библиотеки поле mPendingActions там
                this::class.java.superclass?.declaredFields?.find { it.name == PENDING_ACTIONS_FIELD_NAME }
            }
            ?: kotlin.run {
                //Не нашли, так не нашли, ничего страшного.
                return emptyList()
            }
    pendingActionsField.isAccessible = true
    @Suppress("UNCHECKED_CAST")
    return pendingActionsField.get(this) as? List<Any>? ?: emptyList()
}

private fun FragmentManager.pendingBackStackEntries(): List<FragmentManager.BackStackEntry> {
    return getPendingActions().filterIsInstance<FragmentManager.BackStackEntry>()
}