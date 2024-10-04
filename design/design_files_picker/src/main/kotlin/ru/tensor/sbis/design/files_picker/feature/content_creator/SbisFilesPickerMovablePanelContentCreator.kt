package ru.tensor.sbis.design.files_picker.feature.content_creator

import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTab
import ru.tensor.sbis.design.files_picker.view.FilesPickerConfig
import ru.tensor.sbis.design.files_picker.view.FilesPickerFragment
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreatorParcelable

/**
 * Реализация [ContentCreatorParcelable] для шторки.
 *
 * @author ai.abramenko
 */
@Parcelize
internal class SbisFilesPickerMovablePanelContentCreator(
    private val tabs: Set<SbisFilesPickerTab>,
    private val featureKey: String?,
    private val storeOwnerClass: Class<Any>
) : ContentCreatorParcelable {

    override fun createFragment(): Fragment =
        FilesPickerFragment.newInstance(
            FilesPickerConfig(featureKey = featureKey, tabs = tabs, featureStoreOwnerClass = storeOwnerClass)
        )
}