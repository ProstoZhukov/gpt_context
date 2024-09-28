package ru.tensor.sbis.mvp.presenter.loadcontent

import io.reactivex.Observable
import ru.tensor.sbis.common.exceptions.ErrorHandler
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseReadObservableCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.ReadObservableCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.event.EventData
import ru.tensor.sbis.mvp.interactor.crudinterface.subscribing.SubscriptionManager

/**
 * Базовый презентер экрана с карточкой. В качестве способа загрузки данных
 * используется [ReadObservableCommand] в связке с CRUD фасадом.
 *
 * @author am.boldinov
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
abstract class BaseCRUDCardPresenter<V : BaseCardView<VIEW_MODEL, EMPTY_VIEW_DATA>, VIEW_MODEL, EMPTY_VIEW_DATA, UUID>(
    private val readCommand: BaseReadObservableCommand<VIEW_MODEL, UUID>,
    protected val subscriptionManager: SubscriptionManager,
    errorHandler: ErrorHandler<EMPTY_VIEW_DATA>?
) : AbstractCardPresenter<V, VIEW_MODEL, EMPTY_VIEW_DATA>(errorHandler) {

    /**
     * Идентификатор для загрузки контента.
     */
    protected abstract val contentUuid: UUID

    /**
     * Название события синхронизации.
     */
    protected abstract val syncEventName: String?

    /**
     * Нужно ли обновлять карточку по данному событию.
     */
    protected open fun isSyncRequired(data: EventData): Boolean = true

    override fun initializeLoadingProcess() {
        subscriptionManager.addConsumer { onEvent(it) }
        val batch = subscriptionManager.batch()
        configureSubscriptions(batch)
        batch.doAfterSubscribing { startLoading(force = true, showProcess = true) } // Начинаем загрузку данных
        syncEventName?.let { name -> batch.subscribeOn(name) } // Подписываемся на событие синхронизации
        batch.subscribe()
    }

    /**
     * Переопределите этот метод, если вам нужна подписка на какие-либо события
     * кроме события синхронизации.
     */
    protected open fun configureSubscriptions(batch: SubscriptionManager.Batch) {

    }

    override fun attachView(view: V) {
        super.attachView(view)
        subscriptionManager.resume()
    }

    override fun detachView() {
        super.detachView()
        subscriptionManager.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptionManager.dispose()
    }

    protected open fun onEvent(data: EventData) {
        val eventName = syncEventName
        if (eventName != null && data.isEvent(eventName)) {
            if (isSyncRequired(data)) {
                // Запускаем обновление данных по событию обновления
                startLoading(force = false, showProcess = false)
            }
        }
    }

    override fun getContentSource(refresh: Boolean): Observable<VIEW_MODEL> {
        return if (refresh) {
            readCommand.refresh(contentUuid)
        } else {
            readCommand.read(contentUuid)
        }
    }

}