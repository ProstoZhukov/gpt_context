package ru.tensor.sbis.design.picker_files_tab.view.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.timetravel.store.TimeTravelStoreFactory
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.BuildConfig
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTab
import ru.tensor.sbis.design.gallery.decl.GalleryComponent
import ru.tensor.sbis.design.gallery.decl.GalleryComponentFactory
import ru.tensor.sbis.design.picker_files_tab.view.PickerFilesTabFragment
import ru.tensor.sbis.mvi_extension.AndroidStoreFactory
import ru.tensor.sbis.mvi_extension.LabelBufferStrategy

/**
 * Dagger модуль для экрана "Вкладка Файлы".
 *
 * @author ai.abramenko
 */
@Module
internal class PickerFilesTabDIModule {

    @Provides
    @PickerFilesTabDIScope
    fun provideStoreFactory(): StoreFactory =
        if (BuildConfig.DEBUG) {
            LoggingStoreFactory(AndroidStoreFactory(TimeTravelStoreFactory(), LabelBufferStrategy.Buffer()))
        } else {
            AndroidStoreFactory(DefaultStoreFactory(), LabelBufferStrategy.Buffer())
        }

    @Provides
    @PickerFilesTabDIScope
    fun provideGalleryComponent(
        fragment: PickerFilesTabFragment,
        galleryComponentFactory: GalleryComponentFactory
    ): GalleryComponent =
        galleryComponentFactory.createGalleryComponent(SbisFilesPickerTab.Gallery(), fragment)
}