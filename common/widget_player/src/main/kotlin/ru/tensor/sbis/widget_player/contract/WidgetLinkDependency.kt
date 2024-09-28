package ru.tensor.sbis.widget_player.contract

import ru.tensor.sbis.edo_decl.document.DocWebViewerFeature
import ru.tensor.sbis.toolbox_decl.linkopener.OpenLinkController
import ru.tensor.sbis.toolbox_decl.linkopener.service.LinkDecoratorServiceRepository

/**
 * @author am.boldinov
 */
interface WidgetLinkDependency {

    val decoratedLinkServiceRepository: LinkDecoratorServiceRepository?

    val openLinkController: OpenLinkController?

    val webViewerFeature: DocWebViewerFeature?
}