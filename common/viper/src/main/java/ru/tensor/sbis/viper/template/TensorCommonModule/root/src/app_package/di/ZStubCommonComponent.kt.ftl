package ${packageName}.di

import android.content.Context
import android.content.SharedPreferences
import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.common.contract.CommonDependency
import ru.tensor.sbis.common.data.mapper.base.BaseItemMapper
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.common.util.scroll.ScrollHelper
import ru.tensor.sbis.common.rx.RxBus
import ${packageName}.contract.${moduleName}CommonDependency
import ${packageName}.contract.${moduleName}CommonFeature
import javax.inject.Named

@${moduleName}CommonScope
@Component(dependencies = [CommonSingletonComponent::class],
        modules = [${moduleName}CommonModule::class])
interface ${moduleName}CommonComponent {

    fun getFeature(): ${moduleName}CommonFeature

    fun getDependency(): ${moduleName}CommonDependency

    fun getBaseItemMapper(): BaseItemMapper

    @Named("containerId")
    fun getContainerId(): Int

    @Named("subContainerId")
    fun getSubContainerId(): Int

    // Common

    fun getContext(): Context

    fun getCommonDependency(): CommonDependency

    fun getNetworkUtils(): NetworkUtils

    fun getScrollHelper(): ScrollHelper

    fun getRxBus(): RxBus

    fun getResourceProvider(): ResourceProvider

    fun getSharedPreferences(): SharedPreferences

    @Component.Builder
    interface Builder {

        fun commonSingletonComponent(commonSingletonComponent: CommonSingletonComponent): Builder

        @BindsInstance
        fun dependency(dependency: ${moduleName}CommonDependency): Builder

        @BindsInstance
        fun containerId(@Named("containerId") containerId: Int): Builder

        @BindsInstance
        fun subContainerId(@Named("subContainerId") subContainerId: Int): Builder

        fun build(): ${moduleName}CommonComponent
    }
}