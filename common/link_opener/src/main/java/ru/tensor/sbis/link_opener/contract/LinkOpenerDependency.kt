package ru.tensor.sbis.link_opener.contract

import ru.tensor.sbis.android_ext_decl.MainActivityProvider
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.edo_decl.document.DocWebViewerFeature
import ru.tensor.sbis.toolbox_decl.linkopener.service.DecoratedLinkFeature

/**
 * Перечень зависимостей необходимых для работы [LinkOpenerFeature].
 *
 * @author as.chadov
 */
interface LinkOpenerDependency :
    MainActivityProvider,
    DocWebViewerFeature,
    DecoratedLinkFeature {

    val networkUtils: NetworkUtils
}