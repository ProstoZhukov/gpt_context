package ru.tensor.sbis.verification_decl.auth.auth_device

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Поставщик фрагмента Безопасность с опциональным фрагментом списка сессий.
 *
 * @author ar.leschev
 */
interface AuthDevicesFragmentProvider : Feature {

    /**
     * Получить фрагмент экрана девайсов.
     */
    fun getDeviceListFragment(
        withNavigation: Boolean = true,
        withChangePassword: Boolean = true,
        withQrToWebScanner: Boolean = true
    ): Fragment

}