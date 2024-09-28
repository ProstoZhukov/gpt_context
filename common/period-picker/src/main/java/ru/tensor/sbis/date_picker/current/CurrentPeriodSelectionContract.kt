package ru.tensor.sbis.date_picker.current

import ru.tensor.sbis.date_picker.Period
import ru.tensor.sbis.mvp.presenter.BasePresenter

/**
 * @author mb.kruglova
 */
interface CurrentPeriodSelectionContract {

    interface View {
        fun showData(data: List<Any>)
    }

    interface Presenter : BasePresenter<View> {
        fun setArgs(selectedPeriod: Period, visibleCurrentPeriods: List<CurrentPeriod>)
    }
}