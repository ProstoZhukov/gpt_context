package ru.tensor.sbis.recipient_selection.employee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.tensor.sbis.android_ext_decl.IntentAction
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.communication_decl.employeeselection.EmployeesSelectionFilter
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.stubview.StubView
import ru.tensor.sbis.design.stubview.StubViewContent
import ru.tensor.sbis.mvp.multiselection.MultiSelectionFragment
import ru.tensor.sbis.recipient_selection.R
import ru.tensor.sbis.recipient_selection.employee.di.EmployeeSelectionComponent
import ru.tensor.sbis.recipient_selection.employee.di.EmployeeSelectionComponentProvider
import ru.tensor.sbis.common.R as RCommon

/**
 * Фрагмент выбора получателей из сотрудников.
 */
internal class EmployeeSelectionFragment : MultiSelectionFragment(), EmployeeSelectionViewContract {

    companion object {
        private const val TITLE_KEY = "TITLE_KEY"

        fun newInstance(parameters: EmployeesSelectionFilter) = EmployeeSelectionFragment().apply {
            arguments = parameters.getBundle()
        }
    }

    private lateinit var component: EmployeeSelectionComponent
    private var title: TextView? = null
    private var stubView: StubView? = null
    private var searchView: View? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private val isTablet: Boolean get() = DeviceConfigurationUtils.isTablet(requireContext())
            && arguments?.getBoolean(IntentAction.Extra.TABLET_ACTION) ?: false

    override fun inject() {
        if (arguments != null) {
            component = EmployeeSelectionComponentProvider.getEmployeesSelectionComponent(
                requireActivity().application,
                EmployeesSelectionFilter(requireArguments())
            )
        }
        component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = ThemeContextBuilder(
            context = requireContext(),
            fragment = this,
            defStyleAttr = R.attr.employeeSelectionTheme,
            defaultStyle = R.style.Custom_AppTheme_Swipe_Back
        ).build()
            .let(LayoutInflater::from)
            .run {
                super.onCreateView(this, container, savedInstanceState)!!
            }

        return addToSwipeBackLayout(view)
    }

    override fun initToolbar(mainView: View) {
        super.initToolbar(mainView)
        title = mToolbar!!.findViewById(RCommon.id.sharing_title)
        setTitle(getString(R.string.recipient_selection_employees_title))
    }

    override fun initViews(mainView: View, savedInstanceState: Bundle?) {
        super.initViews(mainView, savedInstanceState)
        stubView = mainView.findViewById(R.id.stub_view)
        searchView = mainView.findViewById(R.id.search_filter_panel)

        swipeRefreshLayout = mainView.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)?.apply {
            setColorSchemeResources(ru.tensor.sbis.design.R.color.color_accent_3)
            setOnRefreshListener {
                presenter!!.onRefresh()
            }
        }
    }

    override fun setTitle(title: String?) {
        this.title!!.text = title
    }

    override fun listUpdated() {
        swipeRefreshLayout?.isRefreshing = false
    }

    override fun showStubView(content: StubViewContent?) {
        if (content == null) {
            stubView?.visibility = View.GONE
            searchView?.visibility = View.VISIBLE
        } else {
            stubView?.setContent(content)
            stubView?.visibility = View.VISIBLE
            searchView?.visibility = View.GONE
        }
    }

    override fun onBackPressed() =
        presenter!!.onBackButtonClicked()

    override fun onCloseButtonClick() {
        if (!presenter!!.onBackButtonClicked()) {
            presenter.finishSelection(false)
            if (activity !is EmployeeSelectionActivity) activity?.onBackPressed()
        }
    }

    override fun finishSelection() {
        (activity as? EmployeeSelectionActivity)?.finish()
    }

    override fun getLayoutRes() = R.layout.recipient_selection_fragment_for_repost

    override fun createPresenter() = component.getPresenter()

    override fun onDestroyView() {
        super.onDestroyView()
        title = null
    }

    override fun needToRemoveAllViews(): Boolean {
        return false
    }

    override fun swipeBackEnabled() = !isTablet

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TITLE_KEY, title!!.text.toString())
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            setTitle(savedInstanceState.getString(TITLE_KEY))
        }
    }
}