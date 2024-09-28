package ru.tensor.sbis.crud4.hierarchy_storage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import ru.tensor.sbis.service.CollectionStorageProtocol
import ru.tensor.sbis.service.PathProtocol

/** @SelfDocumented */
internal class HierarchyStorageViewModel<COLLECTION, PATH_MODEL : PathProtocol<IDENT>, IDENT, FILTER>(
    val storage: CollectionStorageProtocol<COLLECTION, PATH_MODEL, IDENT, FILTER>,
    val savedStateHandle: SavedStateHandle
): ViewModel() {

    override fun onCleared() {
        storage.dispose()
    }
}