package ru.tensor.sbis.our_organisations.feature.di

/**
 * Контракт предоставления фичи.
 *
 * @author aa.mezencev
 */
interface OurOrgComponent {
    /**
     * Получить экземпляр фичи.
     */
    fun getFeature(): OurOrgFeature
}