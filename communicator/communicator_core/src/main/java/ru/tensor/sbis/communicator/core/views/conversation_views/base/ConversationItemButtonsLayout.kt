package ru.tensor.sbis.communicator.core.views.conversation_views.base

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.RelativeLayout.LayoutParams
import ru.tensor.sbis.communicator.common.data.theme.ConversationButton
import ru.tensor.sbis.communicator.common.themes_registry.DialogListActionsListener
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.buttons.SbisLinkButton
import ru.tensor.sbis.design.buttons.base.models.state.SbisButtonState
import ru.tensor.sbis.design.buttons.base.models.style.PrimaryButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.UnaccentedButtonStyle
import ru.tensor.sbis.design.buttons.button.models.SbisButtonSize
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeUnspecifiedSpec
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.utils.extentions.setLeftMargin

/**
 * Layout для отображения области кнопок в ячейке реестра диалогов/каналов.
 *
 * @author da.zhukov
 */
internal class ConversationItemButtonsLayout(
    private val buttonGroup: LinearLayout,
    private val dialogActionsListener: DialogListActionsListener
) {

    private val buttonsPadding = CommunicatorTheme.offsetM

    init {
        buttonGroup.apply {
            layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            gravity = Gravity.END
        }
    }

    /**
     * Список кнопок.
     */
    var data: List<ConversationButton> = emptyList()
        set(value) {
            val isChanged = field != value
            field = value
            if (isChanged) {
                with(buttonGroup) {
                    removeAllViewsInLayout()
                    value.forEach { buttonData ->
                        addView(createButton(context, buttonData, dialogActionsListener))
                    }
                }
            }
        }

    /**
     * Высота разметки области кнопок.
     */
    val height: Int
        get() = buttonGroup.measuredHeight + buttonsPadding

    /**
     * Высота разметки области кнопок.
     */
    val width: Int
        get() = buttonGroup.measuredWidth

    /**
     * Померить всю разметку для определения высоты кнопок ячейки диалога/канала.
     */
    fun measure(availableWidth: Int) {
        buttonGroup.measure(availableWidth, makeUnspecifiedSpec())
    }

    /**
     * Разместить кнопки.
     */
    fun layout(left: Int, top: Int) {
        buttonGroup.layout(left, top + buttonsPadding)
    }

    private fun createButton(
        context: Context,
        buttonData: ConversationButton,
        dialogActionsListener: DialogListActionsListener
    ): View {
        val button = if (buttonData.isOutlineMode) {
            SbisButton(context).apply {
                layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                setLeftMargin(CommunicatorTheme.offsetS)
                size = SbisButtonSize.S
            }
        } else {
            SbisLinkButton(context).apply {
                layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                setLeftMargin(CommunicatorTheme.offsetS)
                size = SbisButtonSize.S
            }
        }
        return button.apply {
            setTitle(buttonData.title)
            style =  if (buttonData.isOutlineMode) PrimaryButtonStyle else UnaccentedButtonStyle
            setOnClickListener {
                button.state = SbisButtonState.IN_PROGRESS
                dialogActionsListener.onButtonViewClick(buttonData)
            }
        }
    }
}