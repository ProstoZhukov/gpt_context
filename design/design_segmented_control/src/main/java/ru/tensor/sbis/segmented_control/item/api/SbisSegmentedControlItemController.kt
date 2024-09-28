package ru.tensor.sbis.segmented_control.item.api

import android.view.View
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.segmented_control.item.SbisSegmentedControlItem
import ru.tensor.sbis.segmented_control.item.models.SbisSegmentedControlCounter
import ru.tensor.sbis.segmented_control.item.models.SbisSegmentedControlIcon
import ru.tensor.sbis.segmented_control.item.models.SbisSegmentedControlItemModel
import ru.tensor.sbis.segmented_control.item.models.SbisSegmentedControlTitle

/**
 * Контроллер сегмента в сегмент-контроле.
 *
 * @author ps.smirnyh
 */
internal class SbisSegmentedControlItemController : SbisSegmentedControlItemApi {

    /** @SelfDocumented */
    internal lateinit var segment: View

    override var model: SbisSegmentedControlItemModel = SbisSegmentedControlItemModel()
        set(value) {
            if (field == value) return
            field = value

            val segmentItem = segment as SbisSegmentedControlItem
            val iconChanged =
                segmentItem.updateIcon(field.icon, field.icon?.size ?: segmentItem.size.iconSize)
            val titleChanged = segmentItem.updateTitle(
                field.title,
                field.title?.size?.titleSize ?: segmentItem.size.titleSize
            )
            val counterChanged =
                segmentItem.updateCounter(field.counter, segmentItem.size.titleSize)

            title = model.title
            icon = model.icon
            counter = model.counter

            segmentItem.updateAccessibilityText(title, counter)

            if (iconChanged || titleChanged || counterChanged) {
                segment.safeRequestLayout()
            }
        }

    /** @SelfDocumented */
    internal var title: SbisSegmentedControlTitle? = null

    /** @SelfDocumented */
    internal var icon: SbisSegmentedControlIcon? = null

    /** @SelfDocumented */
    internal var counter: SbisSegmentedControlCounter? = null

    /** Инициализация класса. Начальная настройка. */
    internal fun attach(segment: View) {
        this.segment = segment
    }
}