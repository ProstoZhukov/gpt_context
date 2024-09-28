package ru.tensor.sbis.link_opener.domain.utils

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreview
import ru.tensor.sbis.toolbox_decl.linkopener.action.OnDocumentIntentListener
import ru.tensor.sbis.toolbox_decl.linkopener.action.OnDocumentOpenListener

/**
 * Обработчик при открытии документа по ссылке.
 */
internal typealias OpenLinkAction = (LinkPreview) -> Unit
/**
 * Обработчик при открытии документа по ссылке с доступом к контексту.
 */
internal typealias OpenLinkActionWithContext = (LinkPreview, Context) -> Unit

/**
 * Создатель интерфейса обработчика открытия ссылки на документ [OnDocumentOpenListener].
 *
 * @author as.chadov
 */
internal object OnDocumentOpenListenerCreator {

    /** Создать обработчик [OnDocumentOpenListener] для колбэка с превью ссылки. */
    fun create(action: OpenLinkAction): OnDocumentOpenListener =
        object : OnDocumentOpenListener {
            override fun onOpen(data: LinkPreview, context: Context?) = action(data)
        }

    /** Создать обработчик [OnDocumentOpenListener] для колбэка с превью ссылки и контекстом приложения. */
    fun create(action: OpenLinkActionWithContext): OnDocumentOpenListener =
        object : OnDocumentOpenListener {
            override fun onOpen(data: LinkPreview, context: Context?) = action(data, requireNotNull(context))
        }

    /** Создать обработчик [OnDocumentIntentListener] для колбэка с превью ссылки. */
    fun createIntent(action: (LinkPreview) -> Intent?): OnDocumentIntentListener =
        object : OnDocumentIntentListener {
            override fun onOpenIntent(data: LinkPreview, context: Context?): Intent? = action(data)
        }

    /** Создать обработчик [OnDocumentIntentListener] для колбэка с превью ссылки и контекстом приложения. */
    fun createIntent(action: (LinkPreview, Context) -> Intent?): OnDocumentIntentListener =
        object : OnDocumentIntentListener {
            override fun onOpenIntent(data: LinkPreview, context: Context?): Intent? =
                action(data, requireNotNull(context))
        }
}
