package ru.tensor.sbis.our_organisations.presentation

import androidx.fragment.app.Fragment
import ru.tensor.sbis.our_organisations.feature.OurOrgFragmentFactory
import ru.tensor.sbis.our_organisations.feature.OurOrgNecessaryFragmentResult
import ru.tensor.sbis.our_organisations.feature.OurOrgSelectionWindowFactory
import ru.tensor.sbis.our_organisations.feature.OurOrgUnnecessaryFragmentResult

/**
 * Контракт для доступа к модулю.
 *
 * @author mv.ilin
 */
internal interface OurOrgListModule {
    /**
     * Получить фабрику создания фрагмента с обязательным выбором из списка организаций.
     *
     * @param fragment фрагмент.
     * @param onResult сюда будет возвращен результат выбора.
     */
    fun ourOrgWithNecessaryChoiceFragmentFactory(
        fragment: Fragment,
        onResult: (OurOrgNecessaryFragmentResult) -> Unit
    ): OurOrgFragmentFactory

    /**
     * Получить фабрику создания фрагмента с обязательным выбором из списка организаций.
     *
     * @param fragment фрагмент.
     * @param onResult сюда будет возвращен результат выбора.
     */
    fun ourOrgWithNecessaryChoiceSelectionWindowFactory(
        fragment: Fragment,
        onResult: (OurOrgNecessaryFragmentResult) -> Unit
    ): OurOrgSelectionWindowFactory

    /**
     * Получить фабрику создания фрагмента с необязательным выбором из списка организаций.
     *
     * @param fragment фрагмент.
     * @param onResult сюда будет возвращен результат выбора.
     */
    fun ourOrgWithUnnecessaryChoiceFragmentFactory(
        fragment: Fragment,
        onResult: (OurOrgUnnecessaryFragmentResult) -> Unit
    ): OurOrgFragmentFactory

    /**
     * Получить фабрику создания фрагмента с необязательным выбором из списка организаций.
     *
     * @param fragment фрагмент.
     * @param onResult сюда будет возвращен результат выбора.
     */
    fun ourOrgWithUnnecessaryChoiceSelectionWindowFactory(
        fragment: Fragment,
        onResult: (OurOrgUnnecessaryFragmentResult) -> Unit
    ): OurOrgSelectionWindowFactory
}
