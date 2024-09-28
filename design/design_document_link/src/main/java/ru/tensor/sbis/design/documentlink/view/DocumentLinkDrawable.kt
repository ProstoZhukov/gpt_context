package ru.tensor.sbis.design.documentlink.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import androidx.annotation.Dimension
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.withSave
import androidx.core.graphics.withTranslation
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.utils.StaticLayoutConfigurator
import ru.tensor.sbis.design.custom_view_tools.utils.getTextWidth
import ru.tensor.sbis.design.documentlink.R
import ru.tensor.sbis.design.documentlink.models.DocumentLinkModel
import ru.tensor.sbis.design.documentlink.utils.DocumentLinkStyle
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import kotlin.math.roundToInt
import ru.tensor.sbis.design.R as RDesing

/**
 * [Drawable] реализация документа-основания для использования в "плоских" view. Если компонент нужен как
 * самостоятельная view, нужно использовать [DocumentLinkView]
 *
 * @author da.zolotarev
 */
class DocumentLinkDrawable @JvmOverloads constructor(
    val context: Context,
    attrs: AttributeSet? = null
) : Drawable() {

    @StringRes
    private val arrowIconRes = RDesing.string.design_mobile_icon_arrow_narrow_right

    private val iconArrowTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = TypefaceManager.getSbisMobileIconTypeface(context)
    }
    private val iconDocTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = TypefaceManager.getSbisMobileIconTypeface(context)
    }
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val bottomBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = context.resources.getDimension(R.dimen.design_document_link_bottom_border_height)
    }
    private val titleTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val commentTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    @Dimension(unit = Dimension.PX)
    private val height = context.resources.getDimension(R.dimen.design_document_link_height)

    @Dimension(unit = Dimension.PX)
    private var availableSpace = 0f

    private var docIconWidth = 0
    private var arrowIconWidth = 0

    private var headerTextWidth = 0
    private var commentTextWidth = 0

    @Dimension(unit = Dimension.PX)
    private val horizontalMarginWithIcon =
        context.resources.getDimension(R.dimen.design_document_link_icon_horizontal_margin)

    @Dimension(unit = Dimension.PX)
    private val horizontalTextMargin =
        context.resources.getDimension(R.dimen.design_document_link_horizontal_text_margin)

    @Dimension(unit = Dimension.PX)
    private val horizontalMarginWithoutIcon =
        context.resources.getDimension(R.dimen.design_document_link_horizontal_margin)

    private var docIconlayout = StaticLayoutConfigurator.createStaticLayout("", iconDocTextPaint)
    private var arrowIconLayout =
        StaticLayoutConfigurator.createStaticLayout("", iconArrowTextPaint)
    private var commentLayout = StaticLayoutConfigurator.createStaticLayout("", commentTextPaint)
    private var headerLayout = StaticLayoutConfigurator.createStaticLayout("", commentTextPaint)

    /**
     * Местонахождение заголовка (для автотестов)
     */
    internal var headerBounds: RectF = RectF(0f, 0f, 0f, 0f)

    /**
     * Местонахождение комментария (для автотестов)
     */
    internal var commentBounds: RectF = RectF(0f, 0f, 0f, 0f)

    /**
     * Иконка стрелочки (будет появляться  если у DocumentLinKView будет выставлен кликлистенер
     */
    internal var withArrowIcon: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                measureIconsAndText(documentLinkModel)
                measureAvailableSpace()
                createTextLayouts()
            }
        }

    /**
     * Стиль документа-основания
     */
    var style: DocumentLinkStyle = DocumentLinkStyle.DocumentLinkDefaultStyle
        set(value) {
            if (field != value) {
                field = value
                val style = ThemeContextBuilder(context, field.styleAttr, field.styleRes).buildThemeRes()
                context.withStyledAttributes(style, R.styleable.DocumentLink) { loadStyle(this) }
            }
        }

    init {
        context.withStyledAttributes(attrs, R.styleable.DocumentLink, style.styleAttr, style.styleRes) {
            loadStyle(this)
        }
        arrowIconLayout =
            StaticLayoutConfigurator.createStaticLayout(context.getString(arrowIconRes), iconArrowTextPaint)
    }

    /**
     * Модель данных счетчика
     * @see DocumentLinkModel
     */
    var documentLinkModel: DocumentLinkModel = DocumentLinkModel("Some header", "Some text")
        set(value) {
            if (field != value) {
                field = value
                measureIconsAndText(value)
                measureAvailableSpace()
                createTextLayouts()
            }
        }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)
        measureAvailableSpace()
        createTextLayouts()
    }

    override fun draw(canvas: Canvas) {
        drawBackground(canvas)
        drawDocIcon(canvas)
        if (withArrowIcon) drawArrowIcon(canvas)
        drawText(canvas)
    }

    private fun measureAvailableSpace() {
        availableSpace = if (documentLinkModel.icon != null) {
            bounds.right - (horizontalMarginWithIcon * 4) - docIconWidth - arrowIconWidth
        } else {
            bounds.right - (horizontalMarginWithIcon * 2) - arrowIconWidth - horizontalMarginWithoutIcon
        }
    }

    private fun createTextLayouts() {
        createHeaderLayout()
        createCommentLayout()
    }

    private fun createHeaderLayout() {
        headerLayout = StaticLayoutConfigurator.createStaticLayout(documentLinkModel.title, titleTextPaint) {
            maxLines = if (documentLinkModel.comment.isEmpty() && !documentLinkModel.isSingleLineMode) 2 else 1
            width = minOf(headerTextWidth, availableSpace.roundToInt())
        }
    }

    private fun createCommentLayout() {
        commentLayout = StaticLayoutConfigurator.createStaticLayout(documentLinkModel.comment, commentTextPaint) {
            maxLines = if (documentLinkModel.title.isEmpty() && !documentLinkModel.isSingleLineMode) 2 else 1
            width = if (documentLinkModel.isSingleLineMode && documentLinkModel.title.isNotEmpty()) minOf(
                commentTextWidth,
                (availableSpace - headerLayout.width).roundToInt()
            ) else minOf(commentTextWidth, availableSpace.roundToInt())
        }
    }

    private fun loadStyle(arr: TypedArray) {
        arr.apply {
            titleTextPaint.color = getColor(
                R.styleable.DocumentLink_DocumentLink_titleColor,
                ContextCompat.getColor(context, RDesing.color.palette_color_blue11)
            )
            titleTextPaint.textSize = getDimension(
                R.styleable.DocumentLink_DocumentLink_titleTextSize,
                context.resources.getDimension(R.dimen.design_document_link_header_text_size)
            )

            commentTextPaint.color = getColor(
                R.styleable.DocumentLink_DocumentLink_commentColor,
                ContextCompat.getColor(context, RDesing.color.palette_color_blue12)
            )
            commentTextPaint.textSize = getDimension(
                R.styleable.DocumentLink_DocumentLink_commentTextSize,
                context.resources.getDimension(R.dimen.design_document_link_comment_text_size)
            )

            iconArrowTextPaint.color = getColor(
                R.styleable.DocumentLink_DocumentLink_iconColor,
                ContextCompat.getColor(context, RDesing.color.palette_color_blue12)
            )
            iconArrowTextPaint.textSize = getDimension(
                R.styleable.DocumentLink_DocumentLink_arrowIconSize,
                context.resources.getDimension(R.dimen.design_document_link_icon_arrow_text_size)
            )

            iconDocTextPaint.color = getColor(
                R.styleable.DocumentLink_DocumentLink_iconColor,
                ContextCompat.getColor(context, RDesing.color.palette_color_blue12)
            )
            iconDocTextPaint.textSize = getDimension(
                R.styleable.DocumentLink_DocumentLink_iconSize,
                context.resources.getDimension(R.dimen.design_document_link_icon_doc_text_size)
            )

            bottomBorderPaint.color = getColor(
                R.styleable.DocumentLink_DocumentLink_bottomBorderColor,
                ContextCompat.getColor(context, RDesing.color.palette_alpha_color_black1)
            )

            backgroundPaint.color = getColor(
                R.styleable.DocumentLink_DocumentLink_backgroundColor,
                ContextCompat.getColor(context, RDesing.color.palette_color_white10)
            )
        }
    }

    private fun drawText(canvas: Canvas) {
        when {
            documentLinkModel.title.isEmpty() -> drawCommentText(canvas)
            documentLinkModel.comment.isEmpty() -> drawHeaderText(canvas)
            else -> drawAllText(canvas)
        }
    }

    private fun measureIconsAndText(model: DocumentLinkModel) {
        arrowIconWidth =
            if (withArrowIcon) iconArrowTextPaint.getTextWidth(context.getString(arrowIconRes)) else 0
        docIconWidth = iconDocTextPaint.getTextWidth(model.icon?.character.toString())
        headerTextWidth = titleTextPaint.getTextWidth(model.title)
        commentTextWidth = commentTextPaint.getTextWidth(model.comment)
        docIconlayout =
            StaticLayoutConfigurator.createStaticLayout(model.icon?.character.toString(), iconDocTextPaint)
    }

    private fun drawCommentText(canvas: Canvas) {
        val textStartMargin =
            if (documentLinkModel.icon != null) horizontalMarginWithIcon * 2 + docIconWidth
            else horizontalMarginWithoutIcon
        commentBounds = RectF(
            textStartMargin,
            height / 2.0f - commentLayout.height / 2,
            textStartMargin + commentLayout.width,
            height / 2.0f + commentLayout.height / 2
        )
        headerBounds = RectF(0f, 0f, 0f, 0f)
        canvas.withSave {
            translate(textStartMargin, height / 2.0f - commentLayout.height / 2)
            commentLayout.draw(canvas)
        }
    }

    private fun drawHeaderText(canvas: Canvas) {
        val textStartMargin =
            if (documentLinkModel.icon != null) horizontalMarginWithIcon * 2 + docIconWidth
            else horizontalMarginWithoutIcon
        headerBounds = RectF(
            textStartMargin,
            height / 2.0f - headerLayout.height / 2,
            textStartMargin + headerLayout.width,
            height / 2.0f + headerLayout.height / 2
        )
        commentBounds = RectF(0f, 0f, 0f, 0f)
        canvas.withTranslation(textStartMargin, height / 2.0f - headerLayout.height / 2) {
            headerLayout.draw(canvas)
        }
    }

    private fun drawAllText(canvas: Canvas) {
        val textMargin =
            if (documentLinkModel.icon != null) horizontalMarginWithIcon * 2 + docIconWidth
            else horizontalMarginWithoutIcon
        when {
            (headerLayout.width + commentLayout.width + horizontalMarginWithIcon > availableSpace) &&
                !documentLinkModel.isSingleLineMode -> {
                headerBounds = RectF(
                    textMargin,
                    height / 2.0f - headerLayout.height,
                    headerLayout.width + textMargin,
                    height / 2.0f
                )
                commentBounds = RectF(
                    textMargin,
                    height / 2.0f,
                    commentLayout.width + textMargin,
                    commentLayout.height + height / 2.0f
                )

                canvas.withTranslation(textMargin, height / 2.0f - headerLayout.height) { headerLayout.draw(canvas) }
                canvas.withTranslation(textMargin, height / 2.0f) { commentLayout.draw(canvas) }
            }

            (headerLayout.width + commentLayout.width + horizontalMarginWithIcon <= availableSpace) ||
                documentLinkModel.isSingleLineMode -> {
                headerBounds = RectF(
                    textMargin,
                    height / 2.0f - headerLayout.height / 2,
                    headerLayout.width + textMargin,
                    height / 2.0f + headerLayout.height / 2
                )
                commentBounds = RectF(
                    textMargin + headerLayout.width + horizontalTextMargin,
                    height / 2.0f - headerLayout.height / 2,
                    textMargin + headerLayout.width + horizontalTextMargin + commentLayout.width,
                    height / 2.0f + headerLayout.height / 2
                )

                canvas.withTranslation(
                    textMargin, height / 2.0f - headerLayout.height / 2
                ) { headerLayout.draw(canvas) }
                canvas.withTranslation(
                    textMargin + headerLayout.width + horizontalTextMargin, height / 2.0f - headerLayout.height / 2
                ) { commentLayout.draw(canvas) }
            }
        }
    }

    private fun drawDocIcon(canvas: Canvas) {
        if (documentLinkModel.icon == null) return
        canvas.withTranslation(
            bounds.left.toFloat() + horizontalMarginWithIcon,
            (height / 2.0f - docIconlayout.height / 2.0f)
        ) { docIconlayout.draw(canvas) }
    }

    private fun drawArrowIcon(canvas: Canvas) {
        canvas.withTranslation(
            bounds.right.toFloat() - horizontalMarginWithIcon - arrowIconLayout.width,
            (height / 2.0f - arrowIconLayout.height / 2.0f)
        ) { arrowIconLayout.draw(canvas) }
    }

    private fun drawBackground(canvas: Canvas) {
        canvas.drawRect(0f, 0f, bounds.right.toFloat(), height, backgroundPaint)
        canvas.drawLine(
            bounds.left.toFloat(),
            height,
            bounds.right.toFloat(),
            height,
            bottomBorderPaint
        )
    }

    override fun setAlpha(alpha: Int) = Unit
    override fun setColorFilter(colorFilter: ColorFilter?) = Unit

    @Deprecated("Deprecated in Java", ReplaceWith("PixelFormat.TRANSLUCENT", "android.graphics.PixelFormat"))
    override fun getOpacity() = PixelFormat.TRANSLUCENT
    override fun getIntrinsicWidth(): Int = bounds.width()
    override fun getIntrinsicHeight(): Int = height.roundToInt()
}