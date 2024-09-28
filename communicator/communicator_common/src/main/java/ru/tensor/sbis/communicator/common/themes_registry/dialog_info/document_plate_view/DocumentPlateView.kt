package ru.tensor.sbis.communicator.common.themes_registry.dialog_info.document_plate_view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import ru.tensor.sbis.communicator.common.R
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.styles.CanvasStylesProvider
import ru.tensor.sbis.design.custom_view_tools.styles.StyleParams
import ru.tensor.sbis.design.custom_view_tools.styles.StyleParamsProvider
import ru.tensor.sbis.design.custom_view_tools.utils.TextLayoutAutoTestsHelper
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import ru.tensor.sbis.design.utils.getThemeColor
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign
import ru.tensor.sbis.design.R as RDesign

/**
 * Табличка для отображения онформации о документе
 * Содержит разметки для отображения названия документа и текста документа в одну или две строки,
 * иконку для отображения типа документа
  *
 * @author da.zhukov
 */
class DocumentPlateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes styleRes: Int = R.style.CommunicatorDocumentPlateViewStyle,
    styleParamsProvider: StyleParamsProvider<StyleParams.TextStyle>? = null
) : ViewGroup(context, attrs, defStyleAttr, styleRes) {

    init {
        id = R.id.communicator_document_plate_view_id
    }

    constructor(context: Context, canvasStylesProvider: CanvasStylesProvider) :
            this(context, styleParamsProvider = canvasStylesProvider.textStyleProvider)

    /**
     * Список дочерних разметок, которые необходимо разместить и отобразить.
     */
    private val children: MutableSet<TextLayout> = mutableSetOf()

    /**
     * Разметка информации о документе в одну строку
     */
    private val soloDocumentTitle = TextLayout.createTextLayoutByStyle(
        context,
        R.style.CommunicatorDocumentPlateSoloTitle,
        styleParamsProvider
    ).apply { id = R.id.communicator_document_plate_view_solo_title_id }

    /**
     * Разметка названия документа
     */
    private val documentTitle = TextLayout.createTextLayoutByStyle(
        context,
        R.style.CommunicatorDocumentPlateTitle,
        styleParamsProvider
    ).apply { id = R.id.communicator_document_plate_view_title_id }

    /**
     * Разметка текста документа
     */
    private val documentSubtitle = TextLayout.createTextLayoutByStyle(
        context,
        R.style.CommunicatorDocumentPlateTextStyle,
        styleParamsProvider
    ).apply { id = R.id.communicator_document_plate_view_subtitle_id }

    /**
     * Разметка иконки документа
     */
    private val leftIcon = TextLayout.createTextLayoutByStyle(
        context,
        R.style.CommunicatorDocumentPlateLeftIconStyle,
        styleParamsProvider
    ).apply { id = R.id.communicator_document_plate_view_left_icon_id }

    /**
     * Разметка правой иконки
     */
    private val rightIcon = TextLayout.createTextLayoutByStyle(
        context,
        R.style.CommunicatorDocumentPlateRightIconStyle,
        styleParamsProvider
    ).apply { id = R.id.communicator_document_plate_view_right_icon_id }

    private val bottomDivider = View(context).apply {
        setBackgroundColor(context.getColorFromAttr(RDesign.attr.borderColor))
    }

    private val dividerHeight = context.resources.dp(1)

    /**
     * Данные для отображения таблички о документе
     */
    private var data: DocumentPlateViewModel? = null

    private var iconLeftAndRightSpacing: Int = 0

    /**
     * Высота таблички о документе
     */
    private var currentHeight: Int = 0

    init {
        addView(bottomDivider)
        children.addAll(
            listOf(
                soloDocumentTitle,
                documentTitle,
                documentSubtitle,
                leftIcon,
                rightIcon
            )
        )
        accessibilityDelegate = TextLayoutAutoTestsHelper(this, children)
        setWillNotDraw(false)

        setBackgroundColor(context.getColorFromAttr(RDesign.attr.contrastBackgroundColor))
        iconLeftAndRightSpacing = resources.getDimensionPixelSize(RCommunicatorDesign.dimen.communicator_document_name_icon_padding_end_and_start)
        currentHeight = resources.getDimensionPixelSize(RCommunicatorDesign.dimen.communicator_document_name_place_size)

        if (isInEditMode) {
            val longString = "Это просто длинная строка для отображения названия документа"
            bindData(
                DocumentPlateViewModel(
                    "",
                    longString,
                    longString,
                    RDesign.string.design_mobile_icon_document
                )
            )
        }
    }

    /**
     * Установать данные для отображения таблички о документе
     */
    fun bindData(data: DocumentPlateViewModel) {
        this.data = data
        if (!isLayoutRequested && measuredWidth != 0) {
            configureLayout(measuredWidth)
        }
        if (!isLayoutRequested) {
            internalLayout()
        }
        invalidate()
    }

    /**
     * Настроить дочерние разметки по заданной ширине [sizeWidth].
     */
    private fun configureLayout(sizeWidth: Int) {
        val model = data ?: return
        leftIcon.configure {
            text = context.getString(model.iconRes)
        }

        val contentAvailableWidth = sizeWidth - paddingLeft - paddingRight - leftIcon.width - rightIcon.width

        soloDocumentTitle.configure {
            text = model.soloDocumentTitle
            layoutWidth = contentAvailableWidth
            isVisible = model.documentTitle.isEmpty()
        }
        documentTitle.configure {
            text = model.documentTitle
            layoutWidth = contentAvailableWidth
        }
        documentSubtitle.configure {
            text = model.documentSubTitle
            layoutWidth = contentAvailableWidth
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        configureLayout(width)

        setMeasuredDimension(width, currentHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        internalLayout()
    }

    /**
     * Разместить дочернюю разметку
     */
    private fun internalLayout() {
        leftIcon.layout(
            0,
            (currentHeight - leftIcon.height) / 2
        )
        rightIcon.layout(
            measuredWidth - rightIcon.width,
            (currentHeight - rightIcon.height) / 2
        )

        soloDocumentTitle.layout(
            leftIcon.right,
            (currentHeight - soloDocumentTitle.height) / 2
        )
        val sumHeight = documentTitle.height + documentSubtitle.height
        documentTitle.layout(
            leftIcon.right,
            (currentHeight - sumHeight) / 2
        )
        documentSubtitle.layout(leftIcon.right, documentTitle.bottom)
        bottomDivider.layout(0, measuredHeight - dividerHeight, measuredWidth, measuredHeight)
    }

    /**@SelfDocumented*/
    override fun hasOverlappingRendering() = false

    override fun onDraw(canvas: Canvas) {
        children.forEach { it.draw(canvas) }
    }
}