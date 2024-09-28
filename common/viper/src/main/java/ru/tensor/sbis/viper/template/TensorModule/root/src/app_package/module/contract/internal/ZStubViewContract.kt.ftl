package ${packageName}.contract.internal

import ru.tensor.sbis.viper.arch.router.RouterProxyPresenter
import ru.tensor.sbis.viper.arch.view.IsTabletView
import ${commonNamespace}.model.${modelName}
<#if includeList>
import ru.tensor.sbis.mvp.presenter.BaseTwoWayPaginationPresenter
import ru.tensor.sbis.mvp.presenter.BaseTwoWayPaginationView
<#elseif includeCrud>
import ru.tensor.sbis.mvp.presenter.BasePresenter
</#if>

internal interface ${modelName}ViewContract {

<#if includeList>
	interface View : BaseTwoWayPaginationView<${modelName}>, IsTabletView {
    }

    interface Presenter : BaseTwoWayPaginationPresenter<View>,
            RouterProxyPresenter<${modelName}Router> {
    }
<#elseif includeCrud>
    interface View : IsTabletView {
    }

    interface Presenter : BasePresenter<View>,
            RouterProxyPresenter<${modelName}Router> {
    }
</#if>
}