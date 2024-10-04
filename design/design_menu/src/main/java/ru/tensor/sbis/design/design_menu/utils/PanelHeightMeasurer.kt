package ru.tensor.sbis.design.design_menu.utils

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import ru.tensor.sbis.design.design_dialogs.R
import ru.tensor.sbis.design.design_menu.SbisMenu
import ru.tensor.sbis.design.design_menu.model.MenuSelectionStyle
import ru.tensor.sbis.design.design_menu.viewholders.MenuAdapter
import ru.tensor.sbis.design.theme.global_variables.InlineHeight
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanel
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanelPeekHeight
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableDelegateImpl.PeekHeightParams
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableDelegateImpl.PeekHeightType

/**
 * Методы предварительного расчёта максимальной высоты [шторки][MovablePanel]. Необходимы для корректного расчёта высоты
 * шторки в [максимальном подъёме][PeekHeightType.EXPANDED] по контенту.
 *
 * Измерения должны быть произведены и переданы при настройке шторки, именно в составе [параметров][PeekHeightParams] в
 * конструкторе класса [MovablePanelPeekHeight.Absolute]. Тогда шторка будет иметь минимальный размер, равный высоте
 * элементов управления шторки + header.height + footer.height. Высота [RecyclerView] с элементами меню будет
 * подстраиваться под доступную высоту в шторке в диапазоне [0px .. match_parent].
 *
 * Если выполнять расчёт в методе [Fragment.onCreateView], то минимальная высота шторки будет рассчитываться
 * некорректно. Тогда при сжатии высоты шторки, [RecyclerView] будет сжиматься не до [0px] а до некоторого конечного
 * значения. Подвал, в свою очередь, будучи прижатым к низу [RecyclerView], при минимальном положении шторки, будет
 * уезжать вниз за пределы видимости экрана.
 *
 * @author ra.geraskin
 */
internal fun measureMenuInPanelHeight(menu: SbisMenu, context: Context): Int = with(context.resources) {
    // Высоты контента меню шторки.
    val listHeight = measureMenuItemListHeight(menu, context)
    val headerHeight = measureHeaderHeight(menu, context)
    val footerHeight = measureFooterHeight(menu, FrameLayout(context))

    // Высоты view-элементов управления шторкой. Вынужденный шаг, т.к. расчёт производится до отображения шторки.
    val panelInsetHeight = getDimensionPixelSize(R.dimen.movable_panel_inset_view_default_height)
    val panelGripHeight = getDimensionPixelSize(R.dimen.movable_panel_grip_height)
    val panelGripMarginTop = getDimensionPixelSize(R.dimen.movable_panel_layout_offset_6)
    val panelGripMarginBottom = getDimensionPixelSize(R.dimen.movable_panel_layout_offset_8)

    return listHeight +
        headerHeight +
        footerHeight +
        panelInsetHeight +
        panelGripHeight +
        panelGripMarginTop +
        panelGripMarginBottom
}

/** @SelfDocumented */
private fun measureHeaderHeight(menu: SbisMenu, context: Context) =
    if (menu.needShowTitle && menu.title != null) InlineHeight.M.getDimenPx(context) else 0

/** @SelfDocumented */
private fun measureFooterHeight(menu: SbisMenu, container: ViewGroup): Int {
    val footer = menu.footer ?: return 0
    val footerView = footer(container.context, container)
    if (footerView.layoutParams.height > 0) return footerView.layoutParams.height
    footerView.measure(MeasureSpecUtils.makeUnspecifiedSpec(), MeasureSpecUtils.makeUnspecifiedSpec())
    return footerView.measuredHeight
}

/** @SelfDocumented */
private fun measureMenuItemListHeight(menu: SbisMenu, context: Context): Int = MenuAdapter(
    hideDefaultDividers = menu.hideDefaultDividers,
    selectionEnabled = menu.selectionEnabled,
    hasTitle = menu.title != null,
    styleHolder = SbisMenuStyleHolder.createStyleHolderForPanel(context, MenuSelectionStyle.MARKER),
    minItemWidth = ViewGroup.LayoutParams.MATCH_PARENT,
    containerType = ContainerType.PANEL,
    twoLinesItemsTitle = menu.twoLinesItemsTitle
).apply {
    setItems(menu.children, context)
}.measureFullMenuHeightForPanel(context)