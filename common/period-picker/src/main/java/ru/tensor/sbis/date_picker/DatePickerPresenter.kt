package ru.tensor.sbis.date_picker

import android.view.View
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.common.rx.RxBus
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.date_picker.current.CurrentPeriod
import ru.tensor.sbis.date_picker.current.CurrentPeriodSelectedEvent
import ru.tensor.sbis.date_picker.free.items.LabelVM
import ru.tensor.sbis.date_picker.free.items.RecentPeriodVM
import ru.tensor.sbis.date_picker.items.CalendarVmFactory
import ru.tensor.sbis.date_picker.items.CalendarVmStorage
import ru.tensor.sbis.date_picker.items.NamedItemVM
import ru.tensor.sbis.date_picker.items.SpecialDates
import ru.tensor.sbis.date_picker.range.CalendarDayRange
import ru.tensor.sbis.date_picker.selection.SelectionStrategy
import ru.tensor.sbis.date_picker.selection.SelectionStrategyFactory
import java.util.*
import ru.tensor.sbis.common.R as RCommon

const val MAX_DATE_STRING_LENGTH = 8

// Минимальная дата получается из правила форматирования полей ввода по маске DD.MM.YY
// (чтобы система по двухзначному числу года понимала, что за год перед ней), а именно
// если указанный год попадает в промежуток [00 - "текущий год + 10 лет"], то век подставляется текущий,
// если нет, то подставляется прошлый век.
// Соответственно, у минимально возможного введенного пользователем числа день и месяц будет равен 1 января,
// а год вычисляется следующим образом:
// берется текущий год, к нему прибавляется 10. Полученное число равно максимально возможному году,
// которое может ввести пользователь.
// Прибавляем 1, чтобы число перестало попадать в промежуток [00 - "текущий год + 10 лет"]
// и отнимаем 100 (лет), так как нам нужен прошлый век.
// Например, если текущий год 2023, то минимальная дата должна быть 1 января 1934.
val MIN_DATE: GregorianCalendar
    get() {
        val currentYear = GregorianCalendar().year
        return GregorianCalendar(currentYear + 11 - 100, 0, 1)
    }

// Максимальная дата получается аналогично минимальной по правилам форматирования полей ввода:
// у максимально возможного введенного пользователем числа день и месяц равен 31 декабря, а
// год вычисляется следующим образом: берется текущий год, к нему прибавляется 10.
// Например, если текущий год 2023, то максимальная дата должна быть 31 декабря 2033.
val MAX_DATE: GregorianCalendar
    get() {
        val currentYear = GregorianCalendar().year
        return GregorianCalendar(currentYear + 10, 11, 31)
    }

const val HOLIDAYS_YEAR_RANGE = 3
const val UPDATE_HOLIDAYS_YEAR_RANGE = 1

/**
 * Презентер, управляющий компонентом выбора периода.
 * Отвечает за выбор режима ("Год" или "Месяц"), обрабатывает нажатия элементов календарной сетки,
 * реализует хранение выбранного периода и сигнализирует об отображении
 *
 * @author mb.kruglova
 */
class DatePickerPresenter(
    private val interactor: DatePickerInteractor,
    private val mBus: RxBus,
    private val mResourceProvider: ResourceProvider,
    private val periodHelper: PeriodHelper,
    private val calendarVmFactory: CalendarVmFactory,
    private val selectionStrategyFactory: SelectionStrategyFactory,
    private val validator: Validator,
    private val dayCountersRepository: DayCountersRepository
) : DatePickerContract.Presenter {

    private var isInitialized = false
    private var mView: DatePickerContract.View? = null
    private var storage = CalendarVmStorage()
    private var specialDates = SpecialDates()
    private val mRecentPeriods = LinkedList<Any>()
    private val mCompositeDisposable = CompositeDisposable()
    private var mHistoryKey = ""
    private var mMode = Mode.YEAR
    private var mPreviousMode = Mode.YEAR
    private var mMin: Calendar = MIN_DATE
    private var mMax: Calendar = MAX_DATE
    private var mInitialDate = getCurrentDay()
    private var mDateToLastCursorPosition = 0
    private var mPickerType = PickerType.PERIOD
    private var mResultReceiverId = ""
    private var innerVisualParams: VisualParams? = null
    private val halfYearTitles = mResourceProvider.getStringArray(R.array.half_year).toList()
    private val quartersTitles = mResourceProvider.getStringArray(R.array.quarters).toList()
    private val monthTitles = mResourceProvider.getStringArray(RCommon.array.common_months).toList()
    private var loadPageDisposable: Disposable = Disposables.disposed()

    private lateinit var selectionStrategy: SelectionStrategy

    // свойство, автоматически обновляющее поля ввода дат
    private var _selectedPeriod = Period.create()
    private var selectedPeriod: Period
        get() = _selectedPeriod
        set(value) {
            _selectedPeriod = value
            calculateGeneratePeriod()
            mView?.showPeriod(
                prepareDateIntervalTitle(halfYearTitles, quartersTitles, monthTitles, value),
                checkPeriodIsCurrentAndGetString(value),
                _selectedPeriod.fromFormatted,
                _selectedPeriod.toFormatted
            )
            updateFloatingButtons()
        }
    private var selectedPeriodFromParams: Period? = null

    // свойство, автоматически устанавливающее стиль выбранного заголовка
    private var _selectedNamedItem: NamedItemVM? = null
    private var selectedNamedItem: NamedItemVM?
        get() = _selectedNamedItem
        set(value) {
            _selectedNamedItem?.setNoSelected()
            _selectedNamedItem = value
            _selectedNamedItem?.setSelected()
        }

    // автоматически заставляет подгрузить новые счётчики и обновлять если требуется список выходных
    private val visibleDaysRangePublisher = PublishSubject.create<CalendarDayRange>()

    // содержит период для которого загружены выходные и праздничные дни
    private val holidaysPeriodPublisher = PublishSubject.create<Period>()

    private lateinit var generatedRange: CalendarDayRange

    init {
        mCompositeDisposable.addAll(
            observeCurrentPeriodSelectedEvent(),
            observeDayCounters(),
            observeHolidaysPeriod(),
            observeHolidaysPeriodExtension()
        )
    }

    override fun attachView(view: DatePickerContract.View) {
        mView = view

        innerVisualParams?.let { mView?.applyVisualParams(it) } ?: when (mPickerType) {
            PickerType.DATE -> mView?.initDateMode()
            PickerType.PERIOD_BY_ONE_CLICK -> mView?.initPeriodByOneClickMode()
            PickerType.DATE_ONCE -> mView?.initDateOnceMode()
            PickerType.MONTH_ONCE -> mView?.initMonthOnceMode()
            else -> {
                // default
            }
        }

        val preferredMode = innerVisualParams?.preferredMode
        when {
            preferredMode != null -> mMode = preferredMode
            else -> updateMode()
        }

        rebuildView()

        if (specialDates.holidays.isEmpty())
            holidaysPeriodPublisher.onNext(Period.fromYearRange(mInitialDate, HOLIDAYS_YEAR_RANGE))
        if (specialDates.unavailableDays.isEmpty())
            getUnavailablePeriods()
        if (specialDates.fixedMonths.isEmpty())
            getFixedMonths()

        if (storage.grid.isEmpty())
            getCalendarGrid(syncIfPossible = true)
        else
            showData()

        _selectedNamedItem?.setSelected()
    }

    override val visualParams: VisualParams?
        get() = innerVisualParams

    override fun setParams(params: DatePickerParams) {
        if (!isInitialized) {
            mHistoryKey = params.historyKey
            if (params.minDate > params.maxDate) {
                throw IllegalArgumentException("Максимально допустимая дата календарной сетки должна быть позднее минимальной")
            }
            mMin = params.minDate
            mMax = params.maxDate
            selectedPeriod = params.period
            selectedPeriodFromParams = params.period
            mPickerType = params.type
            selectionStrategy = selectionStrategyFactory.create(
                    mPickerType,
                    selectedPeriod,
                    storage,
                    { period -> saveAndExit(period) },
                    { period -> selectedPeriod = period },
                    { namedItem -> selectedNamedItem = namedItem },
                    specialDates
            )
            mInitialDate = params.initialDate
            mResultReceiverId = params.resultReceiverId
            innerVisualParams = params.visualParams
            isInitialized = true
        }
    }

    private fun getDateTimestampOffset(date: Calendar, offset: Int): Calendar {
        return (date.clone() as Calendar).apply { timeInMillis += offset }
    }

    private fun isGeneratedToBorder(isNextPage: Boolean): Boolean {
        return (isNextPage && generatedRange.endInclusive.timeInMillis >= mMax.timeInMillis) ||
                (!isNextPage && generatedRange.start.timeInMillis <= mMin.timeInMillis)
    }

    override fun generatePage(isNextPage: Boolean) {
        if (!loadPageDisposable.isDisposed || mMode != Mode.MONTH || isGeneratedToBorder(isNextPage)) return
        loadPageDisposable = Disposables.empty()

        val periodStart: Calendar
        val periodEnd: Calendar

        if (isNextPage) {
            periodStart = getDateTimestampOffset(generatedRange.endInclusive, DAY_OFFSET)
            periodEnd = getDateWithOffset(generatedRange.endInclusive, mMax, true)
            generatedRange.endInclusive.timeInMillis = periodEnd.timeInMillis
        } else {
            periodStart = getDateWithOffset(generatedRange.start, mMin, false)
            periodEnd = getDateTimestampOffset(generatedRange.start, -DAY_OFFSET)
            generatedRange.start.timeInMillis = periodStart.timeInMillis
        }

        loadPageDisposable = generateMonthGrid(periodStart, periodEnd).map { storage.concat(it, isNextPage) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(Consumer {
                selectionStrategy.selectMonthPeriods()
                storage.selectDays(selectedPeriod, mPickerType.isForDateSelection)
                showHolidays()
                showUnavailableDays()
                mView?.addItems(it.grid, isNextPage)
            })
    }

    private fun getDateWithOffset(date: Calendar, borderPeriod: Calendar, isPositiveOffset: Boolean): Calendar {
        val newDate = (date.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MONTH, if (isPositiveOffset) MONTH_OFFSET else -MONTH_OFFSET)
            if (isPositiveOffset) set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
        }

        return if (isPositiveOffset && newDate.timeInMillis > borderPeriod.timeInMillis ||
            !isPositiveOffset && newDate.timeInMillis < borderPeriod.timeInMillis
        ) {
            borderPeriod
        } else {
            newDate
        }
    }

    private fun calculateGeneratePeriod() {
        val currentDay = getCurrentDay()
        //Если таймштамп текущего дня больше таймштампа начала периода, то можно генерировать от начала - MONTH_OFFSET. Иначе требуется сгенерировать
        //всю сетку от текущего дня (требуется для функционала кнопки "Домой")
        val startDate = if (selectedPeriod.hasFrom) {
            if (currentDay.timeInMillis > selectedPeriod.dateFrom!!.timeInMillis) {
                selectedPeriod.dateFrom!!
            } else {
                currentDay
            }
        } else if (mInitialDate.timeInMillis > mMax.timeInMillis) {
            mMin
        } else {
            mInitialDate
        }

        //Если таймштамп текущего дня больше таймштампа конца периода, то можно генерировать до периода + MONTH_OFFSET. Иначе требуется сгенерировать
        //всю сетку до текущего дня (требуется для функционала кнопки "Домой")
        val endDate = if (selectedPeriod.hasTo) {
            if (currentDay.timeInMillis < selectedPeriod.dateTo!!.timeInMillis) {
                selectedPeriod.dateTo!!
            } else {
                currentDay
            }
        } else if (selectedPeriod.hasFrom && mInitialDate.timeInMillis < selectedPeriod.dateFrom!!.timeInMillis) {
            Calendar.getInstance().apply {
                timeInMillis = selectedPeriod.dateFrom!!.timeInMillis
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        } else {
            mInitialDate
        }

        generatedRange =
            CalendarDayRange(getDateWithOffset(startDate, mMin, false), getDateWithOffset(endDate, mMax, true))
    }

    private fun updateMode() {
        mMode = if (selectedPeriod.containsMonthOrMore) {
            Mode.YEAR // если выбран месяц(ы) (период включает все дни одного или более месяцев), переходим в режим "Год"
        } else {
            Mode.MONTH // иначе переходим в режим "Месяц"
        }
        if (mPickerType.isForDateSelection) {
            mMode = Mode.MONTH // при выборе одиночной даты включается режим "Месяц"
        }
        if (mPickerType.isForMonthOnceSelection || mPickerType.isForPeriodOnlySelection) {
            mMode = Mode.YEAR // при выборе месяца и более включается режим "Год"
        }
    }

    private fun rebuildView() {
        // дата начала периода показывается только в произвольном режиме
        val dateFromVisibility = mMode == Mode.FREE
        // дата конца периода показывается только в произвольном режиме при выборе периода
        val dateToVisibility = mMode == Mode.FREE && !mPickerType.isForDateSelection
        // заголовок убирается в произвольном режиме
        val titleVisibility = mMode != Mode.FREE
        // кнопка "Домой" убирается в произвольном режиме (либо если это указано явно)
        val homeVisibility = titleVisibility && innerVisualParams?.let { it.homeVisibility == View.VISIBLE } ?: true
        mView?.updateTopBar(getModeIcon(), dateFromVisibility, dateToVisibility, titleVisibility, homeVisibility)

        updateFloatingButtons()

        if (!mPickerType.isForPeriodSelectionByOneClick) {
            if (mMode == Mode.FREE) {
                mView?.showKeyboard()
            } else {
                mView?.hideKeyboard()
            }
        }
    }

    private fun updateFloatingButtons() {
        // кнопка подтверждения показывается везде, кроме режима выбора одиночной даты "сразу же" (либо убирается явно)
        var doneVisibility = (mPickerType != PickerType.DATE_ONCE || mMode != Mode.MONTH)
                && innerVisualParams?.let { it.doneVisibility == View.VISIBLE } ?: true
        // проверка режима когда кнопка подтверждения не отображается, пока пользователь не начнет выбирать новый период
        if (doneVisibility && innerVisualParams?.doneVisibleOnPeriodChanged == true) {
            doneVisibility = selectedPeriod != selectedPeriodFromParams && validator.checkInputPeriodOrDate(selectedPeriod)
        }
        // если указана какая-либо дата и кнопка подтверждения не отображается, то показывается кнопка сброса (либо убирается явно)
        val resetVisibility = selectedPeriod.hasFrom
                && !doneVisibility
                && innerVisualParams?.let { it.resetVisibility == View.VISIBLE } ?: true
        mView?.updateFloatingButtons(doneVisibility, resetVisibility)
    }

    private fun getModeIcon(): Int {
        // для режима произвольного выбора показывается такая же иконка, как и для предыдущего режима
        return getModeIcon(if (mMode == Mode.FREE) mPreviousMode else mMode)
    }

    private fun getModeIcon(mode: Mode): Int {
        return if (mode == Mode.YEAR) {
            // для режима "Год" показываем иконку месяца
            R.string.date_picker_date_date_picker_month_icon
        } else {
            // иначе показываем иконку года
            R.string.date_picker_date_date_picker_year_icon
        }
    }

    private fun getCalendarGrid(syncIfPossible: Boolean = false) {
        // в зависимости от режима и типа компонента выбирается источник данных
        val observable = when (mMode) {
            Mode.YEAR -> {
                val needQuartersAndHalfYears =
                    !mPickerType.isForMonthOnceSelection  // полугодия и кварталы нужны везде, кроме режима [PickerType.MONTH_ONCE]
                val needFixedIndicators =
                    mPickerType.isForMonthOnceSelection  // индикаторы зафиксированных/незафиксированных месяцев нужны только в режиме [PickerType.MONTH_ONCE]
                val yearLabelsClickable = innerVisualParams?.yearLabelsClickable ?: true
                val yearPeriods = calendarVmFactory.generateYearPeriods(
                    mMin,
                    mMax,
                    needQuartersAndHalfYears,
                    needFixedIndicators,
                    yearLabelsClickable,
                    selectionStrategy
                )
                Observable.just(yearPeriods)
                    .map { storage.init(it) }
                    .run {
                        if (syncIfPossible)
                            this@run
                        else
                            subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    }
            }
            Mode.MONTH -> {
                generateMonthGrid(generatedRange.start, generatedRange.endInclusive).toObservable()
                    .map { storage.init(it) }
                    .run {
                        if (syncIfPossible)
                            this@run
                        else
                            subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    }
            }
            Mode.FREE -> {
                interactor.getRecentPeriods(mHistoryKey)
                    .map {
                        val filteredPeriods = it.filter { historyPeriod ->
                            historyPeriod.from.time >= mMin.timeInMillis &&
                                (historyPeriod.to?.time ?: historyPeriod.from.time) <= mMax.timeInMillis
                        }

                        filteredPeriods.map { historyPeriod ->
                            RecentPeriodVM(historyPeriod.title) {
                                selectionStrategy.recentPeriodClicked(historyPeriod)
                                saveAndExit(selectedPeriod)
                            }
                        }
                    }
                    .map {
                        mRecentPeriods.clear()
                        mRecentPeriods.addAll(it)
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
            }
        }

        mCompositeDisposable.add(observable.subscribe { showData() })
    }

    private fun generateMonthGrid(start: Calendar, end: Calendar) = Single.fromCallable {
        calendarVmFactory.generateMonthPeriods(start, end, selectionStrategy)
    }

    private fun showData() {
        when (mMode) {
            Mode.YEAR -> {
                selectionStrategy.selectYearPeriods()
                storage.selectMonths(selectedPeriod)
                mView?.showData(storage.grid, calcFocusPosition())
            }
            Mode.MONTH -> {
                selectionStrategy.selectMonthPeriods()
                storage.selectDays(selectedPeriod, mPickerType.isForDateSelection)
                showHolidays()
                showUnavailableDays()
                mView?.showData(storage.grid, calcFocusPosition(), true, generatedRange.endInclusive == mMax)
            }
            Mode.FREE -> {
                if (mRecentPeriods.isEmpty()) {
                    mView?.showEmptyView()
                } else {
                    mView?.showData(listOf(LabelVM()) + mRecentPeriods)
                }
            }
        }

        mView?.showPeriod(
            prepareDateIntervalTitle(halfYearTitles, quartersTitles, monthTitles, _selectedPeriod),
            checkPeriodIsCurrentAndGetString(_selectedPeriod),
            _selectedPeriod.fromFormatted,
            _selectedPeriod.toFormatted
        )
    }

    override fun detachView() {
        mView = null
    }

    override fun onDestroy() {
        mCompositeDisposable.dispose()
    }

    override fun onTitleClick() {
        changeMode(true)
    }

    override fun onCloseClick() {
        mView?.closeDialog()
    }

    /**
     * Подтверждение и сохранение выбранной пользователем даты. Выход из диалога
     */
    override fun onDoneClick() {
        if (validator.hasUnavailableDays(selectedPeriod, specialDates.unavailableDays)) {
            mView?.showPeriodUnavailableToast()
        } else if (validator.checkInputPeriodOrDate(selectedPeriod)) {
            val period = validator.preparePeriodBeforeConfirmation(selectedPeriod)
            saveAndExit(period)
        } else {
            if (mPickerType.isForDateSelection) {
                mView?.setDateFromError()
                mView?.showDateInvalidToast()
            } else {
                mView?.showPeriodInvalidToast()
            }
        }
    }

    override fun onModeClick() {
        changeMode()
    }

    override fun onHomeClick() {
        mView?.scrollToCurrentPeriod(getFocusPositionByCalendar(getCurrentDay(), ScrollTarget.CENTER))
    }

    override fun onSelectCurrentPeriodClick() {
        mView?.showCurrentPeriodSelectionWindowFragment(
            selectedPeriod,
            innerVisualParams?.visibleCurrentPeriods ?: listOf(*CurrentPeriod.values())
        )
    }

    override fun onResetClick() {
        mBus.post(ResetButtonClickedEvent())
        mView?.run {
            returnResult(mResultReceiverId, null)
            closeDialog()
        }
    }

    override fun onDateFromTextChanged(dateFrom: String, dateTo: String, selectionStart: Int, selectionEnd: Int) {
        if (mMode == Mode.FREE) {
            getPeriodFromText(dateFrom, dateTo)
            moveCursorAfterInput(selectionStart, selectionEnd)
            updateFloatingButtons()
        }
    }

    override fun onVisibleItemsRangeChanged(firstItemPosition: Int, lastItemPosition: Int, totalCount: Int) {
        // дни видны только в режиме месяца
        if (mMode == Mode.MONTH) {
            storage.dayRangeForMonthGrid(firstItemPosition, lastItemPosition, totalCount)?.let { dayRange ->
                visibleDaysRangePublisher.onNext(dayRange)
            }
        }
    }

    override fun onBackPressed() {
        if (mMode == Mode.FREE) {
            changeMode()
        } else {
            mView?.closeDialog()
        }
    }

    /**
     * Перемещение курсора в поле конечной даты после заполнения поля начальной даты
     * @param selectionStart начало выделенного текста
     * @param selectionEnd конец выделенного текста
     * Если нет выделенного текста, то selectionEnd и selectionStart совпадают
     */
    private fun moveCursorAfterInput(selectionStart: Int, selectionEnd: Int) {
        // если был введен последний символ, курсор перемещается в следующее поле
        if (selectionEnd == selectionStart && selectionEnd == MAX_DATE_STRING_LENGTH) {
            mView?.setDateToCursor()
        }
    }

    override fun onDateToTextChanged(dateFrom: String, dateTo: String, selectionStart: Int, selectionEnd: Int) {
        if (mMode == Mode.FREE && selectedPeriod.toFormatted != dateTo) {
            getPeriodFromText(dateFrom, dateTo)
            moveCursorAfterDelete(selectionStart, selectionEnd)
            updateFloatingButtons()
        }
    }

    /**
     * Перемещение курсора в поле начальной даты после очистки поля конечной даты
     * @param selectionStart начало выделенного текста
     * @param selectionEnd конец выделенного текста
     * Если нет выделенного текста, то selectionEnd и selectionStart совпадают
     */
    private fun moveCursorAfterDelete(selectionStart: Int, selectionEnd: Int) {
        // если был удален первый символ, курсор перемещается в предыдущее поле
        if (mDateToLastCursorPosition == 1
            && selectionEnd == selectionStart
            && selectionEnd == 0
        ) {
            mView?.setDateFromCursor()
        }
        mDateToLastCursorPosition = selectionEnd
    }

    private fun changeMode(toFreeMode: Boolean = false) {
        val oldMode = mMode

        mMode = when {
            toFreeMode -> Mode.FREE // принудительный переход в произвольный режим
            mPickerType.isForDateSelection -> Mode.MONTH //  переход в режим "Месяц" при выборе одиночной даты (режим "Год" отсутствует)
            mMode == Mode.YEAR -> Mode.MONTH // смена режима "Год" на режим "Месяц"
            mMode == Mode.FREE -> mPreviousMode // смена произвольного режима на предыдущий режим
            else -> Mode.YEAR
        }

        if (oldMode != mMode) {
            // сохранение предыдущего режима
            mPreviousMode = oldMode
            // сброс выделения текста
            selectedNamedItem = null

            rebuildView()
            getCalendarGrid()
        }
    }

    /**
     * Получение периода из введенного текста
     * @param dateFrom строка начальной даты
     * @param dateTo строка конечной даты
     */
    private fun getPeriodFromText(dateFrom: String, dateTo: String) {
        _selectedPeriod = validator.parseAndValidatePeriodFromText(
            dateFrom,
            if (mPickerType.isForDateSelection) "" else dateTo, // в режиме выбора даты конец периода не учитывается
            mMin,
            mMax,
            mView
        )
    }

    // region Year
    private fun calcFocusPosition(): Int {
        mInitialDate = _selectedPeriod.dateFrom
            ?: mInitialDate  // если ранее был выбран период, считаем его фокусом при переключении режима
        return getFocusPositionByCalendar(mInitialDate)
    }

    private fun getFocusPositionByCalendar(calendar: Calendar, target: ScrollTarget = ScrollTarget.TOP): Int {
        return when (mMode) {
            Mode.YEAR -> calendar.year - mMin.year
            Mode.MONTH -> {
                val monthsBefore = storage.monthDaysAligned.filterKeys {
                    (it.year < calendar.year) || (it.year == calendar.year && it.month <= calendar.month)
                }.values

                val monthsCount = monthsBefore.size
                val daysCount = monthsBefore.sum()
                val daysInSameMonth =
                    storage.monthDaysAligned[PeriodsVMKey.createMonthKey(calendar.year, calendar.month)] ?: 0
                val endOfMonthPosition = monthsCount + daysCount - 1

                return when (target) {
                    // если нужно проскроллить к верхней границе, получаем позицию заголовка месяца
                    ScrollTarget.TOP -> endOfMonthPosition - daysInSameMonth
                    // если нужно проскроллить к середине, получаем позицию последнего дня месяца
                    ScrollTarget.CENTER -> endOfMonthPosition
                }
            }
            else -> 0
        }
    }
    // endregion

    // region Common
    private fun checkPeriodIsCurrentAndGetString(period: Period): String? {
        val resultAndStringRes = periodHelper.checkPeriodIsCurrentAndGetStringRes(period)
        val result = resultAndStringRes.first
        return if (result) {
            val stringRes = resultAndStringRes.second
            mResourceProvider.getString(stringRes)
        } else {
            null
        }
    }

    private fun saveAndExit(period: Period) {
        interactor.savePeriod(mHistoryKey, period)
        mBus.post(PeriodPickedEvent(period, mResultReceiverId))
        mView?.returnResult(mResultReceiverId, period)
        mView?.closeDialog()
    }

    // endregion

    // region Holidays
    private fun observeHolidaysPeriodExtension(): Disposable {
        return visibleDaysRangePublisher.distinctUntilChanged()
            .withLatestFrom(holidaysPeriodPublisher,
                BiFunction<CalendarDayRange, Period, Pair<CalendarDayRange, Period>> { visibleRange, holidaysPeriod ->
                    Pair(visibleRange, holidaysPeriod)
                })
            .observeOn(Schedulers.io())
            .flatMap { (visibleRange, holidaysPeriod) ->
                val extendedPeriod = extendHolidaysPeriodIfNeeded(visibleRange, holidaysPeriod)
                return@flatMap when {
                    extendedPeriod != holidaysPeriod -> Observable.just(extendedPeriod)
                    else -> Observable.empty()
                }
            }
            .subscribe { holidaysPeriodPublisher.onNext(it) }
    }

    private fun extendHolidaysPeriodIfNeeded(visibleRange: CalendarDayRange, holidaysPeriod: Period): Period {
        val holidaysRange = holidaysPeriod.dayRange ?: return holidaysPeriod
        val startThreshold = holidaysRange.start.withYearShift(UPDATE_HOLIDAYS_YEAR_RANGE)
        val endThreshold = holidaysRange.endInclusive.withYearShift(-UPDATE_HOLIDAYS_YEAR_RANGE)
        val newStart = when {
            visibleRange.start < startThreshold -> visibleRange.start.withYearShift(-HOLIDAYS_YEAR_RANGE)
            else -> holidaysRange.start
        }
        val newEnd = when {
            visibleRange.endInclusive > endThreshold -> visibleRange.endInclusive.withYearShift(HOLIDAYS_YEAR_RANGE)
            else -> holidaysRange.endInclusive
        }
        return Period(newStart, newEnd)
    }

    private fun observeHolidaysPeriod(): Disposable {
        return holidaysPeriodPublisher.distinctUntilChanged()
            .observeOn(Schedulers.io())
            .scan { previous, new ->
                new - previous
            }
            .flatMap { period ->
                interactor.getHolidays(period)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .map { holidays -> prepareHolidays(holidays) }
            .subscribe { showHolidays() }
    }

    private fun prepareHolidays(holidays: List<Calendar>) {
        val currentDay = getCurrentDay()
        // новый список добавляется к уже существующему
        specialDates.holidays = specialDates.holidays.toMutableSet().apply {
            addAll(holidays
                .filterNot { holiday -> holiday sameDay currentDay }
                .map { holiday -> holiday.toDay() }
                .toSet())
        }
    }

    private fun showHolidays() {
        specialDates.holidays.mapNotNull { holiday -> storage.days[holiday] }
            .forEach { dayVm -> dayVm.setHolidayColor() }
    }
    // endregion

    // region Unavailable Periods
    private fun getUnavailablePeriods() {
        mCompositeDisposable.add(
            interactor.getUnavailablePeriods()
                .map { unavailablePeriods -> prepareUnavailablePeriods(unavailablePeriods) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { showUnavailableDays() }
        )
    }

    private fun prepareUnavailablePeriods(unavailablePeriods: List<Period>) {
        specialDates.unavailableDays = unavailablePeriods
            .flatMap { unavailablePeriod -> unavailablePeriod.dayRange ?: emptyList<Calendar>() }
            .map { unavailableDay -> unavailableDay.toDay() }
            .toSet()
    }

    private fun showUnavailableDays() {
        specialDates.unavailableDays
            .mapNotNull { unavailableDay -> storage.days[unavailableDay] }
            .forEach { dayVm -> dayVm.setUnavailable() }
    }
    // endregion

    private fun getFixedMonths() {
        mCompositeDisposable.add(
            interactor.getMarkedMonths()
                .map { fixedMonths -> prepareFixedMonths(fixedMonths) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { showFixedMonths() }
        )
    }

    private fun prepareFixedMonths(fixedMonths: List<Calendar>) {
        specialDates.fixedMonths = fixedMonths
            .map { fixedMonth -> fixedMonth.toMonth() }
            .toSet()
    }

    private fun showFixedMonths() {
        specialDates.fixedMonths
            .mapNotNull { month -> storage.months[month] }
            .forEach { monthVm -> monthVm.setFixed() }
    }

    private fun observeCurrentPeriodSelectedEvent(): Disposable {
        return mBus.subscribe(CurrentPeriodSelectedEvent::class.java).subscribe {
            saveAndExit(it.period)
        }
    }

    private fun observeDayCounters(): Disposable {
        return visibleDaysRangePublisher.distinctUntilChanged()
            .observeOn(Schedulers.io())
            .switchMap { loadRange ->
                dayCountersRepository.getDayCountersUpdatesObservable(loadRange)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::updateDaysCounters)
    }

    private fun updateDaysCounters(newCounters: Map<Calendar, Int>) {
        val lastCounters = specialDates.counters
        for (key in lastCounters) {
            storage.days[key]?.apply { counter.set("") }
        }

        specialDates.counters = if (newCounters.isEmpty())
            emptyList()
        else
            newCounters.map {
                val key = it.key.toDay()
                storage.days[key]?.apply { counter.set(if (it.value < 1) "" else it.value.toString()) }
                key
            }
    }

    companion object {
        private const val MONTH_OFFSET = 4
        private const val DAY_OFFSET = 1000 * 60 * 60 * 24
    }
}