package ru.tensor.sbis.design.picker_files_tab.view.ui

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import ru.tensor.sbis.design.gallery.decl.GalleryComponent
import ru.tensor.sbis.design.gallery.decl.GalleryConfig
import ru.tensor.sbis.design.picker_files_tab.view.di.FRAGMENT_CONTAINER_ID_INSTANCE
import ru.tensor.sbis.design.picker_files_tab.view.di.PickerFilesTabDIScope
import ru.tensor.sbis.mvi_extension.router.BaseRouter
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

private const val CURRENT_FRAGMENT_TAG = "PickerFilesTabRouter.CURRENT_FRAGMENT_TAG"

/**
 * Роутер экрана "Вкладка Файлы".
 *
 * @author ai.abramenko
 */
@PickerFilesTabDIScope
internal class PickerFilesTabRouter @Inject constructor(
    private val galleryComponent: GalleryComponent,
    @Named(FRAGMENT_CONTAINER_ID_INSTANCE) @IdRes private val containerId: Int,
    private val storageResultLauncher: ActivityResultLauncher<Intent>,
) : BaseRouter<Fragment>() {

    /**
     * Открыть экран галереи [GalleryComponent.createFragment]
     */
    fun openGalleryScreen(galleryConfig: GalleryConfig) {
        execute {
            addFragmentWithBackStack(galleryComponent.createFragment(galleryConfig))
        }
    }

    /**
     * Открыть экран внутреннего хранилища через [ActivityResultLauncher]
     */
    fun openStorageScreen(isMultiply: Boolean) {
        storageResultLauncher.launch(
            Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, isMultiply)
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            }
        )
    }

    fun isCanNavigateBack(): Boolean {
        var isCan = false
        execute {
            isCan = childFragmentManager.findFragmentByTag(CURRENT_FRAGMENT_TAG) != null
        }
        return isCan
    }
    /**
     * Перейти на предыдущий экран.
     * Если в стеке только один фрагмент, то управление отдаём вызывающему.
     */
    fun navigateBack(): Boolean {
        var isNavigated = false
        execute {
            isNavigated = childFragmentManager.popBackStackImmediate()
        }
        return isNavigated
    }

    private fun Fragment.addFragmentWithBackStack(fragment: Fragment) {
        if (childFragmentManager.findFragmentByTag(CURRENT_FRAGMENT_TAG) != null) {
            Timber.d("$fragment already commit.")
            return
        }
        childFragmentManager.commit {
            add(containerId, fragment, CURRENT_FRAGMENT_TAG)
            addToBackStack(null)
        }
    }
}