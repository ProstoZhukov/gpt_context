package ru.tensor.sbis.mobile_services_decl

import android.app.Activity
import android.content.Context
import androidx.annotation.UiThread

/**
 * Интерфейс сервиса проверки доступности мобильных сервисов.
 *
 * @author ev.grigoreva
 */
interface ApiAvailabilityService {

    /**
     * Проверяет доступность сервисов
     */
    fun isServicesAvailable(context: Context): Boolean

    /**
     * Проверяет наличие, доступность и актуальность системных сервисов.
     */
    fun checkServicesAvailability(context: Context): ServiceConnectionResult

    /**
     * Показывает диалог с предложением обновить/включить системные сервисы в случае если их версия не актуальна
     * либо они отключены.
     * Если сервисы не установлены на устройстве то будет показан информационный диалог, что функционал с ними связанный
     * работать не будет.
     */
    @UiThread
    fun showServicesUnavailableDialog(activity: Activity)
}