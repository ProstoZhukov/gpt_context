package ru.tensor.sbis.design.toolbar

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.R as RDesign

private const val LIGHT_COLOR_SCHEME_ID = 1

/**
 * @author sa.nikitin
 */
@Suppress("unused")
class SbisBottomButtonsView : LinearLayout, View.OnClickListener {

    interface OnBottomButtonClickListener {

        fun onBottomButtonClick(id: Int)
    }

    data class BottomButtonParams @JvmOverloads constructor(
        val id: Int,
        val icon: SbisMobileIcon.Icon,
        @StringRes var textRes: Int? = null,
        val vertical: Boolean = true,
        var isEnabled: Boolean = true
    ) {
        fun icon() = icon.character.toString()
    }

    companion object {

        private fun cancelButtonId() = R.id.toolbar_cancel_button_id

        @JvmOverloads
        fun cancelButtonParams(vertical: Boolean = true) =
            BottomButtonParams(
                cancelButtonId(),
                SbisMobileIcon.Icon.smi_close,
                R.string.toolbar_button_cancel,
                vertical
            )
    }

    private var maxHeight: Int = 0
    private var isTablet: Boolean = false
    private var showTextForTablet: Boolean = true
    private var colorSchemeId: Int = LIGHT_COLOR_SCHEME_ID
    var onBottomButtonClickListener: OnBottomButtonClickListener? = null
    private var topLinePaint: Paint? = null

    //region constructor
    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        if (isInEditMode) return

        maxHeight = resources.getDimensionPixelSize(R.dimen.toolbar_bottom_buttons_view_height)
        isTablet = resources.getBoolean(RDesign.bool.is_tablet)

        if (attrs != null) {
            val attrsArray =
                context.theme.obtainStyledAttributes(attrs, R.styleable.SbisBottomButtonsView, 0, 0)
            try {
                showTextForTablet =
                    attrsArray.getBoolean(R.styleable.SbisBottomButtonsView_showTextForTablet, true)
                colorSchemeId =
                    attrsArray.getInt(R.styleable.SbisBottomButtonsView_viewColorScheme, LIGHT_COLOR_SCHEME_ID)
            } finally {
                attrsArray.recycle()
            }
        }

        setBackgroundColor(getBackgroundColor())
        orientation = HORIZONTAL

        if (colorSchemeId == LIGHT_COLOR_SCHEME_ID) {
            topLinePaint = Paint().also { paint ->
                paint.strokeWidth =
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, resources.displayMetrics)
                paint.style = Paint.Style.FILL
                paint.color = ContextCompat.getColor(context, RDesign.color.palette_color_gray6)
            }
        }
    }
    //endregion

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY))
    }

    @SuppressLint("CanvasSize")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        topLinePaint?.let { topLinePaint ->
            canvas?.drawRect(
                0f,
                0f,
                canvas.width.toFloat(),
                topLinePaint.strokeWidth,
                topLinePaint
            )
        }
    }

    //region OnClickListener
    override fun onClick(view: View) {
        onBottomButtonClickListener?.onBottomButtonClick(view.id)
    }
    //endregion

    //region support
    private fun isLightColorScheme() = colorSchemeId == LIGHT_COLOR_SCHEME_ID

    private fun getBackgroundColor() =
        ContextCompat.getColor(
            context,
            if (isLightColorScheme()) R.color.toolbar_bottom_background_color
            else R.color.toolbar_sbis_bottom_toolbar_backgorund_color
        )

    private fun getTextColor() =
        ContextCompat.getColorStateList(
            context,
            if (isLightColorScheme()) R.color.toolbar_bottom_action_panel_button_light_theme
            else R.color.toolbar_white_and_disabled_grey_text_color
        )
    //endregion

    fun addButtons(vararg params: BottomButtonParams) {
        addButtons(listOf(*params))
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun addButtons(params: List<BottomButtonParams>) {
        val redundantButtonCount = childCount - params.size
        if (redundantButtonCount > 0) {
            removeViews(0, redundantButtonCount)
        }
        for (i in 0..params.lastIndex) {
            if (i < childCount) {
                val buttonView = getChildAt(i)
                val buttonParams = buttonView.getTag(R.id.toolbar_bottom_button_params_tag_key) as? BottomButtonParams
                if (buttonParams != params[i]) {
                    applyButtonParams(buttonView, params[i])
                }
            } else {
                addButton(generateButton(params[i]))
            }
        }
    }

    fun removeAllButtons() {
        removeAllViews()
    }

    private fun addButton(buttonView: View) {
        addView(
            buttonView,
            LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
                1f
            )
        )
    }

    private fun applyButtonParams(buttonView: View, params: BottomButtonParams) {
        buttonView.id = params.id
        buttonView.setTag(R.id.toolbar_bottom_button_params_tag_key, params)

        val buttonTextView: SbisTextView = buttonView.findViewById(R.id.toolbar_panel_button_text)
        val buttonIconView: SbisTextView = buttonView.findViewById(R.id.toolbar_panel_button_icon_text)

        val textColor = getTextColor()
        buttonTextView.setTextColor(textColor)
        if (params.textRes != null && (!params.vertical || !isTablet || showTextForTablet)) {
            buttonTextView.setText(params.textRes!!)
            buttonTextView.visibility = View.VISIBLE
        } else {
            buttonTextView.visibility = View.GONE
        }

        buttonIconView.setTextColor(textColor)
        buttonIconView.text = params.icon()

        setButtonEnabled(buttonView, buttonTextView, buttonIconView, params.isEnabled)
    }

    private fun generateButton(params: BottomButtonParams): View =
        inflateButton(params.vertical).also { buttonView ->
            applyButtonParams(buttonView, params)
            buttonView.setOnClickListener(this)
        }

    private fun inflateButton(vertical: Boolean): View =
        LayoutInflater.from(context).inflate(getButtonLayoutRes(vertical), this, false)

    private fun getButtonLayoutRes(vertical: Boolean) =
        if (vertical) {
            R.layout.toolbar_bottom_panel_button
        } else {
            R.layout.toolbar_bottom_panel_button_horizontal
        }

    fun setButtonEnabled(buttonId: Int, enabled: Boolean) {
        findViewById<View>(buttonId)?.also { setButtonEnabled(it, enabled) }
    }

    fun setButtonText(buttonId: Int, @StringRes textRes: Int) {
        findViewById<View>(buttonId)?.also { buttonView ->
            val params: BottomButtonParams =
                buttonView.getTag(R.id.toolbar_bottom_button_params_tag_key) as BottomButtonParams
            if (params.textRes != textRes) {
                params.textRes = textRes
                buttonView.findViewById<SbisTextView>(R.id.toolbar_panel_button_text).setText(textRes)
            }
        }
    }

    private fun setButtonEnabled(buttonView: View, enabled: Boolean) {
        setButtonEnabled(
            buttonView,
            buttonView.findViewById(R.id.toolbar_panel_button_text),
            buttonView.findViewById(R.id.toolbar_panel_button_icon_text),
            enabled
        )
    }

    private fun setButtonEnabled(buttonView: View, buttonTextView: View, buttonIconView: View, enabled: Boolean) {
        buttonView.isEnabled = enabled
        buttonTextView.isEnabled = enabled
        buttonIconView.isEnabled = enabled
    }
}