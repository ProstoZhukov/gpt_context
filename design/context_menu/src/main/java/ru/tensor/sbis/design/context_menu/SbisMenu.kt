package ru.tensor.sbis.design.context_menu

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.DimenRes
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.container.DimType
import ru.tensor.sbis.design.container.createViewContainer
import ru.tensor.sbis.design.container.locator.*
import ru.tensor.sbis.design.context_menu.utils.CheckboxIcon
import ru.tensor.sbis.design.context_menu.utils.SbisMenuStyleHolder
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.theme.res.SbisColor
import timber.log.Timber
import kotlin.math.max

private const val SCROLL_DIRECTION_UP = -1
private const val SCROLL_DIRECTION_DOWN = 1

/**
 * Компонент Сбис-меню.
 *
 * [Макет](http://axure.tensor.ru/MobileStandart8/#p=меню&g=1)
 *
 * @author ma.kolpakov
 */
class SbisMenu(
    title: String? = null,
    image: SbisMobileIcon.Icon? = null,
    imageColor: SbisColor = SbisColor.NotSpecified,
    destructive: Boolean = false,
    /** Вид иконки. */
    val stateOnIcon: CheckboxIcon = CheckboxIcon.CHECK,
    private val needShowTitle: Boolean = true,
    private val hideDefaultDividers: Boolean = false,
    /** @SelfDocumented */
    val children: Iterable<Item>,
    imageAlignment: HorizontalPosition = HorizontalPosition.RIGHT
) : BaseItem(
    title?.let { PlatformSbisString.Value(it) },
    image = image,
    imageColor = imageColor,
    imageAlignment = imageAlignment,
    destructive = destructive,
) {

    private val menuCloseListeners: MutableList<(() -> Unit)> = mutableListOf()

    private val styleHolder = SbisMenuStyleHolder()

    /** Слушатель для отображения подменю. */
    internal var showSubMenuListener: ((SbisMenu) -> Unit)? = null

    /** Создать view меню. */
    fun createMenuView(context: Context, container: ViewGroup?, @DimenRes maxWidthRes: Int? = null): View {
        styleHolder.loadStyle(context, stateOnIcon, maxWidthRes)
        val recyclerView: RecyclerView
        val root: View
        val minWidth = if (title != null && needShowTitle) {
            root = LayoutInflater.from(context).inflate(R.layout.context_menu_title, container, false)
            val titleContainer = root.findViewById<LinearLayout>(R.id.context_menu_title_container)
            val tileView = root.findViewById<SbisTextView>(R.id.context_menu_title)
            val titleImage = root.findViewById<SbisTextView>(R.id.context_menu_title_image)
            tileView.text = title.getString(context).uppercase()

            recyclerView = root.findViewById(R.id.context_menu_recycler)
            titleImage.isVisible = image != null

            image?.let {
                titleImage.text = it.character.toString()
            }

            (
                titleContainer.paddingStart +
                    titleImage.measureText(titleImage.text) +
                    context.resources.getDimensionPixelSize(R.dimen.context_menu_icon_margin_start) +
                    tileView.measureText(title.getString(context).uppercase()) +
                    Offset.L.getDimenPx(context)
                ).toInt().coerceIn(styleHolder.minItemWidth..styleHolder.maxItemWidth)
        } else {
            root = LayoutInflater.from(context).inflate(R.layout.context_menu, container, false)
            recyclerView = root.findViewById(R.id.context_menu_recycler)
            0
        }
        val menuAdapter = MenuAdapter(hideDefaultDividers, title != null && needShowTitle, styleHolder, minWidth)
        recyclerView.adapter = menuAdapter
        menuAdapter.setItems(children, root.context)
        if (title != null && needShowTitle) {
            // root.layoutParams будут null, если container == null, так работает LayoutInflater
            val params = root.layoutParams ?: ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            root.layoutParams = params.apply {
                width = max(
                    minWidth,
                    menuAdapter.maxItemWidth
                        ?.coerceIn(styleHolder.minItemWidth..styleHolder.maxItemWidth) ?: 0
                )
            }
        }
        menuAdapter.clickListener = { item ->
            if (item is SbisMenu) {
                showSubmenu(item)
            } else {
                item.handler?.invoke()
            }
            closeMenu(context)
        }
        return root
    }

    /** Добавить слушатель закрытия меню. */
    fun addCloseListener(listener: () -> Unit) {
        menuCloseListeners.add(listener)
    }

    /** Удалить слушатель закрытия меню. */
    fun removeCloseListener(listener: () -> Unit) {
        menuCloseListeners.remove(listener)
    }

    /** Закрыть меню. */
    fun closeMenu(context: Context) {
        try {
            menuCloseListeners.forEach { it.invoke() }
            menuCloseListeners.clear()
        } catch (e: ConcurrentModificationException) {
            val stringBuffer = StringBuffer()
            children.forEach { stringBuffer.appendLine((it as? BaseItem)?.title?.getString(context)) }
            Timber.d("Exception on close menu with items [\n $stringBuffer \n]")
        }
    }

    private fun showSubmenu(menu: SbisMenu) {
        showSubMenuListener?.invoke(menu)
        showSubMenuListener = null
    }
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
) {
    val menuContainer = createViewContainer(SbisMenuContentCreator(this, customWidth), cutoutBounds).apply {
        isAnimated = true
    }
    menuContainer.dimType = dimType
    menuContainer.show(
        fragmentManager,
        AnchorHorizontalLocator(
            HorizontalAlignment.CENTER,
        ).apply { anchorView = anchor },
        AnchorVerticalLocator(
            VerticalAlignment.BOTTOM,
            force = false,
            offsetRes = R.dimen.context_menu_anchor_margin
        ).apply { anchorView = anchor }
    )
}

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
) {
    val menuContainer = createViewContainer(SbisMenuContentCreator(this, customWidth), cutoutBounds).apply {
        isAnimated = true
    }
    menuContainer.dimType = dimType
    menuContainer.show(
        fragmentManager,
        ScreenHorizontalLocator(
            screenHorizontalAlignment
        ),
        AnchorVerticalLocator(
            VerticalAlignment.BOTTOM,
            force = false,
            offsetRes = R.dimen.context_menu_anchor_margin
        ).apply { anchorView = anchor }
    )
}

/**
 * Универсальный метод отображения меню. Поведение меню настраивается с помощью [ScreenLocator] [AnchorLocator].
 */
fun SbisMenu.showMenuWithLocators(
    fragmentManager: FragmentManager,
    verticalLocator: VerticalLocator,
    horizontalLocator: HorizontalLocator,
    dimType: DimType = DimType.SOLID,
    cutoutBounds: Rect? = null,
    customWidth: Int? = null
) {
    val menuContainer = createViewContainer(SbisMenuContentCreator(this, customWidth), cutoutBounds).apply {
        isAnimated = true
    }
    menuContainer.dimType = dimType
    menuContainer.show(fragmentManager, horizontalLocator, verticalLocator)
}

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
) {
    val menuContainer = createViewContainer(SbisMenuContentCreator(this, customWidth), cutoutBounds).apply {
        isAnimated = true
    }
    menuContainer.dimType = dimType
    menuContainer.show(
        fragmentManager,
        TagAnchorHorizontalLocator(horizontalLocator, anchorTag, parentTag),
        TagAnchorVerticalLocator(verticalLocator, anchorTag, parentTag)
    )
}

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
    val menuContainer = createViewContainer(SbisMenuContentCreator(this), cutoutBounds).apply {
        isAnimated = true
    }
    menuContainer.dimType = dimType
    val anchorVerticalLocator = AnchorVerticalLocator(
        VerticalAlignment.BOTTOM,
        force = false,
        innerPosition = false,
        offsetRes = R.dimen.context_menu_anchor_margin
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
        R.dimen.context_menu_anchor_margin
    )

    menuContainer.setOnDismissListener { onDismissListener?.invoke() }

    menuContainer.show(
        fragmentManager,
        anchorHorizontalLocator,
        anchorVerticalLocator
    )
}