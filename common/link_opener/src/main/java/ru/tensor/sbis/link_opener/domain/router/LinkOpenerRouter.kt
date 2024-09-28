package ru.tensor.sbis.link_opener.domain.router

import android.content.Context
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.tensor.sbis.common.util.setNewTaskFlagIfNeeded
import ru.tensor.sbis.link_opener.domain.auth.PendingDeepLinkPrefs
import ru.tensor.sbis.link_opener.ui.LinkOpenerProgressDispatcher
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreview
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenEventHandler
import timber.log.Timber
import javax.inject.Inject

/**
 * Роутер навигации по ссылкам контента/документов.
 *
 * @param context контекст приложения.
 * @param factory фабрика обработчиков открытия ссылок на документы.
 * @param progressDispatcher диспетчер UI прогресса открытия ссылки.
 *
 * @author as.chadov
 */
internal class LinkOpenerRouter @Inject constructor(
    private val context: Context,
    private val factory: LinkOpenHandlerFactory,
    private val progressDispatcher: LinkOpenerProgressDispatcher
) {
    /**
     * Выполнить переход по событию открытия ссылки на документ [link].
     *
     * @param link данные о ссылке на контент/документ.
     * @return true если переход был обработан, иначе false.
     */
    fun navigate(link: LinkPreview): Observable<Boolean> =
        Observable.fromCallable { factory.create(link) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { processHandler(link, it) }
            .doOnError { progressDispatcher.unregister() }

    /**
     * Выполнить полученный обработчик [performNavigateAction].
     * Если специфичный прикладной обработчик [LinkOpenEventHandler.actionRouter] не смог создать интент для
     * зарегистрированного типа [preview] делегируем обработку стандартному [LinkOpenHandlerFactory.createWebViewHandler].
     */
    private fun processHandler(preview: LinkPreview, handler: LinkOpenEventHandler): Boolean {
        progressDispatcher.unregister()
        PendingDeepLinkPrefs.removeIntent(context)
        return when {
            preview.isWebViewVisitor -> performNavigateAction(preview, factory.createWebViewHandler(preview))
            performNavigateAction(preview, handler) -> true
            preview.href.isNotEmpty() -> performNavigateAction(preview, factory.createWebViewHandler(preview))
            else -> {
                Timber.e("Попытка обработать невалидное превью ссылки на документ $preview")
                false
            }
        }
    }

    private fun performNavigateAction(preview: LinkPreview, handler: LinkOpenEventHandler): Boolean {
        handler.action?.run {
            onOpen(data = preview, context = context.applicationContext)
            return true
        }
        handler.actionRouter?.onOpenIntent(preview, context.applicationContext)?.let { intent ->
            intent.setNewTaskFlagIfNeeded(context)
            context.startActivity(intent)
            return true
        }
        return false
    }
}
