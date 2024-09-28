package ru.tensor.sbis.design.cloud_view.content.signing

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.RxView
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.buttons.base.models.state.SbisButtonState
import ru.tensor.sbis.design.buttons.base.models.style.*
import ru.tensor.sbis.design.buttons.base.models.title.SbisButtonTitle
import ru.tensor.sbis.design.buttons.base.models.title.SbisButtonTitleSize
import ru.tensor.sbis.design.buttons.button.models.SbisButtonModel
import ru.tensor.sbis.design.buttons.button.models.SbisButtonSize
import ru.tensor.sbis.design.cloud_view.R
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import java.util.concurrent.TimeUnit
import ru.tensor.sbis.design.R as RDesign

/**
 * Кнопки принятия и отклонения подписи
 *
 * @author vv.chekurda
 */
@SuppressWarnings("CheckResult")
internal class SigningButtonsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private val rejectButton: SbisButton = SbisButton(context).apply {
        id = R.id.cloud_view_message_block_signing_reject_button_id
        size = SbisButtonSize.S
        style = DefaultButtonStyle
        model = SbisButtonModel(
            title = SbisButtonTitle(
                text = context.getText(R.string.cloud_view_action_reject_signature),
                // Размер должен быть Xs, но SbisButtonTitleSize.M - это FontSize.XS.
                size = SbisButtonTitleSize.M
            )
        )
    }
    private val acceptButton: SbisButton = SbisButton(context).apply {
        id = R.id.cloud_view_message_block_signing_accept_button_id
        size = SbisButtonSize.S
        style = PrimaryButtonStyle
        model = SbisButtonModel(
            title = SbisButtonTitle(
                text = context.getText(R.string.cloud_view_action_accept_signature),
                size = SbisButtonTitleSize.M
            ),
        )
    }
    private var signingActionListener: SigningActionListener? = null
    private val buttonsSpaceSize = resources.getDimensionPixelOffset(RDesign.dimen.design_signing_actions_buttons_space)

    /** @SelfDocumented */
    fun setButtonClickListener(actionListener: SigningActionListener?) {
        signingActionListener = actionListener
    }

    companion object {
        const val ACTION_TIMEOUT_MILLIS = 1000
    }

    init {
        addView(rejectButton, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        addView(acceptButton, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        RxView.clicks(acceptButton).throttleFirst(ACTION_TIMEOUT_MILLIS.toLong(), TimeUnit.MILLISECONDS).subscribe {
            signingActionListener?.onAcceptClicked()
        }
        RxView.clicks(rejectButton).throttleFirst(ACTION_TIMEOUT_MILLIS.toLong(), TimeUnit.MILLISECONDS).subscribe {
            signingActionListener?.onDeclineClicked()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val buttonSize = (MeasureSpec.getSize(widthMeasureSpec) - buttonsSpaceSize) / 2
        measureChild(acceptButton, MeasureSpecUtils.makeExactlySpec(buttonSize), heightMeasureSpec)
        measureChild(rejectButton, MeasureSpecUtils.makeExactlySpec(buttonSize), heightMeasureSpec)
        setMeasuredDimension(
            MeasureSpecUtils.makeExactlySpec(buttonSize * 2 + buttonsSpaceSize),
            MeasureSpecUtils.makeExactlySpec(rejectButton.measuredHeight)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        rejectButton.layout(0, 0, rejectButton.measuredWidth, rejectButton.measuredHeight)
        val dx = rejectButton.measuredWidth + buttonsSpaceSize
        acceptButton.layout(dx, 0, dx + acceptButton.measuredWidth, acceptButton.measuredHeight)
    }

    /**
     * Метод отображения/скрытия прогресс-бара у кнопки отклонения подписи
     *
     * @param show - true, если нужно отобразить
     */
    fun showRejectProgress(show: Boolean) {
        rejectButton.isClickable = !show

        if (show) {
            rejectButton.state = SbisButtonState.IN_PROGRESS
        } else {
            rejectButton.state = SbisButtonState.ENABLED
        }
    }
}