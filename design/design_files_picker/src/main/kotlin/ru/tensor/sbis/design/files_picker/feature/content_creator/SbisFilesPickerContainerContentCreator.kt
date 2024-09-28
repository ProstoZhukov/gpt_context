package ru.tensor.sbis.design.files_picker.feature.content_creator

import android.os.Parcelable
import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.container.ContentCreator
import ru.tensor.sbis.design.container.FragmentContent
import ru.tensor.sbis.design.container.SbisContainerImpl
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTab
import ru.tensor.sbis.design.files_picker.view.FilesPickerConfig
import ru.tensor.sbis.design.files_picker.view.FilesPickerFragment

/**
 * Реализация [ContentCreator] для контейнера.
 *
 * @author ai.abramenko
 */
@Parcelize
internal class SbisFilesPickerContainerContentCreator(
    private val tabs: Set<SbisFilesPickerTab>,
    private val featureKey: String?,
    private val storeOwnerClass: Class<Any>
) : ContentCreator<FragmentContent>, Parcelable {
    override fun createContent(): FragmentContent {
        return object : FragmentContent {

            override fun getFragment(containerFragment: SbisContainerImpl): Fragment =
                FilesPickerFragment.newInstance(
                    FilesPickerConfig(featureKey = featureKey, tabs = tabs, featureStoreOwnerClass = storeOwnerClass)
                )

            override fun onRestoreFragment(containerFragment: SbisContainerImpl, fragment: Fragment) = Unit

            override fun useDefaultHorizontalOffset(): Boolean = false
        }
    }
}