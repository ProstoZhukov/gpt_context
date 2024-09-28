package ru.tensor.sbis.manage_features.data.di

import ru.tensor.sbis.common.di.BaseSingletonComponentInitializer
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.manage_features.contract.ManageFeaturesDependency

/**
 * Класс для создания и инициализации [ManageFeaturesComponent]
 *
 * @param dependency зависимости модуля
 */
class ManageFeaturesComponentInitializer(
    private val dependency: ManageFeaturesDependency
) : BaseSingletonComponentInitializer<ManageFeaturesComponent>() {

    override fun createComponent(commonSingletonComponent: CommonSingletonComponent): ManageFeaturesComponent =
        DaggerManageFeaturesComponent.factory().create(dependency)
}