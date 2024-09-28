package ru.tensor.sbis.business.common.ui.bind_adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.content.res.Resources.NotFoundException
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import androidx.annotation.Px
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import ru.tensor.sbis.business.common.R
import ru.tensor.sbis.business.common.ui.utils.IconSpan
import ru.tensor.sbis.business.common.ui.utils.PhotoUrlUtils
import ru.tensor.sbis.common.util.UrlUtils
import ru.tensor.sbis.design.profile.titleview.SbisTitleView
import ru.tensor.sbis.design.profile.titleview.TitleTextView
import ru.tensor.sbis.design.profile_decl.person.InitialsStubData
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.profile_decl.titleview.Default
import ru.tensor.sbis.design.profile_decl.titleview.ListContent
import ru.tensor.sbis.design.profile_decl.titleview.TitleViewItem
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.text_span.span.CustomTypefaceSpan
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.design.toolbar.Toolbar
import ru.tensor.sbis.design.utils.getDataFromAttrOrNull
import ru.tensor.sbis.design.view_ext.SimplifiedTextView
import ru.tensor.sbis.mvvm.BR
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.profile.R as RDesignProfile
import ru.tensor.sbis.design.toolbar.R as RToolbar

/**
 * Data Binding Адаптеры используемые для макетов Тулбара (шапки)
 *
 * @author as.chadov
 *
 * @see BindingAdapter
 */
@BindingAdapter(
    "toolbarVisibility",
    "toolbarCustomViewVisibility",
    "toolbarColor",
    "toolbarColorAttr",
    "toolbarShadow",
    "toolbarAction",
    requireAll = false
)
fun Toolbar.setSbisToolbarAttrs(
    toolbarVisibility: Boolean,
    toolbarCustomViewVisibility: Int,
    toolbarColor: Int,
    @AttrRes toolbarColorAttr: Int,
    toolbarShadow: Int,
    toolbarAction: (() -> Unit)?
) {
    if (toolbarVisibility) {
        customViewContainer.visibility = toolbarCustomViewVisibility
    } else {
        customViewContainer.visibility = GONE
    }
    if (toolbarShadow != View.NO_ID) {
        shadow.visibility = toolbarShadow
    }
    if (toolbarColorAttr != 0) {
        context.getDataFromAttrOrNull(toolbarColorAttr)?.let(::setMainColor)
    } else if (toolbarColor != 0) {
        try {
            toolbar.setBackgroundResource(toolbarColor) // если передано ColorRes
        } catch (e: NotFoundException) {
            setMainColor(toolbarColor) // если передано ColorInt
        }
    }
    toolbarAction?.let { action ->
        setOnClickListener{ action() }
    }
}

/**
 * Адаптер тулбара используемый для кастомизации через [Toolbar.customViewContainer]
 */
@BindingAdapter(
    "customViewLayoutId",
    "customViewData",
    "customViewAction",
    requireAll = false
)
fun Toolbar.setSbisToolbarCustomView(
    customViewLayoutId: Int,
    customViewData: Any?,
    customViewAction: (() -> Unit)?
) {
    if (customViewLayoutId == 0 || (customViewData == null && customViewAction == null)) {
        return
    }
    val binding = DataBindingUtil.inflate<ViewDataBinding>(
        LayoutInflater.from(context),
        customViewLayoutId,
        customViewContainer,
        true
    )
    customViewAction?.let { action -> binding.root.setOnClickListener { action() } }
    if (customViewData != null) {
        binding.setVariable(BR.viewModel, customViewData)
    }
}

@SuppressLint("ClickableViewAccessibility")
@BindingAdapter(
    "leftIconShown",
    "leftIconActive",
    "leftIconText",
    "leftIconColor",
    "leftIconAction",
    requireAll = false
)
fun Toolbar.setSbisToolbarLeftIcon(
    leftIconShown: Boolean,
    leftIconActive: Boolean,
    @StringRes iconText: Int,
    leftIconColor: Int,
    iconAction: (() -> Unit)?
) {
    listOf(leftPanel, leftIcon).forEach {
        it.visibility = if (leftIconShown) VISIBLE else GONE
    }
    leftIcon.adjustSbisToolbarIconText(iconText)
    if (leftIconColor != 0) {
        leftIcon.resolveAndSetColor(leftIconColor)
    }
    leftPanel.isEnabled = leftIconActive
    leftPanel.isClickable = leftIconActive
    if (iconAction == null) {
        leftPanel.setOnClickListener(null)
        leftPanel.setOnTouchListener(null)
    } else {
        leftPanel.setOnClickListener { iconAction() }
        leftPanel.setOnTouchListener { _, event ->
            if (MotionEvent.ACTION_DOWN == event?.actionMasked) {
                parent.requestDisallowInterceptTouchEvent(true)
            }
            false
        }
    }
}

@BindingAdapter(
    "rightIconShown",
    "rightIconActive",
    "rightIconText",
    "rightIconColor",
    "rightIconAction",
    requireAll = false
)
fun Toolbar.setSbisToolbarRightIcon(
    rightIconShown: Boolean,
    rightIconActive: Boolean,
    @StringRes iconText: Int,
    rightIconColor: SbisColor,
    iconAction: (() -> Unit)?
) {
    listOf(rightPanel1, rightIcon1).forEach {
        it.visibility = if (rightIconShown) VISIBLE else GONE
    }

    rightIcon1.adjustSbisToolbarIconText(iconText)
    @Suppress("SENSELESS_COMPARISON")
    if (rightIconColor != null && rightIconColor != SbisColor.NotSpecified) {
        rightIcon1.setTextColor(rightIconColor.getColor(context))
    }
    rightIcon1.isEnabled = rightIconActive
    rightPanel1.isClickable = rightIconActive
    iconAction
        ?.let { rightPanel1.setOnClickListener { it() } }
        ?: rightPanel1.setOnClickListener(null)
}

/**
 * Применение Int цвета или ColorStateList ко всем TextView тулбара
 * Если есть ColorStateList, применяем его, если нет, применяем цвет
 */
@BindingAdapter("textColor")
fun Toolbar.setSbisToolbarIconColor(
    @ColorRes colorResId: Int
) {
    if (colorResId != 0) {

        ContextCompat.getColorStateList(context, colorResId)?.let { colorState: ColorStateList ->
            leftIcon.setTextColor(colorState)
            rightIcon1.setTextColor(colorState)
            rightIcon2.setTextColor(colorState)
            return
        }

        ContextCompat.getColor(context, colorResId).let { color: Int ->
            leftIcon.setTextColor(color)
            rightIcon1.setTextColor(color)
            rightIcon2.setTextColor(color)
        }
    }
}

@BindingAdapter(
    "menuIconShown",
    "menuIconAction",
    "toolbarSecondRightIconShown",
    "toolbarSecondRightIconText",
    "toolbarSecondRightIconAction",
    "toolbarSecondRightIconColor",
    requireAll = false
)
fun Toolbar.setMenu(
    menuIconShown: Boolean,
    menuIconAction: ((anchor: View) -> Unit)?,
    rightIconShown: Boolean,
    @StringRes rightIconText: Int,
    rightIconAction: (() -> Unit)?,
    rightIconColor: Int
) {
    val iconVisibility = if (menuIconShown || rightIconShown) VISIBLE else GONE
    rightIcon2.visibility = iconVisibility
    rightPanel2.visibility = iconVisibility
    if (menuIconShown) {
        rightIcon2.text = resources.getString(RDesign.string.design_mobile_icon_dots_vertical)
        menuIconAction?.let { action ->
            rightPanel2.setOnClickListener { action(rightIcon2) }
        }
    }
    if (rightIconShown) {
        if (rightIconColor != 0) {
            rightIcon2.resolveAndSetColor(rightIconColor)
        } else {
            rightIcon2.setTextColor(rightIcon1.textColors)
        }
        rightIcon2.text = resources.getString(rightIconText)
        rightIconAction?.let { action -> rightPanel2.setOnClickListener { action() } }
    }
}

/**
 * Функция расширение [Toolbar] для конфигурации заголовков напрямую в тулбаре
 */
@BindingAdapter(
    "leftTitle",
    "rightTitle",
    "leftTitleAction",
    "rightTitleAction",
    "rightTitleShown",
    "titleReducible",
    "titleTailIcon",
    requireAll = false
)
fun Toolbar.setSbisTextAttrs(
    leftTitle: CharSequence?,
    rightTitle: CharSequence?,
    leftTitleAction: (() -> Unit)?,
    rightTitleAction: (() -> Unit)?,
    rightTitleShown: Boolean,
    titleReducible: Boolean,
    titleTailIcon: CharSequence?
) {
    if (!leftTitle.isNullOrBlank()) {
        leftText.showText(leftTitle)
        leftText.setWrapContent()
        if (titleReducible) {
            leftText.lineBreakIfNecessary()
        } else if (!titleTailIcon.isNullOrBlank()) {
            leftText.pinTitleTailIcon(titleTailIcon)
        }
    } else {
        leftText.visibility = GONE
    }

    if (!rightTitle.isNullOrBlank()) {
        rightText.showText(rightTitle)
        if (!rightTitleShown) {
            rightText.visibility = GONE
        }
    } else {
        rightText.visibility = GONE
    }

    leftTitleAction?.let { action ->
        leftText.setOnClickListener { action() }
    }
    rightTitleAction?.let { action ->
        rightText.setOnClickListener { action() }
    }
}

/**
 * Функция расширение [SbisTitleView] для конфигурации заголовков во вложенном в тулбар [SbisTitleView]
 */
@Suppress("UselessCallOnNotNull")
@BindingAdapter(
    "title",
    "subtitle",
    "personId",
    "personUuid",
    "disableMerging",
    "textColor",
    "textStyle",
    "titleReducible",
    "titleAction",
    "titleTailIcon",
    requireAll = false
)
fun SbisTitleView.setContentAttrs(
    titleText: CharSequence,
    subtitleText: CharSequence,
    personId: String,
    personUuid: String,
    disableMerging: Boolean,
    @ColorRes textColorResId: Int,
    @StyleRes textStyle: Int,
    titleReducible: Boolean,
    titleAction: (() -> Unit)?,
    titleTailIcon: CharSequence?
) {
    /** скрываем [SbisTitleView] если на нем нечего отображать для предотвращения обработки
     *  кликов за кастомную вью в [Toolbar] */
    val noText = titleText.isNullOrBlank() && subtitleText.isNullOrBlank()
    val noPerson = personUuid.isNullOrBlank() && personId.isNullOrBlank()
    visibility = if (noText && noPerson) {
        GONE
    } else {
        VISIBLE
    }
    if (visibility == GONE) {
        return
    }
    singleLineTitle = true
    titleAction?.let { action ->
        isFocusableInTouchMode = false
        setOnClickListener { action() }
    }
    colorTextView(
        textColorResId,
        RDesignProfile.id.design_profile_title_view_title,
        RDesignProfile.id.design_profile_title_view_subtitle
    )
    styleTextView(
        textStyle,
        RDesignProfile.id.design_profile_title_view_title,
    )
    val showPersonInitials = personUuid.isNotBlank() && (personId.isBlank() || personId == SHOULD_SHOW_INITIALS_MARKER)
    val ellipsizedTitleView = ellipsizeTextView<TitleTextView>(RDesignProfile.id.design_profile_title_view_title)
    val imageUrl = when {
        showPersonInitials      -> null
        personId.isBlank()      -> null
        personId.isNotBlank()   -> PhotoUrlUtils.getPhotoUrlById(personId, TOOLBAR_PHOTO_SIZE)
        personUuid.isNotBlank() -> UrlUtils.getImageUrl(personUuid, UrlUtils.ImageSize.DEFAULT)
        else                    -> null
    }
    when {
        // Если нужно показать инициалы персоны
        showPersonInitials                            -> {
            content = ListContent(
                listOf(
                    TitleViewItem(
                        photoData = PersonData(
                            uuid = null, // указываем null вместо uuid чтобы лишить элемент кликабельности
                            initialsStubData = InitialsStubData.createByFullName(titleText)
                        ),
                        title = titleText,
                        subtitle = subtitleText
                    )
                )
            )
        }
        // Если отсутствует один из двух заголовков, передаем присутствующий тест в заголовок
        titleText.isBlank() || subtitleText.isBlank() -> {
            var text = titleText.takeIf(CharSequence::isNotBlank) ?: subtitleText
            if (!titleTailIcon.isNullOrBlank()) {
                text = mergeTitleAndIcon(text, titleTailIcon)
            }
            content = Default(
                title = text,
                imageUrl = imageUrl.orEmpty()
            )
            if (titleReducible) {
                ellipsizedTitleView.lineBreakIfNecessary()
            }
        }
        // Есть оба заголовка, выводятся в две строки
        disableMerging                                -> {
            content = Default(
                title = titleText,
                subtitle = subtitleText,
                imageUrl = imageUrl.orEmpty()
            )
        }
        /* Иначе соединяем заголовки с последующей проверкой переполнения:
            - если текст, полученный путем соединения titleText и subtitleText,
             влезает в строку, он остается в текущем виде.
            - если текст не влезает, разбивается как при disableMerging. */
        else                                          -> {
            val mergedText = resources.getString(R.string.business_one_line_title, titleText, subtitleText)
            content = Default(
                title = mergedText,
                imageUrl = imageUrl.orEmpty()
            )
            // Используется addOnLayoutChangeListener, а не doOnNextLayout, т.к. nextLayout
            // не гарантированно последний, размеры могут еще измениться
            val listener = object : View.OnLayoutChangeListener {
                override fun onLayoutChange(
                    v: View?,
                    l: Int,
                    t: Int,
                    r: Int,
                    b: Int,
                    oldL: Int,
                    oldT: Int,
                    oldR: Int,
                    oldB: Int
                ) {
                    // Отписка от onLayoutChangeListener с предыдущего состояния тулбара
                    if (ellipsizedTitleView.text != mergedText) {
                        removeOnLayoutChangeListener(this)
                        return
                    }

                    val ellipsisCount = ellipsizedTitleView.getEllipsisCount(0)
                    if (ellipsisCount != 0 && subtitleText.isNotBlank()) {
                        content = Default(
                            title = titleText,
                            subtitle = subtitleText,
                            imageUrl = imageUrl.orEmpty()
                        )
                        ellipsizeTextView<SbisTextView>(RDesignProfile.id.design_profile_title_view_subtitle)
                        removeOnLayoutChangeListener(this)
                    }
                }
            }
            addOnLayoutChangeListener(listener)
        }
    }
}

//region private impl
private fun SbisTitleView.colorTextView(
    @ColorRes textColorResId: Int,
    @IdRes vararg ids: Int
) {
    if (textColorResId == 0) return
    val color = ContextCompat.getColor(context, textColorResId)
    ids.forEach { id ->
        findViewById<View>(id).also {
            if (it is SbisTextView) {
                it.setTextColor(color)
            }
            if (it is TitleTextView) {
                it.setTextColor(color)
            }
        }
    }
}

private fun SbisTitleView.styleTextView(
    @StyleRes styleResId: Int,
    @IdRes ids: Int
) {
    if (styleResId == 0) return
    @Suppress("SimpleRedundantLet")
    findViewById<TitleTextView>(ids)?.let {
        it.setTextAppearance(context, styleResId)
    }
}

private fun <T : View> SbisTitleView.ellipsizeTextView(@IdRes id: Int): T =
    findViewById<T>(id).also { view ->
        if (view is TitleTextView) {
            view.maxLines = 1
            view.isSaveEnabled = false
            view.setEllipsize(TextUtils.TruncateAt.END)
        }
        if (view is SbisTextView) {
            view.isSingleLine = true
            view.isSaveEnabled = false
            view.ellipsize = TextUtils.TruncateAt.END
        }
    }

private fun SbisTextView.adjustSbisToolbarIconText(@StringRes iconText: Int) {
    if (iconText != 0) {
        text = resources.getString(iconText)
    }
}

/** Допустимо передавать color res или attr res */
private fun SbisTextView.resolveAndSetColor(color: Int) {
    val resolvedColor = resolveColor(this, color)
    setTextColor(resolvedColor)
}

/** Допустимо передавать color res или attr res */
private fun SimplifiedTextView.resolveAndSetColor(color: Int) {
    val resolvedColor = resolveColor(this, color)
    setTextColor(resolvedColor)
}

private fun resolveColor(view: View, color: Int) =
    try {
        ContextCompat.getColor(view.context, color)
    } catch (e: NotFoundException) {
        view.context.getDataFromAttrOrNull(color) ?: RDesign.color.palette_color_white1
    }

private fun SimplifiedTextView.adjustSbisToolbarIconText(@StringRes iconText: Int) {
    if (iconText != 0) {
        text = resources.getString(iconText)
    }
}

private fun TitleTextView.lineBreakIfNecessary() {
    isSaveEnabled = false
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            val ellipsisCount = getEllipsisCount(0)
            if (ellipsisCount > 0) {
                setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    resources.getDimensionPixelSize(RToolbar.dimen.toolbar_subtitle_text_size).toFloat()
                )
                maxLines = 2
            }
        }
    })
}

private fun SimplifiedTextView.lineBreakIfNecessary() {
    isSaveEnabled = false
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            val ellipsisCount = getEllipsisCount(0)
            if (ellipsisCount > 0) {
                setTextAppearance(context, R.style.ToolbarTitleLineBreakText)
                maxLines = 2
            }
        }
    })
}

private fun View.mergeTitleAndIcon(
    title: CharSequence,
    icon: CharSequence,
    iconPadding: Int = resources.getDimensionPixelSize(R.dimen.business_margin_very_tiny),
    iconSize: Int = resources.getDimensionPixelSize(RDesign.dimen.size_body1_scaleOff)
): CharSequence {
    val fontSpan =
        CustomTypefaceSpan(ResourcesCompat.getFont(context, RDesign.font.sbis_mobile_icons))
    val iconSpan = IconSpan(iconPadding, iconSize, resources.getColor(RDesign.color.text_color_white))
    return SpannableStringBuilder()
        .append(title)
        .append(icon)
        .apply {
            setSpan(iconSpan, title.length, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(fontSpan, title.length, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
}

private fun SimplifiedTextView.pinTitleTailIcon(icon: CharSequence) {
    isSaveEnabled = false
    val (iconPadding, iconSize) = resources.run {
        getDimensionPixelSize(R.dimen.business_margin_very_tiny) to getDimensionPixelSize(RDesign.dimen.size_body1_scaleOff)
    }
    val mergedText = mergeTitleAndIcon(text?.toString().orEmpty(), icon, iconPadding, iconSize)
    showText(mergedText)
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            val ellipsisCount = getEllipsisCount(0)
            if (ellipsisCount > 0 && text?.endsWith(icon) == true) {
                val availableTitleWidth = ellipsizedWidth.toFloat() - iconSize - iconPadding * 2
                val visiblePart = TextUtils.ellipsize(
                    text,
                    paint,
                    availableTitleWidth,
                    TextUtils.TruncateAt.END
                )
                showText(mergeTitleAndIcon(visiblePart, icon))
            }
        }
    })
}

private fun SimplifiedTextView.showText(title: CharSequence) {
    configureTitleView()
    text = title
}

private fun View.configureTitleView() {
    isEnabled = true
    isClickable = true
    visibility = VISIBLE
    val rightPaddingPx = resources.getDimensionPixelSize(R.dimen.business_margin_large)
    if (rightPaddingPx != paddingRight) {
        setPadding(paddingLeft, paddingTop, rightPaddingPx, paddingBottom)
    }
}

private fun SimplifiedTextView.setWrapContent() {
    if (layoutParams.width != ViewGroup.LayoutParams.WRAP_CONTENT) {
        val lp = layoutParams
        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT
        layoutParams = lp
    }
}

private const val SHOULD_SHOW_INITIALS_MARKER = "Умлч"

@Px
private const val TOOLBAR_PHOTO_SIZE = 100
//endregion private impl