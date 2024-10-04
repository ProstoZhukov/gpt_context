package ru.tensor.sbis.design.topNavigation.util

import android.content.res.ColorStateList
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIconSize
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonTextIcon
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonIconStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonResourceStyle
import ru.tensor.sbis.design.buttons.base.models.style.SecondaryButtonStyle
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonSize
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonType
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationApi
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import ru.tensor.sbis.design.buttons.R as RB
import ru.tensor.sbis.design.topNavigation.R as RT

/**
 * Фабрика создания наиболее используемых кнопок в новой шапке.
 *
 * @author da.zolotarev
 */
object ButtonsFactory {

    /**
     * Создать кнопку-иконку c прозрачным фоном, на основе контекста шапки и [SbisTopNavigationApi.isOldToolbarDesign].
     */
    fun SbisTopNavigationView.createDefaultButton(icon: PlatformSbisString) = SbisRoundButton(currContext).also {
        val oldToolbarIconTextColor = TextColor.DEFAULT.getValue(currContext)
        it.style = if (isOldToolbarDesign) {
            SecondaryButtonStyle
        } else {
            SbisButtonResourceStyle(
                RB.attr.secondarySbisButtonTheme,
                RB.style.SbisButtonDefaultSecondaryTheme,
                0,
                RT.style.SbisTopNavigationIconButtonStyle,
                RB.attr.secondarySbisLinkButtonTheme,
                RB.style.SbisLinkButtonDefaultSecondaryTheme
            )
        }
        it.icon = createDefaultTextIcon(icon, oldToolbarIconTextColor)
        it.type = if (isOldToolbarDesign) SbisRoundButtonType.Transparent else SbisRoundButtonType.Filled
        it.size = SbisRoundButtonSize.S
    }

    /**
     * Создать иконку для размещения в [SbisRoundButton].
     * Необходимо вызывать вручную в случае динамического обновления иконки.
     */
    fun SbisTopNavigationView.createDefaultTextIcon(icon: PlatformSbisString): SbisButtonTextIcon {
        val oldToolbarIconTextColor = TextColor.DEFAULT.getValue(currContext)
        return createDefaultTextIcon(icon, oldToolbarIconTextColor)
    }

    private fun SbisTopNavigationView.createDefaultTextIcon(
        icon: PlatformSbisString,
        oldToolbarIconTextColor: Int
    ): SbisButtonTextIcon {
        val iconTextColor = StyleColor.SECONDARY.getIconColor(currContext)
        return SbisButtonTextIcon(
            style = if (isOldToolbarDesign) {
                SbisButtonIconStyle(ColorStateList.valueOf(oldToolbarIconTextColor))
            } else {
                SbisButtonIconStyle(ColorStateList.valueOf(iconTextColor))
            },
            icon = icon.getCharSequence(currContext),
            size = SbisButtonIconSize.X2L
        )
    }
}