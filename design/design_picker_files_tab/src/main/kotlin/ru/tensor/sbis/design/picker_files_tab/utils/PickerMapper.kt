package ru.tensor.sbis.design.picker_files_tab.utils

import android.net.Uri
import ru.tensor.sbis.design.files_picker.decl.SbisPickedItem

@JvmName("mapUriToPickedItem")
internal fun Uri.mapToPickedItem(): SbisPickedItem.LocalFile =
    SbisPickedItem.LocalFile(this.toString())

@JvmName("mapUrisToPickedItems")
internal fun List<Uri>.mapToPickedItems(): List<SbisPickedItem.LocalFile> =
    map { it.mapToPickedItem() }