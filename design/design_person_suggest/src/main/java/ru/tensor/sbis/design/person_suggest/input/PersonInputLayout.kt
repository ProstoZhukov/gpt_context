package ru.tensor.sbis.design.person_suggest.input

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import androidx.core.view.children
import androidx.core.view.isVisible
import org.apache.commons.lang3.StringUtils.EMPTY
import ru.tensor.sbis.common.util.illegalState
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.styles.StyleParams.StyleKey
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeExactlySpec
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeUnspecifiedSpec
import ru.tensor.sbis.design.custom_view_tools.utils.TextLayoutTouchManager
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.person_suggest.R
import ru.tensor.sbis.design.person_suggest.input.contract.PersonInputLayoutApi
import ru.tensor.sbis.design.person_suggest.input.controller.PersonInputLayoutController
import ru.tensor.sbis.design.person_suggest.service.PersonSuggestData
import ru.tensor.sbis.design.profile.person.PersonView
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.profile_decl.person.PhotoSize
import ru.tensor.sbis.design.view.input.searchinput.SearchInput
import ru.tensor.sbis.persons.PersonName
import kotlin.math.roundToInt
import ru.tensor.sbis.design.R as RDesign

/**
 * Контейнер для компонента поисковой строки [SearchInput] с возможностью отображения фильтра по персоне.
 * @see PersonInputLayoutApi
 *
 * @author vv.chekurda
 */
class PersonInputLayout private constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int,
    private val controller: PersonInputLayoutController
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes),
    PersonInputLayoutApi by controller {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = R.attr.personInputLayoutTheme,
        @StyleRes defStyleRes: Int = R.style.PersonInputLayoutDefaultStyle,
    ) : this(context, attrs, defStyleAttr, defStyleRes, PersonInputLayoutController())

    @StyleRes
    private var personPhotoStyle: Int = R.style.PersonInputDefaultPhotoStyle
    private val personPhoto: PersonView by lazy {
        PersonView(ContextThemeWrapper(getContext(), personPhotoStyle)).apply {
            id = R.id.design_person_suggest_input_person_view_id
            isVisible = false
            this@PersonInputLayout.addView(this)
        }
    }

    private val personName = TextLayout.createTextLayoutByStyle(
        getContext(),
        StyleKey(R.style.PersonInputDefaultNameStyle, R.attr.PersonInputLayout_personNameStyle)
    ) {
        isVisible = false
    }.apply {
        id = R.id.design_person_suggest_input_person_name_id
    }

    private val clearButton = TextLayout.createTextLayoutByStyle(
        getContext(),
        StyleKey(R.style.PersonInputDefaultClearButtonStyle, R.attr.PersonInputLayout_clearButtonStyle)
    ) {
        isVisible = false
    }.apply {
        id = R.id.design_person_suggest_input_clear_button_id
    }
    private val clearButtonClickableRect = Rect()

    private val dividerPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = VERTICAL_DIVIDER_WIDTH_PX.toFloat()
    }

    private val touchManager = TextLayoutTouchManager(this, personName, clearButton)

    lateinit var searchInput: SearchInput

    init {
        setWillNotDraw(false)

        getContext().withStyledAttributes(attrs, R.styleable.PersonInputLayout, defStyleAttr, defStyleRes) {
            personPhotoStyle = getResourceId(R.styleable.PersonInputLayout_PersonInputLayout_personPhotoStyle, personPhotoStyle)
            dividerPaint.color = getColor(R.styleable.PersonInputLayout_PersonInputLayout_verticalDividerColor, Color.TRANSPARENT)
                .takeIf { it != Color.TRANSPARENT }
                ?: ContextCompat.getColor(context, RDesign.color.palette_alpha_color_black1)
            controller.personInputHint = getString(R.styleable.PersonInputLayout_PersonInputLayout_hintText).orEmpty()
        }
    }

    init {
        if (isInEditMode) {
            controller.isInEditMode = true
            context.theme.applyStyle(RDesign.style.AppGlobalTheme, false)
            addView(SearchInput(getContext()))
            personPhoto.setSize(PhotoSize.XS)
            personFilter = PersonSuggestData(
                PersonData(photoUrl = "."),
                PersonName("Иван", "Епанчин", EMPTY)
            )
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        controller.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        controller.onDetachedFromWindow()
    }

    override fun addView(child: View, index: Int, params: LayoutParams?) {
        super.addView(child, index, params)
        checkAddSearchInput(child)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val horizontalPadding = paddingStart + paddingEnd
        val verticalPadding = paddingTop + paddingBottom
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val availableWidth = width - horizontalPadding

        val searchInputWidth = if (controller.showPersonFilter) {
            personPhoto.measure(makeUnspecifiedSpec(), makeUnspecifiedSpec())
            if (personName.isVisible) {
                val inputMinWidth = searchInput.measureSearchInputMinWidth()
                val nameAvailableWidth = availableWidth - inputMinWidth - personPhoto.measuredWidth -
                        clearButton.width - VERTICAL_DIVIDER_WIDTH_PX

                personName.configure { layoutWidth = null }
                val nameWrappedWidth = personName.width

                if (nameWrappedWidth >= nameAvailableWidth) {
                    personName.configure { layoutWidth = nameAvailableWidth }
                    inputMinWidth
                } else {
                    personName.configure { layoutWidth = nameWrappedWidth }
                    inputMinWidth + (nameAvailableWidth - nameWrappedWidth)
                }
            } else {
                availableWidth - personPhoto.measuredWidth - clearButton.width - VERTICAL_DIVIDER_WIDTH_PX
            }
        } else {
            availableWidth
        }

        searchInput.measure(makeExactlySpec(searchInputWidth), makeUnspecifiedSpec())

        setMeasuredDimension(width, verticalPadding + searchInput.measuredHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        checkHasInputChild()
        if (controller.showPersonFilter) {
            val photoTop = ((measuredHeight - personPhoto.measuredHeight) / 2f).roundToInt()
            val nameHeight = personName.height.takeIf { it != 0 } ?: personName.getDesiredHeight()
            val nameTop = ((measuredHeight - nameHeight) / 2f).roundToInt()
            val clearTop = nameTop + personName.baseline - clearButton.baseline

            personPhoto.layout(paddingStart, photoTop)
            personName.layout(personPhoto.right, nameTop)
            clearButton.layout(personName.right, clearTop)
            searchInput.layout(clearButton.right + VERTICAL_DIVIDER_WIDTH_PX, paddingTop)

            updateClearClickableArea()
        } else {
            searchInput.layout(paddingStart, paddingTop)
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (controller.showPersonFilter) {
            personName.draw(canvas)
            clearButton.draw(canvas)
            drawVerticalDivider(canvas)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean =
        touchManager.onTouch(this, event) || super.onTouchEvent(event)

    override fun onSaveInstanceState(): Parcelable =
        controller.onSaveInstanceState(super.onSaveInstanceState())

    override fun onRestoreInstanceState(state: Parcelable) {
        super.onRestoreInstanceState(controller.onRestoreInstanceState(state))
    }

    private fun updateClearClickableArea() {
        clearButtonClickableRect.set(
            clearButton.left,
            paddingTop,
            clearButton.right,
            measuredHeight - paddingBottom
        )
        clearButton.setStaticTouchRect(clearButtonClickableRect)
    }

    private fun drawVerticalDivider(canvas: Canvas) {
        canvas.drawLine(
            clearButton.right.toFloat(),
            paddingTop.toFloat(),
            (clearButton.right + VERTICAL_DIVIDER_WIDTH_PX).toFloat(),
            (height - paddingBottom).toFloat(),
            dividerPaint
        )
    }

    private fun checkAddSearchInput(view: View) {
        if (view is SearchInput) {
            searchInput = view
            background = ColorDrawable(searchInput.getBackgroundColor())
            controller.attachViews(this, view, personName, clearButton) { personPhoto }
        }
    }

    private fun checkHasInputChild() {
        if (childCount != 2 && children.find { it is SearchInput } == null) {
            illegalState { "PersonInputLayout should contain SearchInput view" }
        }
    }
}

private const val VERTICAL_DIVIDER_WIDTH_PX = 1