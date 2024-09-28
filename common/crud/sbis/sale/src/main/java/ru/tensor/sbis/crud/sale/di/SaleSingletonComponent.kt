package ru.tensor.sbis.crud.sale.di

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.crud.sale.SaleCrudPlugin
import ru.tensor.sbis.crud.sale.di.repository.*
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.sale.mobile.generated.SaleMobileService

/**@SelfDocumented */
@Component(
    modules = [SaleSingletonModule::class,
        RefusalReasonModule::class,
        KkmModule::class]
)
interface SaleSingletonComponent :
        RefusalReasonComponent,
        KkmComponent,
        Feature {

    /**@SelfDocumented */
    fun getControllerProvider(): DependencyProvider<SaleMobileService>

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun applicationContext(context: Context): Builder

        fun build(): SaleSingletonComponent
    }

    /**@SelfDocumented */
    interface Holder {

        /**@SelfDocumented */
        val saleSingletonComponent: SaleSingletonComponent
    }

    companion object {

        /**@SelfDocumented */
        fun getInstance(context: Context): SaleSingletonComponent {
            val application = context.applicationContext as Application
            return if (application is Holder) {
                application.saleSingletonComponent
            } else {
                SaleCrudPlugin.saleSingletonComponent
            }
        }
    }
}