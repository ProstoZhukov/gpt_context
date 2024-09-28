package ru.tensor.sbis.mvp.presenter.loadcontent

import ru.tensor.sbis.common.exceptions.ErrorHandler
import ru.tensor.sbis.mvp.interactor.crudinterface.command.ReadObservableCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.subscribing.SubscriptionManager
import java.util.UUID

/**
 * Базовый презентер экрана с карточкой. В качестве способа загрузки данных
 * используется [ReadObservableCommand] в связке с CRUD фасадом.
 *
 * @author am.boldinov
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
abstract class BaseCardPresenter<V : BaseCardView<VIEW_MODEL, EMPTY_VIEW_DATA>, VIEW_MODEL, EMPTY_VIEW_DATA>(
    readCommand: ReadObservableCommand<VIEW_MODEL>,
    subscriptionManager: SubscriptionManager,
    errorHandler: ErrorHandler<EMPTY_VIEW_DATA>?
) : BaseCRUDCardPresenter<V, VIEW_MODEL, EMPTY_VIEW_DATA, UUID>(readCommand, subscriptionManager, errorHandler)