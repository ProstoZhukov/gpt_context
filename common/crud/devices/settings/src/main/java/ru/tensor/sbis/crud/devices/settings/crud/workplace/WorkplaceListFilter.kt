package ru.tensor.sbis.crud.devices.settings.crud.workplace

import ru.tensor.devices.settings.generated.WorkplaceFilter
import ru.tensor.sbis.mvp.interactor.crudinterface.filter.AnchorPositionQueryBuilder
import ru.tensor.sbis.mvp.interactor.crudinterface.filter.ListFilter
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.crud.devices.settings.model.SettingsSyncStatus
import ru.tensor.sbis.crud.devices.settings.model.VisibilityType
import ru.tensor.sbis.crud.devices.settings.model.map
import java.io.Serializable
import java.util.*

/** Фильтр для получения информации о рабочем месте */
class WorkplaceListFilter : Serializable, ListFilter() {

    /** Выбор по ключу */
    private var byKey: UUID? = null
        set(value) {
            field = if (value == UUIDUtils.NIL_UUID) null else value
        }

    /** Выбор по списку ключей */
    private var byKeys: ArrayList<UUID>? = null

    /** Выбор по имени */
    var byName: String? = null
        set(value) {
            field = if (value.isNullOrBlank()) null else value
        }

    /** Выбор по идентификатору машины */
    private var byDeviceId: String? = null
        set(value) {
            field = if (value.isNullOrBlank()) null else value
        }

    /** Выбор по наименованию машины */
    private var byDeviceName: String? = null
        set(value) {
            field = if (value.isNullOrBlank()) null else value
        }

    /** Выбор по компании */
    var byCompany: Long? = null
        set(value) {
            field = if (value == 0L) null else value
        }

    /** Выбор по свойству видимости */
    var byVisible: VisibilityType = VisibilityType.VISIBLE_ONLY

    /** Выбор по статусу синхронизации */
    private var bySyncStatus: SettingsSyncStatus? = null

    /** Ограничение на количество извлекаемых записей */
    var limit: Int? = null
        set(value) {
            field = if (value == 0) null else value
        }

    /** Смещение относительно начала в выборке записей */
    var offset: Int? = null
        set(value) {
            field = if (value == 0) null else value
        }

    /** Страничное смещение относительно начала в выборке записей */
    private var pageOffset: Int? = null
        set(value) {
            field = if (value == 0) null else value
        }

    override fun queryBuilder(): Builder<*, *> =
            WorkplaceFilterBuilder(byKey,
                    byKeys,
                    byName,
                    byVisible,
                    byDeviceId,
                    byDeviceName,
                    byCompany,
                    bySyncStatus,
                    limit,
                    offset,
                    pageOffset)
                    .searchQuery(mSearchQuery)

    private class WorkplaceFilterBuilder(private var byKey: UUID?,
                                         private var byKeys: ArrayList<UUID>?,
                                         private var byName: String?,
                                         private var byVisible: VisibilityType,
                                         private var byDeviceId: String?,
                                         private var byDeviceName: String?,
                                         private var byCompany: Long?,
                                         private var bySyncStatus: SettingsSyncStatus?,
                                         private var limit: Int?,
                                         private var offset: Int?,
                                         private var pageOffset: Int?) :
            AnchorPositionQueryBuilder<Any, WorkplaceFilter>() {

        override fun build(): WorkplaceFilter =
                WorkplaceFilter(byKey,
                        byKeys,
                        byName,
                        byVisible.map(),
                        byDeviceId,
                        byDeviceName,
                        byCompany,
                        bySyncStatus?.map(),
                        limit,
                        offset,
                        pageOffset)
    }
}