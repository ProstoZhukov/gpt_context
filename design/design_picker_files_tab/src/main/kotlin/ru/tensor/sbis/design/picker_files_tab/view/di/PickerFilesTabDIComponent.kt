package ru.tensor.sbis.design.picker_files_tab.view.di

import android.app.Application
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.IdRes
import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.design.gallery.decl.GalleryComponentFactory
import ru.tensor.sbis.design.picker_files_tab.feature.PickerFilesTabFeature
import ru.tensor.sbis.design.picker_files_tab.view.PickerFilesTabConfig
import ru.tensor.sbis.design.picker_files_tab.view.PickerFilesTabFragment
import ru.tensor.sbis.design.picker_files_tab.view.ui.PickerFilesTabView
import javax.inject.Named

internal const val FRAGMENT_CONTAINER_ID_INSTANCE = "fragmentContainerId"

/**
 * Dagger Component для экрана "Вкладка Файлы".
 *
 * @author ai.abramenko
 */
@PickerFilesTabDIScope
@Component(modules = [PickerFilesTabDIModule::class])
internal interface PickerFilesTabDIComponent {

    fun inject(fragment: PickerFilesTabFragment)

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance application: Application,
            @BindsInstance fragment: PickerFilesTabFragment,
            @BindsInstance viewFactory: PickerFilesTabView.Factory,
            @BindsInstance config: PickerFilesTabConfig,
            @BindsInstance tabFeature: PickerFilesTabFeature,
            @BindsInstance galleryComponentFactory: GalleryComponentFactory,
            @BindsInstance @Named(FRAGMENT_CONTAINER_ID_INSTANCE) @IdRes containerId: Int,
            @BindsInstance storageResultLauncher: ActivityResultLauncher<Intent>
        ): PickerFilesTabDIComponent
    }
}