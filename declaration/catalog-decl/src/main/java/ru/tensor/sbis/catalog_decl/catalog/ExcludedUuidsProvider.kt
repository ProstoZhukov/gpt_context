package ru.tensor.sbis.catalog_decl.catalog

import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.*
import kotlin.collections.ArrayList

/**
 * Контракт поставщика запрещенных разделов каталога.
 *
 * @author aa.mezencev.
 */
interface ExcludedUuidsProvider : Feature {

    /** @SelfDocumented */
    fun getFolders(): Single<ArrayList<UUID>>

    /** @SelfDocumented */
    fun getAndSyncFolders(): Flow<ArrayList<UUID>>
}