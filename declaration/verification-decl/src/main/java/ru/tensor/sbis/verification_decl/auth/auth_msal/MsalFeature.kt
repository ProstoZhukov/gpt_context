package ru.tensor.sbis.verification_decl.auth.auth_msal

import android.app.Activity
import io.reactivex.Observable
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс, описывающий фичу работы с MSAL библиотекой.
 *
 * @author ar.leschev
 */
interface MsalFeature : Feature {

    /**
     * Логин через MSAL
     *
     * @param activity MSAL требует активность для запуска браузера или активости приложения-брокера (например, Microsoft Authenticator)
     * @param ssoClientId id клиента, требуется для установки корректного клиента на вебе
     * @param providerName имя провайдера, требуется на вебе
     * @param authUrl url авторизации. Необходимо для совместной работы единичных и мульти-тинантных приложений на Azure.
     */
    fun login(activity: Activity, ssoClientId: String, providerName: String, authUrl: String)

    /**
     * Разлогин через MSAL.
     */
    fun logout()

    /**
     * Прокидывает результат работы со MSAL.
     */
    val msalFeatureResult: Observable<MsalResponse>

    /**
     * Провайдер фичи.
     */
    interface Provider : Feature {
        /** @SelfDocumented */
        val msalFeature: MsalFeature?
    }
}