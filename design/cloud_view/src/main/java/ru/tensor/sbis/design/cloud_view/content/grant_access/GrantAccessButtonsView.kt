package ru.tensor.sbis.design.cloud_view.content.grant_access

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.RxView
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.buttons.base.models.state.SbisButtonState
import ru.tensor.sbis.design.buttons.base.models.style.PrimaryButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.UnaccentedButtonStyle
import ru.tensor.sbis.design.buttons.base.models.title.SbisButtonTitle
import ru.tensor.sbis.design.buttons.base.models.title.SbisButtonTitleSize
import ru.tensor.sbis.design.buttons.button.models.SbisButtonModel
import ru.tensor.sbis.design.buttons.button.models.SbisButtonSize
import ru.tensor.sbis.design.cloud_view.R
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import java.util.concurrent.TimeUnit
import ru.tensor.sbis.design.R as RDesign

private const val ACTION_TIMEOUT_MILLIS = 1000

/**
 * Кнопки разрешения и отклонения доступа к файлу
 *
 * @author rv.krohalev
 */
@SuppressWarnings("CheckResult")
internal class GrantAccessButtonsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private val rejectButton: SbisButton = SbisButton(context).apply {
        id = R.id.cloud_view_message_block_grant_access_deny_button_id
        size = SbisButtonSize.S
        style = UnaccentedButtonStyle
        model = SbisButtonModel(
            title = SbisButtonTitle(
                text = context.getText(R.string.cloud_view_action_deny_access),
                size = SbisButtonTitleSize.XS
            )
        )
    }
    private val acceptButton: SbisButton = SbisButton(context).apply {
        id = R.id.cloud_view_message_block_grant_access_allow_button_id
        size = SbisButtonSize.S
        style = PrimaryButtonStyle
        model = SbisButtonModel(
            title = SbisButtonTitle(
                text = context.getText(R.string.cloud_view_action_grant_access),
                size = SbisButtonTitleSize.XS
            )
        )
    }
    private var grantAccessActionListener: GrantAccessActionListener? = null
    private val buttonsSpaceSize =
        resources.getDimensionPixelOffset(RDesign.dimen.design_grant_access_actions_buttons_space)

    /** @SelfDocumented */
    fun setButtonClickListener(actionListener: GrantAccessActionListener?) {
        grantAccessActionListener = actionListener
    }

    init {
        addView(rejectButton, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        addView(acceptButton, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        RxView.clicks(acceptButton).throttleFirst(ACTION_TIMEOUT_MILLIS.toLong(), TimeUnit.MILLISECONDS).subscribe {
            grantAccessActionListener?.onGrantAccessClicked(acceptButton)
        }
        RxView.clicks(rejectButton).throttleFirst(ACTION_TIMEOUT_MILLIS.toLong(), TimeUnit.MILLISECONDS).subscribe {
            grantAccessActionListener?.onDenyAccessClicked()
        }
    }

    /**
     * Метод отображения/скрытия прогресс-бара у кнопки отклонения доступа
     *
     * @param show - true, если нужно отобразить
     */
    fun showRejectProgress(show: Boolean) {
        rejectButton.isClickable = !show
        rejectButton.state = if (show) SbisButtonState.IN_PROGRESS else SbisButtonState.ENABLED
    }

    /**
     * Метод отображения/скрытия прогресс-бара у кнопки разрешения доступа
     *
     * @param show - true, если нужно отобразить
     */
    fun showAcceptProgress(show: Boolean) {
        acceptButton.isClickable = !show
        acceptButton.state = if (show) SbisButtonState.IN_PROGRESS else SbisButtonState.ENABLED
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
}