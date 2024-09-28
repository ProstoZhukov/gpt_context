package ru.tensor.sbis.onboarding_tour.ui.views

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.verification_decl.onboarding_tour.data.BannerButtonType

/** @SelfDocumented */
internal fun SbisTextView.setOptionalText(@StringRes resId: Int?) {
    if (resId != null && resId != ResourcesCompat.ID_NULL) setText(resId) else text = null
}

/** @SelfDocumented */
internal fun SbisButton.setOptionalText(@StringRes resId: Int?, updateVisibility: Boolean = false) {
    if (resId != null && resId != ResourcesCompat.ID_NULL) setTitleRes(resId) else setTitle(null)
    if (updateVisibility) visibilityByRes(resId)
}

/** @SelfDocumented */
internal fun ImageView.setOptionalImage(@DrawableRes resId: Int?) =
    if (resId != null && resId != ResourcesCompat.ID_NULL) setImageResource(resId) else setImageDrawable(null)

/** @SelfDocumented */
internal fun setVisibility(visibility: Int, vararg views: View) {
    views.forEach { it.visibility = visibility }
}

/** @SelfDocumented */
internal fun SbisTextView.setBannerIconTypeface(type: BannerButtonType) {
    typeface = if (type == BannerButtonType.CLOSE) {
        TypefaceManager.getSbisMobileIconTypeface(context)
    } else {
        TypefaceManager.getRobotoRegularFont(context)
    }
}

/** @SelfDocumented */
internal fun TextView.setClickableText(
    caption: String,
    textAndLink: Map<String, String>,
    onLinkClick: (String) -> Unit
) {
    val spannableStringBuilder = SpannableStringBuilder(caption)
    textAndLink.forEach { (substring, link) ->
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) = onLinkClick(link)
            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = true
            }
        }
        spannableStringBuilder.setSpan(
            clickableSpan,
            caption.indexOf(substring),
            caption.indexOf(substring) + substring.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    text = spannableStringBuilder
}

/**
 * Метод обновялет текст в [SbisTextView] и кол-во строк в текущей вью после анимации.
 *
 * @param textResId ресурс с текстом
 * @param affordableLineCount доступное количество строк взятое из вью на место которой мы свайпим и состояние которой нам
 * потребуется применить после завершения анимации. Решает проблему с видимым обновлением кол-ва строк.
 */
fun SbisTextView.setTextAndLineCount(
    @StringRes textResId: Int?,
    affordableLineCount: Int = 0
) {
    if (textResId == null || textResId == ResourcesCompat.ID_NULL) {
        text = null
    } else {
        if (resources.getString(textResId) == text) {
            return
        }
        setText(textResId)
        if (affordableLineCount != 0 && lineCount != affordableLineCount) {
            lines = affordableLineCount
        }
    }
}

/** @SelfDocumented */
private fun View.visibilityByRes(resId: Int?) =
    if (resId == null || resId == ResourcesCompat.ID_NULL) {
        visibility = View.INVISIBLE
    } else {
        visibility = View.VISIBLE
    }