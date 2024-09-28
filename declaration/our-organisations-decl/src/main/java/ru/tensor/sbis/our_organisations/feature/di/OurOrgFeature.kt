package ru.tensor.sbis.our_organisations.feature.di

import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import ru.tensor.sbis.our_organisations.feature.OurOrgFragmentFactory
import ru.tensor.sbis.our_organisations.feature.OurOrgNecessaryFragmentResult
import ru.tensor.sbis.our_organisations.feature.OurOrgSelectionWindowFactory
import ru.tensor.sbis.our_organisations.feature.OurOrgUnnecessaryFragmentResult
import ru.tensor.sbis.our_organisations.feature.data.Organisation
import ru.tensor.sbis.our_organisations.feature.data.OurOrgDataServiceWrapper
import ru.tensor.sbis.our_organisations.feature.data.OurOrgParams
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Контракт фичи модуля "Наши организации".
 *
 * @author aa.mezencev
 */
interface OurOrgFeature : Feature {

    /**
     * Возвращает контракт Activity для выбора организации.
     */
    fun getOurOrgChoiceActivityContract(): ActivityResultContract<OurOrgParams, List<Organisation>>

    /**
     * Получить сервис для работы с организациями.
     */
    fun getOurOrgDataServiceWrapper(): OurOrgDataServiceWrapper

    /**
     * Получить список организаций с обязательным выбором.
     *
     * @param fragment фрагмент.
     * @param onResult сюда будет возвращен результат выбора.
     */
    fun ourOrgWithNecessaryChoiceFragmentFactory(
        fragment: Fragment,
        onResult: (OurOrgNecessaryFragmentResult) -> Unit
    ): OurOrgFragmentFactory

    /**
     * Получить список организаций с обязательным выбором.
     *
     * @param fragment фрагмент.
     * @param onResult сюда будет возвращен результат выбора.
     */
    fun ourOrgWithNecessaryChoiceSelectionWindowFactory(
        fragment: Fragment,
        onResult: (OurOrgNecessaryFragmentResult) -> Unit
    ): OurOrgSelectionWindowFactory

    /**
     * Получить список организаций с необязательным выбором.
     *
     * @param fragment фрагмент.
     * @param onResult сюда будет возвращен результат выбора.
     */
    fun ourOrgWithUnnecessaryChoiceFragmentFactory(
        fragment: Fragment,
        onResult: (OurOrgUnnecessaryFragmentResult) -> Unit
    ): OurOrgFragmentFactory

    /**
     * Получить список организаций с необязательным выбором.
     *
     * @param fragment фрагмент.
     * @param onResult сюда будет возвращен результат выбора.
     */
    fun ourOrgWithUnnecessaryChoiceSelectionWindowFactory(
        fragment: Fragment,
        onResult: (OurOrgUnnecessaryFragmentResult) -> Unit
    ): OurOrgSelectionWindowFactory
}