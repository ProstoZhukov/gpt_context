package ru.tensor.sbis.appdesign.cloudview.resources

import android.content.Context
import androidx.core.content.ContextCompat
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.design.cloud_view.content.utils.DEFAULT_TEXT
import ru.tensor.sbis.design.cloud_view.content.utils.DISABLED_TEXT
import ru.tensor.sbis.design.cloud_view.content.utils.MessageResourcesHolder
import ru.tensor.sbis.design.R as RDesign

/**
 * @author ma.kolpakov
 */
internal class DemoMessageResourcesHolder(
    private val context: Context
) : MessageResourcesHolder {

    override fun getTextColor(type: Int): Int = when (type) {
        DEFAULT_TEXT  -> ContextCompat.getColor(context, R.color.text_color_black_1)
        DISABLED_TEXT -> ContextCompat.getColor(context, R.color.text_color_black_3)
        else          -> error("Unexpected text color type $type")
    }

    override fun getCertificateBadgeColor(mine: Boolean): Int =
        if (mine)
            ContextCompat.getColor(context, RDesign.color.text_color_accent_1)
        else
            ContextCompat.getColor(context, RDesign.color.text_color_accent_3)
}