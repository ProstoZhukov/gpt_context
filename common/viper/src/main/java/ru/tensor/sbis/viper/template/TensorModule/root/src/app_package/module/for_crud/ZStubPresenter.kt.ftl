package ${packageName}.presentation.presenter

import io.reactivex.disposables.CompositeDisposable
import ru.tensor.sbis.viper.arch.router.RouterProxy
import ${packageName}.contract.internal.${modelName}Interactor
import ${packageName}.contract.internal.${modelName}Router
import ${packageName}.contract.internal.${modelName}ViewContract
import ${packageName}.presentation.viewmodel.${modelName}ViewModel

internal class ${modelName}Presenter constructor(private val viewModel: ${modelName}ViewModel,
                                          private val interactor: ${modelName}Interactor,
                                          private val routerProxy: RouterProxy<${modelName}Router>) :
        ${modelName}ViewContract.Presenter {

    private val disposer = CompositeDisposable()

    override fun getRouterProxy() = routerProxy

    override fun attachView(view: ${modelName}ViewContract.View) {
    }

    override fun detachView() {
    }

    override fun onDestroy() {
    }
}