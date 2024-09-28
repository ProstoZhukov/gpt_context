package ru.tensor.sbis.crud.sale

import ru.tensor.sbis.crud.sale.crud.kkm_service_mobile.FiscalRepository
import ru.tensor.sbis.crud.sale.di.DaggerSaleSingletonComponent
import ru.tensor.sbis.crud.sale.di.SaleSingletonComponent
import ru.tensor.sbis.crud.sale.di.repository.DaggerFiscalComponent
import ru.tensor.sbis.crud.sale.di.repository.FiscalComponent
import ru.tensor.sbis.crud.sale.di.repository.KkmComponent
import ru.tensor.sbis.crud.sale.di.repository.RefusalReasonComponent
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

/**
 * Плагин источника данных(CRUD) продажи
 *
 * @author ds.vershinin
 */
object SaleCrudPlugin : BasePlugin<Unit>(), SaleSingletonComponent.Holder {
    private lateinit var singletonComponent: SaleSingletonComponent
    private lateinit var fiscalComponent: FiscalComponent

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(SaleSingletonComponent::class.java) { singletonComponent },
        FeatureWrapper(RefusalReasonComponent::class.java) { saleSingletonComponent },
        FeatureWrapper(KkmComponent::class.java) { saleSingletonComponent },
        FeatureWrapper(FiscalRepository::class.java) { fiscalComponent.fiscalRepository }
    )

    override val dependency: Dependency = Dependency.Builder().build()

    override val customizationOptions: Unit = Unit

    override fun initialize() {
        singletonComponent = DaggerSaleSingletonComponent
            .builder()
            .applicationContext(application)
            .build()
        fiscalComponent = DaggerFiscalComponent.create()
    }

    override val saleSingletonComponent: SaleSingletonComponent
        get() = singletonComponent
}