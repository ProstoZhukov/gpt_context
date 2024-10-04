package ru.tensor.sbis.design.radio_group.control.api

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.forEach
import androidx.core.view.plusAssign
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.radio_group.SbisRadioGroupView
import ru.tensor.sbis.design.radio_group.control.PairModelView
import ru.tensor.sbis.design.radio_group.control.RadioGroupStyleHolder
import ru.tensor.sbis.design.radio_group.control.layout.RadioGroupLayoutHorizontalStrategy
import ru.tensor.sbis.design.radio_group.control.layout.RadioGroupLayoutVerticalStrategy
import ru.tensor.sbis.design.radio_group.control.layout.RadioGroupLayoutView
import ru.tensor.sbis.design.radio_group.control.models.SbisRadioGroupItem
import ru.tensor.sbis.design.radio_group.control.models.SbisRadioGroupValidationStatus
import ru.tensor.sbis.design.radio_group.item.RadioGroupItemViewFactory
import ru.tensor.sbis.design.radio_group.item.SbisRadioGroupItemView
import ru.tensor.sbis.design.utils.delegateNotEqual
import kotlin.properties.Delegates

/**
 * Класс реализации api и логики [SbisRadioGroupView].
 *
 * [styleHolder] вспомогательный класс для управления стилизацией компонента.
 * [radioGroupItemViewFactory] вспомогательный класс для создания экземпляров view радиокнопок по переданным параметрам.
 *
 * @author ps.smirnyh
 */
internal class SbisRadioGroupController(
    internal val styleHolder: RadioGroupStyleHolder = RadioGroupStyleHolder(),
    private val radioGroupItemViewFactory: RadioGroupItemViewFactory = RadioGroupItemViewFactory()
) : SbisRadioGroupViewApi {

    private var radioGroupView: SbisRadioGroupView by Delegates.notNull()

    private val radioGroupLayoutView: RadioGroupLayoutView
        get() = radioGroupView.radioGroupLayoutView

    private val context: Context
        get() = radioGroupView.context

    private val itemViews = mutableMapOf<String, PairModelView>()

    override var items: List<SbisRadioGroupItem> by delegateNotEqual(emptyList()) { value ->
        radioGroupLayoutView.removeAllViews()
        itemViews.clear()
        value.forEach { model ->
            val itemId = model.id.trim()
            require(itemViews[itemId] == null) {
                "The IDs must be unique in the list. Multiple items with id = $itemId"
            }
            radioGroupItemViewFactory.createView(
                context = context,
                styleHolder = styleHolder,
                model = model
            ).forEach { pairModelView ->
                itemViews[pairModelView.view.itemId] = pairModelView
                pairModelView.view.apply {
                    setOnClickListener {
                        selectedKey = pairModelView.view.itemId
                    }
                    titlePosition = this@SbisRadioGroupController.titlePosition
                    radioGroupLayoutView += this
                }
            }
        }
    }

    override var titlePosition: SbisRadioGroupTitlePosition by delegateNotEqual(
        SbisRadioGroupTitlePosition.RIGHT
    ) { value ->
        with(radioGroupLayoutView) {
            forEach {
                it as SbisRadioGroupItemView
                it.titlePosition = value
                it.forceLayout()
            }
            titlePosition = value
            safeRequestLayout()
        }

    }

    override var multiline: Boolean by delegateNotEqual(true) { value ->
        if (orientation != SbisRadioGroupOrientation.HORIZONTAL) {
            return@delegateNotEqual
        }
        radioGroupLayoutView.strategy = RadioGroupLayoutHorizontalStrategy(styleHolder.defaultHorizontalPadding, value)
    }

    override var readOnly: Boolean by delegateNotEqual(false) { value ->
        radioGroupLayoutView.isEnabled = !value
    }

    override var selectedKey: String = NO_ID
        set(value) {
            if (field == value) {
                return
            }
            itemViews[field]?.view?.changeSelected(false)
            field = value
            itemViews[value]?.let {
                it.view.changeSelected(true)
                doIfChildExist(it.model) { childId ->
                    itemViews[childId]?.view?.isSelected = true
                    field = childId
                }
            }
            onSelectedKeyChanged?.invoke(field)
        }

    override var orientation: SbisRadioGroupOrientation by delegateNotEqual(
        SbisRadioGroupOrientation.VERTICAL
    ) { value ->
        radioGroupLayoutView.strategy = value.toLayoutStrategy()
        itemViews[selectedKey]?.let {
            it.view.changeSelected(isSelected = false, isHierarchy = true)
            it.view.changeSelected(isSelected = true)
            doIfChildExist(it.model) { childId ->
                selectedKey = childId
            }
        }
    }

    override var validationStatus: SbisRadioGroupValidationStatus by delegateNotEqual(
        SbisRadioGroupValidationStatus.VALID
    ) { value ->
        radioGroupLayoutView.validationStatus = value
    }

    override var onSelectedKeyChanged: ((String) -> Unit)? = null

    /** Входная точка для инициализации компонента. Загрузка стилей и сохранение экземпляра [SbisRadioGroupView]. */
    fun attach(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int,
        radioGroupView: SbisRadioGroupView
    ) {
        this.radioGroupView = radioGroupView
        styleHolder.loadStyle(context, attrs, defStyleAttr, defStyleRes)
    }

    private inline fun doIfChildExist(model: SbisRadioGroupItem, action: (childId: String) -> Unit) {
        if (orientation == SbisRadioGroupOrientation.HORIZONTAL) return
        val firstChildId = model.children.firstOrNull()?.id?.trim() ?: return
        action(firstChildId)
    }

    private fun SbisRadioGroupOrientation.toLayoutStrategy() = when (this) {
        SbisRadioGroupOrientation.VERTICAL -> RadioGroupLayoutVerticalStrategy(styleHolder::hierarchyPadding)
        SbisRadioGroupOrientation.HORIZONTAL -> RadioGroupLayoutHorizontalStrategy(
            styleHolder.defaultHorizontalPadding,
            multiline
        )
    }

    private tailrec fun SbisRadioGroupItemView.changeSelected(
        isSelected: Boolean,
        isHierarchy: Boolean = orientation == SbisRadioGroupOrientation.VERTICAL
    ) {
        this.isSelected = isSelected
        if (!isHierarchy) return
        parentItem?.changeSelected(isSelected)
    }
}