package ru.tensor.sbis.catalog_decl.catalog

import androidx.annotation.WorkerThread
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Контракт чекера для проверки областей видимости по каталогу.
 *
 * @author aa.mezencev
 */
interface CatalogScopeChecker : Feature {

    /**
     * Есть ли область видимости на просмотр остатков.
     */
    @WorkerThread
    fun canViewBalancesByCatalog(): Boolean
}