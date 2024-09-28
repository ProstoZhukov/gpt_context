package ru.tensor.sbis.design.link_share.di

import android.content.Context
import dagger.Component
import ru.tensor.sbis.common.di.CommonSingletonComponent

/**@SelfDocumented*/
@LinkShareScope
@Component(
    dependencies = [(CommonSingletonComponent::class)]
)
interface LinkShareComponent {

    /**@SelfDocumented*/
    fun getContext(): Context

    @Component.Factory
    interface Factory {
        /**@SelfDocumented*/
        fun create(commonSingletonComponent: CommonSingletonComponent): LinkShareComponent
    }
}