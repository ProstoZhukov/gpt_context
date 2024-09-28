package ru.tensor.sbis.red_button.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.common.rx.RxBus
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.common.util.di.PerApp
import ru.tensor.sbis.red_button.RedButtonDependency
import ru.tensor.sbis.red_button.RedButtonPlugin
import ru.tensor.sbis.red_button.feature.RedButtonFeature

/**
 * Компонент di для модуля "Красной Кнопки"
 *
 * @author ra.stepanov
 */
@PerApp
@Component(dependencies = [CommonSingletonComponent::class], modules = [RedButtonModule::class])
interface RedButtonComponent {

    /** @SelfDocumented */
    val rxBus: RxBus

    /** @SelfDocumented */
    val dependency: RedButtonDependency

    /** @SelfDocumented */
    val redButtonFeature: RedButtonFeature

    /** @SelfDocumented */
    val networkUtils: NetworkUtils

    /** @SelfDocumented */
    val resourceProvider: ResourceProvider

    @Component.Factory
    interface Factory {

        /** @SelfDocumented */
        fun create(
            commonSingletonComponent: CommonSingletonComponent,
            @BindsInstance dependency: RedButtonDependency
        ): RedButtonComponent
    }

    /** @SelfDocumented */
    object Initializer {

        /** @SelfDocumented */
        @JvmStatic
        fun init(
            communicatorCommonComponent: CommonSingletonComponent,
            dependency: RedButtonDependency
        ): RedButtonComponent =
            DaggerRedButtonComponent.factory().create(communicatorCommonComponent, dependency)
    }

    companion object {

        /** @SelfDocumented */
        @JvmStatic
        fun get(context: Context): RedButtonComponent {
            /* ComponentProvider оставляем, в будущем может быть полезным для подмены реализации. */
            return RedButtonPlugin.redButtonComponent
        }
    }
}