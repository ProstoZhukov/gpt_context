package ru.tensor.sbis.business.common.ui.bind_adapter

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.TextAppearanceSpan
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.IntegerRes
import androidx.annotation.StyleRes
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat.*
import androidx.core.widget.TextViewCompat
import androidx.databinding.BaseObservable
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.tensor.sbis.base_components.adapter.vmadapter.ViewModelAdapter
import ru.tensor.sbis.business.common.R
import ru.tensor.sbis.business.common.ui.utils.PhotoUrlUtils
import ru.tensor.sbis.business.common.ui.utils.isCharacterAfterPointDigit
import ru.tensor.sbis.business.common.ui.viewmodel.BreadCrumbsVm
import ru.tensor.sbis.business.common.ui.viewmodel.TextWithHighlights
import ru.tensor.sbis.common.util.UrlUtils
import ru.tensor.sbis.design.breadcrumbs.breadcrumbs.BreadCrumbsView
import ru.tensor.sbis.design.breadcrumbs.breadcrumbs.model.BreadCrumb
import ru.tensor.sbis.design.navigation.util.scrollToTop
import ru.tensor.sbis.design.profile.person.PersonView
import ru.tensor.sbis.design.profile_decl.person.InitialsStubData
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.progress.SbisPullToRefresh
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.text_span.span.BreadCrumbsSpan
import ru.tensor.sbis.design.text_span.span.CustomTypefaceSpan
import ru.tensor.sbis.design.text_span.span.util.BreadCrumbsAttributes
import ru.tensor.sbis.design.utils.extentions.getActivity
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.utils.getThemeColor
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.business.theme.R as RBusinessTheme
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.profile.R as RDesignProfile

/**
 * Адаптеры используемые с Data Binding
 *
 * @author as.chadov
 *
 * @see BindingAdapter
 */

/*region View*/
/** @SelfDocumented */
@BindingAdapter("clickable")
fun View.setClickableState(clickable: Boolean) {
    isClickable = clickable
    isEnabled = clickable
}

/** @SelfDocumented */
@BindingAdapter("onClickAndClickable")
fun View.setClickAndClickable(action: (() -> Unit)?) {
    isClickable = action != null
    isEnabled = action != null
    setOnClickListener { action?.invoke() }
}

/** @SelfDocumented */
@BindingAdapter("backgroundColorRes")
fun View.setBackgroundColorRes(@ColorRes backgroundColorRes: Int) {
    if (backgroundColorRes != 0) {
        setBackgroundColor(ContextCompat.getColor(context, backgroundColorRes))
    }
}

/**
 * Установка цвета фона из текущей темы по атрибуту [attrResId], если же такой не найден то [defaultResId]
 * @param attrResId атрибут с id цвета из темы
 * @param defaultResId id цвета по-умолчанию
 */
@BindingAdapter(value = ["backgroundAttrRes", "backgroundDefaultRes"], requireAll = true)
fun View.setAttrBackgroundColorRes(
    @AttrRes attrResId: Int,
    @ColorRes defaultResId: Int
) {
    var colorRes = context.getThemeColor(attrResId)
    if (colorRes == 0) {
        val newColor = context.getColorFromAttr(attrResId)
        if (newColor != 0) {
            setBackgroundColor(newColor)
            return
        }
        colorRes = defaultResId
    }
    setBackgroundColorRes(colorRes)
}

/** @SelfDocumented */
@BindingAdapter("heightWrapOrMatchConstraint")
fun View.setHeightWrapContentOrMatchConstraint(isHeightWrapContent: Boolean) {
    val parent = parent as? ConstraintLayout
        ?: return
    ConstraintSet().run {
        clone(parent)
        constrainHeight(
            id,
            if (isHeightWrapContent) ConstraintSet.WRAP_CONTENT else ConstraintSet.MATCH_CONSTRAINT
        )
        applyTo(parent)
    }
}

/** @SelfDocumented */
@BindingAdapter("layoutHeight")
fun View.setLayoutHeight(@DimenRes heightRes: Int) {
    if (heightRes == 0) return
    val newHeight = resources.getDimensionPixelSize(heightRes)
    val viewLayoutParams = layoutParams
    viewLayoutParams.height = newHeight
    layoutParams = viewLayoutParams
}

/** @SelfDocumented */
@BindingAdapter("layoutHeightAttr")
fun View.setLayoutHeightAttr(@AttrRes heightRes: Int) {
    if (heightRes == 0) return
    val newHeight = context.getDimenPx(heightRes)
    val viewLayoutParams = layoutParams
    viewLayoutParams.height = newHeight
    layoutParams = viewLayoutParams
}

/** @SelfDocumented */
@BindingAdapter("paddingBottomRes")
fun View.setPaddingBottomRes(@DimenRes paddingBottomRes: Int) {
    if (paddingBottomRes == 0) return
    val newPaddingBottom = resources.getDimensionPixelSize(paddingBottomRes)
    setPadding(paddingLeft, paddingTop, paddingRight, newPaddingBottom)
}

/** @SelfDocumented */
@BindingAdapter("marginLeftRes")
fun View.marginLeftRes(@DimenRes marginLeftResId: Int) {
    if (marginLeftResId == 0) return
    val marginLeft = context.resources.getDimensionPixelSize(marginLeftResId)
    val params = layoutParams as ViewGroup.MarginLayoutParams
    params.leftMargin = marginLeft
    layoutParams = params
}

/** @SelfDocumented */
@BindingAdapter("marginBottomRes")
fun View.setMarginBottomRes(@DimenRes marginBottomRes: Int) {
    if (marginBottomRes == 0) return
    val marginBottom = resources.getDimensionPixelSize(marginBottomRes)
    layoutParams = (layoutParams as ViewGroup.MarginLayoutParams).apply {
        bottomMargin = marginBottom
    }
}

/** @SelfDocumented */
@BindingAdapter("marginTopRes")
fun View.setMarginTopRes(@DimenRes marginTopRes: Int) {
    if (marginTopRes == 0) return
    val marginTop = resources.getDimensionPixelSize(marginTopRes)
    layoutParams = (layoutParams as ViewGroup.MarginLayoutParams).apply {
        topMargin = marginTop
    }
}

/** @SelfDocumented */
@BindingAdapter(value = ["marginLeftAttr", "marginTopAttr", "marginRightAttr", "marginBottomAttr"], requireAll = false)
fun View.setMarginAttrRes(
    @AttrRes marginLeft: Int? = null,
    @AttrRes marginTop: Int? = null,
    @AttrRes marginRight: Int? = null,
    @AttrRes marginBottom: Int? = null
) {
    (layoutParams as ViewGroup.MarginLayoutParams).apply {
        marginLeft?.let {
            if (marginLeft != ID_NULL) {
                leftMargin = context.getDimenPx(marginLeft)
            }
        }
        marginTop?.let {
            if (marginTop != ID_NULL) {
                topMargin = context.getDimenPx(marginTop)
            }
        }
        marginRight?.let {
            if (marginRight != ID_NULL) {
                rightMargin = context.getDimenPx(marginRight)
            }
        }
        marginBottom?.let {
            if (marginBottom != ID_NULL) {
                bottomMargin = context.getDimenPx(marginBottom)
            }
        }
    }
}

/** @SelfDocumented */
@BindingAdapter("paddingBottomAttrRes")
fun View.setPaddingBottomAttrRes(@AttrRes paddingBottomAttrRes: Int) {
    if (paddingBottomAttrRes == 0) return
    val newPaddingBottom = context.getDimenPx(paddingBottomAttrRes)
    setPadding(paddingLeft, paddingTop, paddingRight, newPaddingBottom)
}

/** @SelfDocumented */
@BindingAdapter("paddingTopAttrRes")
fun View.setPaddingTopAttrRes(@AttrRes paddingTopAttrRes: Int) {
    if (paddingTopAttrRes == 0) return
    val newPaddingTop = context.getDimenPx(paddingTopAttrRes)
    setPadding(paddingLeft, newPaddingTop, paddingRight, paddingBottom)
}
/*endregion View*/

/*region ImageView*/
/** @SelfDocumented */
@BindingAdapter(
    value = ["personViewDataById", "personViewDataByUuid", "personViewInitials", "personViewSizeRes"],
    requireAll = false
)
fun PersonView.setPersonViewDataByUuid(
    photoId: String,
    uuid: String,
    initials: InitialsStubData?,
    @DimenRes photoSizeResId: Int
) {
    var photoUrl: String? = null
    if (photoId.isNotEmpty()) {
        val photoSize = if (photoSizeResId != 0) {
            resources.getDimensionPixelSize(photoSizeResId)
        } else {
            resources.getDimensionPixelSize(RDesignProfile.dimen.design_profile_sbis_person_view_photo_very_large_size)
        }
        photoUrl = PhotoUrlUtils.getPhotoUrlById(photoId, photoSize)
    } else if (uuid.isNotEmpty()) {
        photoUrl = UrlUtils.getImageUrl(uuid, UrlUtils.ImageSize.DEFAULT)
    }
    setData(PersonData(photoUrl = photoUrl, initialsStubData = initials))
}
/*endregion ImageView*/

/*region TextView*/
/** @SelfDocumented */
@BindingAdapter("textAndVisibility")
fun TextView.setTextAndVisibility(text: CharSequence?) {
    this.text = text
    val visible = text != null && text.isNotEmpty()
    visibility = if (visible) View.VISIBLE else View.GONE
}

/** @SelfDocumented */
@BindingAdapter("clickableSpan")
fun TextView.setClickableTextSpan(clickableSpan: Boolean) {
    if (clickableSpan) {
        movementMethod = LinkMovementMethod.getInstance()
    }
}

/** @SelfDocumented */
@BindingAdapter("textStyle")
fun TextView.setFontStyle(@StyleRes textStyle: Int) {
    TextViewCompat.setTextAppearance(this, textStyle)
}

/** @SelfDocumented */
@BindingAdapter("textWithHighlightedRanges", "highlightedRanges")
fun TextView.setTextWithHighlightedRanges(
    text: String,
    highlightedRanges: List<IntRange>
) {
    val spannableString = SpannableString(text)
    highlightedRanges.forEach {
        val colorSpan = BackgroundColorSpan(resources.getColor(RDesign.color.text_search_highlight_color))
        spannableString.setSpan(colorSpan, it.first, it.last + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    setText(spannableString)
}
/*endregion TextView*/

/*region List*/
/** @SelfDocumented */
@BindingAdapter("swipeAction")
fun SbisPullToRefresh.setSwipeAction(action: SwipeRefreshLayout.OnRefreshListener) {
    setOnRefreshListener(action)
}

/** @SelfDocumented */
@BindingAdapter("loadMore")
fun RecyclerView.setLoadMoreProgress(visibility: Boolean) {
    if (visibility) {
        (adapter as? ViewModelAdapter)?.showLoadMoreProgress()
    }
}

/**
 * Установка контента списка.
 *
 * @param isHiddenWhenEmpty стоит ли скрывать список при отсутствии контента, по умолчанию true
 */
@BindingAdapter("list", "scrollToTopPosition", "attachedRefreshView", "isHiddenWhenEmpty", requireAll = false)
fun RecyclerView.setContent(
    content: List<BaseObservable>?,
    scrollToTopPosition: Boolean,
    refreshView: SbisPullToRefresh?,
    isHiddenWhenEmpty: Boolean = true
) {
    if (content == null) return
    val adapter = adapter as? ViewModelAdapter ?: return
    adapter.reload(content)
    if (scrollToTopPosition) {
        scrollToTop()
    }
    refreshView?.isRefreshing = false
    if (isHiddenWhenEmpty) {
        visibility = if (content.isNotEmpty()) View.VISIBLE else View.INVISIBLE
    }
}
/*endregion List*/

/*region Kopecks SbisTextView*/
/**
 *  Data Binding адаптер метод для отображения сумм.
 *
 *  @revenue сумма с копейками. Например "884.80"
 *  @kopeckAppearance отдельный стиль для копеек
 *  @kopeckAppearance отдельный стиль для текста ед. измерения суммы. Например в "301 млн"
 *  @textStyleBeforeKopecks назначение стиля суммы в обход атрибута style
 *  @hideKopecks true если требуется отсечение копеек
 */
@BindingAdapter(
    value = [
        "textWithKopecks",
        "kopeckAppearanceStyle",
        "unitAppearanceStyle",
        "textStyleBeforeKopecks",
        "hideKopecks",
        "boldBasic"
    ],
    requireAll = false
)
fun SbisTextView.textWithKopecks(
    revenue: String?,
    @StyleRes kopeckAppearance: Int,
    @StyleRes unitAppearanceStyle: Int = ID_NULL,
    @StyleRes textStyleBeforeKopecks: Int = ID_NULL,
    hideKopecks: Boolean = false,
    boldBasic: Boolean = false
) {
    if (revenue == null) return
    text = when {
        hideKopecks && unitAppearanceStyle != ID_NULL -> {
            val noKopecksRevenue = revenue.takeWhile { it != DOT_CHAR }
            val unitStartPosition = noKopecksRevenue.indexOfFirst(Char::isLetter)
            SpannableString(noKopecksRevenue).apply {
                if (unitStartPosition != -1) {
                    setSpan(
                        TextAppearanceSpan(context, unitAppearanceStyle),
                        unitStartPosition,
                        noKopecksRevenue.length,
                        SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }

        hideKopecks -> revenue.takeWhile { it != DOT_CHAR }

        else -> {
            if (textStyleBeforeKopecks != ID_NULL) {
                setTextAppearance(context, textStyleBeforeKopecks)
            }
            val kopeckAppearanceStyle = if (kopeckAppearance != ID_NULL) kopeckAppearance else R.style.KopeckAppearance
            makeSpannableStringToFractionalMoney(
                context = context,
                value = revenue,
                kopecksStyle = kopeckAppearanceStyle,
                unitStyle = unitAppearanceStyle,
                isBoldBasic = boldBasic
            )
        }
    }
}

/** @SelfDocumented */
@BindingAdapter(value = ["textWithKopecksRegular", "boldBasic"], requireAll = false)
fun SbisTextView.textWithKopecksRegular(
    revenue: String,
    boldBasic: Boolean
) = textWithKopecks(
    revenue = revenue,
    kopeckAppearance = R.style.KopeckAppearance,
    boldBasic = boldBasic
)
/*endregion Kopecks SbisTextView*/

/** @SelfDocumented */
fun makeSpannableStringToFractionalMoney(
    context: Context,
    value: String,
    @StyleRes kopecksStyle: Int,
    @StyleRes unitStyle: Int = ID_NULL,
    isBoldBasic: Boolean,
    typeface: Typeface? = null
): SpannableString {
    var kopecksStartPosition = value.lastIndexOf(DOT_CHAR)
    val unitStartPosition = value.takeIf { unitStyle != ID_NULL }?.indexOfFirst(Char::isLetter) ?: -1
    if (value.endsWith(DOT_CHAR)) {
        kopecksStartPosition = value.lastIndexOf(' ')
    }
    val spannableString = SpannableString(value)
    val kopecksSpan = TextAppearanceSpan(context, kopecksStyle)
    if (unitStyle != ID_NULL && unitStartPosition != -1) {
        spannableString.setSpan(
            TextAppearanceSpan(context, unitStyle),
            unitStartPosition,
            value.length,
            SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    if (kopecksStartPosition != -1 && isCharacterAfterPointDigit(value, kopecksStartPosition)) {
        spannableString.setSpan(kopecksSpan, kopecksStartPosition, value.length, SPAN_EXCLUSIVE_EXCLUSIVE)
        if (isBoldBasic) {
            val spanTypeface = typeface ?: getFont(context, RDesign.font.roboto_medium)
            val basePartAppearance = CustomTypefaceSpan(spanTypeface)
            spannableString.setSpan(basePartAppearance, 0, kopecksStartPosition, SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
    return spannableString
}

/** @SelfDocumented */
@BindingAdapter("heightRes")
fun setHeightRes(view: View, @DimenRes height: Int) {
    val lp = view.layoutParams
    lp.height = view.resources.getDimensionPixelSize(height)
    view.layoutParams = lp
}

/** @SelfDocumented */
@BindingAdapter(value = ["widthAttrRes", "ignoreExtraDensity"], requireAll = false)
fun View.getDimenFromAttr(@AttrRes attrResId: Int, ignoreExtraDensity: Boolean = false) {
    if (attrResId != 0) {
        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(attrResId, typedValue, true)
        this.setWidth(typedValue.resourceId, ignoreExtraDensity = ignoreExtraDensity)
    }
}

/** @SelfDocumented */
@BindingAdapter(value = ["widthRes", "forceWidthIfEmpty", "ignoreExtraDensity"], requireAll = false)
fun View.setWidth(
    @DimenRes @IntegerRes widthRes: Int,
    forceWrapContent: Boolean = false,
    ignoreExtraDensity: Boolean = false
) {
    if (forceWrapContent) {
        layoutParams = layoutParams.apply {
            width = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        return
    }
    if (widthRes == 0) {
        return
    }
    val spec = runCatching { resources.getInteger(widthRes) }.getOrNull()
    val newWidth = when {
        widthRes == RDesign.dimen.match_constraint -> ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
        spec == ConstraintLayout.LayoutParams.MATCH_CONSTRAINT -> ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
        widthRes == RDesign.dimen.wrap_content -> ViewGroup.LayoutParams.WRAP_CONTENT
        spec == ViewGroup.LayoutParams.WRAP_CONTENT -> ViewGroup.LayoutParams.WRAP_CONTENT
        widthRes == RDesign.dimen.match_parent -> ViewGroup.LayoutParams.MATCH_PARENT
        spec == ViewGroup.LayoutParams.MATCH_PARENT -> ViewGroup.LayoutParams.MATCH_PARENT
        else -> {
            val width =
                runCatching { resources.getDimensionPixelSize(widthRes) }.getOrDefault(ViewGroup.LayoutParams.MATCH_PARENT)

            if (ignoreExtraDensity) {
                val metrics = DisplayMetrics()
                getActivity().windowManager.defaultDisplay.getMetrics(metrics)

                val originDensity = metrics.density
                val currentDensity = resources.displayMetrics.density

                ((width / currentDensity) * originDensity).toInt()
            } else {
                width
            }
        }
    }
    layoutParams = layoutParams.apply {
        width = newWidth
    }
}

/** @SelfDocumented */
@BindingAdapter("breadCrumbs")
internal fun BreadCrumbsView.setBreadCrumbsItems(
    breadCrumbs: BreadCrumbsVm
) {
    setItems(breadCrumbs.items)
    setItemClickListener {
        breadCrumbs.clickAction()
    }
}

/** @SelfDocumented */
@BindingAdapter("cardBackground")
internal fun CardView.setCardBackground(cardBackgroundAttr: Int) {
    if (cardBackgroundAttr != 0)
        setCardBackgroundColor(context.getThemeColorInt(cardBackgroundAttr))
}

private const val DOT_CHAR = '.'