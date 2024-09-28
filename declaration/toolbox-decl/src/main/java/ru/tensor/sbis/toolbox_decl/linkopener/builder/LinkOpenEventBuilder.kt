package ru.tensor.sbis.toolbox_decl.linkopener.builder

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreview
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType
import ru.tensor.sbis.toolbox_decl.linkopener.data.LinkDocSubtype

/**
 * Интерфейс билдера обработчика события открытия ссылки на контент/документ типа [type], подтипа [subtype].
 *
 * @author as.chadov
 */
interface LinkOpenEventBuilder {

    /** Установить поддерживаемый тип ссылки. */
    var type: DocType

    /** Установить поддерживаемый подтип ссылки. */
    var subtype: LinkDocSubtype

    /** Установить поддерживаемые типы ссылок. */
    fun types(vararg type: DocType)

    /** Установить поддерживаемые подтипы ссылок. */
    fun subtypes(vararg types: LinkDocSubtype)

    /** Назначить действие [action] выполняемое по событию открытия данного типа ссылок. */
    fun accomplish(action: (LinkPreview) -> Unit)

    /** Назначить действие [action] выполняемое по событию открытия данного типа ссылок с предоставлением контекста приложения. */
    fun accomplish(action: (LinkPreview, Context) -> Unit)

    /** Назначить действие [action] выполняемое для получения [Intent] что будет запущен [Context.startActivity] из компонента. */
    fun accomplishStart(action: (LinkPreview) -> Intent?)

    /**
     * Назначить действие [action] с предоставлением контекста приложения выполняемое для получения [Intent], что будет
     * запущен [Context.startActivity] из компонента.
     */
    fun accomplishStart(action: (LinkPreview, Context) -> Intent?)
}