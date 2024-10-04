package ru.tensor.sbis.design.link_share.presentation.adapter.holder

import android.view.ViewGroup
import androidx.core.view.setPadding
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.common.data.model.base.BaseItem
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.R as RDesign

/**@SelfDocumented*/
internal class CustomBlockTitleHolder(
    parent: ViewGroup
) : AbstractViewHolder<BaseItem<Any>>(SbisTextView(parent.context)) {

    override fun bind(model: BaseItem<Any>) {
        super.bind(model)
        (itemView as SbisTextView).apply {
            setPadding(context.getDimenPx(RDesign.attr.offset_m))
            maxLines = 1
            textSize = context.getDimenPx(RDesign.attr.fontSize_4xl_scaleOff).toFloat()
            setTextColor(context.getColorFromAttr(RDesign.attr.textColor))
            text = model.data as String
            typeface = TypefaceManager.getRobotoBoldFont(context)
        }
    }
}