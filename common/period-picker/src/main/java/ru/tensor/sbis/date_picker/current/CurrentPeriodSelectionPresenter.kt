package ru.tensor.sbis.date_picker.current

import ru.tensor.sbis.common.rx.RxBus
import ru.tensor.sbis.date_picker.Period

/**
 * @author mb.kruglova
 */
class CurrentPeriodSelectionPresenter(private val bus: RxBus, private val vmFactory: CurrentPeriodVmFactory) :
    CurrentPeriodSelectionContract.Presenter {

    private var isInitialized = false
    private var view: CurrentPeriodSelectionContract.View? = null
    private lateinit var selectedPeriod: Period
    private lateinit var visibleCurrentPeriods: List<CurrentPeriod>

    private val onItemClick: (Period) -> Unit = {
        bus.post(CurrentPeriodSelectedEvent(it))
    }

    override fun attachView(view: CurrentPeriodSelectionContract.View) {
        this.view = view
        this.view?.showData(vmFactory.createItems(onItemClick, selectedPeriod, visibleCurrentPeriods))
    }

    override fun detachView() {
        view = null
    }

    override fun onDestroy() {
        //not implemented
    }

    override fun setArgs(selectedPeriod: Period, visibleCurrentPeriods: List<CurrentPeriod>) {
        if (isInitialized) return

        this.selectedPeriod = selectedPeriod
        this.visibleCurrentPeriods = visibleCurrentPeriods
        isInitialized = true
    }
}