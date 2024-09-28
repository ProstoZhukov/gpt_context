package ru.tensor.sbis.crud.sbis.pricing

import android.app.Application
import ru.tensor.sbis.crud.sbis.pricing.di.DaggerPricingSingletonComponent
import ru.tensor.sbis.crud.sbis.pricing.di.PricingSingletonComponent
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

/**
 * Плагин источника данных(CRUD) предоставляющего прайс-листы
 *
 * @author ds.vershinin
 */
object PricingCrudPlugin : BasePlugin<Unit>(), PricingSingletonComponent.Holder {
    private lateinit var singletonComponent: PricingSingletonComponent

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(PricingSingletonComponent::class.java) { singletonComponent }
    )

    override val dependency: Dependency = Dependency.Builder().build()

    override val customizationOptions: Unit = Unit

    override fun initialize() {
        singletonComponent = DaggerPricingSingletonComponent
            .builder()
            .applicationContext(application)
            .build()
    }

    override val pricingSingletonComponent: PricingSingletonComponent
        get() = singletonComponent

}