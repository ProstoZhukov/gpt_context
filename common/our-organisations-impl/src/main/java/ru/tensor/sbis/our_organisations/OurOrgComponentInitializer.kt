package ru.tensor.sbis.our_organisations

import ru.tensor.sbis.common.di.BaseSingletonComponentInitializer
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.our_organisations.feature.di.OurOrgComponent

/**
 * Объект для инициализации [OurOrgComponent].
 *
 * @author mv.ilin
 */
class OurOrgComponentInitializer : BaseSingletonComponentInitializer<OurOrgDiComponent>() {
    /**
     * Получить [OurOrgDiComponent].
     *
     * @param commonSingletonComponent
     */
    override fun createComponent(commonSingletonComponent: CommonSingletonComponent): OurOrgDiComponent {
        return DaggerOurOrgDiComponent.factory()
            .create(commonSingletonComponent)
    }
}
