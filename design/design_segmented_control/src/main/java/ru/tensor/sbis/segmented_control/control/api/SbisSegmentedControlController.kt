package ru.tensor.sbis.segmented_control.control.api

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.isNotEmpty
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.utils.delegateNotEqual
import ru.tensor.sbis.segmented_control.SbisSegmentedControl
import ru.tensor.sbis.segmented_control.control.models.SbisSegmentedControlDistribution
import ru.tensor.sbis.segmented_control.control.models.SbisSegmentedControlSize
import ru.tensor.sbis.segmented_control.control.models.SbisSegmentedControlStyle
import ru.tensor.sbis.segmented_control.item.SbisSegmentedControlItem
import ru.tensor.sbis.segmented_control.item.api.SbisSegmentedControlItemFactory
import ru.tensor.sbis.segmented_control.item.api.SbisSegmentedControlItemFactoryDefault
import ru.tensor.sbis.segmented_control.item.models.SbisSegmentedControlCounter
import ru.tensor.sbis.segmented_control.item.models.SbisSegmentedControlItemModel
import ru.tensor.sbis.segmented_control.utils.SegmentedControlStyleHolder

/**
 * Класс реализации api и логики [SbisSegmentedControl].
 *
 * @author ps.smirnyh
 */
internal class SbisSegmentedControlController(
    internal val styleHolder: SegmentedControlStyleHolder = SegmentedControlStyleHolder(),
    private val segmentItemFactory: SbisSegmentedControlItemFactory = SbisSegmentedControlItemFactoryDefault
) : SbisSegmentedControlApi {

    private lateinit var segmentedControl: SbisSegmentedControl
    private val context: Context
        get() = segmentedControl.context

    /** Список сегментов. */
    internal var listSegments = mutableListOf<SbisSegmentedControlItem>()

    override var style: SbisSegmentedControlStyle by delegateNotEqual(SbisSegmentedControlStyle.DEFAULT) { newValue ->
        styleHolder.onStyleChanged(context, newValue)
        segmentedControl.onStyleChanged(styleHolder, contrast)
    }

    override var contrast: Boolean = false
        set(value) {
            field = value
            segmentedControl.onStyleChanged(styleHolder, value)
        }

    override var distribution by delegateNotEqual(
        SbisSegmentedControlDistribution.EQUAL
    ) { newValue ->
        listSegments.forEach {
            it.layoutParams = newValue.getLayoutParams()
        }
        segmentedControl.safeRequestLayout()
    }

    override var size: SbisSegmentedControlSize = SbisSegmentedControlSize.S
        set(value) {
            field = value
            segmentedControl.onSizeChanged(value)
        }

    override var hasShadow = false
        set(value) {
            if (field == value) return
            field = if (contrast) value else false
            segmentedControl.elevation = if (field) {
                styleHolder.elevation.toFloat()
            } else {
                0f
            }
        }

    override var selectedSegmentIndex = 0
        set(value) {
            field = value
            listSegments.forEachIndexed { index, sbisSegmentedControlItem ->
                val isSelected = value == index
                sbisSegmentedControlItem.onSelectedChanged(
                    isSelected,
                    styleHolder.getItemTextColorBySelected(contrast),
                    styleHolder.getItemIconColorBySelected(contrast)
                )
                sbisSegmentedControlItem.isSelected = isSelected
            }
        }

    override var onSelectedSegmentChangedListener: (
        (
            segmentIndex: Int,
            segment: SbisSegmentedControlItemModel
        ) -> Unit
    )? = null

    override fun setSelectedSegmentIndex(segmentIndex: Int, animated: Boolean) {
        if (selectedSegmentIndex == segmentIndex) return

        selectedSegmentIndex = segmentIndex
        segmentedControl.changeSelectedSegment(
            listSegments[selectedSegmentIndex],
            animated
        )

    }

    override fun setSegments(segments: List<SbisSegmentedControlItemModel>) {
        // Если список сегментов пустой, тогда не нужно обнулять индекс
        // т.к. сломается восстановление при пересоздании вью
        if (listSegments.isNotEmpty() || segmentedControl.isNotEmpty()) {
            selectedSegmentIndex = 0
        }

        segmentedControl.removeAllViews()
        listSegments.clear()
        segments.forEachIndexed { index, newModel ->
            val isSelected = selectedSegmentIndex == index
            segmentedControl.addView(
                segmentItemFactory.createItem(
                    context = segmentedControl.context,
                    newModel = newModel,
                    isSelected = isSelected,
                    isContrast = contrast,
                    styleHolder = styleHolder
                ).apply {
                    setOnClickListener {
                        setSelectedSegmentIndex(segments.indexOf(newModel), true)
                    }
                    this.isSelected = isSelected
                }
            )
        }
        segmentedControl.onStateUpdateChildren()
    }

    override fun updateCounter(segmentIndex: Int, counterModel: SbisSegmentedControlCounter?) {
        listSegments[segmentIndex].model = listSegments[segmentIndex].model.copy(counter = counterModel)
    }

    /** Инициализация класса. Начальная настройка. */
    internal fun attach(
        segmentedControl: SbisSegmentedControl,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        this.segmentedControl = segmentedControl
        styleHolder.init(context, attrs, defStyleAttr, defStyleRes)
        applyStyles()
        segmentedControl.onStyleChanged(styleHolder, contrast)
        segmentedControl.onSizeChanged(size)
    }

    /** Callback при изменении выбранного сегмента. */
    internal fun onChangedSelectedSegment() {
        onSelectedSegmentChangedListener?.invoke(
            selectedSegmentIndex,
            listSegments[selectedSegmentIndex].model
        )
    }

    private fun applyStyles() {
        contrast = styleHolder.contrast
        size = styleHolder.size
        distribution = styleHolder.distribution
        hasShadow = styleHolder.hasShadow
        selectedSegmentIndex = styleHolder.selectedSegmentIndex
    }
}