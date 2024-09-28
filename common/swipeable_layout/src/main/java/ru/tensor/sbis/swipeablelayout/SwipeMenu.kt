package ru.tensor.sbis.swipeablelayout

import android.view.View
import android.view.View.*
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.core.content.res.ResourcesCompat
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.swipeable_layout.BR
import ru.tensor.sbis.swipeable_layout.R
import ru.tensor.sbis.swipeablelayout.util.SwipeMenuItemBindingDelegate
import ru.tensor.sbis.swipeablelayout.util.SwipeMenuItemDataBindingDelegate
import timber.log.Timber
import ru.tensor.sbis.design.R as RDesign

private const val MAX_LABEL_CHARACTERS = 9

/** @SelfDocumented */
@ColorRes
val DEFAULT_SWIPE_MENU_DIVIDER_COLOR = RDesign.color.palette_alpha_color_black2

/** @SelfDocumented */
@LayoutRes
val DEFAULT_SWIPE_MENU_ITEM_LAYOUT = R.layout.swipeable_layout_default_menu_item

/**
 * Класс конфигурации свайп-меню
 *
 * @param ITEM_TYPE тип элемента меню
 * @property items элементы меню
 * @property itemLayoutRes макет элемента меню
 * @property itemBindingId id переменной в DataBinding, требуемый при использовании [SwipeMenuItemDataBindingDelegate]
 * @property hasRemoveOption присутствует ли в меню опция удаления (в этом случае поддерживается удаление по смахиванию)
 * @property isLarge большого ли размера элементы меню
 * @property backgroundColor ресурс цвета фона меню
 * @property overlayBackgroundColor ресурс цвета фона вне области меню (по умолчанию соответствует цвету фона меню)
 * @property smoothEdgeColor ресурс цвета градиента на краю меню
 * @property dividerColor ресурс цвета разделителя пунктов меню
 * вычисления ширины пункта меню (на телефоне, или если задан 0, используется реальная ширина экрана, иначе значение
 * ресурса для портретной ориентации)
 * @property selectedItemSmoothEdgeColor параметр неактуален
 * @property isItemLayoutWidthIgnored определяет, необходимо ли игнорировать ширину элементов меню, заданную в
 * [itemLayoutRes], и использовать стандартный размер, в соответствии со значением [isLarge]
 * @property itemBindingDelegate отвечает за привязку данных ко [View] пунктов меню. По умолчанию привязка выполняется
 * посредством DataBinding (id переменной указывается в [itemBindingId])
 * @property itemBackground ресурс фона элемента меню (в т.ч. цвет при нажатии). По умолчанию используется цвет,
 * указанный в макете
 *
 * @author us.bessonov
 */
@Deprecated(
    "Будет удалено по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3",
    ReplaceWith("List<ru.tensor.sbis.swipeablelayout.api.menu.SwipeMenuItem>")
)
abstract class SwipeMenu<ITEM_TYPE : MenuItem>(
    val items: List<ITEM_TYPE>,
    @LayoutRes val itemLayoutRes: Int,
    val itemBindingId: Int,
    val hasRemoveOption: Boolean,
    val isLarge: Boolean,
    @ColorRes val backgroundColor: Int,
    @DrawableRes val itemBackground: Int? = null,
    @ColorRes val overlayBackgroundColor: Int = backgroundColor,
    @ColorRes val smoothEdgeColor: Int,
    @ColorRes val dividerColor: Int,
    @Deprecated("14.04.21 https://online.sbis.ru/opendoc.html?guid=fa23dc8c-9c95-49ed-95be-eb61cb185688") val selectedItemSmoothEdgeColor: Int = ResourcesCompat.ID_NULL,
    val isItemLayoutWidthIgnored: Boolean = true,
    val itemBindingDelegate: SwipeMenuItemBindingDelegate<ITEM_TYPE> = SwipeMenuItemDataBindingDelegate(itemBindingId)
)

/**
 * Стандартная конфигурация свайп-меню
 *
 * @param items элементы меню
 * @param style стиль стандартного меню
 */
@Deprecated(
    "Будет удалено по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3",
    ReplaceWith("List<ru.tensor.sbis.swipeablelayout.api.menu.SwipeMenuItem>")
)
class DefaultSwipeMenu @JvmOverloads constructor(
    items: List<DefaultMenuItem> = emptyList(),
    itemBindingId: Int = BR.viewModel,
    itemBindingDelegate: SwipeMenuItemBindingDelegate<DefaultMenuItem> = SwipeMenuItemDataBindingDelegate(itemBindingId)
) : SwipeMenu<DefaultMenuItem>(
    items = items,
    itemLayoutRes = DEFAULT_SWIPE_MENU_ITEM_LAYOUT,
    itemBindingId = itemBindingId,
    hasRemoveOption = items.any { it.isRemoveOption },
    isLarge = items.any { it.hasLabel },
    backgroundColor = RDesign.color.item_menu_background,
    smoothEdgeColor = RDesign.color.default_swipe_menu_smooth_edge_color,
    dividerColor = DEFAULT_SWIPE_MENU_DIVIDER_COLOR,
    itemBindingDelegate = itemBindingDelegate
)

/**
 * Пункт свайп-меню
 *
 * @property id идентификатор элемента меню, используемый в качестве id корневой [View] элемента (положительное
 * значение, либо [View.NO_ID])
 */
@Deprecated("Будет удалено по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3")
interface MenuItem {
    val id: Int get() = NO_ID
}

/**
 * Релизация пункта меню по умолчанию
 *
 * @property icon иконка меню
 * @property label отображаемое название пункта меню
 * @property clickAction действие по клику
 * @property iconColor цвет иконки
 * @property hasLabel должно ли отображаться название пункта
 * @property isLongLabel `true`, если длина названия не позволяет разместить его на одной строке
 * @property isRemoveOption предназначен ли пункт меню для удаления элемента
 * @property id идентификатор view пункта меню
 * @property isClickPostponedUntilMenuClosed `true` если клик по пункту должен инициировать закрытие меню, а обработчик
 * ([clickAction]) вызваться лишь по окончании анимации закрытия
 * @property colorfulItemIcon Иконка для пункта меню в дизайне с цветным фоном
 *
 * @author us.bessonov
 */
@Deprecated(
    "Будет удалено по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3",
    ReplaceWith("ru.tensor.sbis.swipeablelayout.api.menu.SwipeMenuItem")
)
data class DefaultMenuItem(
    val icon: String = "",
    val label: String? = null,
    val clickAction: Runnable = Runnable { },
    @ColorRes val iconColor: Int = RDesign.color.item_menu_icon_color,
    val hasLabel: Boolean = label != null,
    val isLongLabel: Boolean = false,
    val isRemoveOption: Boolean = false,
    override var id: Int = NO_ID,
    var isClickPostponedUntilMenuClosed: Boolean = false,
    val colorfulItemIcon: ColorfulMenuItemIcon? = null,
    var autotestsText: String? = null
) : MenuItem {

    init {
        label?.let {
            if (!isLongLabel && isLabelTooLong(it)) {
                val exception = IllegalArgumentException(
                    "Длина подписи ($label) на кнопке свайп-меню не должна превышать $MAX_LABEL_CHARACTERS символов," + " или ${MAX_LABEL_CHARACTERS + 1} символов и оканчиваться точкой." + " Выставите флаг isLongLabel для вывода значения в несколько строк," + " либо сократите надпись"
                )
                //в макете чатов есть длинные надписи, требуется решение дизайнеров
                /*if (BuildConfig.DEBUG) {
                    throw exception
                }*/
                Timber.e(exception)
            }
        }
    }

    companion object {

        /**
         * Создаёт пункт свайп-меню с подписью
         *
         * @param label подпись
         * @param icon иконка
         * @param onClick действие по клику
         */
        @JvmStatic
        @JvmOverloads
        @Deprecated(
            "Будет удалено по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3",
            ReplaceWith("ru.tensor.sbis.swipeablelayout.api.menu.IconWithLabelItem")
        )
        fun createWithLabel(
            label: String, icon: SbisMobileIcon.Icon, onClick: Runnable = Runnable { }
        ) = DefaultMenuItem(icon.asString(), label, onClick, isLongLabel = false)

        /**
         * Создаёт пункт свайп-меню с длинной подписью
         *
         * @param label подпись
         * @param icon иконка
         * @param onClick действие по клику
         */
        @JvmStatic
        @JvmOverloads
        @Deprecated(
            "Будет удалено по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3",
            ReplaceWith("ru.tensor.sbis.swipeablelayout.api.menu.IconWithLabelItem")
        )
        fun createWithLongLabel(
            label: String, icon: SbisMobileIcon.Icon, onClick: Runnable = Runnable { }
        ) = DefaultMenuItem(icon.asString(), label, onClick, isLongLabel = true)

        /**
         * Создаёт пункт свайп-меню только с иконкой
         *
         * @param icon иконка
         * @param onClick действие по клику
         */
        @JvmStatic
        @Deprecated(
            "Будет удалено по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3",
            ReplaceWith("ru.tensor.sbis.swipeablelayout.api.menu.IconItem")
        )
        fun createWithNoLabel(
            icon: SbisMobileIcon.Icon, onClick: Runnable
        ) = DefaultMenuItem(icon.asString(), clickAction = onClick)

        /**
         * Создаёт пункт свайп-меню для опции удаления
         *
         * @param onClick действие по клику
         * @param label подпись к иконке, если требуется
         */
        @JvmStatic
        @JvmOverloads
        @Deprecated(
            "Будет удалено по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3",
            ReplaceWith("ru.tensor.sbis.swipeablelayout.api.menu.SwipeMenuItem")
        )
        fun createRemoveOption(
            label: String? = null, onClick: Runnable = Runnable { }
        ) = DefaultMenuItem(
            colorfulItemIcon = DeleteIcon,
            label = label,
            clickAction = onClick,
            iconColor = RDesign.color.red_color_for_delete_icon,
            isRemoveOption = true
        )

        /**
         * @see createWithLabel
         */
        @JvmStatic
        @Deprecated(
            "Будет удалено по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3",
            ReplaceWith("ru.tensor.sbis.swipeablelayout.api.menu.IconWithLabelItem")
        )
        fun createWithLabel(
            label: String, icon: ColorfulMenuItemIcon, onClick: Runnable = Runnable { }
        ) = DefaultMenuItem(colorfulItemIcon = icon, label = label, clickAction = onClick, isLongLabel = false)

        /**
         * @see createWithLongLabel
         */
        @Deprecated(
            "Будет удалено по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3",
            ReplaceWith("ru.tensor.sbis.swipeablelayout.api.menu.IconWithLabelItem")
        )
        fun createWithLongLabel(
            label: String, icon: ColorfulMenuItemIcon, onClick: Runnable = Runnable { }
        ) = DefaultMenuItem(colorfulItemIcon = icon, label = label, clickAction = onClick, isLongLabel = true)

        /**
         * @see createWithNoLabel
         */
        @Deprecated(
            "Будет удалено по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3",
            ReplaceWith("ru.tensor.sbis.swipeablelayout.api.menu.IconItem")
        )
        fun createWithNoLabel(
            icon: ColorfulMenuItemIcon, onClick: Runnable
        ) = DefaultMenuItem(colorfulItemIcon = icon, clickAction = onClick)

        private fun isLabelTooLong(label: String): Boolean {
            return label.length > MAX_LABEL_CHARACTERS + 1 || label.length > MAX_LABEL_CHARACTERS && label.last() != '.'
        }
    }
}

/*** @SelfDocumented */
fun SbisMobileIcon.Icon.asString() = "$character"