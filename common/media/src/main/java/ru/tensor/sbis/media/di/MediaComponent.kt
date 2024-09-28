package ru.tensor.sbis.media.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.common.util.uri.UriWrapper
import ru.tensor.sbis.media.MediaPlugin
import ru.tensor.sbis.media.contract.MediaDependency
import ru.tensor.sbis.storage.internal.SbisInternalStorage

/**
 * @author sa.nikitin
 */
@MediaScope
@Component(dependencies = [CommonSingletonComponent::class], modules = [MediaModule::class])
abstract class MediaComponent {

    abstract val context: Context
    abstract val uriWrapper: UriWrapper
    abstract val dependency: MediaDependency
    abstract val sbisInternalStorage: SbisInternalStorage

    @Component.Builder
    interface Builder {

        fun commonSingletonComponent(commonSingletonComponent: CommonSingletonComponent): Builder

        @BindsInstance
        fun dependency(dependency: MediaDependency): Builder

        fun build(): MediaComponent
    }

    companion object {

        @JvmStatic
        fun fromContext(context: Context): MediaComponent {
            /* ComponentProvider оставляем, в будущем может быть полезным для подмены реализации. */
            return MediaPlugin.mediaComponent
        }
    }
}
