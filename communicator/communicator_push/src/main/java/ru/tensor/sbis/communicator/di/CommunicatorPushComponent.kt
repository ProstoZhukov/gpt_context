package ru.tensor.sbis.communicator.di

import android.content.Context
import dagger.Component
import ru.tensor.sbis.communicator.CommunicatorPushPlugin
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent
import ru.tensor.sbis.communicator.contract.CommunicatorPushDependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import javax.inject.Scope

/**
 * DI scope компонента пушей сообщений
 *
 * @author vv.chekurda
 */
@Scope
@kotlin.annotation.Retention
internal annotation class CommunicatorPushScope

/**
 * DI компонент пушей сообщений
 */
@CommunicatorPushScope
@Component(
    dependencies = [CommunicatorCommonComponent::class, CommunicatorPushDependency::class]
)
interface CommunicatorPushComponent : Feature {

    val dependency: CommunicatorPushDependency

    @Component.Builder
    interface Builder {
        fun communicatorCommonComponent(communicatorCommonComponent: CommunicatorCommonComponent): Builder
        fun dependency(dependency: CommunicatorPushDependency): Builder
        fun build(): CommunicatorPushComponent
    }

    class Initializer(private val dependency: CommunicatorPushDependency) {

        fun init(communicatorCommonComponent: CommunicatorCommonComponent): CommunicatorPushComponent =
            DaggerCommunicatorPushComponent.builder()
                .communicatorCommonComponent(communicatorCommonComponent)
                .dependency(dependency)
                .build()
    }

    interface Holder {
        val communicatorPushComponent: CommunicatorPushComponent
    }

    companion object {
        @JvmStatic
        fun getInstance(context: Context): CommunicatorPushComponent {
            return when(val app = context.applicationContext) {
                is Holder -> app.communicatorPushComponent
                else -> CommunicatorPushPlugin.communicatorPushComponent
            }
        }
    }
}