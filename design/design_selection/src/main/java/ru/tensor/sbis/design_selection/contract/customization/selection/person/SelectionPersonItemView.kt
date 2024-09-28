package ru.tensor.sbis.design_selection.contract.customization.selection.person

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import org.apache.commons.lang3.StringUtils.EMPTY
import org.json.JSONObject
import ru.tensor.sbis.common_views.SearchSpan
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.styles.CanvasStylesProvider
import ru.tensor.sbis.design.custom_view_tools.styles.StyleParams.StyleKey
import ru.tensor.sbis.design.custom_view_tools.utils.HighlightSpan
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeAtMostSpec
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeExactlySpec
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeUnspecifiedSpec
import ru.tensor.sbis.design.custom_view_tools.utils.TextHighlights
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.custom_view_tools.utils.textHeight
import ru.tensor.sbis.design.profile.person.PersonView
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import ru.tensor.sbis.design_selection.R
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design_selection.contract.data.SelectionPersonItem
import ru.tensor.sbis.design_selection.contract.customization.selection.person.SelectionPersonItemStylesProvider.activateResourceCacheForRecycler
import ru.tensor.sbis.design_selection.contract.customization.selection.person.SelectionPersonItemStylesProvider.textStyleProvider
import kotlin.math.roundToInt
import ru.tensor.sbis.design.R as RDesign

/**
 * View ячейки списка для отображения информации о персоне в компоненте выбора.
 * При помощи темизации поддерживает отображение для одиночного и множественного выбора.
 *
 * @author vv.chekurda
 */
class SelectionPersonItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.SelectionItem_personTheme,
    @StyleRes defStyleRes: Int = R.style.SelectionItemTheme_Person
) : FrameLayout(
    ThemeContextBuilder(context, attrs, defStyleAttr, defStyleRes).build(),
    attrs,
    R.attr.SelectionItem_personContainerStyle
) {

    /**
     * View фотографии персоны.
     */
    private val personView: PersonView

    /**
     * View иконки выбора.
     */
    private val selectionIconView: SbisTextView

    /**
     * View для определения кликабельной области выбора.
     */
    private val clickArea: View

    /**
     * Текстовая разметка заголовка.
     */
    private val title: TextLayout

    /**
     * Признак сотрудника сторонней организации.
     */
    private val outerCompanyIcon: TextLayout

    /**
     * Текстовая разметка подзаголовка.
     */
    private val subtitle: TextLayout

    private val viewsHorizontalPadding = getContext().getDimenPx(RDesign.attr.offset_s)
    private val selectionIconMargin = getContext().getDimenPx(RDesign.attr.offset_m)
    private val imageVerticalMargin = getContext().getDimenPx(RDesign.attr.offset_xs)
    private val textVerticalSpacing = getContext().getDimenPx(RDesign.attr.offset_2xs)
    private val textToCornerSpacing = getContext().getDimenPx(RDesign.attr.offset_m) - imageVerticalMargin
    private val highlightColor = getContext().getColorFromAttr(RDesign.attr.textBackgroundColorDecoratorHighlight)

    private val isMultiSelection: Boolean
        get() = selectionIconView.isVisible

    private val textPaddingEnd: Int
        get() = if (isMultiSelection) {
            selectionIconMargin
        } else {
            viewsHorizontalPadding
        }

    init {
        setWillNotDraw(false)
        personView = PersonView(
            ThemeContextBuilder(getContext(), defStyleAttr = R.attr.SelectionItem_photoStyle).build()
        ).apply {
            id = R.id.selection_person_item_photo
            updatePadding(left = viewsHorizontalPadding, right = viewsHorizontalPadding)
            setHasActivityStatus(true)
        }

        selectionIconView = SbisTextView(
            ThemeContextBuilder(getContext(), defStyleAttr = R.attr.SelectionItem_selectionIconStyle).build()
        )

        clickArea = View(getContext()).apply {
            id = R.id.selection_person_item_selection_icon_click_area
            visibility = selectionIconView.visibility
        }

        title = TextLayout.createTextLayoutByStyle(
            getContext(),
            StyleKey(
                styleAttr = R.attr.SelectionItem_titleStyle,
                styleRes = R.style.SelectionItemTitleStyle
            ),
            textStyleProvider
        ).apply {
            updatePadding(top = textToCornerSpacing)
        }

        subtitle = TextLayout.createTextLayoutByStyle(
            getContext(),
            StyleKey(
                styleAttr = R.attr.SelectionItem_subtitleStyle,
                styleRes = R.style.SelectionItemSubtitleStyle
            ),
            textStyleProvider
        ).apply {
            updatePadding(
                top = textVerticalSpacing,
                bottom = textToCornerSpacing,
                end = textPaddingEnd
            )
        }

        outerCompanyIcon = TextLayout.createTextLayoutByStyle(
            getContext(),
            StyleKey(
                styleAttr = R.attr.SelectionItem_titleIconStyle,
                styleRes = R.style.SelectionItemTitleIconStyle_Person
            ),
            textStyleProvider
        )

        updatePadding(top = imageVerticalMargin, bottom = imageVerticalMargin)

        addView(personView)
        addView(selectionIconView)
        addView(clickArea)

        setupForAutoTests()
        if (isInEditMode) showPreview()
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        if (!selected) selectionIconView.setText(RDesign.string.design_mobile_icon_action_add)
        else selectionIconView.setText(RDesign.string.design_mobile_icon_unsuccess_big)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        activateResourceCacheForRecycler(this)
    }

    /** @SelfDocumented */
    fun setData(data: SelectionPersonItem) {
        personView.setData(data.photoData)
        val isSubtitleChanged = this.subtitle.configure {
            text = data.subtitle ?: EMPTY
            isVisible = !data.subtitle.isNullOrBlank()
        }
        val hasSubtitle = this.subtitle.isVisible
        val isTitleChanged = this.title.configure {
            text = data.title
            highlights = data.titleHighlights.toTextHighlights(highlightColor)
        }
        outerCompanyIcon.configure { isVisible = !data.isInMyCompany }
        val isTitlePaddingChanged = this.title.updatePadding(bottom = if (hasSubtitle) 0 else textToCornerSpacing)
        val isChanged = isTitleChanged || isSubtitleChanged || isTitlePaddingChanged
        if (isChanged) safeRequestLayout()
    }

    fun changeSelectButtonVisibility(isVisible: Boolean) {
        clickArea.isVisible = isVisible
        selectionIconView.isVisible = isVisible
        subtitle.updatePadding(end = textPaddingEnd)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChild(
            personView,
            makeUnspecifiedSpec(),
            makeUnspecifiedSpec()
        )
        val contentMaxHeight = maxOf(personView.measuredHeight, title.height + subtitle.height)
        val height = maxOf(
            paddingTop + contentMaxHeight + paddingBottom,
            minimumHeight
        )

        if (isMultiSelection) {
            measureChild(
                selectionIconView,
                makeUnspecifiedSpec(),
                makeUnspecifiedSpec()
            )
            measureChild(
                clickArea,
                makeAtMostSpec(selectionIconView.measuredWidth),
                makeAtMostSpec(height - paddingTop - paddingBottom)
            )
        }

        setMeasuredDimension(
            makeExactlySpec(MeasureSpec.getSize(widthMeasureSpec)),
            height
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        personView.layout(
            paddingStart,
            paddingTop,
            paddingStart + personView.measuredWidth,
            paddingTop + personView.measuredHeight
        )

        val titleTop = paddingTop
        val centerOfTitleText = titleTop + title.paddingTop + (title.textPaint.textHeight / 2f).roundToInt()
        // Центр иконки выбора должен быть на одной линии с серединой текста заголовка.
        val selectionIconTop = maxOf(
            centerOfTitleText - (selectionIconView.measuredHeight / 2f).roundToInt(),
            titleTop
        )
        selectionIconView.layout(
            measuredWidth - paddingEnd - selectionIconView.measuredWidth,
            selectionIconTop,
            measuredWidth - paddingEnd,
            selectionIconTop + selectionIconView.measuredHeight
        )

        clickArea.layout(
            selectionIconView.left,
            paddingTop,
            selectionIconView.right,
            measuredHeight - paddingBottom
        )

        outerCompanyIcon.updatePadding(end = textPaddingEnd)
        title.run {
            configure { maxWidth = selectionIconView.left - outerCompanyIcon.width - personView.right }
            updatePadding(end = if (outerCompanyIcon.isVisible) 0 else textPaddingEnd)
            layout(personView.right, titleTop)
        }
        outerCompanyIcon.layout(title.right, title.top + title.baseline - outerCompanyIcon.baseline)

        subtitle.run {
            configure { layoutWidth = selectionIconView.left - personView.right }
            layout(title.left, title.bottom)
        }
    }

    override fun onDraw(canvas: Canvas) {
        title.draw(canvas)
        outerCompanyIcon.draw(canvas)
        subtitle.draw(canvas)
    }

    override fun hasOverlappingRendering(): Boolean = false

    /**
     * Настроить для автотестов.
     */
    private fun setupForAutoTests() {
        accessibilityDelegate = object : AccessibilityDelegate() {
            override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                info.text = JSONObject(
                    mapOf(
                        context.resources.getResourceEntryName(R.id.selection_person_item_title) to title.text,
                        context.resources.getResourceEntryName(R.id.selection_person_item_subtitle) to subtitle.text
                    )
                ).toString()
            }
        }
    }

    /**
     * Показать preview в студии.
     */
    private fun showPreview() {
        title.configure {
            text = "Иванов Василий"
        }
        subtitle.configure {
            text = "Отдел внутреннего контроля отделов внутреннего контроля"
        }
    }
}

/**
 *  Подготавливает список специализированных моделей [HighlightSpan] для выделения текста.
 */
private fun List<SearchSpan>.toTextHighlights(@ColorInt highlightColor: Int): TextHighlights =
    TextHighlights(map { HighlightSpan(it.start, it.end) }, highlightColor)

/**
 * Поставщик кэшируемых стилей текстовой разметки [SelectionPersonItemView].
 */
private object SelectionPersonItemStylesProvider : CanvasStylesProvider()
