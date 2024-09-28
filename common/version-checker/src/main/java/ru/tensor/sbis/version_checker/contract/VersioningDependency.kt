package ru.tensor.sbis.version_checker.contract

import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.feature_ctrl.SbisFeatureService
import ru.tensor.sbis.network_native.apiservice.contract.ApiService
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.version_checker_decl.VersioningFeature
import ru.tensor.sbis.version_checker_decl.VersioningSettings
import ru.tensor.sbis.webviewer.contract.WebViewerFeature

/**
 * Перечень зависимостей необходимых для работы [VersioningFeature].
 *
 * @author as.chadov
 */
interface VersioningDependency :
    VersioningSettings.Provider,
    ApiService.Provider {

    /** @SelfDocumented */
    val networkUtils: NetworkUtils

    /** Опциональная зависимость т.к. не все МП поддерживают работу с сервисом авторизации */
    val loginInterfaceProvider: LoginInterface.Provider?

    /** Опциональная зависимость т.к. не все МП поддерживают работу с сервисом авторизации требуемым для фичи */
    val webViewerFeatureProvider: WebViewerFeature.Provider?

    /** Опциональная зависимость т.к. не все МП поддерживают работу с сервисом проверки фичей*/
    val sbisFeatureService: SbisFeatureService?
}