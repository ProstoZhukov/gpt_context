package ru.tensor.sbis.design.design_menu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DimenRes
import androidx.core.view.isVisible
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.design_menu.api.BaseMenuItem
import ru.tensor.sbis.design.design_menu.api.EMPTY_STRING
import ru.tensor.sbis.design.design_menu.api.MenuItem
import ru.tensor.sbis.design.design_menu.api.MenuItemClickListener
import ru.tensor.sbis.design.design_menu.databinding.MenuInContainerBinding
import ru.tensor.sbis.design.design_menu.model.MenuItemSettings
import ru.tensor.sbis.design.design_menu.model.MenuSelectionStyle
import ru.tensor.sbis.design.design_menu.utils.ContainerType
import ru.tensor.sbis.design.design_menu.utils.SbisMenuStyleHolder
import ru.tensor.sbis.design.design_menu.viewholders.MenuAdapter
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.global_variables.Offset
import timber.log.Timber
import kotlin.math.max

/**
 * Компонент Сбис-меню.
 *
 * [Макет](https://www.figma.com/proto/VN4mmF4SyNlVItzbIz5H2u/%E2%9C%94%EF%B8%8F-%D0%9C%D0%B5%D0%BD%D1%8E?page-id=10359%3A21991&node-id=58233-101110&t=vpZtmG0evMrEbNmN-0&scaling=min-zoom&starting-point-node-id=58233%3A101110&hide-ui=1)
 *
 * @param children Список дочерних элементов меню.
 * @param title Заголовок элемента Меню.
 * @param icon Иконка элемента Меню.
 * @param subTitle Комментарий элемента Меню.
 * @param settings Настройки элемента Меню.
 * @param handler Callback нажатия на элемент. (не работает в шторке, используй [MenuItemClickListener]])
 * @param footer Прикладной контент в подвале.
 * @param selectionStyle Стиль маркера при единичном выборе.
 * @param hideDefaultDividers Прятать ли стандартные тонкие разделители между элементами меню.
 * @param selectionEnabled Включение режима единичного выбора в данном меню.
 * @param needShowTitle Отображать ли заголовок меню в раскрытом виде.
 * @param twoLinesItemsTitle Если заголовок не помещается в одну строку, он переносится на вторую строку.
 *
 * @author ra.geraskin
 */
@Parcelize
class SbisMenu(
    val children: List<MenuItem>,
    override val title: String? = null,
    override val icon: SbisMobileIcon.Icon? = null,
    override val subTitle: String? = null,
    override val settings: MenuItemSettings = MenuItemSettings(),
    override var handler: (() -> Unit)? = null,
    val footer: ((Context, ViewGroup) -> View)? = null,
    val selectionStyle: MenuSelectionStyle = MenuSelectionStyle.MARKER,
    val hideDefaultDividers: Boolean = true,
    val selectionEnabled: Boolean = false,
    val needShowTitle: Boolean = true,
    val twoLinesItemsTitle: Boolean = false,
    override val id: String = EMPTY_STRING
) : BaseMenuItem(
    title,
    icon,
    subTitle,
    settings,
    handler,
    id = id
) {

    private lateinit var styleHolder: SbisMenuStyleHolder

    private val menuCloseListeners: MutableList<(() -> Unit)> = mutableListOf()

    /** Слушатель для отображения подменю. */
    internal var showSubMenuListener: ((SbisMenu) -> Unit)? = null

    /**
     * public метод из общего API
     */
    fun createView(context: Context, container: ViewGroup?, @DimenRes maxWidthRes: Int? = null): View {
        val binding = MenuInContainerBinding.inflate(LayoutInflater.from(context), container, false)
        return with(binding) {
            styleHolder = SbisMenuStyleHolder.createStyleHolderForContainer(context, selectionStyle, maxWidthRes)

            footer?.let {
                menuFooterContainer.visibility = View.VISIBLE
                menuFooterContainer.addView(it(context, menuFooterContainer))
            }
            var minWidth = 0
            if (title != null && needShowTitle) {
                menuTitleContainer.visibility = View.VISIBLE
                menuTitle.text = title.uppercase()
                menuTitleImage.isVisible = icon != null

                icon?.let {
                    menuTitleImage.text = it.character.toString()
                }

                minWidth = measureMinWidth(menuTitleContainer, menuTitleImage, menuTitle, title, styleHolder)
            }
            val menuAdapter = MenuAdapter(
                hideDefaultDividers = hideDefaultDividers,
                selectionEnabled = selectionEnabled,
                hasTitle = title != null,
                styleHolder = styleHolder,
                minItemWidth = minWidth,
                containerType = ContainerType.CONTAINER,
                twoLinesItemsTitle = twoLinesItemsTitle
            )
            menuRecycler.adapter = menuAdapter
            menuAdapter.setItems(children, root.context)

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

            menuAdapter.clickListener = { item ->
                when {
                    item !is SbisMenu -> item.handler?.invoke()
                    showSubMenuListener != null -> showSubmenu(item)
                    else -> menuAdapter.setItems(item.children, context)
                }
                closeMenu()
            }
            root
        }
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
    fun closeMenu() {
        try {
            menuCloseListeners.forEach { it.invoke() }
            menuCloseListeners.clear()
        } catch (e: ConcurrentModificationException) {
            val stringBuffer = StringBuffer()
            children.forEach { stringBuffer.appendLine((it as? BaseMenuItem)?.title) }
            Timber.d("Exception on close menu with items [\n $stringBuffer \n]")
        }
    }

    private fun showSubmenu(menu: SbisMenu) {
        showSubMenuListener?.invoke(menu)
        showSubMenuListener = null
    }

    private fun measureMinWidth(
        titleContainerView: View,
        titleImageView: SbisTextView,
        titleView: SbisTextView,
        title: String,
        styleHolder: SbisMenuStyleHolder
    ): Int =
        (
            titleContainerView.paddingStart +
                titleImageView.measureText(titleImageView.text) +
                titleContainerView.context.resources.getDimensionPixelSize(R.dimen.menu_icon_margin_start) +
                titleView.measureText(title.uppercase()) +
                Offset.L.getDimenPx(titleContainerView.context)
            ).toInt().coerceIn(styleHolder.minItemWidth..styleHolder.maxItemWidth)
}