package ru.tensor.sbis.crud.sale.di.repository

import dagger.Component
import ru.tensor.sbis.crud.sale.crud.kkm_service_mobile.FiscalRepository

/** Компонент для функционала "Регистрация ККТ". */
@FiscalScope
@Component(modules = [FiscalModule::class])
internal interface FiscalComponent {

    /** @SelfDocumented */
    val fiscalRepository: FiscalRepository

    /**@SelfDocumented */
    @Component.Factory
    interface Factory {
        fun create(): FiscalComponent
    }
}