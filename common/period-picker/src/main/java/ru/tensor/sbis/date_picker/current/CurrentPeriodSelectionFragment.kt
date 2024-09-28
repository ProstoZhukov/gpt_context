package ru.tensor.sbis.date_picker.current

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.tensor.sbis.base_components.adapter.vmadapter.ViewModelAdapter
import ru.tensor.sbis.base_components.fragment.selection.ViewDependencyProvider
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.date_picker.Period
import ru.tensor.sbis.date_picker.R
import ru.tensor.sbis.date_picker.databinding.FragmentCurrentPeriodSelectionContentBinding
import ru.tensor.sbis.date_picker.di.DatePickerComponentProvider
import ru.tensor.sbis.design.list_utils.decoration.SimpleDividerItemDecoration
import ru.tensor.sbis.mvp.presenter.BasePresenterFragment

private const val SELECTED_PERIOD_KEY = "SELECTED_PERIOD_KEY"
private const val VISIBLE_CURRENT_PERIODS_KEY = "VISIBLE_CURRENT_PERIODS_KEY"

/**
 * @author mb.kruglova
 */
class CurrentPeriodSelectionFragment :
    BasePresenterFragment<CurrentPeriodSelectionContract.View, CurrentPeriodSelectionContract.Presenter>(),
    CurrentPeriodSelectionContract.View,
    ViewDependencyProvider {

    companion object {
        val TAG = CurrentPeriodSelectionFragment::class.java.canonicalName!!

        fun newInstance(
            selectedPeriod: Period,
            visibleCurrentPeriods: List<CurrentPeriod>
        ): CurrentPeriodSelectionFragment {
            return CurrentPeriodSelectionFragment().withArgs {
                putSerializable(SELECTED_PERIOD_KEY, selectedPeriod)
                val visibleCurrentPeriodsArrayList = ArrayList(visibleCurrentPeriods.map { it.name })
                putStringArrayList(VISIBLE_CURRENT_PERIODS_KEY, visibleCurrentPeriodsArrayList)
            }
        }
    }

    private var adapter: ViewModelAdapter? = ViewModelAdapter().apply {
        cell<CurrentPeriodVm>(R.layout.item_current_period)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.run {
            val visibleCurrentPeriods =
                getStringArrayList(VISIBLE_CURRENT_PERIODS_KEY)!!.map { CurrentPeriod.valueOf(it) }
            presenter.setArgs(getSerializable(SELECTED_PERIOD_KEY) as Period, visibleCurrentPeriods)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentCurrentPeriodSelectionContentBinding.inflate(inflater, container, false).apply {
            recyclerView.adapter = adapter
            recyclerView.addItemDecoration(SimpleDividerItemDecoration(requireContext()))
        }.root
    }

    override fun getContentViewId(): Int = R.id.recycler_view

    override fun getPresenterView() = this

    override fun createPresenter(): CurrentPeriodSelectionContract.Presenter =
        DatePickerComponentProvider.get(requireActivity().application).currentPeriodSelectionPresenter

    override fun inject() {
        //ignore
    }

    override fun showData(data: List<Any>) {
        adapter?.reload(data)
    }

    override fun onDestroyView() {
        adapter = null
        super.onDestroyView()
    }
}