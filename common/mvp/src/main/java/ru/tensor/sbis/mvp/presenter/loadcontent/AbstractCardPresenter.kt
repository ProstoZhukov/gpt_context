package ru.tensor.sbis.mvp.presenter.loadcontent

import androidx.annotation.CallSuper
import androidx.annotation.UiThread
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.internal.functions.Functions
import ru.tensor.sbis.common.exceptions.ErrorHandler
import ru.tensor.sbis.common.exceptions.LoadDataException
import ru.tensor.sbis.common.rx.consumer.FallbackErrorConsumer
import timber.log.Timber

/**
 * Абстрактный презентер экрана с карточкой. Описывает основное
 * поведение карточки при загрузке данных, обрабатывает ошибки.
 * Не декларирует способ загрузки данных.
 *
 * @author am.boldinov
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
abstract class AbstractCardPresenter<V : BaseCardView<VIEW_MODEL, EMPTY_VIEW_DATA>, VIEW_MODEL, EMPTY_VIEW_DATA>(
    private val errorHandler: ErrorHandler<EMPTY_VIEW_DATA>?
) : AbstractLoadContentPresenter<V>() {

    private val loadContentDisposable = SerialDisposable()
    private val uiActionDisposable = CompositeDisposable()
    private var syncFailed = false
    private var syncError: LoadDataException? = null
    protected var viewModel: VIEW_MODEL? = null

    @Suppress("MemberVisibilityCanBePrivate")
    protected var notFound = false

    /**
     * Нужно ли отображать текущую вью-модель в момент присоединения вью.
     */
    protected open fun needDisplayOnAttach(viewModel: VIEW_MODEL): Boolean = true

    override fun attachView(view: V) {
        super.attachView(view)
        viewModel?.let {
            if (needDisplayOnAttach(it)) {
                // Данные уже загружены, отображаем
                displayData(view, it)
            }
        } ?: run {
            if (!notFound) {
                // Переводим empty view в режим ожидания данных
                view.updateEmptyView(null)
            }
            if (loadingStatus == LoadingStatus.NOT_LOADED) {
                initializeLoadingProcess()
            }
        }
    }

    /**
     * Инициализировать загрузку данных по карточке.
     */
    protected abstract fun initializeLoadingProcess()

    /**
     * Уведомить presenter о том, что процесс синхронизации не удался.
     */
    protected fun notifySyncFailed(error: LoadDataException?) {
        // Если во время загрузки произошла ошибка или сейчас не происходит загрузка, сразу обрабатываем
        val handleError = syncError != null || loadingStatus != LoadingStatus.IN_PROGRESS
        syncFailed = true
        syncError = error
        if (handleError) {
            handleSyncError()
        }
    }

    /**
     * Обработать ошибку синхронизации.
     */
    private fun handleSyncError() {
        onLoadingError(syncError)
        resetSyncError()
    }

    /**
     * Сбросить информацию об ошибке синхронизации.
     */
    private fun resetSyncError() {
        syncFailed = false
        syncError = null
    }

    @UiThread
    override fun startLoading(force: Boolean, showProcess: Boolean) {
        if (loadingStatus == LoadingStatus.NOT_LOADED) {
            mView?.showLoadingProcess(true)
        }
        super.startLoading(force, showProcess && !notFound)
    }

    override fun loadContent(force: Boolean) {
        loadContentDisposable.set(
            getContentSource(!force)
                .subscribe(
                    { vm ->
                        onViewModelLoaded(vm)
                        resetSyncError()
                    },
                    { throwable ->
                        if (throwable is LoadDataException && throwable.type == LoadDataException.Type.NOT_LOADED_YET) {
                            if (syncFailed) {
                                handleSyncError()
                            } else {
                                syncError = throwable
                            }
                        } else {
                            Timber.d(throwable)
                            onLoadingError(throwable as? LoadDataException)
                            resetSyncError()
                        }
                    },
                    { onLoadingCompleted() }
                )
        )
    }

    /**
     * Получить источник для загрузки данных по карточке.
     *
     * @param refresh true - нужно выполнить обновление данных из кеша,
     * false - нужно выполнить обновление данных с сервера
     */
    protected abstract fun getContentSource(refresh: Boolean): Observable<VIEW_MODEL>

    /**
     * Обработать событие загрузки модели данных.
     */
    @CallSuper
    @UiThread
    protected open fun onViewModelLoaded(viewModel: VIEW_MODEL) {
        notFound = false
        this.viewModel = viewModel
        requestDisplayData()
    }

    override fun onLoadingError(exception: LoadDataException?) {
        if (exception?.type != LoadDataException.Type.NOT_LOADED_YET) {
            if (exception?.type == LoadDataException.Type.NOT_FOUND) {
                notFound = true
                viewModel = null
                onDataNotFound()
            }
            super.onLoadingError(exception)
        }
    }

    /**
     * Обработать отсутствие данных для отображения.
     */
    protected open fun onDataNotFound() {
        // Очищаем карточку
        mView?.clearData()
    }

    @UiThread
    protected fun requestDisplayData() {
        mView?.let { view ->
            viewModel?.let { viewModel ->
                displayData(view, viewModel)
            }
        }
    }

    @UiThread
    protected open fun displayData(view: V, viewModel: VIEW_MODEL) {
        // Отображаем данные и скрываем empty view
        view.displayData(viewModel)
        view.updateEmptyView(null)
    }

    @UiThread
    override fun showLoadingError(view: V, exception: LoadDataException?) {
        if (errorHandler != null) {
            // Получаем данные для отображение ошибки
            val (userMessage, emptyViewData) = errorHandler.getErrorData(exception)
            // Отображаем empty view
            emptyViewData?.let { view.updateEmptyView(it) }
            if (emptyViewData == null && exception?.type != LoadDataException.Type.NOT_FOUND) {
                // Отображаем toast
                userMessage?.let { view.showLoadingError(userMessage) }
            }
        } else {
            Timber.d("Error occurred, but exception errorTranslator not specified.")
        }
    }

    protected fun runOnUiThread(action: () -> Unit) {
        uiActionDisposable.add(
            Completable.fromAction(action)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(Functions.EMPTY_ACTION, FallbackErrorConsumer.DEFAULT)
        )
    }

    override fun onDestroy() {
        if (!loadContentDisposable.isDisposed) {
            loadContentDisposable.dispose()
        }
        uiActionDisposable.dispose()
    }

}