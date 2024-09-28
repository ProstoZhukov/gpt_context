package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.action_buttons

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.annotation.AttrRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.communicator.crm.conversation.R
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonTextIcon
import ru.tensor.sbis.design.buttons.base.models.style.PrimaryButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.UnaccentedButtonStyle
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonSize
import ru.tensor.sbis.design.theme.global_variables.Elevation
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.utils.extentions.setLeftPadding

/**
 * Группа кнопок действий над чатом CRM со стороны оператора.
 * Действия: возобновить, забрать, пропустить чат.
 *
 * @author dv.baranov
 */
internal class CRMActionButtons @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
    private val controller: CRMActionButtonsController
) : RelativeLayout(context, attrs, defStyleAttr, defStyleRes),
    CRMActionButtonsAPI by controller {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = 0,
        @StyleRes defStyleRes: Int = 0
    ) : this(
        context,
        attrs,
        defStyleAttr,
        defStyleRes,
        CRMActionButtonsController()
    )

    init {
        controller.initController(this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        placeTakeButton()
        placeReopenButton()
        placeNextButton()
    }

    private fun placeTakeButton() {
        addView(
            createPrimaryActionButton(
                R.id.communicator_crm_take_button,
                R.string.communicator_crm_take_button_text
            ) { listener?.takeConsultation() }
        )
    }

    private fun placeReopenButton() {
        addView(
            createPrimaryActionButton(
                R.id.communicator_crm_reopen_button,
                R.string.communicator_crm_reopen_button_text
            ) { listener?.reopenConsultation() }
        )
    }

    private fun createPrimaryActionButton(
        buttonId: Int,
        @StringRes titleRes: Int,
        listener: OnClickListener
    ) = SbisButton(context).apply {
        id = buttonId
        layoutParams = ViewGroup.LayoutParams(
            context.resources.getDimensionPixelSize(R.dimen.communicator_crm_conversation_take_button_width),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        elevation = Elevation.L.getDimenPx(context).toFloat()
        style = PrimaryButtonStyle
        setTitleRes(titleRes)
        setOnClickListener(listener)
    }

    private fun placeNextButton() {
        addView(
            SbisRoundButton(context).apply {
                id = R.id.communicator_crm_next_button
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setLeftPadding(Offset.M.getDimenPx(context))
                elevation = Elevation.L.getDimenPx(context).toFloat()
                icon = SbisButtonTextIcon(context.getString(ru.tensor.sbis.design.R.string.design_mobile_icon_arrow_right))
                size = SbisRoundButtonSize.M
                style = UnaccentedButtonStyle
            }
        )
    }
}
