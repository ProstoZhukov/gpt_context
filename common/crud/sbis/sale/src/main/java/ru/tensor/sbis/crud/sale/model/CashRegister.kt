package ru.tensor.sbis.crud.sale.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.crud.devices.settings.model.RemoteWorkplaceInfo

/**
 * Модель для кассового аппарата/банковского терминала
 */
@Parcelize
data class CashRegister(
        var id: Long = 0,
        var type: Type? = null,
        var modelName: String? = null,
        var port: String? = null,
        var deviceName: String? = null,
        var ipAddress: String? = null,
        var tcpPort: Int? = 0,
        var serialNumber: String? = null,
        var registerNumber: String? = null,
        var imageUrl: String? = null,
        var macAddress: String = "",
        var driver: String? = null,
        var isActive: Boolean = true,
        var cloudActive: Boolean = true,
        var isConnected: Boolean = false,
        var vid: Int = 0,
        var pid: Int = 0,
        var connectionType: Int? = 0,
        var prefix: String? = null,
        var suffix: String? = null,
        var timeout: Int? = 0,
        var companyId: Long? = 0,
        var workplaceId: Long? = 0,
        var kkmId: Long? = null,
        var remoteKkmId: Long? = null,
        var printReceiptEnabled: Boolean = false,
        var printTicket: Boolean = false,
        var remoteWorkplaces: List<RemoteWorkplaceInfo>? = null,
        var login: String = "",
        var password: String = "",
        var host: String? = null,
        var passwordReadId: Long? = null,
        var countryCode: Int = 0
) : Parcelable {

    enum class Type {
        CASH_REGISTER, // кассовый аппарат
        @Suppress("unused")
        BANK_TERMINAL  // банковский терминал
    }
}


/**
 * Метод позволяет проверить, является ли касса расшаренной
 * (установлена на другом рабочем месте, но доступная для работы)
 *
 * @return true если касса расшаренная
 */
fun CashRegister.isRemote(): Boolean {
    return remoteKkmId != null
}