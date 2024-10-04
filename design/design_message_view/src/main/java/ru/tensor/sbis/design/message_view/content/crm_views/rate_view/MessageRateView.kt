package ru.tensor.sbis.design.message_view.content.crm_views.rate_view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.message_view.R
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.rating.SbisRatingView
import ru.tensor.sbis.design.rating.model.SbisRatingColorsMode
import ru.tensor.sbis.design.rating.model.SbisRatingFilledMode
import ru.tensor.sbis.design.rating.model.SbisRatingIconType
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.global_variables.IconSize
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import ru.tensor.sbis.design.utils.getDimen
import ru.tensor.sbis.design.utils.getDimenPx

/**
 * Вью для отображения оценки работы оператора.
 *
 * @author da.zhukov
 */
@SuppressLint("ViewConstructor")
internal class MessageRateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val mobileIconFont = TypefaceManager.getSbisMobileIconTypeface(context)

    private val excellentRatingColor by lazy { context.getColorFromAttr(RDesign.attr.successIconColor) }
    private val okRatingColor by lazy { context.getColorFromAttr(RDesign.attr.warningIconColor) }
    private val badRatingColor by lazy { context.getColorFromAttr(RDesign.attr.dangerIconColor) }

    private val emojiAndThumbSize by lazy { context.getDimen(RDesign.attr.iconSize_4xl) }

    init {
        id = R.id.design_message_view_message_cloud_rate
        setPadding(0, context.getDimenPx(RDesign.attr.offset_xs), 0, 0)
    }

    private var data: ServiceRateData? = null

    /** Поместить нужные иконки в вью в зависимости от переданной data. */
    fun setData(data: ServiceRateData) {
        if (data == this.data) return
        removeAllViews()
        when (val type = data.consultationRateType) {
            is StarType -> {
                addView(getRateStars(type.starsCount.toDouble()))
            }
            is EmojiType -> {
                addView(getEmojiIcon(type))
            }
            is ThumbType -> {
                addView(getThumbIcon(type))
            }
        }
    }

    private fun getRateStars(rate: Double) = SbisRatingView(context).apply {
        maxValue = STARS_MAX_COUNT
        iconSize = IconSize.X4L
        iconType = SbisRatingIconType.STARS
        emptyIconFilledMode = SbisRatingFilledMode.FILLED
        colorsMode = SbisRatingColorsMode.DYNAMIC
        value = rate
        readOnly = true
    }

    private fun getEmojiIcon(type: EmojiType): SbisTextView {
        val (textColor, text) = when (type.emoji) {
            EmojiRate.SMILE -> excellentRatingColor to RDesign.string.design_mobile_icon_emoicon_smile_invert
            EmojiRate.NEUTRAL -> okRatingColor to RDesign.string.design_mobile_icon_emoicon_neutral_invert
            EmojiRate.ANNOYED -> badRatingColor to RDesign.string.design_mobile_icon_emoicon_annoyed_invert
        }
        return SbisTextView(context).defPrepareParams(textColor, emojiAndThumbSize, text)
    }

    private fun getThumbIcon(type: ThumbType): SbisTextView {
        val (textColor, text) = when (type.finger) {
            FingerRating.LIKE -> excellentRatingColor to RDesign.string.design_mobile_icon_like_icon
            FingerRating.DISLIKE -> badRatingColor to RDesign.string.design_mobile_icon_dislike_icon
        }
        return SbisTextView(context).defPrepareParams(textColor, emojiAndThumbSize, text)
    }

    private fun SbisTextView.defPrepareParams(color: Int, size: Float, text: Int): SbisTextView {
        return this.apply {
            typeface = mobileIconFont
            setTextColor(color)
            setText(text)
            textSize = size
        }
    }
}

private const val STARS_MAX_COUNT = 5