package ru.tensor.sbis.crud.devices.settings.model

import ru.tensor.sbis.common.util.UUIDUtils
import java.util.*
import ru.tensor.devices.settings.generated.Workplace as ControllerWorkplace

/**
 * Модель рабочего места
 *
 * @param id Int - Идентификатор рабочего места
 * @param key UUID - Идентификатор рабочего места
 * @param name String - Наименование рабочего места
 * @param visible Boolean - Показывать ли рабочее место в списке
 * @param deviceId String? - Идентификатор устройства на котором запущено приложение
 * @param deviceName String? - Имя устройства на котором запущено приложение
 * @param company Int - Привязка рабочего места к организации
 * @param syncStatus SettingsSyncStatus - Статус синхронизации
 * @param workplaceSettings WorkplaceSettings - Настройки рабочего места
 * @param devices ArrayList<DeviceInside> - Список устройств привязанных к рабочему месту
 *
 * @see UUID
 * @see SettingsSyncStatus
 * @see WorkplaceSettings
 * @see DeviceInside
 */
data class Workplace(val id: Long,
                     val key: UUID,
                     val name: String,
                     var visible: Boolean,
                     val deviceId: String?,
                     val deviceName: String?,
                     val company: Long,
                     val syncStatus: SettingsSyncStatus,
                     val workplaceSettings: WorkplaceSettings,
                     var devices: ArrayList<DeviceInside>) {

    companion object {
        fun stub(): Workplace = Workplace(
                0,
                UUIDUtils.NIL_UUID,
                "",
                false,
                null,
                null,
                0,
                SettingsSyncStatus.NOT_REQUIRED,
                WorkplaceSettings.stub(),
                arrayListOf())

        fun create(workplace: Workplace, init: Builder.() -> Unit) = Builder(workplace, init).build()

        class Builder private constructor(val workplace: Workplace) {

            constructor(workplace: Workplace, init: Builder.() -> Unit) : this(workplace) {
                init()
            }

            var name: String? = null
            var deviceId: String? = null
            var deviceName: String? = null
            var company: Long? = null
            var visible: Boolean? = null

            fun name(init: Builder.() -> String?) = apply { name = init() }
            fun deviceId(init: Builder.() -> String?) = apply { deviceId = init() }
            fun deviceName(init: Builder.() -> String?) = apply { deviceName = init() }
            fun company(init: Builder.() -> Long?) = apply { company = init() }
            fun visible(init: Builder.() -> Boolean?) = apply { visible = init() }

            fun build() = Workplace(this)
        }
    }

    private constructor(builder: Builder) : this(
            builder.workplace.id,
            builder.workplace.key,
            builder.name ?: builder.workplace.name,
            builder.visible ?: builder.workplace.visible,
            builder.deviceId,
            builder.deviceName,
            builder.workplace.company,
            builder.workplace.syncStatus,
            builder.workplace.workplaceSettings,
            builder.workplace.devices)
}

/**
 * Маппер для преобразования модели контроллера во вью модель
 */
fun ControllerWorkplace.map(): Workplace = Workplace(
        id,
        key ?: UUIDUtils.NIL_UUID,
        name,
        visible,
        deviceId,
        deviceName,
        company,
        syncStatus.map(),
        settings.map(),
        arrayListOf())

/**
 * Маппер для преобразования вью модели в модель контроллера
 */
fun Workplace.map(): ControllerWorkplace = ControllerWorkplace(
        id,
        key,
        name,
        visible,
        deviceId,
        deviceName,
        company,
        workplaceSettings.map(),
        syncStatus.map())
