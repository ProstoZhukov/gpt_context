package ${packageName}.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.tensor.sbis.mvp.presenter.BasePresenterFragment
import ru.tensor.sbis.viper.helper.getViewModel
import ${packageName}.R
import ${packageName}.contract.internal.${modelName}Router
import ${packageName}.contract.internal.${modelName}ViewContract
import ${packageName}.di.${modelName}ComponentHolder
import ${packageName}.di.view.Dagger${modelName}ViewComponent
import ${packageName}.di.view.${modelName}ViewComponent

internal class ${modelName}Fragment :
        BasePresenterFragment<${modelName}ViewContract.View, ${modelName}ViewContract.Presenter>(),
        ${modelName}ViewContract.View {

    companion object {
        fun newInstance(): ${modelName}Fragment {
            return ${modelName}Fragment()
        }
    }

    private val component: ${modelName}ViewComponent by lazy {
    val componentBuilder = Dagger${modelName}ViewComponent.builder()
                .component((activity!!.application as ${modelName}ComponentHolder).${"${modelName}"?lower_case}Component)
                .viewModel(getViewModel())
                .androidComponent(this)
                .isTablet(isTablet(resources))
            arguments?.run {

            }
            componentBuilder.build()
    }

    override fun getPresenterView(): ${modelName}ViewContract.View = this

    override fun createPresenter(): ${modelName}ViewContract.Presenter = component.getPresenter()

    override fun inject() = component.inject(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
    inflater.inflate(R.layout.${fragmentName}, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(component.getViewModel()) {

        }
    }

    override fun onStart() {
        super.onStart()
        presenter.getRouterProxy().router = component.getRouter()
    }

    override fun onStop() {
        presenter.getRouterProxy().router = null
        super.onStop()
    }
}