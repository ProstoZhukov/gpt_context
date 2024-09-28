package ru.tensor.sbis.business.common.ui.viewmodel

import android.view.inputmethod.EditorInfo
import androidx.annotation.DimenRes
import androidx.annotation.Px
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import ru.tensor.sbis.business.common.di.PerFragment
import ru.tensor.sbis.business.common.ui.base.contract.FilterSearchContract
import ru.tensor.sbis.business.common.ui.utils.FilterSearchBackgroundLevel
import ru.tensor.sbis.business.common.ui.utils.ViewActionObservable
import ru.tensor.sbis.business.common.ui.utils.toTrue
import ru.tensor.sbis.common.rx.RxBus
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.design.view.input.searchinput.DEFAULT_SEARCH_QUERY
import ru.tensor.sbis.design.view.input.searchinput.SearchInput
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.view.input.R as RViewInput

/**
 * Вью-модель для Строки поиска с фильтром
 * Ссылка на стандарт:
 * @see <a href="http://axure.tensor.ru/MobileStandart8/#p=строка_поиска">Строка поиска с фильтром</a>
 *
 * @property panelHeight высота Строки поиска с фильтром
 * @property searchAction лямбда действия по изменению строки поиска
 * @property clickFilterAction лямбда действия по клику на кнопку фильтра
 * @property closeFilterAction лямбда действия для закрытия фильтра, используется опционально
 * @property minQueryLengthForForceSearch минимальное количество знаков для инициирования запроса данных при принудительном поиске
 * @property isInHost Находится ли строка поиска в хост фрагменте
 * @property hasSearchHandler true если назначен обработчик ввода в строку поиска
 */
@PerFragment
open class FilterSearchPanelVM(
    val resourceProvider: ResourceProvider,
    val rxBus: RxBus,
    private val debounceScheduler: Scheduler
) : BaseObservable(),
    FilterSearchContract,
    Disposable {

    @Inject
    constructor(
        resourceProvider: ResourceProvider,
        rxBus: RxBus
    ) : this(resourceProvider, rxBus, AndroidSchedulers.mainThread())

    lateinit var searchAction: (String) -> Unit
    lateinit var clickFilterAction: () -> Unit
    var closeFilterAction: (() -> Unit)? = null

    val hasSearchHandler: Boolean
        get() = ::searchAction.isInitialized

    var minQueryLengthForForceSearch = MIN_QUERY_LENGTH_FOR_FORCE_SEARCH

    /**@SelfDocumented */
    @get:Px
    val panelHeight: Int
        get() = resourceProvider.getDimensionPixelSize(panelHeightRes)

    /**@SelfDocumented */
    @get:DimenRes
    val panelHeightRes: Int
        get() = RViewInput.dimen.input_view_filter_search_panel_height

    open val isInHost: Boolean = false

    override val searchHintState = ObservableField(resourceProvider.getString(RDesign.string.design_search_panel_hint))
    override val currentFiltersState = ObservableField<List<String>?>()
    override val clearText = ObservableBoolean()
    override val isVisible = ObservableBoolean(true)
    override val isElevated = ObservableBoolean()
    override val isAnimated = ObservableBoolean(false)
    override val hideKeyboardOnSearchClick: Boolean = true
    override val drawableLevel = ObservableInt(FilterSearchBackgroundLevel.DEFAULT.level)

    /** Текст для поиска, обновляется по debounce */
    override var inputSearchText = DEFAULT_SEARCH_QUERY

    override val cancelSearchChannel: ObservableField<Subject<Any>?>
        get() = ObservableField(cancelSearchSubject)

    override val searchFieldEditorChannel: ObservableField<Subject<Int>?>
        get() = ObservableField(searchFieldEditorSubject)

    override val searchQueryChangedChannel: ObservableField<Subject<String>?>
        get() = ObservableField(searchQueryChangedSubject)

    override val searchFocusChangedChannel: ObservableField<Subject<Boolean>?>
        get() = ObservableField(searchFocusChangedSubject)

    override val clickFilterChannel: ObservableField<Subject<Any>?>
        get() = ObservableField(filterClickSubject)

    override val hasFilterState = ObservableBoolean(true)

    override val viewActionChannel = ViewActionObservable<SearchInput>()

    override fun isDisposed(): Boolean = disposables.isDisposed

    /**
     * Очистить использованные ресурсы
     */
    override fun dispose() = disposables.dispose()

    /** Прячет клавиатуру для строки поиска с фильтром [SearchInput] */
    fun hideKeyboardForPanel() = viewActionChannel.set {
        if (isAttachedToWindow) hideKeyboard()
    }

    /** Показывает клавиатуру для строки поиска с фильтром [SearchInput] */
    fun showKeyboardForPanel() = viewActionChannel.set {
        if (isShown) showKeyboard()
    }

    /** Сбросить текст поискового запроса в строке фильтра. */
    fun onResetInputSearchText() {
        clearText.toTrue
        inputSearchText = DEFAULT_SEARCH_QUERY
    }

    private val cancelSearchSubject: Subject<Any> by lazy {
        PublishSubject.create<Any>().also { subject ->
            subject
                .throttleFirst(MULTICAST_WINDOW_DELAY_MILL_SEC, TimeUnit.MILLISECONDS)
                .subscribe {
                    onResetInputSearchText()
                    searchAction(DEFAULT_SEARCH_QUERY)
                }.addTo(disposables)
        }
    }

    /**
     * Пользователь нажал на "лупу" или "вперед" на клавиатуре
     * Делегирует обработку [searchQueryChangedSubject]
     */
    private val searchFieldEditorSubject: Subject<Int> by lazy {
        PublishSubject.create<Int>()
            .apply {
                subscribe {
                    if (it == EditorInfo.IME_ACTION_NEXT || it == EditorInfo.IME_ACTION_SEARCH) {
                        isForcedRequest = true
                        searchQueryChangedSubject.onNext(currentText)
                    }
                }.addTo(disposables)
            }
    }

    /**
     * Сигнализирует о требовании произвести поиск
     */
    private var isForcedRequest = false

    /**
     * Общий обработчик поиска для случаев:
     *  - автопоиск при изменении строки поиска от [MIN_SEARCH_QUERY_LENGTH]
     *  - пользователь сам инициировал поиск [isForcedRequest] = true
     * Поиск произойдет через [INPUT_SEARCH_DELAY_SEC]
     */
    private val searchQueryChangedSubject: Subject<String> by lazy {
        PublishSubject.create<String>()
            .apply {
                map { QueryState(it, isForcedRequest) }
                    .doOnNext { currentText = it.newSearch }
                    .debounce(INPUT_SEARCH_DELAY_SEC, TimeUnit.SECONDS, debounceScheduler)
                    .subscribe { queryState ->
                        //автопоиск при изменении и поиск при нажатии на "лупу"
                        if (queryState.isUpdated || queryState.force) {
                            onUpdateSearchQuery(queryState)
                            performSearch(queryState.force)
                            isForcedRequest = false
                        }
                    }.addTo(disposables)
            }
    }

    /**
     * Правка возможной ошибки когда при быстром клике: "строка поиска - дабл клик фильтра - строка поиска" открывается
     * панель фильтра, а после клавиатура под ней
     */
    private val searchFocusChangedSubject: Subject<Boolean> by lazy {
        PublishSubject.create<Boolean>().also { subject ->
            subject.subscribe { focused ->
                if (focused) closeFilterAction?.let { it() }
            }.addTo(disposables)
        }
    }

    /**
     * Обновить текущий поисковый запрос.
     */
    private fun onUpdateSearchQuery(newQuery: QueryState) {
        if (newQuery.isUpdated) {
            inputSearchText = newQuery.newSearch
        }
    }

    private fun performSearch(forceSearch: Boolean) = inputSearchText.let { text ->
        if (text == DEFAULT_SEARCH_QUERY || text.length >= MIN_SEARCH_QUERY_LENGTH || forceSearch) {
            searchAction(text)
        }
    }

    private val filterClickSubject: Subject<Any> by lazy {
        PublishSubject.create<Any>().also { subject ->
            subject
                .distinctUntilChanged()
                .subscribe {
                    clickFilterAction()
                }.addTo(disposables)
        }
    }

    private val disposables = CompositeDisposable()

    /** Текущий текст, введённый в текстовое поле, обновляется сразу */
    private var currentText = DEFAULT_SEARCH_QUERY

    /**
     * Состояние поискового запроса
     *
     * @param newSearch новый запрос
     * @property force true если следует инициировать обновление по нему
     * @property isUpdated true если был обновлен
     * @property forced true если запрос определенно стоит произвести
     */
    private inner class QueryState(val newSearch: String = "", private val forced: Boolean) {

        val isUpdated: Boolean
            get() = inputSearchText != newSearch

        val force: Boolean
            get() = forced || needToUpdateSearchQuery(newSearch)

        /**
         * Сверить поисковые запросы
         *
         * @return true если следует инициировать запрос данных для новой строки
         */
        private fun needToUpdateSearchQuery(newSearch: String) =
            if (inputSearchText != newSearch) {
                val newLength = newSearch.length
                val previousLength = inputSearchText.length
                newLength == 0 || previousLength > newLength && newLength == MIN_SEARCH_QUERY_LENGTH.dec()
            } else false
    }

    private companion object {
        const val MIN_QUERY_LENGTH_FOR_FORCE_SEARCH: Int = 0
        const val MIN_SEARCH_QUERY_LENGTH: Int = 3
        const val INPUT_SEARCH_DELAY_SEC = 1L
        /** эмпирическое значение задержки между обрабатываемыми share() событиями от панели поиска [SearchInput] */
        const val MULTICAST_WINDOW_DELAY_MILL_SEC = 150L
    }
}
