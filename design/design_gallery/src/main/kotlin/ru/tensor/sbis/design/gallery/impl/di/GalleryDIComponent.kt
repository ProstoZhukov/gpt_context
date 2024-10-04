package ru.tensor.sbis.design.gallery.impl.di

import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.Context
import android.view.View
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.timetravel.store.TimeTravelStoreFactory
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.BuildConfig
import ru.tensor.sbis.common.util.FileUriUtil
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.design.gallery.decl.GalleryConfig
import ru.tensor.sbis.design.gallery.impl.GalleryComponentImpl
import ru.tensor.sbis.design.gallery.impl.ui.GalleryFragment
import ru.tensor.sbis.design.gallery.impl.ui.GalleryController
import ru.tensor.sbis.design.gallery.impl.ui.GalleryView
import ru.tensor.sbis.mvi_extension.AndroidStoreFactory
import ru.tensor.sbis.mvi_extension.LabelBufferStrategy
import javax.inject.Named
import javax.inject.Scope

internal const val NEED_ONLY_IMAGES_BOOL_NAME = "need_only_images_bool_name"

@Scope
@Retention(AnnotationRetention.RUNTIME)
internal annotation class GalleryDIScope

@GalleryDIScope
@Component(modules = [GalleryDIModule::class])
internal interface GalleryDIComponent {

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance fragment: GalleryFragment,
            @BindsInstance view: (View) -> GalleryView,
            @BindsInstance config: GalleryConfig,
            @BindsInstance component: GalleryComponentImpl,
            @BindsInstance context: Context,
            @BindsInstance resourceProvider: ResourceProvider,
            @BindsInstance contentResolver: ContentResolver,
            @BindsInstance fileUriUtil: FileUriUtil,
            @BindsInstance clipboardManager: ClipboardManager?,
            @BindsInstance @Named(NEED_ONLY_IMAGES_BOOL_NAME) needOnlyImages: Boolean,
        ): GalleryDIComponent

    }

    fun injectController(): GalleryController
}

@Module
class GalleryDIModule {

    @Provides
    @GalleryDIScope
    fun provideStoreFactory(): StoreFactory =
        if (BuildConfig.DEBUG) {
            LoggingStoreFactory(AndroidStoreFactory(TimeTravelStoreFactory(), LabelBufferStrategy.Buffer()))
        } else {
            AndroidStoreFactory(DefaultStoreFactory(), LabelBufferStrategy.Buffer())
        }
}