package ru.tensor.sbis.link_opener.domain

import ru.tensor.sbis.link_opener.domain.router.LinkHandlersHolder
import ru.tensor.sbis.toolbox_decl.linkopener.LinkOpenerRegistrar
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenHandler
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Реализация регистратора [LinkOpenerRegistrar].
 *
 * @author as.chadov
 */
@Singleton
internal class LinkOpenerRegistrarImpl @Inject constructor(
    appHandlerMap: dagger.Lazy<LinkHandlersHolder>
) : LinkOpenerRegistrar {

    private val appHandlerMap: LinkHandlersHolder by lazy { appHandlerMap.get() }

    override fun register(vararg handler: LinkOpenHandler) = addToMap(*handler)

    override fun registerProvider(vararg handlerProviders: LinkOpenHandler.Provider) {
        val handlers = handlerProviders.map(LinkOpenHandler.Provider::getLinkOpenHandler)
        addToMap(*handlers.toTypedArray())
    }

    override fun unregister(vararg handler: LinkOpenHandler) = removeFromMap(*handler)

    private fun addToMap(vararg handlers: LinkOpenHandler) = appHandlerMap.addHandler(*handlers)

    private fun removeFromMap(vararg handlers: LinkOpenHandler) = appHandlerMap.removeHandler(*handlers)

}