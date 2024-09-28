package ${packageName}.presentation.view

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.${fragmentName}.*
import kotlinx.android.synthetic.main.${fragmentName}.view.*
import ru.tensor.sbis.mvp.fragment.BaseListFragmentWithTwoWayPagination
import ru.tensor.sbis.mvp.layoutmanager.PaginationLayoutManager
import ru.tensor.sbis.viper.helper.getViewModel
import ${commonNamespace}.model.${modelName}
import ${packageName}.R
import ${packageName}.contract.internal.${modelName}Router
import ${packageName}.contract.internal.${modelName}ViewContract
import ${packageName}.di.${modelName}ComponentHolder
import ${packageName}.di.view.Dagger${modelName}ViewComponent
import ${packageName}.di.view.${modelName}ViewComponent
import ${packageName}.presentation.adapter.${modelName}Adapter
import javax.inject.Inject

internal class ${modelName}Fragment :
        BaseListFragmentWithTwoWayPagination<
                ${modelName},
                ${modelName}Adapter,
                ${modelName}ViewContract.View,
                ${modelName}ViewContract.Presenter>(),
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

    override fun getLayoutRes(): Int = R.layout.${fragmentName}

    override fun createPresenter(): ${modelName}ViewContract.Presenter = component.getPresenter()

    override fun inject() = component.inject(this)

    @Inject
    protected fun setAdapter(adapter: ${modelName}Adapter) {
        mAdapter = adapter
    }

    override fun initViews(mainView: View, savedInstanceState: Bundle?) {
        mSbisListView = mainView.list_view
        mSbisListView?.setLayoutManager(PaginationLayoutManager(context!!))
        mSbisListView?.setAdapter(mAdapter)

        if (savedInstanceState != null) {
            restoreFromBundle(savedInstanceState)
        }
    }

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