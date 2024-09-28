package ru.tensor.sbis.design.link_share.di

import ru.tensor.sbis.common.di.CommonSingletonComponent

/**@SelfDocumented*/
class LinkShareComponentInitializer(
    private val commonSingletonComponent: CommonSingletonComponent
) {

    /**@SelfDocumented*/
    fun init(): LinkShareComponent {
        return DaggerLinkShareComponent.factory().create(commonSingletonComponent)
    }
}