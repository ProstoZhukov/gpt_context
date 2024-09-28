package ru.tensor.sbis.catalog_decl.catalog

import io.reactivex.Observable
import io.reactivex.Single
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.verification_decl.permission.PermissionLevel
import java.util.UUID

/**
 * Источник данных список номенклатуры.
 *
 * @author sp.lomakin
 */
interface CatalogListDataSource : Feature {

    /**
     * Подписка на изменения прав пользователя.
     */
    val nomenclaturePermissionChanges: Observable<CatalogUserPermission>
        get() = Observable.never()

    /**
     * Получить список.
     */
    fun fetchItemList(filter: Filter): Single<ListResultWrapper<CatalogItemData>>

    /**
     *  Подписка на изменение данных
     */
    fun subscribeDataRefreshEvents(): Observable<DataRefreshEvent> = Observable.never()

    /**
     * Запустить синхронизацию.
     *
     * @return true - если запустили синхронизацию
     */
    fun synchronize(): Boolean = false

    /**
     * Проверка прав.
     */
    fun checkCatalogPermission(): CatalogUserPermission = CatalogUserPermission(
        base = PermissionLevel.READ,
        costPrice = true,
        stockBalances = true
    )

    /**
     *  Описание запроса.
     *
     *  [withFullFolderHierarchy] Получить полную иерархию категорий по каждому наименованию, используется для режима поиска.
     *  [warehouseId] Склад выбранный в фильтре.
     *  [organizationId] Организация выбранная в фильтре.
     *  [userWarehouse] Использовать фильтр по складу пользователя.
     *  [filterByWarehouse] true Фильтровать по складу, false получать остаток по складу.
     *  [foldersOrLeafs] true - если необходимо отбирать только папки, false, если необходимо отбирать только номенклатуры,
     *                          в случае передачи null считается, что отбираются и папки, и номенклатуры.
     * @property sort Сортировка каталога.
     */
    class Filter(
        val parentId: Long?,
        val parentUUID: UUID?,
        val byParent: Boolean,
        val priceListId: Long?,
        val byText: String?,
        val withFullFolderHierarchy: Boolean,
        val warehouseId: Long?,
        val userWarehouse: Boolean,
        val filterByWarehouse: Boolean,
        val organizationId: Long?,
        val useList: Boolean,
        val excludeAlcoholAndTobacco: Boolean,
        val count: Int,
        val offset: Int,
        val sort: CatalogSortType?,
        val foldersOrLeafs: Boolean?,
        val byMeasureUnitList: List<MeasureUnit>?,
        val byCategories: List<CategoryType>?,
        val byAccounting: List<AccountingType>?,
        val bySubAccounting: List<SubAccounting>?,
        val byDraftBeer: Boolean,
        val calcHasChilds: Int? = null
    )

    /**
     *  События об изменении данных
     */
    sealed class DataRefreshEvent {

        /**
         *  Обновление данных
         */
        class Refresh(
            val isStopSync: Boolean = false,
            val taskId: String? = null,
            val hasChanges: Boolean = true
        ) : DataRefreshEvent()

        /**
         *  Зависимый объект обновился и требует обновления нашего
         */
        object DependRefresh : DataRefreshEvent()

        class RefreshHierarchy(
            val noData: Boolean = false,
            val folders: Set<UUID?>?
        ) : DataRefreshEvent()

        class Error(val exception: Throwable) : DataRefreshEvent()
        object Unknown : DataRefreshEvent()
    }
}