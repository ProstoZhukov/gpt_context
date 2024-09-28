package ru.tensor.sbis.crud.sbis.retail_settings

import ru.tensor.sbis.crud.sbis.retail_settings.di.DaggerRetailSettingsSingletonComponent
import ru.tensor.sbis.crud.sbis.retail_settings.di.RetailSettingsSingletonComponent
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

/**
 * Плагин источника данных(CRUD) предоставляющего настройки для Розницы
 *
 * @author ds.vershinin
 */
object RetailSettingsCrudPlugin : BasePlugin<Unit>(), RetailSettingsSingletonComponent.Holder {
    private lateinit var singletonComponent: RetailSettingsSingletonComponent

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(RetailSettingsSingletonComponent::class.java) { singletonComponent }
    )

    override val dependency: Dependency = Dependency.Builder().build()

    override val customizationOptions: Unit = Unit

    override fun initialize() {
        singletonComponent = DaggerRetailSettingsSingletonComponent
            .builder()
            .applicationContext(application)
            .build()
    }

    override val retailSettingsSingletonComponent: RetailSettingsSingletonComponent
        get() = singletonComponent

}