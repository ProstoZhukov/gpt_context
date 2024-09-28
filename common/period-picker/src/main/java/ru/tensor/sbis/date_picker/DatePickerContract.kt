package ru.tensor.sbis.date_picker

import ru.tensor.sbis.mvp.presenter.BasePresenter
import ru.tensor.sbis.date_picker.current.CurrentPeriod

/**
 * Интерфейсы для взаимодействия компонента выбора периода с презентером
 *
 * @author mb.kruglova
 */
interface DatePickerContract {

    interface View {

        fun addItems(data: List<Any>, addToBottom: Boolean)

        fun showData(data: List<Any>, position: Int = 0, needDayLabels: Boolean = false, addBottomStub: Boolean = true)

        fun showEmptyView()

        fun closeDialog()

        fun updateTopBar(
            iconRes: Int,
            dateFromVisibility: Boolean,
            dateToVisibility: Boolean,
            titleVisibility: Boolean,
            homeVisibility: Boolean
        )

        fun updateFloatingButtons(doneVisibility: Boolean, resetVisibility: Boolean)

        fun showKeyboard()

        fun hideKeyboard()

        fun applyVisualParams(visualParams: VisualParams)

        @Deprecated("Рекомендуется использовать fun applyVisualParams(visualParams: VisualParams)")
        fun initDateMode()

        @Deprecated("Рекомендуется использовать fun applyVisualParams(visualParams: VisualParams)")
        fun initPeriodByOneClickMode()

        @Deprecated("Рекомендуется использовать fun applyVisualParams(visualParams: VisualParams)")
        fun initDateOnceMode()

        @Deprecated("Рекомендуется использовать fun applyVisualParams(visualParams: VisualParams)")
        fun initMonthOnceMode()

        fun showPeriod(periodText: PeriodText, subTitleText: String?, from: String, to: String)

        fun setDateFromError()

        fun setDateToError()

        fun setDateFromOk()

        fun setDateToOk()

        fun setDateFromCursor()

        fun setDateToCursor()

        fun showPeriodInvalidToast()

        fun showDateInvalidToast()

        fun showPeriodUnavailableToast()

        fun scrollToCurrentPeriod(position: Int)

        fun showCurrentPeriodSelectionWindowFragment(selectedPeriod: Period, visibleCurrentPeriods: List<CurrentPeriod>)

        fun returnResult(resultReceiverId: String, period: Period?)
    }

    interface Presenter : BasePresenter<View> {

        val visualParams: VisualParams?

        fun onBackPressed()

        fun setParams(params: DatePickerParams)

        fun onTitleClick()

        fun onCloseClick()

        fun onDoneClick()

        fun onModeClick()

        fun onHomeClick()

        fun onSelectCurrentPeriodClick()

        fun onResetClick()

        fun onDateToTextChanged(dateFrom: String, dateTo: String, selectionStart: Int, selectionEnd: Int)

        fun onDateFromTextChanged(dateFrom: String, dateTo: String, selectionStart: Int, selectionEnd: Int)

        fun onVisibleItemsRangeChanged(firstItemPosition: Int, lastItemPosition: Int, totalCount: Int)

        fun generatePage(isNextPage: Boolean)
    }
}