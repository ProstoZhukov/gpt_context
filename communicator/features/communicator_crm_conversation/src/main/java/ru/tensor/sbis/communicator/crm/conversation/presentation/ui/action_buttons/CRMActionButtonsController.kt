package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.action_buttons

import android.content.Context
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.communication_decl.crm.CRMConsultationCase
import ru.tensor.sbis.communicator.crm.conversation.R
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.action_buttons.ActionButtonType.NEXT
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.action_buttons.ActionButtonType.REOPEN
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.action_buttons.ActionButtonType.TAKE
import ru.tensor.sbis.communication_decl.crm.CRMConsultationOpenParams
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.buttons.SbisRoundButton
import java.util.UUID

/**
 * Контроллер для управления группой кнопок [CRMActionButtons].
 *
 * @author dv.baranov
 */
internal class CRMActionButtonsController : CRMActionButtonsAPI {

    private lateinit var actionButtons: CRMActionButtons

    private val context: Context
        get() = actionButtons.context

    private val needPlaceActionButtonsInCenter: Boolean
        get() = isTablet || DeviceConfigurationUtils.isLandscape(context)

    private val isTablet: Boolean
        get() = DeviceConfigurationUtils.isTablet(context)

    private val nextButton: SbisRoundButton?
        get() = actionButtons.findViewById(R.id.communicator_crm_next_button)

    private val takeButton: SbisButton?
        get() = actionButtons.findViewById(R.id.communicator_crm_take_button)

    private val reopenButton: SbisButton?
        get() = actionButtons.findViewById(R.id.communicator_crm_reopen_button)

    private val types: MutableList<ActionButtonType> = mutableListOf()

    /** @SelfDocumented */
    internal fun initController(view: CRMActionButtons) {
        actionButtons = view
    }

    override var listener: CRMActionButtonsClickListener? = null

    override val isTakeButtonVisible: Boolean
        get() = takeButton?.isVisible == true

    override fun setNextButtonClickListener(uuid: UUID, viewId: UUID) {
        nextButton?.setOnClickListener {
            listener?.openNextConsultation(
                CRMConsultationOpenParams(
                    crmConsultationCase = CRMConsultationCase.Operator(uuid, viewId),
                    needBackButton = !isTablet,
                    isMessagePanelVisible = false
                )
            )
        }
    }

    override fun showActionButton(type: ActionButtonType) {
        if (types.contains(type)) return
        types.add(type)
        updateVisibility()
        updateButtonsLayoutParams()
    }

    override fun hideActionButton(type: ActionButtonType) {
        types.remove(type)
        updateVisibility()
        updateButtonsLayoutParams()
    }

    private fun updateVisibility() {
        actionButtons.isVisible = types.isNotEmpty()
        takeButton?.isVisible = types.contains(TAKE)
        nextButton?.isVisible = types.contains(NEXT)
        reopenButton?.isVisible = types.contains(REOPEN)
    }

    private fun updateButtonsLayoutParams() {
        when {
            types.contains(TAKE) && types.contains(NEXT) -> {
                if (needPlaceActionButtonsInCenter) {
                    placeTakeNextButtonsInCenter()
                } else {
                    placeTakeNextButtonsToEnd()
                }
            }
            types.contains(TAKE) -> placeTakeNextButtonsInCenter()
            types.contains(REOPEN) -> placeReopenButtonInCenter()
            else -> Unit
        }
    }

    private fun placeTakeNextButtonsToEnd() {
        takeButton?.updateLayoutParams<RelativeLayout.LayoutParams> {
            removeRule(RelativeLayout.CENTER_IN_PARENT)
            addRule(RelativeLayout.START_OF, R.id.communicator_crm_next_button)
        }
        nextButton?.updateLayoutParams<RelativeLayout.LayoutParams> {
            removeRule(RelativeLayout.END_OF)
            addRule(RelativeLayout.ALIGN_PARENT_END)
        }
    }

    private fun placeTakeNextButtonsInCenter() {
        takeButton?.updateLayoutParams<RelativeLayout.LayoutParams> {
            removeRule(RelativeLayout.START_OF)
            addRule(RelativeLayout.CENTER_IN_PARENT)
        }
        nextButton?.updateLayoutParams<RelativeLayout.LayoutParams> {
            removeRule(RelativeLayout.ALIGN_PARENT_END)
            addRule(RelativeLayout.END_OF, R.id.communicator_crm_take_button)
        }
    }

    private fun placeReopenButtonInCenter() {
        reopenButton?.updateLayoutParams<RelativeLayout.LayoutParams> {
            addRule(RelativeLayout.CENTER_IN_PARENT)
        }
    }
}
