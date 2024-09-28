package ru.tensor.sbis.widget_player.widget.link

import ru.tensor.sbis.edo_decl.document.DocWebViewerFeature
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext
import ru.tensor.sbis.richtext.contract.DecoratedLinkOpenDependency
import ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkOpener
import ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkRepository
import ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkRepositoryImpl
import ru.tensor.sbis.richtext.converter.cfg.style.DecoratedLinkStyle
import ru.tensor.sbis.richtext.span.decoratedlink.DefaultDecoratedLinkOpener
import ru.tensor.sbis.widget_player.config.WidgetOptionsBuilder
import ru.tensor.sbis.widget_player.contract.WidgetLinkDependency
import ru.tensor.sbis.toolbox_decl.linkopener.OpenLinkController

/**
 * @author am.boldinov
 */
class DecoratedLinkOptions(
    val decorationEnabled: Boolean,
    val repository: DecoratedLinkRepository,
    val linkOpener: DecoratedLinkOpener,
    val linkStyle: DecoratedLinkStyle
)

class DecoratedLinkOptionsBuilder(
    context: SbisThemedContext,
    private val dependency: WidgetLinkDependency
) : WidgetOptionsBuilder<DecoratedLinkOptions>() {

    private val serviceRepo get() = dependency.decoratedLinkServiceRepository
    private val linkStyle = DecoratedLinkStyle(context)

    var decorationEnabled: Boolean = serviceRepo != null
    var repository: DecoratedLinkRepository = DecoratedLinkRepositoryImpl(context, serviceRepo)
    var linkOpener: DecoratedLinkOpener = DefaultDecoratedLinkOpener(dependency.toDecoratedLinkOpenDependency())

    override fun build(): DecoratedLinkOptions {
        return DecoratedLinkOptions(decorationEnabled, repository, linkOpener, linkStyle)
    }

    private fun WidgetLinkDependency.toDecoratedLinkOpenDependency(): DecoratedLinkOpenDependency? {
        return openLinkController?.let { controller ->
            webViewerFeature?.let { webViewer ->
                object : DecoratedLinkOpenDependency, DocWebViewerFeature by webViewer {
                    override val openLinkController: OpenLinkController
                        get() = controller
                }
            }
        }
    }

}