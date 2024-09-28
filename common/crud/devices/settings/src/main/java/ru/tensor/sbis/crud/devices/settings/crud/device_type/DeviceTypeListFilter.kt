package ru.tensor.sbis.crud.devices.settings.crud.device_type

import ru.tensor.devices.settings.generated.DeviceTypeFilter
import ru.tensor.sbis.mvp.interactor.crudinterface.filter.AnchorPositionQueryBuilder
import ru.tensor.sbis.mvp.interactor.crudinterface.filter.ListFilter
import ru.tensor.sbis.crud.devices.settings.model.DeviceKindInside
import ru.tensor.sbis.crud.devices.settings.model.toControllerType
import java.io.Serializable
import java.util.*

/** Фильтр для списка типов подключаемого оборудования */
class DeviceTypeListFilter : Serializable, ListFilter() {

    /** Выбор по идентификатору. */
    var byId: Long? = null

    /** Выбор по имени. */
    var byName: String? = null
        set(value) {
            field = if (value.isNullOrBlank()) null else value
        }

    /** Выбор по типу. */
    private var byKind: DeviceKindInside? = null

    /** Выбор по ключу. */
    var byType: UUID? = null

    /** Выбор по папке. */
    var byFolder: Int? = null

    /** Выбор по типу, папка или нет. */
    var isFolder: Boolean? = null

    /** Выбор по поддерживаемости на мобильных устройствах. */
    var isMobileSupported: Boolean? = true

    /** Показывать скрытие устройства. */
    var showHidden: Boolean? = false

    /** Ограничение на количество извлекаемых записей. */
    var limit: Int? = null

    /** Смещение относительно начала в выборке записей. */
    var offset: Int? = null

    /** Страничное смещение относительно начала в выборке записей. */
    var pageOffset: Int? = null

    override fun queryBuilder(): Builder<*, *> =
            DeviceTypeFilterBuilder(byId,
                    byName,
                    byKind,
                    byType,
                    byFolder,
                    isFolder,
                    isMobileSupported,
                    showHidden,
                    limit,
                    offset,
                    pageOffset)
                    .searchQuery(mSearchQuery)

    private class DeviceTypeFilterBuilder(private var byId: Long?,
                                          private var byName: String?,
                                          private var byKind: DeviceKindInside?,
                                          private var byType: UUID?,
                                          private var byFolder: Int?,
                                          private var isFolder: Boolean?,
                                          private var isMobileSupported: Boolean?,
                                          private var showHidden: Boolean?,
                                          private var limit: Int?,
                                          private var offset: Int?,
                                          private var pageOffset: Int?) :
            AnchorPositionQueryBuilder<Any, DeviceTypeFilter>() {

        override fun build(): DeviceTypeFilter =
                DeviceTypeFilter(
                        byId,
                        byName,
                        byKind?.toControllerType(),
                        byType,
                        byFolder,
                        isFolder,
                        isMobileSupported,
                        showHidden,
                        limit,
                        offset,
                        pageOffset)
    }
}