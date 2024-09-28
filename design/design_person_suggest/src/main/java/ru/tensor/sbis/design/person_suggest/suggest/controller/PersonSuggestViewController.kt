package ru.tensor.sbis.design.person_suggest.suggest.controller

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout.LayoutParams
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.person_suggest.suggest.PersonSuggestView
import ru.tensor.sbis.design.person_suggest.R
import ru.tensor.sbis.design.person_suggest.service.PersonSuggestData
import ru.tensor.sbis.design.person_suggest.suggest.adapter.PersonSuggestAdapter
import ru.tensor.sbis.design.person_suggest.suggest.adapter.PersonSuggestTheme
import ru.tensor.sbis.design.person_suggest.suggest.contract.PersonSelectionListener
import ru.tensor.sbis.design.person_suggest.suggest.contract.PersonSuggestKeyboardBehavior
import ru.tensor.sbis.design.person_suggest.suggest.contract.PersonSuggestViewApi
import ru.tensor.sbis.design.profile_decl.person.PhotoSize
import androidx.core.view.updatePadding

/**
 * Контроллер для управления компонентом панели выбора персоны.
 *
 * @property keyboardEventHandler обработчик событий клавиатуры для стандартного поведения компонента.
 *
 * @author vv.chekurda
 */
internal class PersonSuggestViewController private constructor(
    private val keyboardEventHandler: PersonSuggestKeyboardEventHandler
) : PersonSuggestViewApi,
    PersonSuggestKeyboardBehavior by keyboardEventHandler {

    constructor() : this(PersonSuggestKeyboardEventHandler())

    private lateinit var view: PersonSuggestView
    private lateinit var recyclerView: RecyclerView
    private lateinit var theme: PersonSuggestTheme

    /**
     * Признак необходимости игнорировать сброс позиции списка.
     * Необходим для корректного восстановления состояния списка при повороте экрана с поднятой клавиатурой.
     */
    private var ignoreResetPosition = false

    private val adapter: PersonSuggestAdapter
        get() = checkNotNull(recyclerView.adapter as? PersonSuggestAdapter) {
            "PersonSuggestView is not initialized, call init before setting data"
        }

    val defaultLayoutParams: LayoutParams =
        LayoutParams(MATCH_PARENT, WRAP_CONTENT)

    override var data: List<PersonSuggestData> = emptyList()
        set(value) {
            field = value
            adapter.content = value
            keyboardEventHandler.hasData = value.isNotEmpty()
        }

    override fun init(listener: PersonSelectionListener) {
        recyclerView.adapter = PersonSuggestAdapter(theme, listener)
    }

    /**
     * Считать модель темы компонента.
     */
    fun resolveTheme(
        context: Context,
        attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int,
        @StyleRes defStyleRes: Int
    ) {
        context.withStyledAttributes(attrs, R.styleable.PersonSuggestView, defStyleAttr, defStyleRes) {
            theme = PersonSuggestTheme(
                backgroundColor = getColor(R.styleable.PersonSuggestView_PersonSuggestView_backgroundColor, Color.MAGENTA),
                listHorizontalPadding = getDimensionPixelSize(R.styleable.PersonSuggestView_PersonSuggestView_listHorizontalPadding, 0),
                personHorizontalPadding = getDimensionPixelSize(R.styleable.PersonSuggestView_PersonSuggestView_personHorizontalPadding, 0),
                personVerticalPadding = getDimensionPixelSize(R.styleable.PersonSuggestView_PersonSuggestView_personVerticalPadding, 0),
                photoSize = getInteger(R.styleable.PersonSuggestView_PersonSuggestView_photoSize, 0).let(PhotoSize.values()::get)
            )
        }
    }

    /**
     * Присоединить вью компоненты к контроллеру.
     *
     * @param view вью компонента выбора персоны.
     * @param recyclerView вью списка персон.
     */
    fun attach(view: PersonSuggestView, recyclerView: RecyclerView) {
        this.view = view.apply { setBackgroundColor(theme.backgroundColor) }
        this.recyclerView = recyclerView.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            updatePadding(left = theme.listHorizontalPadding, right = theme.listHorizontalPadding)
            clipToPadding = false
        }
        keyboardEventHandler.targetView = view
    }

    /**
     * Изменилась видимость компонента выбора персоны.
     */
    fun onVisibilityChanged(visibility: Int) {
        if (visibility == View.VISIBLE) {
            tryResetScrollPosition()
        }
    }

    /**
     * Компонент выбора персоны присоединился к окну.
     */
    fun onAttachedToWindow() {
        ignoreResetPosition = true
    }

    /**
     * Попытаться сбросить позицию сколла.
     * Данная логика предотвращает случайные сбрасывания сохраненной позиции сколла после поворота экрана.
     * Показали View -> тут ее потенциально могли поскролить -> скрыли -> снова показали -> сбрасываем позицию на повторный показ.
     */
    private fun tryResetScrollPosition() {
        if (ignoreResetPosition) {
            ignoreResetPosition = false
        } else {
            recyclerView.scrollToPosition(0)
        }
    }

    override fun release() {
        recyclerView.adapter = null
    }
}