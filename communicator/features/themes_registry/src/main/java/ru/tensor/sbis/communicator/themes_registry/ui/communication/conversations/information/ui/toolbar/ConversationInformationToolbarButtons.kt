package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.toolbar

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import ru.tensor.sbis.communicator.themes_registry.R
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonTextIcon
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonIconStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonResourceStyle
import ru.tensor.sbis.design.buttons.base.models.style.SuccessButtonStyle
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonSize
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import ru.tensor.sbis.design.buttons.R as RButtonsDesign
import ru.tensor.sbis.design.topNavigation.R as RTopNavigation

/**
 * Агрегатор кнопок из правой части тулбара: поиск, фильтр, троеточие(меню опций), сохранение заголовка.
 *
 * @author dv.baranov
 */
internal class ConversationInformationToolbarButtons(
    private val context: Context,
    buttonsClickListeners: ConversationInformationToolbarButtonsClickListeners
) {

    private val searchButton = getDefaultButton(SbisMobileIcon.Icon.smi_search).apply {
        id = R.id.themes_registry_conversation_information_search_button
        setOnClickListener { buttonsClickListeners.onSearchButtonClick() }
    }

    private val moreButton = getDefaultButton(SbisMobileIcon.Icon.smi_navBarMore).apply {
        id = R.id.themes_registry_conversation_information_more_button
        setOnClickListener { buttonsClickListeners.onMoreButtonClick() }
    }

    private val filterButton = getDefaultButton(SbisMobileIcon.Icon.smi_filter).apply {
        id = R.id.themes_registry_conversation_information_filter_button
        setOnClickListener { buttonsClickListeners.onFilterButtonClick() }
    }

    private val doneButton = SbisRoundButton(context).apply {
        id = R.id.themes_registry_conversation_information_done_button
        icon = SbisButtonTextIcon(SbisMobileIcon.Icon.smi_checked.character.toString())
        style = SuccessButtonStyle
        size = SbisRoundButtonSize.S
        setOnClickListener { buttonsClickListeners.onDoneButtonClick() }
    }

    private fun getDefaultButton(icon: SbisMobileIcon.Icon): SbisRoundButton =
        SbisRoundButton(context).apply {
            style = SbisButtonResourceStyle(
                RButtonsDesign.attr.secondarySbisButtonTheme,
                RButtonsDesign.style.SbisButtonDefaultSecondaryTheme,
                0,
                RTopNavigation.style.SbisTopNavigationIconButtonStyle,
                RButtonsDesign.attr.secondarySbisLinkButtonTheme,
                RButtonsDesign.style.SbisLinkButtonDefaultSecondaryTheme
            )
            this.icon = SbisButtonTextIcon(
                icon.character.toString(),
                style = SbisButtonIconStyle(ColorStateList.valueOf(StyleColor.SECONDARY.getIconColor(context)))
            )
            size = SbisRoundButtonSize.S
        }

    val searchWithMore = listOf<View>(searchButton, moreButton)

    val searchWithFilter = listOf<View>(searchButton, filterButton)

    val onlyFilter = listOf<View>(filterButton)

    val onlyDone = listOf<View>(doneButton)

    val onlyMore = listOf<View>(moreButton)
}
