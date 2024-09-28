package ru.tensor.sbis.crud.devices.settings

import ru.tensor.sbis.crud.devices.settings.di.DaggerDevicesSettingsSingletonComponent
import ru.tensor.sbis.crud.devices.settings.di.DevicesSettingsSingletonComponent
import ru.tensor.sbis.crud.devices.settings.di.repository.DeviceComponent
import ru.tensor.sbis.crud.devices.settings.di.repository.DeviceTypeComponent
import ru.tensor.sbis.crud.devices.settings.di.repository.SalesPointComponent
import ru.tensor.sbis.crud.devices.settings.di.repository.WorkplaceComponent
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

/**
 * Плагин источника данных(CRUD) предоставляющего настройки устройств
 *
 * @author ds.vershinin
 */
object DevicesSettingsCrudPlugin : BasePlugin<Unit>(), DevicesSettingsSingletonComponent.Holder {
    private lateinit var singletonComponent: DevicesSettingsSingletonComponent

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(DeviceTypeComponent::class.java) { singletonComponent },
        FeatureWrapper(WorkplaceComponent::class.java) { singletonComponent },
        FeatureWrapper(DeviceComponent::class.java) { singletonComponent },
        FeatureWrapper(SalesPointComponent::class.java) { singletonComponent },
        FeatureWrapper(DevicesSettingsSingletonComponent::class.java) { singletonComponent }
    )

    override val dependency: Dependency = Dependency.Builder().build()

    override val customizationOptions: Unit = Unit

    override fun initialize() {
        singletonComponent = DaggerDevicesSettingsSingletonComponent
            .builder()
            .applicationContext(application)
            .build()
    }

    override val devicesSettingsSingletonComponent: DevicesSettingsSingletonComponent
        get() = singletonComponent

}