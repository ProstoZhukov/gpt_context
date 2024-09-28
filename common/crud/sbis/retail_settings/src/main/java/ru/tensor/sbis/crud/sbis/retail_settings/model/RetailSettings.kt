package ru.tensor.sbis.crud.sbis.retail_settings.model

import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.crud.payment_settings.model.PaymentSettings
import ru.tensor.sbis.crud.payment_settings.model.map
import java.util.*
import ru.tensor.sbis.retail_settings.generated.Settings as ControllerRetailSettings

/**
 * Модель прочих настроек розницы
 *
 * @property uuid UUID - идентификатор настроек
 * @property paymentSettings PaymentSettings - Настройки оплаты
 * @property userChangeMode UserChangeMode - тип входа пользователя
 * @property shiftDateSource ShiftDateSource - тип времени открытия смены
 * @property screenLockTimeout Int - количество минут, через которое произойдет автоматическая блокировка
 * @property allowCashierCancel Boolean - флаг, обозначающий возможность удаления причин возврата всем пользователям, если true - можно всем, иначе - только администратору
 * @property returnRequireSale Boolean - флаг, обозначающий возможность делать возврат только по чеку, если true - только по чеку, иначе - нет
 * @property taskQueueCheckInterval
 * @property taskQueuePollingInterval
 * @property taskQueueExecutionTimeout
 * @property sabyDocLinkEnabled Boolean - флаг, включающий функционал по выставлению/отправке счета на онлайн-оплату
 */
data class RetailSettings(
    val uuid: UUID,
    val paymentSettings: PaymentSettings,
    var userChangeMode: UserChangeMode,
    var shiftDateSource: ShiftDateSource,
    var screenLockTimeout: Int,
    var allowCashierCancel: Boolean,
    var returnRequireSale: Boolean,
    var taskQueueCheckInterval: Int,
    var taskQueuePollingInterval: Int,
    var taskQueueExecutionTimeout: Int
) {

    companion object {
        fun stub(): RetailSettings = RetailSettings(
            UUIDUtils.NIL_UUID,
            PaymentSettings.stub(),
            UserChangeMode.LOGIN,
            ShiftDateSource.START,
            0,
            true,
            false,
            0,
            0,
            0
        )
    }
}

/**
 * Маппер для преобразования модели контроллера во вью модель
 */
fun ControllerRetailSettings.map(): RetailSettings = RetailSettings(
    uuid,
    paymentSettings.map(),
    userChangeMode.map(),
    shiftDateSource.map(),
    screenLockTimeout,
    allowCashierCancel,
    returnRequireSale,
    taskQueueCheckInterval,
    taskQueuePollingInterval,
    taskQueueExecutionTimeout
)

/**
 * Маппер для преобразования вью модели в модель контроллера
 */
fun RetailSettings.map(): ControllerRetailSettings = ControllerRetailSettings(
    uuid,
    paymentSettings.map(),
    userChangeMode.map(),
    shiftDateSource.map(),
    screenLockTimeout,
    allowCashierCancel,
    returnRequireSale,
    taskQueueCheckInterval,
    taskQueuePollingInterval,
    taskQueueExecutionTimeout
)