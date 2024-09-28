package ru.tensor.sbis.media.di

import ru.tensor.sbis.common.di.BaseSingletonComponentInitializer
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.media.contract.MediaDependency

/**
 * @author sa.nikitin
 */
class MediaComponentInitializer(
    private val dependency: MediaDependency
) : BaseSingletonComponentInitializer<MediaComponent>() {

    override fun createComponent(commonSingletonComponent: CommonSingletonComponent): MediaComponent =
        DaggerMediaComponent.builder()
            .commonSingletonComponent(commonSingletonComponent)
            .dependency(dependency)
            .build()
}
