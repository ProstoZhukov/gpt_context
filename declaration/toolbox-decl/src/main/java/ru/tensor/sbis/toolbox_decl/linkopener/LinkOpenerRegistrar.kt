package ru.tensor.sbis.toolbox_decl.linkopener

import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenHandler
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс регистратора прикладных обработчиков [LinkOpenHandler] предоставляемых Feature/модулями реализующих [LinkOpenHandler.Provider].
 *
 * @author as.chadov
 */
interface LinkOpenerRegistrar : Feature {

    /**
     * Зарегистрировать новые обработчики открытия контента/документов [handler].
     *
     * @param handler прикладной обработчика открытия.
     */
    fun register(vararg handler: LinkOpenHandler)

    /**
     * Зарегистрировать новые поставщики реализации [LinkOpenHandler].
     *
     * @param handlerProviders поставщик [LinkOpenHandler].
     */
    fun registerProvider(vararg handlerProviders: LinkOpenHandler.Provider)

    /**
     * Удалить регистрацию обработчиков открытия контента/документов [handler].
     */
    fun unregister(vararg handler: LinkOpenHandler)

    /**
     * Поставщик регистратора прикладных обработчиков открытия ссылок [LinkOpenerRegistrar].
     */
    interface Provider : Feature {
        val linkOpenerRegistrar: LinkOpenerRegistrar
    }
}