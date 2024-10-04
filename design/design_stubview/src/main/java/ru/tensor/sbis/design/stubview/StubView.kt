package ru.tensor.sbis.design.stubview

import android.content.Context
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat.ID_NULL
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.stubview.actionrange.ActionRangeProvider
import ru.tensor.sbis.design.stubview.databinding.StubViewBinding
import ru.tensor.sbis.design.stubview.layout_strategies.StubViewComposer
import ru.tensor.sbis.design.stubview.layout_strategies.StubViewComposerFactory
import ru.tensor.sbis.design.stubview.utils.StubViewMeasurer
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.extentions.fixLinksForXiaomi
import ru.tensor.sbis.design.utils.getDataFromAttrOrNull
import ru.tensor.sbis.design.utils.getDimen
import ru.tensor.sbis.design.utils.getThemeColorInt
import timber.log.Timber
import ru.tensor.sbis.design.R as RDesign

/**
 * Заглушка для декорирования пустой области. Может содержать иконку, заголовок и комментарий.
 * Применяется для информирования пользователя об отсутствии контента или об ошибке, например:
 * * нет подключения к интернету;
 * * на экране чата нет ни одного сообщения;
 * * результат поиска или фильтрации пуст.
 *
 * [Стандарт](http://axure.tensor.ru/MobileStandart8/заглушки_ver2.html)
 *
 * @author ma.kolpakov
 */
class StubView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.stubViewTheme,
    @StyleRes defStyleRes: Int = R.style.StubViewDefaultTheme,
) : ViewGroup(
    ThemeContextBuilder(context, attrs, defStyleAttr, defStyleRes).build(),
    attrs,
    defStyleAttr,
    defStyleRes
) {

    private companion object {
        const val MODE_BASE = 0
    }

    private val viewBinding = StubViewBinding.inflate(LayoutInflater.from(getContext()), this).apply {
        designStubviewMessage.setExtension(object : SbisTextView.Extension() {
            override fun attach(view: SbisTextView, textLayout: TextLayout) {
                super.attach(view, textLayout)
                textLayout.configure {
                    isVisibleWhenBlank = false
                }
            }
        })
    }


    private val isLandscape = resources.configuration.orientation == ORIENTATION_LANDSCAPE
    private val isTablet = resources.getBoolean(RDesign.bool.is_tablet)

    @ColorInt
    private val detailsLinkColor: Int

    private var displayMode: StubViewMode = StubViewMode.BASE
    private var isMinHeight: Boolean = true

    private var composer: StubViewComposer? = null
    private lateinit var measurer: StubViewMeasurer

    /**
     * Инициализация view
     */
    init {
        // активация обработки нажатий на spannable ссылки https://stackoverflow.com/a/8662457/3926506
        viewBinding.designStubviewDetails.movementMethod = LinkMovementMethod.getInstance()
    }

    /**
     * Инициализация и применение атрибутов
     */
    init {
        val theme = getContext().theme
        val attributes = theme.obtainStyledAttributes(attrs, R.styleable.StubView, defStyleAttr, defStyleRes)
        minimumHeight = with(resources) {
            if (isLandscape)
                getDimensionPixelSize(R.dimen.stub_view_min_height_landscape)
            else
                getDimensionPixelSize(R.dimen.stub_view_min_height_portrait)
        }
        when (attributes.getInteger(R.styleable.StubView_StubView_commentTextSize, 1)) {
            1 -> isLargeCommentTextSize(false)
            2 -> isLargeCommentTextSize(true)
            else -> throw IllegalStateException("Illegal StubView commentTextSize")
        }
        val displayModeInt = attributes.getInteger(R.styleable.StubView_StubView_mode, MODE_BASE)
        displayMode = StubViewMode.fromId(displayModeInt)
        // TODO: 24.08.2021 https://online.sbis.ru/opendoc.html?guid=c81aa35b-4324-465d-9cfc-736606c0ed8d
        isMinHeight = displayMode != StubViewMode.DENS

        val backgroundColor = attributes.getColor(
            R.styleable.StubView_StubView_backgroundColor,
            ContextCompat.getColor(getContext(), android.R.color.transparent)
        )
        val messageColor = attributes.getColor(
            R.styleable.StubView_StubView_messageColor,
            context.getThemeColorInt(RDesign.attr.placeholderTextColorList)
        )
        val detailsColor = attributes.getColor(
            R.styleable.StubView_StubView_detailsColor,
            context.getThemeColorInt(RDesign.attr.placeholderTextColorList)
        )
        detailsLinkColor = attributes.getColor(
            R.styleable.StubView_StubView_detailsLinkColor,
            context.getThemeColorInt(RDesign.attr.linkTextColor)
        )

        setBackgroundColor(backgroundColor)
        viewBinding.designStubviewMessage.setTextColor(messageColor)
        viewBinding.designStubviewDetails.setTextColor(detailsColor)
        viewBinding.designStubviewDetails.fixLinksForXiaomi()

        attributes.recycle()

        if (isInEditMode) {
            updateComposer(true, viewBinding.designStubviewImageIcon)
        }
    }

    /**
     * Размер текста комментария заглушки
     */
    fun isLargeCommentTextSize(isLarge: Boolean) {
        val size =
            if (isLarge) context.getDimen(RDesign.attr.fontSize_2xl_scaleOn)
            else context.getDimen(RDesign.attr.fontSize_m_scaleOn)
        viewBinding.designStubviewDetails.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            size
        )
    }

    override fun setLayoutParams(params: LayoutParams?) {
        super.setLayoutParams(params)
        measurer = StubViewMeasurer(
            layoutParams,
            resources.getDimensionPixelSize(R.dimen.stub_view_min_height_portrait),
        )
    }

    /**
     * Установка режима отображения
     *
     * @param mode режим отображения заглушки
     * @param minHeight сжимать высоту до минимальной, когда height=wrap_content
     */
    fun setMode(mode: StubViewMode, minHeight: Boolean = true) {
        if (displayMode == mode && this.isMinHeight == minHeight) {
            return
        }

        displayMode = mode
        // TODO: 17.08.2021 Вынести логику минимизации высоты в компоузеры https://online.sbis.ru/opendoc.html?guid=c81aa35b-4324-465d-9cfc-736606c0ed8d
        this.isMinHeight = mode != StubViewMode.DENS && minHeight

        val icon: View? =
            when {
                viewBinding.designStubviewImageIcon.drawable != null -> viewBinding.designStubviewImageIcon
                viewBinding.designStubviewViewIcon.childCount != 0 -> viewBinding.designStubviewViewIcon
                else -> null
            }
        updateComposer(viewBinding.designStubviewImageIcon.drawable != null, icon)

        requestLayout()
    }

    /**
     * Установка контента для отображения
     *
     * @param content контент для отображения в заглушке
     */
    fun setContent(content: StubViewContent) {
        when (content) {
            is ResourceImageStubContent ->
                setContent(content.icon, content.getMessage(), content.getDetails(), content.actions)

            is ResourceAttributeStubContent ->
                setContentByAttribute(content.icon, content.getMessage(), content.getDetails(), content.actions)

            is DrawableImageStubContent ->
                setContent(content.icon, content.getMessage(), content.getDetails(), content.actions)

            is ViewStubContent ->
                setContentByView(content.icon, content.getMessage(), content.getDetails(), content.actions)

            is ImageStubContent ->
                setContent(content.imageType, content.getMessage(), content.getDetails(), content.actions)

            is IconStubContent -> setContent(
                content.icon,
                content.iconColor,
                content.iconSize,
                content.getMessage(),
                content.getDetails(),
                content.actions
            )
        }
        requestLayout()
    }

    /**
     * Установка фабрики контента
     *
     * @param factory фабрика для отложенного отображения контента
     */
    fun setContentFactory(factory: (Context) -> StubViewContent) = setContent(factory(context))

    /**
     * Установка стандартного случая отображения заглушки
     *
     * @param case стандартный случай
     */
    fun setCase(case: StubViewCase) = setContent(case.getContent())

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val (containerWidth, containerHeight) = with(measurer) {
            setSizes(widthMeasureSpec, heightMeasureSpec, paddingTop, paddingBottom)
            measure(composer, isMinHeight || (isLandscape && !isTablet))
        }

        setMeasuredDimension(containerWidth, containerHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        composer?.layout(left, paddingTop, right, bottom)
    }

    private fun updateComposer(isDrawable: Boolean, icon: View?) {

        composer = StubViewComposerFactory.createStubViewComposer(
            isLandscape = isLandscape,
            isTablet = isTablet,
            isDrawable = isDrawable,
            displayMode = displayMode,
            icon = icon,
            message = viewBinding.designStubviewMessage,
            details = viewBinding.designStubviewDetails,
            context = context,
        )
    }

    private fun setContentByAttribute(
        @AttrRes iconAttr: Int,
        messageText: CharSequence?,
        detailsText: String?,
        actions: Map<ActionRangeProvider, () -> Unit>,
    ) {
        val iconRes = context.getDataFromAttrOrNull(iconAttr, false)
        val iconDrawable: Drawable? =
            if (iconRes != null && iconRes != ID_NULL) {
                ContextCompat.getDrawable(context, iconRes)
            } else {
                null
            }
        setContent(iconDrawable, messageText, detailsText, actions)
    }

    private fun setContent(
        @DrawableRes iconRes: Int,
        messageText: CharSequence?,
        detailsText: String?,
        actions: Map<ActionRangeProvider, () -> Unit>,
    ) {
        val iconDrawable: Drawable? =
            if (iconRes != ID_NULL) {
                ContextCompat.getDrawable(context, iconRes)
            } else {
                null
            }
        setContent(iconDrawable, messageText, detailsText, actions)
    }

    private fun setContent(
        imageType: StubViewImageType,
        messageText: CharSequence?,
        detailsText: String?,
        actions: Map<ActionRangeProvider, () -> Unit>,
    ) = imageType.getDrawable(
        context = context,
        onSuccess = { drawable -> setContent(drawable, messageText, detailsText, actions) },
        onFailure = { setContent(null, messageText, detailsText, actions) }
    )

    private fun setContent(
        icon: IIcon,
        @ColorRes color: Int,
        @DimenRes size: Int,
        messageText: CharSequence?,
        detailsText: String?,
        actions: Map<ActionRangeProvider, () -> Unit>,
    ) {
        val iconDrawable = IconicsDrawable(context, icon).apply {
            colorRes(color)
            sizeRes(size)
        }
        setContent(iconDrawable, messageText, detailsText, actions)
    }

    private fun setContent(
        iconDrawable: Drawable?,
        messageText: CharSequence?,
        detailsText: String?,
        actions: Map<ActionRangeProvider, () -> Unit>,
    ) {
        updateComposer(isDrawable = true, icon = viewBinding.designStubviewImageIcon.takeIf { iconDrawable != null })

        viewBinding.designStubviewViewIcon.removeChildren()
        viewBinding.designStubviewViewIcon.visibility = GONE

        viewBinding.designStubviewImageIcon.setImageDrawable(iconDrawable)

        viewBinding.designStubviewMessage.text = messageText
        viewBinding.designStubviewDetails.text = getDetailsText(detailsText, actions)
    }

    private fun setContentByView(
        customIcon: View,
        messageText: CharSequence?,
        detailsText: String?,
        actions: Map<ActionRangeProvider, () -> Unit>,
    ) {
        updateComposer(isDrawable = false, icon = viewBinding.designStubviewViewIcon)

        viewBinding.designStubviewImageIcon.setImageDrawable(null)
        viewBinding.designStubviewImageIcon.visibility = GONE

        viewBinding.designStubviewViewIcon.removeChildren()
        viewBinding.designStubviewViewIcon.addView(customIcon)

        viewBinding.designStubviewMessage.text = messageText
        viewBinding.designStubviewDetails.text = getDetailsText(detailsText, actions)
    }

    private fun getDetailsText(detailsText: String?, actions: Map<ActionRangeProvider, () -> Unit>): CharSequence? {
        if (actions.isEmpty()) {
            return detailsText
        }

        if (detailsText.isNullOrEmpty()) {
            Timber.e("Unable to apply click action to empty text")
            return detailsText
        }

        if (detailsText.isNullOrEmpty() || actions.isEmpty()) {
            return detailsText
        }

        val spannableDetails = SpannableString(detailsText)

        actions.forEach { (rangeProvider, action) ->
            val actionTextRange = rangeProvider.getRange(context, detailsText)
            if (!actionTextRange.isEmpty()) {
                spannableDetails.setClickableSpan(actionTextRange, action)
            }
        }
        return spannableDetails
    }

    private fun SpannableString.setClickableSpan(spanRange: IntRange, action: () -> Unit) {
        val span = object : ClickableSpan() {
            override fun onClick(widget: View) = action()

            override fun updateDrawState(paint: TextPaint) {
                super.updateDrawState(paint)
                paint.isUnderlineText = false
                paint.color = detailsLinkColor
            }
        }

        setSpan(span, spanRange.first, spanRange.last, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    @Suppress("unused")
    private fun ViewGroup.removeChildren() {
        if (childCount > 0) {
            removeAllViews()
        }
    }

    private fun StubViewContent.getMessage(): CharSequence? =
        if (messageRes != ID_NULL) {
            resources.getText(messageRes)
        } else {
            message
        }

    private fun StubViewContent.getDetails(): String? =
        if (detailsRes != ID_NULL) {
            resources.getString(detailsRes)
        } else {
            details
        }
}
