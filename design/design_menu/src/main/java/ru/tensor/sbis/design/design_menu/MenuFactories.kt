package ru.tensor.sbis.design.design_menu

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.design.container.DimType
import ru.tensor.sbis.design.container.createViewContainer
import ru.tensor.sbis.design.container.locator.AnchorHorizontalLocator
import ru.tensor.sbis.design.container.locator.AnchorVerticalLocator
import ru.tensor.sbis.design.container.locator.HorizontalAlignment
import ru.tensor.sbis.design.container.locator.HorizontalLocator
import ru.tensor.sbis.design.container.locator.ScreenHorizontalLocator
import ru.tensor.sbis.design.container.locator.TagAnchorHorizontalLocator
import ru.tensor.sbis.design.container.locator.TagAnchorVerticalLocator
import ru.tensor.sbis.design.container.locator.VerticalAlignment
import ru.tensor.sbis.design.container.locator.VerticalLocator
import ru.tensor.sbis.design.container.locator.configureForConversationRegistry
import ru.tensor.sbis.design.design_menu.utils.DialogContainerContentCreator
import ru.tensor.sbis.design.design_menu.utils.MovablePanelContentCreator
import ru.tensor.sbis.design.design_menu.api.MenuItemClickListener
import ru.tensor.sbis.design.design_menu.api.BaseMenuItem
import ru.tensor.sbis.design.design_menu.utils.measureMenuInPanelHeight
import ru.tensor.sbis.design.theme.global_variables.BackgroundColor
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanelPeekHeight
import ru.tensor.sbis.design_dialogs.movablepanel.PanelWidth
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableDelegateImpl
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableFragment

/**
 * Фабричные методы для отображения меню.
 *
 * @author ra.geraskin
 */
/** Тэг открытия [ContainerMovableFragment] фрагмента, для показа меню в шторке. */
const val MENU_COMPONENT_FRAGMENT_TAG = "menu_panel.menu_panel_fragment_tag"

/**
 * Отображение меню в нижней шторке. Для получения ответа от компонента меню расположенного в шторке, вызывающим
 * фрагментом или Activity следует наследоваться от интерфейса [MenuItemClickListener] и реализовать метод
 * [MenuItemClickListener.onClick]. Этот метод будет вызван при клике на элемент меню, с передачей в аргументе метода
 * модели [BaseMenuItem]. Для поиска нужного элемента можно воспользоваться id: String, которое присутствует во всех
 * элементах меню.
 */
fun SbisMenu.showPanel(
    fragmentManager: FragmentManager,
    container: ViewGroup,
    panelWidth: PanelWidth = PanelWidth.CENTER_HALF
) {

    val containerMovableFragment = ContainerMovableFragment.Builder()
        .setPeekHeightParams(
            listOf(
                ContainerMovableDelegateImpl.PeekHeightParams(
                    ContainerMovableDelegateImpl.PeekHeightType.HIDDEN,
                    MovablePanelPeekHeight.Percent(0F)
                ),
                ContainerMovableDelegateImpl.PeekHeightParams(
                    ContainerMovableDelegateImpl.PeekHeightType.INIT,
                    MovablePanelPeekHeight.Percent(0.25F)
                ),
                ContainerMovableDelegateImpl.PeekHeightParams(
                    ContainerMovableDelegateImpl.PeekHeightType.EXPANDED,
                    MovablePanelPeekHeight.Absolute(measureMenuInPanelHeight(this, container.context))
                ),
            )
        )
        .setContentCreator(MovablePanelContentCreator(this))
        .setAutoCloseable(true)
        .setIgnoreLock(true)
        .setPanelWidthForLandscape(panelWidth)
        .setContainerBackgroundColor(BackgroundColor.STACK.getValue(container.context))
        .setDefaultHeaderPaddingEnabled(true)
        .build()

    fragmentManager.beginTransaction()
        .add(container.id, containerMovableFragment, MENU_COMPONENT_FRAGMENT_TAG)
        .addToBackStack(MENU_COMPONENT_FRAGMENT_TAG)
        .commit()
}

/**
 * Универсальный метод отображения меню. Поведение меню настраивается с помощью [ScreenLocator] [AnchorLocator].
 */
fun SbisMenu.showMenu(
    fragmentManager: FragmentManager,
    verticalLocator: VerticalLocator,
    horizontalLocator: HorizontalLocator,
    dimType: DimType = DimType.SOLID,
    cutoutBounds: Rect? = null,
    customWidth: Int? = null
) {
    val menuContainer = createMenuContainer(this, cutoutBounds, customWidth)
    menuContainer.dimType = dimType
    menuContainer.show(fragmentManager, horizontalLocator, verticalLocator)
}

/**
 * Показывает меню относительно вызывающего элемента (Не в списке).
 * С горизонтальным выравниванием по центру вызывающего элемента.
 * С вертикальным выравниванием снизу от вызывающего элемента. Если не поместилось снизу будет показано сверху.
 */
fun SbisMenu.showMenu(
    fragmentManager: FragmentManager,
    anchor: View,
    dimType: DimType = DimType.CUTOUT,
    cutoutBounds: Rect? = null,
    customWidth: Int? = null
) = showMenu(
    fragmentManager,
    AnchorVerticalLocator(
        VerticalAlignment.BOTTOM,
        force = false,
        offsetRes = R.dimen.menu_anchor_margin
    ).apply { anchorView = anchor },
    AnchorHorizontalLocator(
        HorizontalAlignment.CENTER,
    ).apply { anchorView = anchor },
    dimType,
    cutoutBounds,
    customWidth
)

/**
 * Показывает меню относительно вызывающего элемента внутри RecyclerView
 * с выравниванием по горизонтали относительно экрана,
 * по вертикали относительно якоря снизу. Если не вместилось, то сверху.
 */
fun SbisMenu.showMenuWithScreenAlignment(
    fragmentManager: FragmentManager,
    anchor: View,
    screenHorizontalAlignment: HorizontalAlignment,
    dimType: DimType = DimType.CUTOUT,
    cutoutBounds: Rect? = null,
    customWidth: Int? = null
) = showMenu(
    fragmentManager,
    AnchorVerticalLocator(
        VerticalAlignment.BOTTOM,
        force = false,
        offsetRes = R.dimen.menu_anchor_margin
    ).apply { anchorView = anchor },
    ScreenHorizontalLocator(
        screenHorizontalAlignment
    ),
    dimType, cutoutBounds, customWidth
)

/**
 * Универсальный метод отображения меню относительно вью с использованием тегов для определения якоря.
 * Поведение меню настраивается с помощью  [AnchorLocator].
 */
fun SbisMenu.showMenuWithAnchorLocatorsByTag(
    fragmentManager: FragmentManager,
    anchorTag: String,
    parentTag: String? = null,
    verticalLocator: AnchorVerticalLocator = AnchorVerticalLocator(VerticalAlignment.BOTTOM),
    horizontalLocator: AnchorHorizontalLocator = AnchorHorizontalLocator(HorizontalAlignment.CENTER),
    dimType: DimType = DimType.SOLID,
    cutoutBounds: Rect? = null,
    customWidth: Int? = null,
) = showMenu(
    fragmentManager,
    TagAnchorVerticalLocator(verticalLocator, anchorTag, parentTag),
    TagAnchorHorizontalLocator(horizontalLocator, anchorTag, parentTag),
    dimType,
    cutoutBounds,
    customWidth
)

/**
 * Отобразить меню в переписке по особым правилам расположения меню относительно вызывающего элемента.
 */
fun SbisMenu.showMenuForConversationRegistry(
    fragmentManager: FragmentManager,
    anchor: View,
    priorityHorizontalAlignment: HorizontalAlignment,
    dimType: DimType = DimType.CUTOUT,
    cutoutBounds: Rect? = null,
    boundsViewId: Int,
    onDismissListener: (() -> Unit)? = null
) {
    val menuContainer = createMenuContainer(this, cutoutBounds)
    menuContainer.dimType = dimType
    val anchorVerticalLocator = AnchorVerticalLocator(
        VerticalAlignment.BOTTOM,
        force = false,
        innerPosition = false,
        offsetRes = R.dimen.menu_anchor_margin
    ).apply { anchorView = anchor }

    val anchorHorizontalLocator = AnchorHorizontalLocator(
        priorityHorizontalAlignment,
        boundsViewId = boundsViewId,
        force = false,
        innerPosition = true
    ).apply { anchorView = anchor }

    configureForConversationRegistry(
        anchorVerticalLocator,
        anchorHorizontalLocator,
        R.dimen.menu_anchor_margin
    )

    menuContainer.setOnDismissListener { onDismissListener?.invoke() }

    menuContainer.show(
        fragmentManager,
        anchorHorizontalLocator,
        anchorVerticalLocator
    )
}

/** @SelfDocumented */
private fun createMenuContainer(menu: SbisMenu, cutoutBounds: Rect?, customWidth: Int? = null) =
    createViewContainer(DialogContainerContentCreator(menu, customWidth), cutoutBounds).apply {
        isAnimated = true
    }