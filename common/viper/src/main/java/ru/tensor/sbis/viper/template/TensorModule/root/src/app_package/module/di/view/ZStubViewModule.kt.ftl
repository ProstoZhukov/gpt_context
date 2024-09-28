package ${packageName}.di.view

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.mvp.interactor.crudinterface.event.DefaultEventManagerServiceSubscriber
import ru.tensor.sbis.mvp.interactor.crudinterface.event.EventManagerServiceSubscriber
import ru.tensor.sbis.mvp.interactor.crudinterface.subscribing.SubscriptionManager
import ru.tensor.sbis.declaration.AndroidComponent
import ru.tensor.sbis.viper.arch.router.RouterProxy
import ${packageName}.contract.${modelName}Dependency
import ${packageName}.contract.internal.${modelName}Interactor
import ${packageName}.contract.internal.${modelName}ViewContract
import ${packageName}.contract.internal.${modelName}Router
import ${packageName}.domain.interactor.${modelName}InteractorImpl
import ${packageName}.presentation.presenter.${modelName}Presenter
import ${packageName}.presentation.router.phone.${modelName}PhoneRouter
import ${packageName}.presentation.router.tablet.${modelName}TabletRouter
import ${packageName}.presentation.viewmodel.${modelName}ViewModel
import javax.inject.Named
<#if includeList>
import ${packageName}.presentation.adapter.${modelName}Adapter
import ${commonNamespace}.crud.${moduleName}.${modelName}CommandWrapper
import ${commonNamespace}.crud.${moduleName}.${modelName}ListFilter
import ru.tensor.sbis.common.util.NetworkUtils
</#if>

@Module
internal class ${modelName}ViewModule {

    @${modelName}ViewScope
    @Provides
    internal fun provideEventManagerSubscriber(context: Context): EventManagerServiceSubscriber =
            DefaultEventManagerServiceSubscriber(context)

    @${modelName}ViewScope
    @Provides
    internal fun provideSubscriptionManager(eventManagerServiceSubscriber: EventManagerServiceSubscriber): SubscriptionManager =
            SubscriptionManager(eventManagerServiceSubscriber)

<#if includeList>
    @${modelName}ViewScope
    @Provides
    internal fun providePresenter(viewModel: ${modelName}ViewModel,
                                  interactor: ${modelName}Interactor,
                                  routerProxy: RouterProxy<${modelName}Router>,
                                  filter: ${modelName}ListFilter,
                                  subscriptionManager: SubscriptionManager,
                                  networkUtils: NetworkUtils)
            : ${modelName}ViewContract.Presenter =
            ${modelName}Presenter(viewModel, interactor, routerProxy, filter, subscriptionManager, networkUtils)
<#else>
	@${modelName}ViewScope
    @Provides
    internal fun providePresenter(viewModel: ${modelName}ViewModel,
                                  interactor: ${modelName}Interactor,
                                  routerProxy: RouterProxy<${modelName}Router>)
            : ${modelName}ViewContract.Presenter =
            ${modelName}Presenter(viewModel, interactor, routerProxy)
</#if>

<#if includeList>
    @${modelName}ViewScope
    @Provides
    internal fun provideInteractor(commandWrapper: ${modelName}CommandWrapper):
            ${modelName}Interactor =
            ${modelName}InteractorImpl(commandWrapper)
<#else>
    @${modelName}ViewScope
    @Provides
    internal fun provideInteractor():
            ${modelName}Interactor =
            ${modelName}InteractorImpl()
</#if>

    @${modelName}ViewScope
    @Provides
    internal fun provideRouterProxy(): RouterProxy<${modelName}Router> = RouterProxy()

    @Provides
    internal fun provideRouter(androidComponent: AndroidComponent,
                               dependency: ${modelName}Dependency,
                               @Named("hostContainerId") hostContainerId: Int,
                               @Named("containerId") containerId: Int,
                               @Named("subContainerId") subContainerId: Int,
                               @Named("isTablet") isTablet: Boolean):
            ${modelName}Router =
            if (!isTablet)
                ${modelName}PhoneRouter(dependency, androidComponent, containerId)
            else
                ${modelName}TabletRouter(dependency, androidComponent, hostContainerId, containerId, subContainerId)
				
<#if includeList>
    @${modelName}ViewScope
    @Provides
    internal fun provideAdapter(): ${modelName}Adapter = ${modelName}Adapter()
</#if>
}