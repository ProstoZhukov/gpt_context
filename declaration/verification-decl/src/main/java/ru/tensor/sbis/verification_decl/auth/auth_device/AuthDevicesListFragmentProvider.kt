package ru.tensor.sbis.verification_decl.auth.auth_device

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Провайдер для фрагмента со списком сессий авторизации.
 * Встраивается в результат [AuthDevicesFragmentProvider].
 *
 * @author ar.leschev
 */
interface AuthDevicesListFragmentProvider : Feature {

    /**
     * Метод для получения фрагмента со списком устройств авторизации.
     */
    fun getListFragment(): Fragment

}