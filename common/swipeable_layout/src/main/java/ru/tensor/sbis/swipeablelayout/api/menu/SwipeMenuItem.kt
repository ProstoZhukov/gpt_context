package ru.tensor.sbis.swipeablelayout.api.menu

import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.swipeable_layout.R
import ru.tensor.sbis.swipeablelayout.MenuItem
import ru.tensor.sbis.swipeablelayout.swipeablevm.SwipeMenuItemVm

/**
 * Модель элемента свайп-меню.
 *
 * @author us.bessonov
 */
sealed class SwipeMenuItem(var clickAction: () -> Unit) : MenuItem {

    /** @SelfDocumented */
    internal abstract val size: ItemSize

    override var id: Int = View.NO_ID

    /**
     * Если задано значение `true`, то клик по пункту должен инициировать закрытие меню, а обработчик ([clickAction])
     * вызваться лишь по окончании анимации закрытия.
     * Следует использовать, чтобы избежать подтормаживания анимации закрытия при выполнении операции по клику.
     * Необходимость использования этого флага - повод задуматься об оптимизации тяжёлой операции по клику (обычно,
     * открытия какого-то экрана).
     */
    var isClickPostponedUntilMenuClosed = false

    /** @SelfDocumented */
    var isFocusable = true

    /**
     * Альтернативный обработчик кликов, который, в отличие от [clickAction], позволяет получить доступ ко [View]
     * пункта меню, по которому происходит клик.
     */
    var viewClickAction: ((view: View) -> Unit)? = null

    /** @SelfDocumented */
    internal abstract fun toItemVm(context: Context): SwipeMenuItemVm

    companion object {
        /** @SelfDocumented */
        internal val DEFAULT_ITEM_LAYOUT_RES = R.layout.swipeable_layout_default_menu_item
    }
}

/**
 * Элемент заглушка
 */
class StubItem : SwipeMenuItem({}) {

    override val size = ItemSize.WRAP

    override fun toItemVm(context: Context) = SwipeMenuItemVm(id, clickAction, viewClickAction, Color.MAGENTA)
}

/**
 * Элемент с иконкой.
 */
class IconItem(
    val icon: SwipeIcon, val style: SwipeItemStyle, clickAction: () -> Unit = { }
) : SwipeMenuItem(clickAction) {

    override val size = ItemSize.SMALL

    /** @SelfDocumented */
    var autotestsText: String = ""

    override fun toItemVm(context: Context) = SwipeMenuItemVm(
        id,
        clickAction,
        viewClickAction,
        style.getBackgroundColor(context),
        context.getThemeColorInt(R.attr.SwipeableLayout_iconTextColor),
        icon.getIcon(context.resources),
        isLabelVisible = false,
        isClickPostponedUntilMenuClosed = isClickPostponedUntilMenuClosed,
        autotestsText = autotestsText,
        isFocusable = isFocusable
    )
}

/**
 * Элемент с иконкой и подписью снизу.
 */
class IconWithLabelItem private constructor(
    val icon: SwipeIcon,
    val style: SwipeItemStyle,
    clickAction: () -> Unit,
    private val label: SwipeText
) : SwipeMenuItem(clickAction) {

    override val size = ItemSize.LARGE

    var isLabelSingleLine = true

    constructor(
        icon: SwipeIcon, @StringRes label: Int, style: SwipeItemStyle, clickAction: () -> Unit = { }
    ) : this(
        icon, style, clickAction, label = TextRes(label)
    )

    constructor(icon: SwipeIcon, label: String, style: SwipeItemStyle, clickAction: () -> Unit = { }) : this(
        icon, style, clickAction, label = RawText(label)
    )

    override fun toItemVm(context: Context) = SwipeMenuItemVm(
        id,
        clickAction,
        viewClickAction,
        style.getBackgroundColor(context),
        context.getThemeColorInt(R.attr.SwipeableLayout_iconTextColor),
        icon.getIcon(context.resources),
        label = label.getText(context),
        isLabelSingleLine = isLabelSingleLine,
        isClickPostponedUntilMenuClosed = isClickPostponedUntilMenuClosed,
        isFocusable = isFocusable
    )
}

/**
 * Элемент, аналогичный [IconWithLabelItem], но вместо иконки используется текстовый символ (например, цифра).
 */
class SymbolWithLabelItem private constructor(
    symbol: String,
    val style: SwipeItemStyle,
    clickAction: () -> Unit,
    private val label: SwipeText
) : SwipeMenuItem(clickAction) {

    override val size = ItemSize.LARGE

    private val symbolLiveData = MutableLiveData(symbol)

    constructor(
        symbol: Char, @StringRes label: Int, style: SwipeItemStyle, clickAction: () -> Unit = { }
    ) : this(
        symbol.toString(), style, clickAction, label = TextRes(label)
    )

    constructor(symbol: Char, label: String, style: SwipeItemStyle, clickAction: () -> Unit = { }) : this(
        symbol.toString(), style, clickAction, label = RawText(label)
    )

    /** @SelfDocumented */
    fun update(symbol: Char) {
        symbolLiveData.value = symbol.toString()
    }

    override fun toItemVm(context: Context) = SwipeMenuItemVm(
        id,
        clickAction,
        viewClickAction,
        style.getBackgroundColor(context),
        context.getThemeColorInt(R.attr.SwipeableLayout_iconTextColor),
        symbol = symbolLiveData,
        label = label.getText(context),
        isClickPostponedUntilMenuClosed = isClickPostponedUntilMenuClosed,
        isFocusable = isFocusable
    )
}

/**
 * Элемент, аналогичный [IconItem], но вместо иконки используется текстовый символ (например, цифра).
 */
class SymbolItem(
    private val symbol: Char,
    val style: SwipeItemStyle,
    clickAction: () -> Unit = { }
) : SwipeMenuItem(clickAction) {

    override val size = ItemSize.SMALL

    override fun toItemVm(context: Context) = SwipeMenuItemVm(
        id,
        clickAction,
        viewClickAction,
        style.getBackgroundColor(context),
        context.getThemeColorInt(R.attr.SwipeableLayout_iconTextColor),
        symbol = MutableLiveData(symbol.toString()),
        isClickPostponedUntilMenuClosed = isClickPostponedUntilMenuClosed,
        isFocusable = isFocusable
    )
}

/**
 * Элемент с текстом.
 */
class TextItem private constructor(
    val style: SwipeTextItemStyle,
    clickAction: () -> Unit,
    private val label: SwipeText
) : SwipeMenuItem(clickAction) {

    override val size = ItemSize.WRAP

    constructor(@StringRes label: Int, style: SwipeTextItemStyle, clickAction: () -> Unit = { }) : this(
        style, clickAction, label = TextRes(label)
    )

    constructor(label: String, style: SwipeTextItemStyle, clickAction: () -> Unit = { }) : this(
        style, clickAction, label = RawText(label)
    )

    override fun toItemVm(context: Context) = SwipeMenuItemVm(
        id,
        clickAction,
        viewClickAction,
        style.getBackgroundColor(context),
        label = label.getText(context),
        isClickPostponedUntilMenuClosed = isClickPostponedUntilMenuClosed,
        isFocusable = isFocusable
    )

    companion object {
        /** @SelfDocumented */
        internal val ITEM_LAYOUT_RES = R.layout.swipeable_layout_text_menu_item
    }
}
