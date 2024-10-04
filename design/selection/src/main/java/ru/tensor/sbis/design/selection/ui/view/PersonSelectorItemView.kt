package ru.tensor.sbis.design.selection.ui.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import org.apache.commons.lang3.StringUtils.EMPTY
import org.json.JSONObject
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.styles.CanvasStylesProvider
import ru.tensor.sbis.design.custom_view_tools.utils.HighlightSpan
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeAtMostSpec
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeUnspecifiedSpec
import ru.tensor.sbis.design.custom_view_tools.utils.TextHighlights
import ru.tensor.sbis.design.custom_view_tools.utils.safeMeasuredWidth
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.custom_view_tools.utils.textHeight
import ru.tensor.sbis.design.profile.person.PersonView
import ru.tensor.sbis.design.profile_decl.person.PhotoData
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemMeta
import ru.tensor.sbis.design.selection.ui.list.items.multi.MAX_LINES_WITHOUT_SUBTITLE
import ru.tensor.sbis.design.selection.ui.list.items.multi.MAX_LINES_WITH_SUBTITLE
import ru.tensor.sbis.design.selection.ui.model.recipient.ContractorSelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.PersonSelectorItemModel
import ru.tensor.sbis.design.selection.ui.view.SelectorPersonItemStylesProvider.activateResourceCacheForRecycler
import ru.tensor.sbis.design.selection.ui.view.SelectorPersonItemStylesProvider.textStyleProvider
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import ru.tensor.sbis.design.utils.getDimenPx
import kotlin.math.roundToInt
import ru.tensor.sbis.design.R as RDesign

/**
 * View ячейки списка для отображения информации о персоне в компоненте выбора.
 * При помощи темизации поддерживает отображение для одиночного и множественного выбора.
 *
 * @author vv.chekurda
 */
internal class PersonSelectorItemView private constructor(
    styledContext: PersonItemStyledContext,
    attrs: AttributeSet? = null,
    @AttrRes containerStyleAttr: Int = R.attr.Selector_itemContainerStyle,
    @StyleRes containerStyleRes: Int = styledContext.containerStyle
) : FrameLayout(
    ContextThemeWrapper(styledContext.context, containerStyleRes),
    attrs,
    containerStyleAttr,
    containerStyleRes
) {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = R.attr.Selector_personMultiItemTheme,
        @StyleRes defStyleRes: Int = R.style.SelectionRecipientItemTheme_Multi_Person
    ) : this(
        PersonItemStyledContext(context, attrs, defStyleAttr, defStyleRes),
        attrs
    )

    /**
     * Модель стилизованного контекста.
     * Служит для темизации контекста и получения стилей внутренних элементов ячейки.
     */
    private class PersonItemStyledContext(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = R.attr.Selector_personMultiItemTheme,
        @StyleRes defStyleRes: Int = R.style.SelectionRecipientItemTheme_Multi_Person
    ) {

        val context: Context

        /**
         * Стиль контейнера.
         */
        @StyleRes
        val containerStyle: Int

        /**
         * Стиль заголовка.
         */
        @StyleRes
        val titleStyle: Int

        /**
         * Стиль подзаголовка.
         */
        @StyleRes
        val subtitleStyle: Int

        /**
         * Стиль фотографии персоны.
         */
        @StyleRes
        val personViewStyle: Int

        /**
         * Стиль иконки выбора.
         */
        @StyleRes
        val selectionIconStyle: Int

        init {
            var containerStyleRes = R.style.SelectionDefaultMultiItemBody_Person
            var titleStyleRes = R.style.SelectionDefaultItemTitle_Recipient
            var subtitleStyleRes = R.style.SelectionDefaultItemSubtitle_Recipient
            var personViewStyleRes = R.style.SelectionRecipientPersonPhoto
            var selectionIconStyleRes = R.style.SelectionDefaultItemSelectionIcon

            this.context = ThemeContextBuilder(
                context,
                attrs,
                defStyleAttr,
                defStyleRes
            ).build().also {
                it.withStyledAttributes(attrs = R.styleable.Selector) {
                    containerStyleRes = getResourceId(
                        R.styleable.Selector_Selector_itemContainerStyle,
                        containerStyleRes
                    )
                    titleStyleRes = getResourceId(
                        R.styleable.Selector_Selector_itemTitleStyle,
                        titleStyleRes
                    )
                    subtitleStyleRes = getResourceId(
                        R.styleable.Selector_Selector_itemSubtitleStyle,
                        subtitleStyleRes
                    )
                    personViewStyleRes = getResourceId(
                        R.styleable.Selector_Selector_personPhotoStyle,
                        personViewStyleRes
                    )
                    selectionIconStyleRes = getResourceId(
                        R.styleable.Selector_Selector_itemSelectionIconStyle,
                        selectionIconStyleRes
                    )
                }
            }
            containerStyle = containerStyleRes
            titleStyle = titleStyleRes
            subtitleStyle = subtitleStyleRes
            personViewStyle = personViewStyleRes
            selectionIconStyle = selectionIconStyleRes
        }
    }

    /**
     * View фотографии персоны.
     */
    private val personView: PersonView

    /**
     * View иконки выбора.
     */
    private val selectionIconView: TextView

    /**
     * View для определения кликабельной области выбора.
     */
    private val clickArea: View

    /**
     * Текстовая разметка заголовка.
     */
    private val title: TextLayout

    /**
     * Текстовая разметка подзаголовка.
     */
    private val subtitle: TextLayout

    private val viewsHorizontalPadding = context.getDimenPx(RDesign.attr.offset_s)
    private val selectionIconMargin = context.getDimenPx(RDesign.attr.offset_m)
    private val imageVerticalMargin = context.getDimenPx(RDesign.attr.offset_xs)
    private val textVerticalSpacing = context.getDimenPx(RDesign.attr.offset_2xs)
    private val textToCornerSpacing = context.getDimenPx(RDesign.attr.offset_m) - imageVerticalMargin
    private val highlightColor = context.getColorFromAttr(RDesign.attr.textBackgroundColorDecoratorHighlight)

    init {
        setWillNotDraw(false)
        with(styledContext) {
            personView = PersonView(ContextThemeWrapper(context, personViewStyle)).apply {
                id = R.id.selection_person_photo
                updatePadding(
                    left = viewsHorizontalPadding,
                    right = viewsHorizontalPadding
                )
            }

            selectionIconView = TextView(
                ContextThemeWrapper(context, selectionIconStyle),
                null,
                R.attr.Selector_itemSelectionIconStyle
            )
            selectionIconView.typeface = TypefaceManager.getSbisMobileIconTypeface(context)

            clickArea = View(context).apply {
                id = R.id.selection_icon_click_area
                visibility = selectionIconView.visibility
            }

            title = TextLayout.createTextLayoutByStyle(context, titleStyle, textStyleProvider)
                .also {
                    it.updatePadding(
                        top = textToCornerSpacing,
                        end = if (selectionIconView.visibility != View.GONE) {
                            selectionIconMargin
                        } else {
                            viewsHorizontalPadding
                        }
                    )
                }

            subtitle = TextLayout.createTextLayoutByStyle(context, subtitleStyle, textStyleProvider)
                .also {
                    it.updatePadding(
                        top = textVerticalSpacing,
                        bottom = textToCornerSpacing,
                        end = if (selectionIconView.visibility != View.GONE) {
                            selectionIconMargin
                        } else {
                            viewsHorizontalPadding
                        }
                    )
                }
        }

        updatePadding(
            top = imageVerticalMargin,
            bottom = imageVerticalMargin
        )

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
    fun setData(data: PersonSelectorItemModel) {
        setData(data.personData, data.title, data.subtitle, data.meta)
    }

    /** @SelfDocumented */
    fun setData(data: ContractorSelectorItemModel) {
        setData(data.photoData, data.title, data.subtitle, data.meta)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChild(
            personView,
            makeUnspecifiedSpec(),
            makeUnspecifiedSpec()
        )
        if (selectionIconView.visibility != View.GONE) {
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

        val width = MeasureSpec.getSize(widthMeasureSpec)
        val availableWidth = width - paddingStart - paddingEnd
        val textAvailableWidth = availableWidth - personView.safeMeasuredWidth - selectionIconView.safeMeasuredWidth
        title.configure { layoutWidth = textAvailableWidth }
        subtitle.configure { layoutWidth = textAvailableWidth }

        val contentMaxHeight = maxOf(personView.measuredHeight, title.height + subtitle.height)
        val height = maxOf(
            paddingTop + contentMaxHeight + paddingBottom,
            minimumHeight
        )

        setMeasuredDimension(width, height)
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

        title.layout(personView.right, titleTop)
        subtitle.layout(title.left, title.bottom)
    }

    override fun onDraw(canvas: Canvas) {
        title.draw(canvas)
        subtitle.draw(canvas)
    }

    override fun hasOverlappingRendering(): Boolean = false

    private fun setData(photoData: PhotoData, title: String, subtitle: String?, meta: SelectorItemMeta) {
        personView.setData(photoData)
        val isSubtitleChanged = this.subtitle.configure {
            text = subtitle ?: EMPTY
            isVisible = !subtitle.isNullOrBlank()
        }
        val hasSubtitle = this.subtitle.isVisible
        val isTitleChanged = this.title.configure {
            text = title
            highlights = meta.queryRanges.toTextHighlights(highlightColor)
            maxLines =
                if (selectionIconView.isVisible && !hasSubtitle) {
                    MAX_LINES_WITHOUT_SUBTITLE
                } else {
                    MAX_LINES_WITH_SUBTITLE
                }
        }
        val isTitlePaddingChanged = this.title.updatePadding(bottom = if (hasSubtitle) 0 else textToCornerSpacing)
        val isChanged = isTitleChanged || isSubtitleChanged || isTitlePaddingChanged
        if (isChanged) safeRequestLayout()
    }

    /**
     * Настроить для автотестов.
     */
    private fun setupForAutoTests() {
        id = R.id.selection_person_item_view
        accessibilityDelegate = object : AccessibilityDelegate() {
            override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                info?.text = JSONObject(
                    mapOf(
                        context.resources.getResourceEntryName(R.id.selection_title) to title.text,
                        context.resources.getResourceEntryName(R.id.selection_subtitle) to subtitle.text
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
private fun List<IntRange>.toTextHighlights(@ColorInt highlightColor: Int): TextHighlights =
    TextHighlights(map { HighlightSpan(it.first, it.last) }, highlightColor)

/**
 * Поставщик кэшируемых стилей текстовой разметки [PersonSelectorItemView].
 */
private object SelectorPersonItemStylesProvider : CanvasStylesProvider()
