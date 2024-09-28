package ru.tensor.sbis.link_opener.domain

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreview
import ru.tensor.sbis.toolbox_decl.linkopener.builder.LinkOpenHandlerBuilder
import ru.tensor.sbis.toolbox_decl.linkopener.builder.LinkOpenHandlerCreator
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType
import ru.tensor.sbis.toolbox_decl.linkopener.data.LinkDocSubtype
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenHandler
import ru.tensor.sbis.link_opener.domain.builder.LinkOpenHandlerBuilderImpl
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Реализация создателя [LinkOpenHandlerCreator].
 *
 * @author as.chadov
 */
@Singleton
internal class LinkOpenHandlerCreatorImpl @Inject constructor() : LinkOpenHandlerCreator {

    override fun create(init: LinkOpenHandlerBuilder.() -> Unit): LinkOpenHandler =
        LinkOpenHandlerBuilderImpl().run {
            init()
            build()
        }

    override fun createSingle(
        type: DocType,
        vararg subtypes: LinkDocSubtype,
        handler: (LinkPreview, Context) -> Unit
    ): LinkOpenHandler = LinkOpenHandlerBuilderImpl {
        on {
            this.type = type
            if (subtypes.isNotEmpty()) {
                this.subtypes(*subtypes)
            }
            accomplish { linkPreview, context -> handler(linkPreview, context) }
        }
    }

    override fun createSingleForRouter(
        type: DocType,
        vararg subtypes: LinkDocSubtype,
        handler: (LinkPreview, Context) -> Intent?
    ): LinkOpenHandler = LinkOpenHandlerBuilderImpl {
        on {
            this.type = type
            if (subtypes.isNotEmpty()) {
                this.subtypes(*subtypes)
            }
            accomplishStart { linkPreview, context -> handler(linkPreview, context) }
        }
    }

    override fun createSingleForRouter(
        vararg type: DocType,
        handler: (LinkPreview, Context) -> Intent?
    ): LinkOpenHandler = LinkOpenHandlerBuilderImpl {
        on {
            types(*type)
            accomplishStart { linkPreview, context -> handler(linkPreview, context) }
        }
    }
}