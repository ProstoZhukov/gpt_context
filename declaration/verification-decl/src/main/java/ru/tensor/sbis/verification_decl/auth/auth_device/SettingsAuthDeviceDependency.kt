package ru.tensor.sbis.verification_decl.auth.auth_device

import io.reactivex.Observable

/**
 * Набор зависимостей раздела Безопасность.
 *
 * @author ar.leschev
 */
interface SettingsAuthDeviceDependency {

    /** @SelfDocumented */
    fun getDeviceListHostRouter(): DeviceListHostRouter

    /** @SelfDocumented */
    fun getAuthTypeResourceSource(): Observable<Int>
}