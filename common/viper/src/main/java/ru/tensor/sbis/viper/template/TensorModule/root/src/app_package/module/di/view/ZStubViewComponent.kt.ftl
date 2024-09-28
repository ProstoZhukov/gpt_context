package ${packageName}.di.view

import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.declaration.AndroidComponent
import ${packageName}.contract.${modelName}Dependency
import ${packageName}.contract.internal.${modelName}ViewContract
import ${packageName}.contract.internal.${modelName}Router
import ${packageName}.di.${modelName}Component
import ${packageName}.presentation.view.${modelName}Fragment
import ${packageName}.presentation.viewmodel.${modelName}ViewModel
import javax.inject.Named

@${modelName}ViewScope
@Component(modules = [(${modelName}ViewModule::class)],
        dependencies = [(${modelName}Component::class)])
internal interface ${modelName}ViewComponent {

    fun inject(fragment: ${modelName}Fragment)

    fun getPresenter(): ${modelName}ViewContract.Presenter
    fun getViewModel(): ${modelName}ViewModel
    fun getDependency(): ${modelName}Dependency
    fun getRouter(): ${modelName}Router

    @Component.Builder
    interface Builder {

        fun component(component: ${modelName}Component): Builder

        @BindsInstance
        fun androidComponent(androidComponent: AndroidComponent): Builder

        @BindsInstance
        fun isTablet(@Named("isTablet") isTablet: Boolean): Builder

        @BindsInstance
        fun viewModel(viewModel: ${modelName}ViewModel): Builder

        fun build(): ${modelName}ViewComponent
    }
}