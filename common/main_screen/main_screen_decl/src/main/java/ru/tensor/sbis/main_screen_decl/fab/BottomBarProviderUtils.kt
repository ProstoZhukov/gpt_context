/**
 * Инструменты для управления плавающими кнопками на главном экране
 *
 * @author us.bessonov
 */
package ru.tensor.sbis.main_screen_decl.fab

import android.graphics.drawable.Drawable
import android.view.View
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.main_screen_decl.env.BottomBarProviderExt

/**
 * Показывает заданные кнопки. Все незадействованные кнопки будут скрыты.
 *
 * @throws [IllegalStateException] если число моделей превышает число доступных кнопок
 */
fun BottomBarProviderExt.addActionButtons(vararg buttons: IconFab) {
    val requiredButtons = buttons.toList()
    val availableButtons = getIconButtons()
    check(requiredButtons.size <= availableButtons.size) {
        "Expected at most ${availableButtons.size} buttons, but there are ${requiredButtons.size} of them"
    }

    val (visibleButtons, hiddenButtons) = availableButtons.partition { it.isVisible() }
    if (visibleButtons.size >= requiredButtons.size) {
        updateButtons(visibleButtons.takeLast(requiredButtons.size), requiredButtons)
        visibleButtons.take(visibleButtons.size - requiredButtons.size).forEach {
            it.hide()
        }
    } else {
        hiddenButtons.takeLast(requiredButtons.size - visibleButtons.size).forEach {
            it.show()
        }
        updateButtons(availableButtons.filter { it.isVisible() }, requiredButtons)
    }
}

/**
 * Возвращает объекты для управления доступными кнопками (за исключением кнопки с датой)
 */
internal fun BottomBarProviderExt.getIconButtons() = listOf(
    Button(
        ::isExtraFabShown,
        ::showExtraFabButton,
        ::hideExtraFabButton,
        ::setExtraFabIcon,
        ::setExtraFabStyle,
        ::setExtraFabClickListener
    ),
    Button(
        ::isExtraFab2Shown,
        ::showExtraFab2Button,
        ::hideExtraFab2Button,
        ::setExtraFab2Icon,
        ::setExtraFab2Style,
        ::setExtraFab2ClickListener
    ),
    Button(
        ::isExtraFab3Shown,
        ::showExtraFab3Button,
        ::hideExtraFab3Button,
        ::setExtraFab3Icon,
        ::setExtraFab3Style,
        ::setExtraFab3ClickListener
    ),
    Button(
        ::isExtraFab4Shown,
        ::showExtraFab4Button,
        ::hideExtraFab4Button,
        ::setExtraFab4Icon,
        ::setExtraFab4Style,
        ::setExtraFab4ClickListener
    ),
    Button(
        ::isNavigationFabShown,
        { swapFabButton(true) },
        { swapFabButton(false) },
        ::setNavigationFabIcon,
        ::setNavigationFabStyle,
        ::setNavigationFabClickListener
    )
)

private fun updateButtons(updated: List<Button>, data: List<IconFab>) {
    updated.zip(data).forEach { (button, data) ->
        with(button) {
            setIcon(data.icon)
            setStyle(data.style)
            setOnClickListener(data.clickListener)
        }
    }
}

/**
 * Кнопка главного экрана
 *
 * @author us.bessonov
 */
internal class Button(
    val isVisible: () -> Boolean,
    val show: () -> Unit,
    val hide: () -> Unit,
    val setIcon: (Drawable?) -> Unit,
    val setStyle: (SbisButtonStyle) -> Unit,
    val setOnClickListener: (View.OnClickListener?) -> Unit,
)