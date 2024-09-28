package ru.tensor.sbis.communicator.declaration.crm

import android.content.Context
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr

/**
 * Данные для отображения оценки работы оператора.
 *
 * @author da.zhukov
 */
data class CrmRateInfoData(
    val type: String,
    val value: Int
)

/**
 * Данные для отображения оценки работы оператора.
 */
fun prepareClosedIcon(rateData: CrmRateInfoData?, context: Context): Pair<String,Int> {
    val excellentRatingColor by lazy { context.getColorFromAttr(R.attr.rate10Color) }
    val badRatingColor by lazy { context.getColorFromAttr(R.attr.rate2Color) }
    return when {
        rateData == null -> context.getString(R.string.design_mobile_icon_successful) to context.getColorFromAttr(R.attr.successIconColor)
        rateData.type == "thumbs" -> {
            if (rateData.value > 1) {
                context.getString(R.string.design_mobile_icon_like_icon) to excellentRatingColor
            } else {
                context.getString(R.string.design_mobile_icon_dislike_icon) to badRatingColor
            }
        }
        rateData.type == "smile" -> {
            when(rateData.value) {
                1 -> context.getString(R.string.design_mobile_icon_emoicon_annoyed_invert) to badRatingColor
                3 -> context.getString(R.string.design_mobile_icon_emoicon_neutral_invert) to context.getColorFromAttr(R.attr.warningIconColor)
                else -> context.getString(R.string.design_mobile_icon_emoicon_smile_invert) to excellentRatingColor
            }
        }
        else -> {
            val starIcon = context.getString(R.string.design_mobile_icon_favourite)
            when(rateData.value) {
                1 -> starIcon to badRatingColor
                2 -> starIcon to context.getColorFromAttr(R.attr.rate4Color)
                3 -> starIcon to context.getColorFromAttr(R.attr.rate6Color)
                4 -> starIcon to context.getColorFromAttr(R.attr.rate8Color)
                else -> starIcon to excellentRatingColor
            }
        }
    }
}

