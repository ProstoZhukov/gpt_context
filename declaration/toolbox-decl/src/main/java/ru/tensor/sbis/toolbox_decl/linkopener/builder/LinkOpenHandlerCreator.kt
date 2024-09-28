package ru.tensor.sbis.toolbox_decl.linkopener.builder

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreview
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType
import ru.tensor.sbis.toolbox_decl.linkopener.data.LinkDocSubtype
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenHandler
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс создателя прикладного обработчика [LinkOpenHandler] посредством DSL.
 * Рекомендовано использовать для упрощённого создания.
 *
 * @author as.chadov
 */
interface LinkOpenHandlerCreator : Feature {

    /** Создать прикладной обработчик [LinkOpenHandler] через описание [LinkOpenHandlerBuilder]. */
    fun create(init: LinkOpenHandlerBuilder.() -> Unit): LinkOpenHandler

    /**
     * Создать упрощенный обработчик [LinkOpenHandler] что поддерживает ссылки только одного типа [type].
     *
     * @param handler прикладная обработка для ссылок типа [type], подтипа [subtypes]
     * с входным превью ссылки [LinkPreview] и контекстом приложения [Context].
     */
    fun createSingle(
        type: DocType,
        vararg subtypes: LinkDocSubtype = emptyArray(),
        handler: (LinkPreview, Context) -> Unit
    ): LinkOpenHandler

    /**
     * Создать упрощенный обработчик [LinkOpenHandler] что поддерживает ссылки только одного типа [type]
     * и отдает их на обработку роутеру приложения [RouterInterface].
     *
     * @param handler прикладная обработка для ссылок типа [type], подтипа [subtypes]
     * с входным превью ссылки [LinkPreview] и контекстом приложения [Context].
     */
    fun createSingleForRouter(
        type: DocType,
        vararg subtypes: LinkDocSubtype = emptyArray(),
        handler: (LinkPreview, Context) -> Intent?
    ): LinkOpenHandler

    /**
     * Создать упрощенный обработчик [LinkOpenHandler] что поддерживает ссылки нескольких типов [type]
     * и отдает их на обработку роутеру приложения [RouterInterface].
     *
     * @param handler прикладная обработка для ссылок типов [type]
     * с входным превью ссылки [LinkPreview] и контекстом приложения [Context].
     */
    fun createSingleForRouter(
        vararg type: DocType,
        handler: (LinkPreview, Context) -> Intent?
    ): LinkOpenHandler

    /**
     * Поставщик реализации [LinkOpenHandlerCreator] для упрощенного создания обработчика [LinkOpenHandler].
     * Должен наследоваться Dependency интерфейсом прикладной области/модуля что реализует [LinkOpenHandler].
     */
    interface Provider : Feature {
        val linkOpenerHandlerCreator: LinkOpenHandlerCreator
    }
}