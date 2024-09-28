package ru.tensor.sbis.crud4.hierarchy_storage

import android.os.Bundle
import android.os.Parcelable
import ru.tensor.sbis.service.CollectionStorageProtocol

/**
 * Предназначен для создания [ListComponentFragment] для заданной папки.
 *
 * @author ma.kolpakov
 */
interface ListComponentProvider<PATH_MODEL, COLLECTION, IDENTIFIER, FILTER> : Parcelable {

    /** @SelfDocumented */
    fun create(
        bundle: Bundle?,
        folder: PATH_MODEL?,
    ): ListComponentFragment<COLLECTION, FILTER, PATH_MODEL, IDENTIFIER>

    fun createStorage(): CollectionStorageProtocol<COLLECTION, PATH_MODEL, IDENTIFIER, FILTER>
}