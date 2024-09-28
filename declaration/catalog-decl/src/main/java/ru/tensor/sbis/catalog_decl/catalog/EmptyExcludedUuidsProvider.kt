package ru.tensor.sbis.catalog_decl.catalog

import io.reactivex.Single
import kotlinx.coroutines.flow.flowOf
import java.util.*

/**
 * Поставщик пустого набора запрещенных разделов каталога.
 *
 * @author vo.sedov.
 */
object EmptyExcludedUuidsProvider : ExcludedUuidsProvider {

    override fun getFolders() = Single.just(arrayListOf<UUID>())

    override fun getAndSyncFolders() = flowOf(arrayListOf<UUID>())
}