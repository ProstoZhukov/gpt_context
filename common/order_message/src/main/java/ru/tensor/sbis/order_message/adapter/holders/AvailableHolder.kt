package ru.tensor.sbis.order_message.adapter.holders

import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.TextView
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.common.data.model.base.BaseItem
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.utils.extentions.getColorFrom
import ru.tensor.sbis.design.utils.extentions.setRightPadding
import ru.tensor.sbis.design.utils.extentions.setTopPadding
import ru.tensor.sbis.order_message.R
import ru.tensor.sbis.design.R as RDesign

/**@SelfDocumented*/
internal class AvailableHolder(parent: ViewGroup) : AbstractViewHolder<BaseItem<Any>>(FrameLayout(parent.context)) {

    init {
        val resources = itemView.resources
        (itemView as FrameLayout).apply {
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            addView(TextView(parent.context).apply {
                layoutParams = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).also { it.gravity = Gravity.END }
                text = resources.getString(R.string.order_message_stop_list_available)
                setRightPadding(resources.dp(12))
                typeface = TypefaceManager.getRobotoRegularFont(context)
                textSize = 12f
                setTextColor(itemView.getColorFrom(RDesign.color.palette_color_black3))
            })
        }
    }
}