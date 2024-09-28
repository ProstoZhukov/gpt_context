package ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.view.selecteditems.model.SelectedItem
import ru.tensor.sbis.design.selection.ui.view.selecteditems.model.SelectedItemsViewConfiguration
import ru.tensor.sbis.design.selection.ui.view.selecteditems.utils.showPreview
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.factory.SelectedItemViewHolderFactory
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.view.adapter.SelectedItemsAdapter
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.design.R as DesignR

/**
 * [View] панели, отображающей список выбранных элементов.
 *
 * @author us.bessonov
 */
class SelectedItemsContainerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private val adapter = SelectedItemsAdapter()

    init {
        setAdapter(adapter)
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        (itemAnimator as DefaultItemAnimator).apply {
            moveDuration = addDuration
            supportsChangeAnimations = false
        }

        updatePadding(
            left = context.getDimenPx(DesignR.attr.offset_l),
            right = context.getDimenPx(DesignR.attr.offset_xs)
        )
        clipToPadding = false

        setBackgroundColor(context.getThemeColorInt(DesignR.attr.unaccentedBackgroundColor))

        if (isInEditMode) showPreview()
    }

    /** @SelfDocumented */
    fun setItems(items: List<SelectedItem>) {
        adapter.reload(items)
        // во избежание смаргивания после повторного показа панели
        post { isVisible = items.isNotEmpty() }
    }

    /**
     * Задаёт фабрику для создания вьюхолдеров элементов.
     * Фабрика должна поддерживать все типы элементов, которые могут быть установлены посредством [setItems]
     */
    internal fun setItemFactory(factory: SelectedItemViewHolderFactory) {
        adapter.itemFactory = factory
    }

    /**
     * Задаёт настраиваемые параметры панели
     */
    fun setConfiguration(config: SelectedItemsViewConfiguration) {
        adapter.config = config
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(
            widthSpec,
            MeasureSpec.makeMeasureSpec(
                resources.getDimensionPixelSize(R.dimen.selection_selected_items_view_height),
                MeasureSpec.EXACTLY
            )
        )
    }

}
