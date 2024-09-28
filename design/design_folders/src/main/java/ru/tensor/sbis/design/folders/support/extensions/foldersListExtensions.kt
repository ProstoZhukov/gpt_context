/**
 * Расширения для списка папок
 *
 * @author ma.kolpakov
 */
package ru.tensor.sbis.design.folders.support.extensions

import ru.tensor.sbis.design.folders.data.model.Folder
import ru.tensor.sbis.design.folders.support.FoldersViewModel

/** @SelfDocumented */
internal fun List<Folder>.getNameById(id: String) = this.find { it.id == id }?.title.orEmpty()

/** @SelfDocumented */
internal fun FoldersViewModel.getFolder(id: String) = checkNotNull(folders.value!!.find { it.id == id }) {
    "Cannot find folder with id $id"
}