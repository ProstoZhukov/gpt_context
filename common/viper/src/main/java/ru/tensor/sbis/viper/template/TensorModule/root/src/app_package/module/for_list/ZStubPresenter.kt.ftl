package ${packageName}.presentation.presenter

import io.reactivex.disposables.CompositeDisposable
import PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.ListAbstractTwoWayPaginationPresenter
import ru.tensor.sbis.mvp.interactor.crudinterface.command.ListObservableCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.event.EventData
import ru.tensor.sbis.mvp.interactor.crudinterface.subscribing.SubscriptionManager
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.viper.arch.router.RouterProxy
import ${commonNamespace}.crud.${moduleName}.${modelName}ListFilter
import ${commonNamespace}.model.${modelName}
import ${commonNamespace}.generated.${modelName}Filter
import ${packageName}.R
import ${packageName}.contract.internal.*
import ${packageName}.presentation.viewmodel.${modelName}ViewModel

internal class ${modelName}Presenter constructor(private val viewModel: ${modelName}ViewModel,
                                               private val interactor: ${modelName}Interactor,
                                               private val routerProxy: RouterProxy<${modelName}Router>,
                                               filter: ${modelName}ListFilter,
                                               subscriptionManager: SubscriptionManager,
                                               networkUtils: NetworkUtils) :
        ListAbstractTwoWayPaginationPresenter<${modelName}ViewContract.View,
                ${modelName},
                ${modelName}ListFilter,
                ${modelName}Filter>(filter, subscriptionManager, networkUtils),
        ${modelName}ViewContract.Presenter {

    private val disposer = CompositeDisposable()

    companion object {
        private const val ${"${modelName}"?upper_case}_SYNC_EVENT = "${modelName}SyncEvent"
    }

    override fun getRouterProxy() = routerProxy

    override fun isNeedToDisplayViewState(): Boolean = true

    override fun getEmptyViewErrorId(): Int = R.string.no_items_placeholder

    override fun getListObservableCommand(): ListObservableCommand<out PagedListResult<${modelName}>, ${modelName}Filter> =
    interactor.commandWrapper.listCommand

    override fun onEvent(eventData: EventData) {
        super.onEvent(eventData)
        if (eventData.isEvent(${"${modelName}"?upper_case}_SYNC_EVENT)) {
            updateDataList(false)
        }
    }

    override fun configureSubscriptions(batch: SubscriptionManager.Batch) {
        super.configureSubscriptions(batch)
        batch.subscribeOn(${"${modelName}"?upper_case}_SYNC_EVENT)
    }

    override fun displayViewState(view: ${modelName}ViewContract.View) {
        super.displayViewState(view)
        view.updateDataList(dataList, mDataListOffset)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}