package ru.tensor.sbis.design.radio_group.item

import android.content.Context
import ru.tensor.sbis.design.radio_group.R
import ru.tensor.sbis.design.radio_group.control.PairModelView
import ru.tensor.sbis.design.radio_group.control.RadioGroupStyleHolder
import ru.tensor.sbis.design.radio_group.control.models.SbisRadioGroupItem
import ru.tensor.sbis.design.radio_group.control.to

/**
 * Класс создания экземпляров [SbisRadioGroupItemView] по переданной модели [SbisRadioGroupItem].
 *
 * @author ps.smirnyh
 */
internal class RadioGroupItemViewFactory {

    /** Создать список [SbisRadioGroupItemView] с учетом дочерних элементов. */
    fun createView(
        context: Context,
        styleHolder: RadioGroupStyleHolder,
        model: SbisRadioGroupItem
    ): List<PairModelView> {
        val modelsAndViews = mutableListOf<PairModelView>()
        modelsAndViews += createView(context, styleHolder, model, 0, null)
        return modelsAndViews
    }

    private fun createView(
        context: Context,
        styleHolder: RadioGroupStyleHolder,
        model: SbisRadioGroupItem,
        hierarchyLevel: Int,
        parent: SbisRadioGroupItemView?
    ): List<PairModelView> {
        val modelsAndViews = mutableListOf<PairModelView>()
        val root = SbisRadioGroupItemView(
            context = context,
            itemId = model.id.trim(),
            content = model.content,
            styleHolder = styleHolder
        ).apply {
            this.hierarchyLevel = hierarchyLevel
            isEnabled = !model.readOnly
            parentItem = parent
            id = R.id.radio_group_item_view_id
        }
        model.parentId = parent?.itemId
        modelsAndViews += model to root
        model.children.forEach {
            modelsAndViews += createView(context, styleHolder, it, hierarchyLevel + 1, root)
        }
        return modelsAndViews
    }
}