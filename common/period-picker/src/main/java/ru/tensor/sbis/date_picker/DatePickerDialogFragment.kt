package ru.tensor.sbis.date_picker

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.tensor.sbis.common.util.getCompatColor
import ru.tensor.sbis.common.util.isTablet
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.date_picker.adapter.DatePickerAdapter
import ru.tensor.sbis.date_picker.adapter.viewholder.ItemMonthDayEmptyViewHolder
import ru.tensor.sbis.date_picker.adapter.viewholder.ItemMonthDayViewHolder
import ru.tensor.sbis.date_picker.current.CurrentPeriod
import ru.tensor.sbis.date_picker.current.CurrentPeriodSelectionContentCreator
import ru.tensor.sbis.date_picker.current.CurrentPeriodSelectionFragment
import ru.tensor.sbis.date_picker.databinding.DatePickerLayoutBinding
import ru.tensor.sbis.date_picker.databinding.FragmentDatePickerBinding
import ru.tensor.sbis.date_picker.di.DatePickerComponentHolder
import ru.tensor.sbis.date_picker.di.DatePickerComponentProvider
import ru.tensor.sbis.date_picker.items.BottomStub
import ru.tensor.sbis.design.container.createParcelableFragmentContainer
import ru.tensor.sbis.design.container.locator.AnchorHorizontalLocator
import ru.tensor.sbis.design.container.locator.AnchorVerticalLocator
import ru.tensor.sbis.design.container.locator.HorizontalAlignment
import ru.tensor.sbis.design.container.locator.VerticalAlignment
import ru.tensor.sbis.design.utils.KeyboardUtils
import ru.tensor.sbis.design.utils.LONG_CLICK_DELAY
import ru.tensor.sbis.design.utils.extentions.preventDoubleClickListener
import ru.tensor.sbis.design.utils.getThemeColorByThemeId
import ru.tensor.sbis.design_dialogs.dialogs.container.ContentActionHandler.Helper
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanelPeekHeight
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableFragment
import ru.tensor.sbis.mvp.presenter.BasePresenterDialogFragment

const val PARAMS_KEY = "PARAMS_KEY"
const val DATE_PICKER_RESULT = "DATE_PICKER_RESULT"
const val DATE_PICKER_RESULT_RECEIVER_ID = "DATE_PICKER_RESULT_RECEIVER_ID"

/**
 * Универсальный компонент выбора периода.
 * Разработан согласно стандарту http://axure.tensor.ru/MobileStandart8/#p=%D0%B2%D1%8B%D0%B1%D0%BE%D1%80_%D0%BF%D0%B5%D1%80%D0%B8%D0%BE%D0%B4%D0%B0_ver_2&g=1
 *
 * @author mb.kruglova
 */
open class DatePickerDialogFragment :
    BasePresenterDialogFragment<DatePickerContract.View, DatePickerContract.Presenter>(), DatePickerContract.View {

    @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
    private val periodPicker: DatePickerLayoutBinding
        get() = mBinding.periodPicker!! // не убирать non-null assertion - сборка на CI ломается

    private lateinit var mBinding: FragmentDatePickerBinding
    private val mYearListAdapter = DatePickerAdapter()
    private lateinit var mTitle: String
    private val layoutManager: GridLayoutManager?
        get() = periodPicker.calendar.layoutManager as? GridLayoutManager

    private var bottomStub: BottomStub? = null

    private val isImmersiveFullScreen: Boolean
        get() = presenter.visualParams?.isImmersiveFullScreen ?: false

    private val onScrollListener = DatePickerScrollListener { _, newState ->
        if (newState == SCROLL_STATE_IDLE) {
            updateVisibleItemsRange()
        }
    }

    /**
     * При клике по контейнеру фокус переходит к нему и клавиатура остаётся, ввод получает
     * контейнер, поэтому просто прячем клавиатуру (причём поле единсвенное в окне).
     * https://online.sbis.ru/opendoc.html?guid=24ec6b03-6d32-45b8-ab0f-2cabe011040d
     * https://online.sbis.ru/opendoc.html?guid=869e07c4-fffb-4707-b496-15957d3798ac
     */
    private val rootFocusChangeListener =
        ViewTreeObserver.OnGlobalFocusChangeListener { fromView, toView ->
            if (fromView === toView && fromView === periodPicker.root) {
                hideKeyboard()
            }
        }

    companion object {
        private const val MAX_RECYCLED_DAY_VIEWS = 90
        private const val MAX_RECYCLED_EMPTY_VIEWS = 20
        private const val DAYS_OFFSET_TO_PAGE = 30
        private const val MAX_FLING = 4000

        /**
         * Создание экземпляра диалога выбора периода
         * @param params параметры диалога выбора периода
         */
        @JvmStatic
        fun newInstance(params: DatePickerParams): DatePickerDialogFragment {
            return DatePickerDialogFragment().withArgs {
                putSerializable(PARAMS_KEY, params)
            }
        }
    }

    override fun createPresenter(): DatePickerContract.Presenter {
        return run {
            val parentFragment = parentFragment
            val parentActivity = activity
            when {
                parentFragment is DatePickerComponentHolder -> parentFragment
                parentActivity is DatePickerComponentHolder -> parentActivity
                else -> DatePickerComponentProvider.getComponentHolder()
            }
        }.datePickerComponent.datePickerPresenter
    }

    override fun getPresenterLoaderId() = R.id.date_picker_presenter_loader_id

    override fun inject() = Unit

    override fun getPresenterView() = this

    @IntegerRes
    override fun getTheme() =
        presenter.visualParams?.styleId ?: R.style.PickerStyle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (arguments?.getSerializable(PARAMS_KEY) as? DatePickerParams)?.let { presenter.setParams(it) }
    }

    override fun onStart() {
        super.onStart()
        initFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_date_picker, null, false)
        mBinding.executePendingBindings()
        if (isImmersiveFullScreen) {
            mBinding.periodPickerRoot.fitsSystemWindows = true
        }

        initViews()
        initViewsListeners()
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isImmersiveFullScreen) {
            // нужно сделать диалог non-focusable перед отображением, чтобы предотвратить сброс флагов видимости UI
            setDialogWindowFocusable(false)
            configureImmersiveFullscreen()
        }
        dialog?.setOnShowListener {
            if (isImmersiveFullScreen) {
                // после показа диалога можно сбросить значение FLAG_NOT_FOCUSABLE
                setDialogWindowFocusable(true)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        with(periodPicker) {
            root.viewTreeObserver.removeOnGlobalFocusChangeListener(rootFocusChangeListener)
            calendar.adapter = null
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireActivity(), theme) {
            override fun onBackPressed() {
                presenter.onBackPressed()
            }
        }
    }

    private fun initFragment() {
        with(requireDialog().window!!) {
            // Флаг нужен чтобы задание statusBarColor работало
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = getColorFromAttr(R.attr.date_picker_status_bar_color)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            if (!isImmersiveFullScreen) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or getLightBarFlags()
            }
        }
    }

    private fun initViews() {
        mTitle = getString(R.string.date_picker_title)
        with(periodPicker.calendar) {
            layoutManager = DatePickerLayoutManager(context, mYearListAdapter) {
                updateVisibleItemsRange()
            }

            adapter = mYearListAdapter
            recycledViewPool.setMaxRecycledViews(ItemMonthDayViewHolder.ITEM_TYPE, MAX_RECYCLED_DAY_VIEWS)
            recycledViewPool.setMaxRecycledViews(ItemMonthDayEmptyViewHolder.ITEM_TYPE, MAX_RECYCLED_EMPTY_VIEWS)
            setItemViewCacheSize(MAX_RECYCLED_DAY_VIEWS + MAX_RECYCLED_EMPTY_VIEWS)
            setHasFixedSize(true)
            addOnScrollListener(onScrollListener)

            // замедление скролла - быстрый скролл листает рывками
            onFlingListener = object : RecyclerView.OnFlingListener() {
                override fun onFling(velocityX: Int, velocityY: Int): Boolean {
                    return when {
                        velocityY > MAX_FLING -> {
                            fling(velocityX, MAX_FLING)
                            true
                        }
                        velocityY < -MAX_FLING -> {
                            fling(velocityX, -MAX_FLING)
                            true
                        }
                        else -> false
                    }
                }
            }
        }
    }

    private fun isNeedLoadPage(isNextPage: Boolean): Boolean {
        val layoutManager = periodPicker.calendar.layoutManager as DatePickerLayoutManager
        val position = if (isNextPage) {
            val count: Int = periodPicker.calendar.layoutManager?.itemCount ?: 0
            count - layoutManager.findLastVisibleItemPosition()
        } else {
            layoutManager.findFirstVisibleItemPosition()
        }
        return position <= DAYS_OFFSET_TO_PAGE
    }

    @SuppressLint("CheckResult")
    private fun initViewsListeners() {
        with(periodPicker) {
            title.setOnClickListener { presenter.onTitleClick() }
            close.setOnClickListener { presenter.onCloseClick() }
            done.setOnClickListener { presenter.onDoneClick() }
            mode.setOnClickListener { presenter.onModeClick() }
            home.setOnClickListener { presenter.onHomeClick() }
            selectCurrentPeriod.preventDoubleClickListener(LONG_CLICK_DELAY) {
                presenter.onSelectCurrentPeriodClick()
            }
            reset.setOnClickListener { presenter.onResetClick() }

            calendar.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0 && isNeedLoadPage(true)) presenter.generatePage(true)
                    if (dy < 0 && isNeedLoadPage(false)) presenter.generatePage(false)
                }
            })
            root.viewTreeObserver.addOnGlobalFocusChangeListener(rootFocusChangeListener)

            RxTextView.afterTextChangeEvents(dateFrom)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    presenter.onDateFromTextChanged(
                        dateFrom.text.toString(),
                        dateTo.text.toString(),
                        dateFrom.selectionStart,
                        dateFrom.selectionEnd
                    )
                }

            RxTextView.afterTextChangeEvents(dateTo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    presenter.onDateToTextChanged(
                        dateFrom.text.toString(),
                        dateTo.text.toString(),
                        dateTo.selectionStart,
                        dateTo.selectionEnd
                    )
                }
        }

        mBinding.periodPickerRoot.setOnClickListener { presenter.onCloseClick() }
    }

    private fun getColorFromAttr(@AttrRes colorAttr: Int) =
        requireContext().getCompatColor(requireContext().getThemeColorByThemeId(colorAttr, theme))

    override fun showData(data: List<Any>, position: Int, needDayLabels: Boolean, addBottomStub: Boolean) {
        mYearListAdapter.clear()
        val items = data.toMutableList()
        if (addBottomStub) bottomStub?.let { items.add(it) }
        mYearListAdapter.reload(items)
        with(periodPicker) {
            calendar.visibility = View.VISIBLE
            dayLabels.visibility = if (needDayLabels) View.VISIBLE else View.GONE
            dayLabelsDivider.visibility = if (needDayLabels) View.VISIBLE else View.GONE
            emptyView.visibility = View.GONE
            layoutManager?.scrollToPositionWithOffset(position, 0)
        }
    }

    override fun addItems(data: List<Any>, addToBottom: Boolean) {
        mYearListAdapter.insertItems(data, addToBottom)
    }

    override fun showEmptyView() {
        with(periodPicker) {
            calendar.visibility = View.GONE
            dayLabels.visibility = View.GONE
            dayLabelsDivider.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        }
    }

    override fun returnResult(resultReceiverId: String, period: Period?) {
        Helper.getActionHandler(this)?.onContentAction(
            DATE_PICKER_RESULT,
            Bundle().apply {
                putString(DATE_PICKER_RESULT_RECEIVER_ID, resultReceiverId)
                if (period != null) putSerializable(DATE_PICKER_RESULT, period)
            }
        )
    }

    override fun closeDialog() {
        hideKeyboard()
        dismissAllowingStateLoss()
    }

    override fun updateTopBar(
        iconRes: Int,
        dateFromVisibility: Boolean,
        dateToVisibility: Boolean,
        titleVisibility: Boolean,
        homeVisibility: Boolean
    ) {
        with(periodPicker) {
            mode.setText(iconRes)
            dateFrom.visibility = if (dateFromVisibility) View.VISIBLE else View.GONE
            dash.visibility = if (dateToVisibility) View.VISIBLE else View.INVISIBLE
            dateTo.visibility = if (dateToVisibility) View.VISIBLE else View.INVISIBLE
            textGroup.visibility = if (titleVisibility) View.VISIBLE else View.GONE
            home.visibility = if (homeVisibility) View.VISIBLE else View.INVISIBLE
        }
    }

    override fun updateFloatingButtons(doneVisibility: Boolean, resetVisibility: Boolean) {
        with(periodPicker) {
            done.visibility = if (doneVisibility) View.VISIBLE else View.GONE
            reset.visibility = if (resetVisibility) View.VISIBLE else View.GONE
        }
    }

    override fun showKeyboard() {
        /*
        Если visualParams.dateFromAndToFocusableInTouchMode == false (см. метод applyVisualParams)
        то нет смысла поднимать клавиатуру - фокус поле всё равно не получит и следовательно ввод с
        неё не будет происходить во вьюшку. Воспроизводится на эмуляторе, на устройстве не
        происходит поднятия на вьюшку без фокуса.
        */
        if (!periodPicker.dateFrom.isFocusableInTouchMode) return
        KeyboardUtils.showKeyboard(periodPicker.dateFrom)
    }

    override fun hideKeyboard() {
        val imm = periodPicker.root.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(periodPicker.root.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        periodPicker.root.clearFocus()
    }

    override fun applyVisualParams(visualParams: VisualParams) {
        initBottomStubIfNeeded(visualParams)
        mTitle = getString(visualParams.defaultTitle)
        with(periodPicker) {
            title.isClickable = visualParams.titleClickable
            mode.visibility = visualParams.modeVisibility
            if (visualParams.modeVisibility != View.VISIBLE) modeStub.visibility = View.VISIBLE
            mode.isClickable = visualParams.modeClickable
            if (!visualParams.modeClickable) mode.setTextColor(getColorFromAttr(R.attr.date_picker_buttons_color))
            home.visibility = visualParams.homeVisibility
            selectCurrentPeriod.visibility = visualParams.selectCurrentPeriodVisibility
            dateFrom.isFocusableInTouchMode = visualParams.dateFromAndToFocusableInTouchMode
            dateTo.isFocusableInTouchMode = visualParams.dateFromAndToFocusableInTouchMode
            done.visibility = visualParams.doneVisibility
            reset.visibility = visualParams.resetVisibility
        }
    }

    private fun initBottomStubIfNeeded(visualParams: VisualParams) {
        if (visualParams.doneVisibility == View.VISIBLE ||
            visualParams.selectCurrentPeriodVisibility == View.VISIBLE ||
            visualParams.resetVisibility == View.VISIBLE
        ) bottomStub = BottomStub()
    }

    override fun initDateMode() {
        mTitle = getString(R.string.date_picker_date_picker_title)
        with(periodPicker) {
            mode.visibility = View.GONE
            modeStub.visibility = View.VISIBLE
            selectCurrentPeriod.visibility = View.INVISIBLE
        }
    }

    override fun initPeriodByOneClickMode() {
        with(periodPicker) {
            dateFrom.isFocusableInTouchMode = false
            dateTo.isFocusableInTouchMode = false
        }
    }

    override fun initDateOnceMode() {
        mTitle = getString(R.string.date_picker_date_picker_title)
        with(periodPicker) {
            selectCurrentPeriod.visibility = View.GONE
            done.visibility = View.GONE
            reset.visibility = View.VISIBLE
        }
    }

    override fun initMonthOnceMode() {
        with(periodPicker) {
            title.isClickable = false
            mode.visibility = View.GONE
            modeStub.visibility = View.VISIBLE
            selectCurrentPeriod.visibility = View.GONE
            done.visibility = View.GONE
        }
    }

    override fun showPeriod(periodText: PeriodText, subTitleText: String?, from: String, to: String) {
        with(periodPicker) {
            title.text = if (periodText.isNotEmpty) periodText.toString() else mTitle
            if (title.lineCount > 1) {
                title.text = periodText.toTwoLinesString()
            }

            subtitle.text = subTitleText
            dateFrom.setText(from)
            dateTo.setText(to)
            dateFrom.setBackgroundResource(R.drawable.date_picker_edit_text_background_valid)
            dateTo.setBackgroundResource(R.drawable.date_picker_edit_text_background_valid)
        }
    }

    override fun setDateFromError() {
        periodPicker.dateFrom.setBackgroundResource(R.drawable.date_picker_edit_text_background_invalid)
    }

    override fun setDateToError() {
        periodPicker.dateTo.setBackgroundResource(R.drawable.date_picker_edit_text_background_invalid)
    }

    override fun setDateFromOk() {
        periodPicker.dateFrom.setBackgroundResource(R.drawable.date_picker_edit_text_background_valid)
    }

    override fun setDateToOk() {
        periodPicker.dateTo.setBackgroundResource(R.drawable.date_picker_edit_text_background_valid)
    }

    override fun setDateFromCursor() {
        with(periodPicker) {
            dateFrom.requestFocus()
            val selectedOffset = if (dateFrom.text != null) dateFrom.text!!.length else 0
            dateFrom.setSelection(selectedOffset, selectedOffset)
        }
    }

    override fun setDateToCursor() {
        with(periodPicker) {
            dateTo.requestFocus()
            dateTo.setSelection(0, 0)
        }
    }

    override fun showPeriodInvalidToast() {
        showToast(R.string.date_picker_period_invalid)
    }

    override fun showDateInvalidToast() {
        showToast(R.string.date_picker_date_invalid)
    }

    override fun showPeriodUnavailableToast() {
        showToast(R.string.date_picker_period_unavailable)
    }

    private fun showToast(@StringRes stringRes: Int) {
        SbisPopupNotification.pushToast(requireContext(), stringRes)
    }

    /**
     * Позиционирование нижней границы текущего года/месяца над плавающими кнопками
     */
    override fun scrollToCurrentPeriod(position: Int) {
        if (position < mYearListAdapter.itemCount - 1) {
            // если элемент RecyclerView не последний, скроллим до следущего за ним элемента
            val calendarMargin = resources.getDimensionPixelSize(R.dimen.date_picker_list_margin_bottom)
            // делаем отступ, чтобы требуемый элемент отображался над кнопкой подтверждения
            val offset = periodPicker.calendar.height + calendarMargin - periodPicker.floatingPanel.height
            layoutManager?.scrollToPositionWithOffset(position + 1, offset)
        } else {
            // иначе скроллим в конец
            layoutManager?.scrollToPosition(mYearListAdapter.itemCount - 1)
        }
    }

    override fun showCurrentPeriodSelectionWindowFragment(
        selectedPeriod: Period,
        visibleCurrentPeriods: List<CurrentPeriod>
    ) {

        if (isTablet) {
            val container = createParcelableFragmentContainer(
                CurrentPeriodSelectionContentCreator(selectedPeriod, visibleCurrentPeriods)
            )
            container.show(
                childFragmentManager,
                AnchorHorizontalLocator(HorizontalAlignment.CENTER).apply {
                    anchorView = periodPicker.selectCurrentPeriod
                },
                AnchorVerticalLocator(VerticalAlignment.TOP).apply {
                    anchorView = periodPicker.selectCurrentPeriod
                }
            )
        } else {
            val contentCreator = CurrentPeriodSelectionContentCreator(selectedPeriod, visibleCurrentPeriods)
            val containerFragment = ContainerMovableFragment.Builder()
                .setContentCreator(contentCreator)
                .setExpandedPeekHeight(MovablePanelPeekHeight.FitToContent())
                .build()

            childFragmentManager.beginTransaction()
                .replace(R.id.period_picker_root, containerFragment, CurrentPeriodSelectionFragment.TAG)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun updateVisibleItemsRange() {
        val layoutManager = this@DatePickerDialogFragment.layoutManager ?: return
        val firstItemPosition = layoutManager.findFirstVisibleItemPosition()
        val lastItemPosition = layoutManager.findLastVisibleItemPosition()
        // общее количество ячеек должно совпадать с календарной сеткой в модели данных
        var totalCount = mYearListAdapter.itemCount
        if (mYearListAdapter.tryGetItemAt(totalCount - 1) is BottomStub) {
            totalCount -= 1
        }
        if (firstItemPosition >= 0 && lastItemPosition >= 0 && firstItemPosition <= lastItemPosition) {
            presenter.onVisibleItemsRangeChanged(firstItemPosition, lastItemPosition, totalCount)
        }
    }

    private fun setDialogWindowFocusable(isFocusable: Boolean) {
        val window = dialog?.window ?: return
        if (isFocusable) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            )
        }
    }

    private fun configureImmersiveFullscreen() {
        with(requireDialog().window!!) {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                getLightBarFlags()
        }
    }

    private fun getLightBarFlags(): Int {
        with(requireDialog().window!!) {
            val hasLightStatusBarFlag =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR != 0)
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                else
                    0
            val hasLightNavigationBarFlag =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR != 0)
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                else
                    0

            return hasLightStatusBarFlag or hasLightNavigationBarFlag
        }
    }
}
