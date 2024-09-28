package ru.tensor.sbis.application_tools.logcrashesinfo.crashes.models

import android.os.Build
import ru.tensor.sbis.application_tools.BuildConfig
import ru.tensor.sbis.application_tools.logcrashesinfo.deviceinfo.DeviceInfo

/**
 * @author du.bykov
 *
 * Модель данных о краше.
 */
class Crash(
    val place: String,
    val reason: String,
    val stackTrace: String,
    val date: String
) {
    private val appDetails = HashMap<String, String>()
    val deviceInfo: DeviceInfo = DeviceInfo.Builder()
        .withManufacturer(Build.MANUFACTURER)
        .withModel(Build.MODEL)
        .withBrand(Build.BRAND)
        .withSDK(Build.VERSION.SDK_INT.toString())
        .build()

    val type = Crash::class.java

    init {
        this.appDetails["Version"] = BuildConfig.VERSION_NAME
    }

    fun getAppDetails(): Map<String, String> = appDetails
}
