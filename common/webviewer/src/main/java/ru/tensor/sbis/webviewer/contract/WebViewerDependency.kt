package ru.tensor.sbis.webviewer.contract

import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.network_native.apiservice.contract.ApiService

/**
 * Перечень зависимостей необходимых для работы DocWebViewer
 *
 * @author ma.kolpakov
 */
interface WebViewerDependency :
    LoginInterface.Provider,
    ApiService.Provider