package ru.tensor.sbis.media.di

import dagger.Module
import dagger.Provides
import ru.tensor.sbis.media.contract.MediaDependency
import ru.tensor.sbis.storage.internal.SbisInternalStorage

/**
 * @author sa.nikitin
 */
@Module
internal class MediaModule {

    @Provides
    @MediaScope
    fun internalStorage(dependency: MediaDependency): SbisInternalStorage = dependency.internalStorage
}
