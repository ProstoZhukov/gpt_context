package ru.tensor.sbis.design.person_suggest.input.controller

import android.os.Bundle
import android.os.Parcelable
import androidx.core.view.isVisible
import io.reactivex.disposables.SerialDisposable
import org.apache.commons.lang3.StringUtils.EMPTY
import ru.tensor.sbis.common.R as RCommon
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.person_suggest.input.PersonInputLayout
import ru.tensor.sbis.design.person_suggest.input.contract.PersonInputLayoutApi
import ru.tensor.sbis.design.person_suggest.input.contract.PersonInputLayoutListener
import ru.tensor.sbis.design.person_suggest.input.controller.PersonInputViewState.*
import ru.tensor.sbis.design.person_suggest.service.PersonSuggestData
import ru.tensor.sbis.design.profile.person.PersonView
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.profile_decl.util.PersonNameTemplate
import ru.tensor.sbis.design.utils.DebounceActionHandler
import ru.tensor.sbis.design.view.input.searchinput.SearchInput
import ru.tensor.sbis.persons.util.formatName

/**
 * Контроллер для управления состоянием и внешним видом контейнера [PersonInputLayout].
 * @see PersonInputLayoutApi
 *
 * @author vv.chekurda
 */
internal class PersonInputLayoutController : PersonInputLayoutApi {

    private lateinit var container: PersonInputLayout
    private lateinit var personName: TextLayout
    private lateinit var clearButton: TextLayout
    private lateinit var searchInput: SearchInput
    private lateinit var lazyPersonView: () -> PersonView
    private val personView: PersonView by lazy {
        isPersonViewInitialized = true
        lazyPersonView.invoke()
    }
    private var isPersonViewInitialized = false
    private val onPersonClick = {
        DebounceActionHandler.INSTANCE.handle {
            listener?.onPersonClick(personFilter?.uuid ?: return@handle)
        }
    }

    private var hasFocus: Boolean = false
    private var hasSearchText: Boolean = false
    private val isLandscape: Boolean by lazy {
        container.resources.getBoolean(RCommon.bool.is_landscape)
    }

    private val inputFocusDisposable = SerialDisposable()
    private val inputTextDisposable = SerialDisposable()
    private val inputClearDisposable = SerialDisposable()

    private var viewState: PersonInputViewState = HIDDEN
        set(value) {
            val isChanged = value != field
            field = value
            if (isChanged) performViewState()
        }

    private lateinit var searchInputHint: String

    /**
     * Подсказка, которая отображается при пустой поисковой строке и выбранной персоне в фильтре.
     */
    lateinit var personInputHint: String

    /**
     * Признак необходимости отображать фильтр по персоне.
     */
    val showPersonFilter: Boolean
        get() = viewState != HIDDEN

    /**
     * Признак того, что контейнер находится в режиме отображения студийного превью.
     */
    var isInEditMode: Boolean = false

    override var personFilter: PersonSuggestData? = null
        set(value) {
            val isChanged = value != field
            field = value
            if (isChanged) {
                if (!isPersonViewInitialized) {
                    personView.setOnClickListener { onPersonClick() }
                }
                configureContainer()
            }
        }

    override var listener: PersonInputLayoutListener? = null
        set(value) {
            field = value
            if (value != null) {
                if (isPersonViewInitialized) {
                    personView.setOnClickListener { onPersonClick() }
                }
                personName.setOnClickListener { _, _ -> onPersonClick() }
                clearButton.setOnClickListener { _, _ ->
                    DebounceActionHandler.INSTANCE.handle {
                        clearPersonFilter()
                        value.onCancelPersonFilterClick()
                    }
                }
            } else {
                if (isPersonViewInitialized) {
                    personView.setOnClickListener(null)
                }
                personName.setOnClickListener(null)
                clearButton.setOnClickListener(null)
            }
        }

    override fun clearPersonFilter() {
        if (viewState == HIDDEN) return
        personFilter = null
        hideKeyboard()
        updateViewState()
    }

    fun attachViews(
        container: PersonInputLayout,
        searchInput: SearchInput,
        personName: TextLayout,
        clearButton: TextLayout,
        lazyPersonView: () -> PersonView
    ) {
        this.container = container
        this.searchInput = searchInput
        this.personName = personName
        this.clearButton = clearButton
        this.lazyPersonView = lazyPersonView
        hasFocus = searchInput.hasFocus()
        hasSearchText = searchInput.getSearchText().isNotEmpty()
    }

    fun onAttachedToWindow() {
        searchInput.searchFocusShareChangeObservable()
            .subscribe { isFocused ->
                hasFocus = isFocused
                updateViewState()
            }.storeIn(inputFocusDisposable)
        searchInput.searchQueryChangedObservableWithoutDebounce()
            .subscribe { searchText ->
                hasSearchText = searchText.isNotEmpty()
                updateViewState()
             }.storeIn(inputTextDisposable)
        searchInput.cancelSearchObservable()
            .subscribe { clearPersonFilter() }
            .storeIn(inputClearDisposable)
    }

    fun onDetachedFromWindow() {
        inputFocusDisposable.set(null)
        inputTextDisposable.set(null)
        inputClearDisposable.set(null)
    }

    fun onSaveInstanceState(superState: Parcelable?): Parcelable =
        Bundle().apply {
            putParcelable(SUPER_STATE, superState)
            putParcelable(PERSON_DATA, personFilter)
            putInt(VIEW_STATE, viewState.ordinal)
        }

    fun onRestoreInstanceState(state: Parcelable): Parcelable? =
        if (state is Bundle) {
            state.getParcelable<PersonSuggestData>(PERSON_DATA)?.also {
                personFilter = it
            }
            viewState = PersonInputViewState.values()[state.getInt(VIEW_STATE, HIDDEN.ordinal)]
            state.getParcelable(SUPER_STATE)
        } else {
            null
        }

    private fun configureContainer() {
        if (!isInEditMode) personView.setData(personFilter?.personData ?: PersonData())
        personName.configure { text = personFilter?.name?.formatName(PersonNameTemplate.SURNAME_N).orEmpty() }
        configureSearchInput()
        updateViewState()
        container.safeRequestLayout()
    }

    private fun configureSearchInput() {
        searchInput.apply {
            if (personFilter != null) {
                this@PersonInputLayoutController.hideKeyboard()
                searchInputHint = searchInput.getSearchHint().toString()
                setSearchHint(personInputHint)
                setLoupeIconVisibility(false)
                setCurrentFiltersVisibility(isLandscape)
                setSearchText(EMPTY)
            } else {
                setSearchHint(searchInputHint)
                setLoupeIconVisibility(true)
                setCurrentFiltersVisibility(true)
            }
        }
    }

    private fun updateViewState() {
        viewState = when {
            personFilter == null -> HIDDEN
            hasSearchText -> ONLY_PHOTO
            hasFocus -> PHOTO_AND_CLEAR
            else -> FULL
        }
    }

    private fun performViewState() {
        var isPhotoVisible = false
        var isNameVisible = false
        var isClearButtonVisible = false
        when (viewState) {
            FULL -> {
                isPhotoVisible = true
                isClearButtonVisible = true
                isNameVisible = true
            }
            PHOTO_AND_CLEAR -> {
                isPhotoVisible = true
                isClearButtonVisible = true
            }
            ONLY_PHOTO -> {
                isPhotoVisible = true
            }
            HIDDEN -> Unit
        }
        personView.isVisible = isPhotoVisible
        personName.configure { isVisible = isNameVisible }
        clearButton.configure { isVisible = isClearButtonVisible }
        container.safeRequestLayout()
    }

    private fun hideKeyboard() {
        searchInput.hideKeyboard()
        searchInput.clearFocus()
    }
}

private enum class PersonInputViewState {
    HIDDEN,
    ONLY_PHOTO,
    PHOTO_AND_CLEAR,
    FULL
}

private const val SUPER_STATE = "super_state"
private const val PERSON_DATA = "person_data"
private const val VIEW_STATE = "view_state"