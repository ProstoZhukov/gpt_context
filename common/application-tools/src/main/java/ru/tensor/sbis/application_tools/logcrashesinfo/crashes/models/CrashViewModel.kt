package ru.tensor.sbis.application_tools.logcrashesinfo.crashes.models

import ru.tensor.sbis.application_tools.logcrashesinfo.appinfo.models.AppInfoViewModel

/**
 * @author du.bykov
 *
 * Модель представления данных о краше.
 */
class CrashViewModel(private val crash: Crash) {

    val appInfoViewModel: AppInfoViewModel = AppInfoViewModel(crash.getAppDetails())
    val place: String
        get() {
            val placeTrail = crash.place.split("\\.".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()
            return if (placeTrail.isEmpty()) {
                NO_PLACE_MSG
            } else {
                placeTrail[placeTrail.size - 1]
            }
        }
    val exactLocationOfCrash = crash.place
    val reasonOfCrash = crash.reason
    val stackTrace = crash.stackTrace
    val deviceName = crash.deviceInfo.name
    val deviceAndroidApiVersion = crash.deviceInfo.sdk
    val deviceBrand = crash.deviceInfo.brand
    val date = crash.date

    companion object {
        private const val NO_PLACE_MSG = "Не удалось определить место краша"
    }
}