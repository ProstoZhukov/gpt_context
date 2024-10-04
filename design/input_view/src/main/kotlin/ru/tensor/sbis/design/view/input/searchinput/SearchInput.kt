package ru.tensor.sbis.design.view.input.searchinput

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.os.ResultReceiver
import android.text.InputFilter
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import com.facebook.drawee.drawable.RoundedColorDrawable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.android_ext_decl.readNullableParcelableCompat
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeAtMostSpec
import ru.tensor.sbis.design.custom_view_tools.utils.TextLayoutTouchManager
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.design.utils.KeyboardUtils
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.view.input.R
import ru.tensor.sbis.design.view.input.base.BaseInputViewMaxLengthFilter.Companion.NO_MAX_LENGTH
import ru.tensor.sbis.design.view.input.searchinput.filter.FilterColorType
import ru.tensor.sbis.design.view.input.searchinput.filter.FilterSize
import ru.tensor.sbis.design.view.input.searchinput.filter.SbisFilterView
import ru.tensor.sbis.design.view.input.searchinput.util.SearchInputContext
import ru.tensor.sbis.design.view.input.searchinput.util.UpdatableTouchDelegate
import ru.tensor.sbis.design.view.input.text.TextInputView
import ru.tensor.sbis.design.view_ext.SimplifiedTextView
import java.util.concurrent.TimeUnit

const val DEFAULT_SEARCH_DELAY = 500L
const val DEFAULT_SEARCH_QUERY = ""

/**
 * Панель поиска, совмещённая с фильтром.
 *
 * - [Стандарт](http://axure.tensor.ru/MobileStandart8/#p=строка_поиска&g=1)
 *
 * @author ma.kolpakov
 */
class SearchInput @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes
    defStyleAttr: Int = R.attr.searchInputTheme,
    @StyleRes
    defStyleRes: Int = R.style.SearchInputDefaultTheme
) : ViewGroup(
    ThemeContextBuilder(context, attrs, defStyleAttr, defStyleRes).build(),
    attrs,
    defStyleAttr,
    defStyleRes
) {
    private var showClear: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                clearButton.isVisible = value
            }
        }
    private val loupeIcon: TextLayout
    private val searchField: TextInputView
    private val bottomDividerRect = Rect()

    private val clearButton: SimplifiedTextView
    private val divider = Rect()
    private var searchText = DEFAULT_SEARCH_QUERY
    private val config = SearchInputConfig()

    private val property = config.property
    private val style = config.style
    private var cancelSearchObservable = PublishSubject.create<Any>()
    private var filterClickObservable = PublishSubject.create<Any>()
    private var onActionObservable = PublishSubject.create<Int>()
    private var onTextChangeObservable = PublishSubject.create<String>()
    private var onFocusChangeObservable = PublishSubject.create<Boolean>()
    private var onClickObservable = PublishSubject.create<Any>()
    private var touchManager: TextLayoutTouchManager

    private val shareFocusSearchObservable by lazy {
        searchFocusChangeObservable().share()
    }

    private val shareFieldEditorActionsObservable by lazy {
        searchFieldEditorActionsObservable().share()
    }

    private val searchFieldBoundsTouch = Rect()
    private val searchFieldBoundsTouchExpanded = Rect()
    private val searchFieldTouchDelegate: UpdatableTouchDelegate

    var filter: SbisFilterView
        private set

    /**
     * Являются ли фильтры, заданные в данный момент, фильтрами по умолчанию
     */
    var isDefault = false
        private set
        get() = filter.isFilterDefault

    /**
     * Вспомогательная сущность для хранения мета-информации о месте использования компонента
     */
    var searchInputContext: SearchInputContext? = null
        private set

    /**
     * Конфигурация типа ввода на клавиатуре.
     * @see android.text.InputType и его константы.
     */
    var inputType: Int
        get() = searchField.inputType
        set(value) {
            searchField.inputType = value
        }

    /**
     * Конфигурация кнопки действия на клавиатуре.
     * @see EditorInfo и его константы.
     */
    var imeOptions: Int
        get() = searchField.imeOptions
        set(value) {
            searchField.imeOptions = value
            property.imeOptions = value
        }

    /**
     * Задать, будет ли поиск круглым, работает как [cornerRadius], но задает стандартное значение.
     */
    var isRoundSearchInputBackground = false
        set(value) {
            field = value
            if (field) {
                cornerRadius = style.panelHeight / 2f
            } else {
                0f
            }
        }

    /**
     * Радиус скругления углов.
     */
    var cornerRadius: Float
        get() = property.searchCornerRadius
        set(value) {
            property.searchCornerRadius = value
            background = RoundedColorDrawable(property.searchCornerRadius, getBackgroundColor())
            searchField.background = RoundedColorDrawable(property.searchCornerRadius, getBackgroundColor())
            filter.setRadius(property.searchCornerRadius)
        }

    /**
     * Максимальная длина вводимого текста.
     */
    var maxLength: Int = NO_MAX_LENGTH
        set(value) {
            if (value == NO_MAX_LENGTH) {
                searchField.filters = arrayOf()
            } else {
                searchField.filters = arrayOf(InputFilter.LengthFilter(value))
            }
        }

    init {
        setWillNotDraw(false)
        isClickable = true
        config.initStyle(context, attrs, defStyleAttr, R.style.SearchInputDefaultTheme)
        background = RoundedColorDrawable(property.searchCornerRadius, getBackgroundColor())

        filter = SbisFilterView(context, attrs)
        addView(filter)
        filter.setRadius(property.searchCornerRadius)

        searchField = TextInputView(this.context).apply {
            id = R.id.search_input_input_field
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            onEditorActionListener = { _, actionId, _ ->
                onActionObservable.onNext(actionId)
                hideKeyboard()
                true
            }
            valueColor = SbisColor.Int(style.searchTextColor)
            clearFocusOnBackPressed = true
            configureTextInput()
            showPlaceholderAsTitle = false
            imeOptions = property.imeOptions
            background = RoundedColorDrawable(property.searchCornerRadius, getBackgroundColor())
            placeholder = property.searchHint
            onValueChanged = { _, value ->
                onTextChanged(value)
                onTextChangeObservable.onNext(value)
                filter.showCurrentFilters(!hasFocus() && this.value.isEmpty())
            }
            setOnFocusChangeListener { _, inFocus ->
                onFocusChangeObservable.onNext(inFocus)
                filter.showCurrentFilters(!inFocus && this.value.isEmpty())
            }
            setOnClickListener {
                onClickObservable.onNext(Any())
            }
        }
        searchFieldTouchDelegate =
            UpdatableTouchDelegate(
                searchFieldBoundsTouchExpanded,
                searchFieldBoundsTouch,
                searchField
            )
        touchDelegate = searchFieldTouchDelegate
        addView(searchField)

        loupeIcon = TextLayout {
            paint.typeface = TypefaceManager.getSbisMobileIconTypeface(context)
            includeFontPad = false
            text = property.loupeIconText
        }.apply {
            id = R.id.search_input_loupe_icon
            textPaint.textSize = style.loupeSize.toFloat()
            textPaint.color = style.iconColor
            this.setOnClickListener { _, _ ->
                requestSearchFocus()
            }
        }

        clearButton = SimplifiedTextView(context, attrs, defStyleAttr, defStyleRes).apply {
            id = R.id.search_input_clear_btn
            paint.typeface = TypefaceManager.getSbisMobileIconTypeface(context)
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            paint.textSize = style.celarSize.toFloat()
            setTextColor(style.iconColor)
            text = property.clearIconText
            setOnClickListener {
                cancelSearchObservable.onNext(Any())
                searchField.clearFocus()
            }
        }
        addView(clearButton)

        touchManager = TextLayoutTouchManager(this, loupeIcon)

        with(filter) {
            id = R.id.search_input_filter
            visibility = if (property.hasFilter) VISIBLE else INVISIBLE

            clickListener = {
                when {
                    KeyboardUtils.isKeyboardVisible(it) -> {
                        searchField.context.getSystemService<InputMethodManager>()
                            ?.hideSoftInputFromWindow(
                                searchField.windowToken,
                                0,
                                object : ResultReceiver(searchField.handler) {
                                    override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                                        super.onReceiveResult(resultCode, resultData)
                                        if (
                                            resultCode == InputMethodManager.RESULT_UNCHANGED_HIDDEN ||
                                            resultCode == InputMethodManager.RESULT_HIDDEN
                                        ) {
                                            filterClickObservable.onNext(Any())
                                        }
                                    }
                                }
                            )
                        hideCursorFromSearch()
                    }

                    else -> {
                        filterClickObservable.onNext(Any())
                        hideKeyboard()
                    }
                }

            }

            setFilterColorType(if (property.searchColor == 1) FilterColorType.BASE else FilterColorType.ADDITIONAL)
            setSize(if (property.searchInputSize == 1) FilterSize.MEDIUM else FilterSize.SMALL)
            setDividerVisible(property.bottomDividerVisible)
        }
    }

    //region API
    /**
     * Возвращает [Observable] для подписки на изменение поисковой строки.
     * К событиям применяется [Observable.debounce] со значением задержки, заданным атрибутом
     * [R.styleable.SearchInput_inputDelay], либо с задержкой по умолчанию [DEFAULT_SEARCH_DELAY]
     */
    fun searchQueryChangedObservable(): Observable<String> {
        return onTextChangeObservable.distinctUntilChanged()
            .debounce(property.inputDelay, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
    }

    /**
     * Померить и получить текущую минимальную ширину панели поиска.
     */
    fun measureSearchInputMinWidth(): Int {
        searchField.measure(makeAtMostSpec(Int.MAX_VALUE), makeAtMostSpec(Int.MAX_VALUE))
        val searchFieldWidth = searchField.measuredWidth
        val horizontalPadding = paddingStart + paddingEnd
        val loupeIconWidth = loupeIcon.width
        val dividerWidth = style.verticalDividerSize
        val filterWidth = filter.measuredWidth
        return horizontalPadding + loupeIconWidth + searchFieldWidth + dividerWidth + filterWidth
    }

    /**
     * Возвращает [Observable] для подписки на изменение поисковой строки.
     * В отличие от [searchQueryChangedObservable], доставляет события без задержек и фильтрации
     */
    fun searchQueryChangedObservableWithoutDebounce(): Observable<String> {
        return onTextChangeObservable.distinctUntilChanged()
    }

    /**
     * Возвращает [Observable] для подписки на изменение состояния фокуса поисковой строки
     */
    fun searchFocusChangeObservable() = onFocusChangeObservable

    /**
     * Возвращает [Observable] для подписки на изменение состояния фокуса поисковой строки.
     * В отличие от [searchFocusChangeObservable] после отписки от источника не зануляет подписку изменение фокуса у View
     */
    fun searchFocusShareChangeObservable(): Observable<Boolean> = shareFocusSearchObservable

    /**
     * Возвращает [Observable] для подписки на события очистки содержимого строки при нажатии на кнопку отмены
     */
    fun cancelSearchObservable(): Observable<Any> = cancelSearchObservable
        .doOnNext { hideKeyboard() }
        .share()

    /**
     * Событие при нажатии кнопки отмены, возвращает observable, для установки кастомного
     * поведения. (Не вызывает скрытие клавиатуры)
     */
    fun cancelButtonCustomObservable(): Observable<Any> {
        return cancelSearchObservable
    }

    /**
     * Возвращает [Observable] для подписки на события [EditorInfo]
     */
    fun searchFieldEditorActionsObservable() = onActionObservable

    /**
     * Возвращает [Observable] для подписки на события [EditorInfo]
     * В отличие от [searchFieldEditorActionsObservable] после отписки от источника не зануляет подписку у View
     */
    fun searchFieldShareEditorActionsObservable(): Observable<Int> =
        shareFieldEditorActionsObservable

    /**
     * Возвращает [Observable] для подписки на события нажатия на иконку фильтра и выбранные фильтры
     */
    fun filterClickObservable(): Observable<Any> = filterClickObservable

    /**
     * Возвращает [Observable] для подписки на события нажатия на поле ввода
     */
    fun searchFieldClickObservable() = onClickObservable

    /**@SelfDocumented */
    fun showCursorInSearch() {
        requestSearchFocus()
        searchField.setSelection(searchField.value.length)
    }

    /**@SelfDocumented */
    fun hideCursorFromSearch() {
        searchField.clearFocus()
    }

    /**@SelfDocumented */
    fun showKeyboard() {
        if (KeyboardUtils.isActiveInput(searchField)) {
            KeyboardUtils.showKeyboard(searchField)
        } else {
            KeyboardUtils.showKeyboardPost(searchField.inputView)
        }
    }

    /**@SelfDocumented */
    fun hideKeyboard() {
        KeyboardUtils.hideKeyboard(searchField)
        hideCursorFromSearch()
    }

    /**@SelfDocumented */
    fun getSearchText(): String = searchText

    /**
     * Возвращает строку с текущими фильтрами, перечисленными через запятую
     */
    fun getFilterString(): String = filter.filterString()

    /**
     * Задаёт необходимость отображения иконки фильтров и строки с текущими фильтрами.
     * По умолчанию фильтры отображаются
     */
    fun setHasFilter(hasFilter: Boolean) {
        property.hasFilter = hasFilter
        filter.visibility = if (hasFilter) VISIBLE else INVISIBLE
        safeRequestLayout()
    }

    /**
     * Задаёт текущую поисковую строку
     */
    fun setSearchText(searchText: String) {
        if (this.searchText != searchText) {
            searchField.value = searchText
        }
    }

    /**
     * Вызывает сброс строки поиска аналогично нажатию кнопки отмены.
     */
    fun clearSearch() {
        clearButton.performClick()
    }

    /**
     * Задаёт подсказку в поле ввода
     */
    fun setSearchHint(hint: String) {
        searchField.placeholder = hint
    }

    /**
     * Установка выбранных фильтров и обновление разметки
     *
     * @param asDefault являются ли данные фильтры фильтрами по умолчанию
     */
    @JvmOverloads
    fun setSelectedFilters(filters: List<String>, asDefault: Boolean = false) {
        filter.setSelectedFilters(filters, asDefault)
    }

    /**
     * Отключение / включение фильтров, иногда по запросу проектировщиков нужно отключать фильтры если что-то введено в строку поиска
     */
    fun enableFilters(enable: Boolean) {
        filter.isEnabled = enable
    }

    /**@SelfDocumented */
    fun getSearchHint(): CharSequence =
        searchField.placeholder

    /**@SelfDocumented */
    fun setLoupeIconVisibility(isVisible: Boolean) {
        if (property.isVisibleLoupe != isVisible) {
            property.isVisibleLoupe = isVisible
            requestLayout()
        }
    }

    /**@SelfDocumented */
    fun setSearchColor(colorType: SearchColorType) {
        if (property.searchColor == colorType.id) return

        property.searchColor = colorType.id
        background = RoundedColorDrawable(property.searchCornerRadius, getBackgroundColor())
        searchField.background = RoundedColorDrawable(property.searchCornerRadius, getBackgroundColor())
        filter.setColor(
            when (colorType) {
                SearchColorType.BASE -> FilterColorType.BASE
                SearchColorType.ADDITIONAL -> FilterColorType.ADDITIONAL
            }
        )
        requestLayout()
    }

    /**@SelfDocumented */
    fun setCurrentFiltersVisibility(isVisible: Boolean) {
        filter.showCurrentFilters(isVisible)
    }
    //endregion API

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpec = MeasureSpec.makeMeasureSpec(style.panelHeight, MeasureSpec.AT_MOST)
        clearButton.setPadding(
            style.horizontalClearOffset,
            style.panelHeight / 2 - style.clearIconSize / 2,
            0,
            0
        )
        measureChild(
            clearButton,
            MeasureSpec.makeMeasureSpec(
                style.horizontalClearOffset + style.clearIconSize + style.horizontalClearOffset,
                MeasureSpec.EXACTLY
            ),
            MeasureSpec.makeMeasureSpec(style.panelHeight, MeasureSpec.EXACTLY)
        )
        measureChild(
            filter,
            MeasureSpec.makeMeasureSpec(
                size - style.minSearchSize,
                MeasureSpec.AT_MOST
            ),
            heightSpec
        )
        val searchAvailableSize =
            size - getSearchFieldOffset() - getClearButtonWidth() - if (property.hasFilter) {
                filter.measuredWidth
            } else {
                0
            }

        measureChild(
            searchField,
            MeasureSpec.makeMeasureSpec(searchAvailableSize, MeasureSpec.EXACTLY),
            heightMeasureSpec
        )

        super.onMeasure(widthMeasureSpec, heightSpec)
    }

    private fun getSearchFieldOffset() =
        style.leftPadding + if (property.isVisibleLoupe) loupeIcon.width + style.horizontalInputOffset else 0

    private fun getClearButtonWidth(): Int {
        var result = 0
        if (showClear) {
            result += style.horizontalClearOffset + style.clearIconSize + style.horizontalClearOffset
        }
        if (showClear && property.hasFilter) {
            result += style.verticalDividerSize
        }
        return result
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val height = b - t
        val width = r - l
        if (property.isVisibleLoupe) {
            loupeIcon.layout(style.leftPadding, (height - loupeIcon.height) / 2)
        }
        searchField.layout(getSearchFieldOffset(), (height - searchField.measuredHeight) / 2)
        if (changed) {
            getChildBoundsWithinSearchView(searchField, searchFieldBoundsTouch)
            searchFieldBoundsTouchExpanded.set(
                searchFieldBoundsTouch.left,
                0,
                searchFieldBoundsTouch.right,
                height
            )
            searchFieldTouchDelegate.setBounds(
                searchFieldBoundsTouchExpanded,
                searchFieldBoundsTouch
            )
        }
        if (showClear) {
            clearButton.layout(searchField.right, 0)

            val dividerOffset = clearButton.right
            val dividerVerticalOffset = (height - style.verticalDividerHeight) / 2
            divider.set(
                dividerOffset,
                dividerVerticalOffset,
                dividerOffset + style.verticalDividerSize,
                dividerVerticalOffset + style.verticalDividerHeight
            )
        }
        if (property.hasFilter) {
            filter.layout(width - filter.measuredWidth, 0)
        }
        bottomDividerRect.set(
            0,
            style.panelHeight - style.bottomDividerHeight,
            r,
            style.panelHeight
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (property.isVisibleLoupe) {
            loupeIcon.draw(canvas)
        }
        if (showClear) {
            if (property.hasFilter) {
                canvas.drawRect(divider, style.separatorPaint)
            }
        }
        if (property.bottomDividerVisible) {
            canvas.drawRect(bottomDividerRect, style.bottomDividerPaint)
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent) =
        touchManager.onTouch(this, event) || super.onTouchEvent(event)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isInEditMode) attachSearchInputContext()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (property.hideKeyboardOnDetach) hideKeyboard()
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState!!)
        ss.searchContext = searchInputContext
        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            searchInputContext = state.searchContext
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    fun getBackgroundColor() =
        if (property.searchColor == 1) style.baseColor else style.additionalColor

    private fun onTextChanged(newText: String) {
        searchText = newText
        showClear = newText.isNotEmpty()
        requestLayout()
    }

    private fun attachSearchInputContext() {
        searchInputContext = SearchInputContext(this)
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        searchField.visibility = visibility
    }

    private fun requestSearchFocus() {
        searchField.requestFocus()
    }

    private fun getChildBoundsWithinSearchView(view: View, rect: Rect) {
        val locationChild = IntArray(2)
        val location = IntArray(2)
        view.getLocationInWindow(locationChild)
        getLocationInWindow(location)
        val top: Int = locationChild[1] - location[1]
        val left: Int = locationChild[0] - location[0]
        rect.set(left, top, left + view.width, top + view.height)
    }

    private fun TextInputView.configureTextInput() {
        valueSize = style.searchTextSize
        setBottomOffsetUnderline(0)
    }

    private class SavedState : BaseSavedState {
        var searchContext: SearchInputContext? = null

        constructor(superState: Parcelable) : super(superState)

        private constructor(`in`: Parcel) : super(`in`) {
            searchContext = `in`.readNullableParcelableCompat(SearchInputContext::class.java)
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeParcelable(searchContext, flags)
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState = SavedState(parcel)

            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }

        override fun describeContents(): Int = 0
    }

}

/**
 * Цвет поиска
 */
enum class SearchColorType(val id: Int) {
    BASE(1),
    ADDITIONAL(2)
}