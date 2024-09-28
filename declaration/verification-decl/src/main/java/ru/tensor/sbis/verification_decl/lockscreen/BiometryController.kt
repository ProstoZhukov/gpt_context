package ru.tensor.sbis.verification_decl.lockscreen

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kotlinx.coroutines.flow.SharedFlow
import ru.tensor.sbis.verification_decl.lockscreen.data.BiometryResult
import ru.tensor.sbis.verification_decl.lockscreen.data.BiometryStatus

/**
 * Контракт работы с биометрией.
 *
 * @author ar.leschev
 */
interface BiometryController {
    /**
     * Результат работы шторки биометрии, поднятой через [runBiometryOn].
     */
    val bioResultFlow: SharedFlow<BiometryResult>

    /**
     * Проверить статус биометрии.
     */
    fun getBiometryStatus(): BiometryStatus

    /**
     * Проверить можно ли вызывать шторку с биометрией.
     */
    fun isBiometryReady(): Boolean

    /**
     * Проверить недоступность биометрии.
     */
    fun isBiometryUnavailable(): Boolean

    /**
     * Показать диалог согласия с переходом в настройки.
     */
    fun showBioEnrollDialog(context: Context, fragmentManager: FragmentManager)

    /**
     * Запустить биометрию с хостом [fragment].
     */
    fun runBiometryOn(fragment: Fragment)

}