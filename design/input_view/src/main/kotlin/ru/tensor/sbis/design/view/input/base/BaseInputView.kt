package ru.tensor.sbis.design.view.input.base

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import androidx.annotation.AttrRes
import androidx.annotation.CallSuper
import androidx.annotation.RequiresApi
import androidx.annotation.StyleRes
import androidx.core.graphics.withTranslation
import androidx.core.view.ViewCompat
import androidx.customview.widget.ExploreByTouchHelper
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import ru.tensor.sbis.android_ext_decl.getParcelableUniversally
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.extentions.getActivity
import ru.tensor.sbis.design.view.input.R
import ru.tensor.sbis.design.view.input.base.api.BaseInputViewApi
import ru.tensor.sbis.design.view.input.base.api.BaseInputViewControllerApi
import kotlin.math.max

/**
 * Базовое поле ввода.
 *
 * [Стандарт](http://axure.tensor.ru/MobileStandart8/#p=%D0%BF%D0%BE%D0%BB%D1%8F_%D0%B2%D0%B2%D0%BE%D0%B4%D0%B0_v2&g=1)
 *
 * @author ps.smirnyh
 */
abstract class BaseInputView internal constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int = R.attr.baseInputViewTheme,
    @StyleRes defStyleRes: Int = R.style.BaseInputViewTheme,
    private val controller: BaseInputViewControllerApi
) : BaseInputViewApi by controller,
    ViewGroup(
        ThemeContextBuilder(context, attrs, defStyleAttr, defStyleRes).build(),
        attrs,
        defStyleAttr,
        defStyleRes
    ) {

    private var drawableStateChanging = false
    private var inputViewAccessibilityDelegate: ExploreByTouchHelper? = null

    private val horizontalPadding
        get() = paddingStart + paddingEnd

    private val verticalPadding
        get() = paddingTop + paddingBottom

    private val underlineBounds = RectF()

    /** Расположение иконки внутри поля ввода. */
    protected val iconBounds = Rect()
        get() = with(controller) {
            iconView.textPaint.getTextBounds(
                iconView.text.toString(),
                0,
                iconView.text.length,
                field
            )
            field
        }

    /**
     * [Paint] для отрисовки линии валидации.
     */
    internal val underlinePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    /** @SelfDocumented */
    internal val inputView = MaskEditText(this.context).apply {
        id = R.id.input_view_internal_field
        isFocusable = true
        isFocusableInTouchMode = true
        background = null
        includeFontPadding = false
        minimumWidth = this@BaseInputView.minimumWidth
    }.also {
        addView(it, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
    }

    init {
        this.setWillNotDraw(false)
        this.setAddStatesFromChildren(true)
    }

    /**
     * Метод аналогичный [EditText.getBaseline] для получения базовой линии.
     */
    override fun getBaseline() = inputView.top + inputView.baseline

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        inputView.visibility = visibility
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent) =
        controller.touchManager.onTouch(this, event) || super.onTouchEvent(event)

    override fun verifyDrawable(who: Drawable): Boolean {
        return isLaidOut &&
            (super.verifyDrawable(who) || (who is CircularProgressDrawable))
    }

    override fun drawableStateChanged() {
        if (drawableStateChanging) return

        drawableStateChanging = true
        super.drawableStateChanged()
        controller.onViewStateChanged(inputView.drawableState)
        drawableStateChanging = false
    }

    final override fun setOnFocusChangeListener(l: OnFocusChangeListener?) {
        controller.focusChangedListener.outer = l
    }

    override fun setOnClickListener(clickListener: OnClickListener?) {
        onFieldClickListener = clickListener
    }

    override fun hasOnClickListeners(): Boolean = onFieldClickListener != null

    override fun setOnTouchListener(touchListener: OnTouchListener?) {
        onFieldTouchListener = touchListener
    }

    override fun clearFocus() {
        super.clearFocus()
        inputView.clearFocus()
    }

    override fun requestFocus(direction: Int, previouslyFocusedRect: Rect?): Boolean {
        return inputView.requestFocus(direction, previouslyFocusedRect)
    }

    final override fun getOnFocusChangeListener() = controller.focusChangedListener.outer

    final override fun setLayoutParams(params: LayoutParams?) {
        params?.height = LayoutParams.WRAP_CONTENT // нельзя менять.
        super.setLayoutParams(params)
    }

    // Нельзя менять т.к. в стандарте изменение высоты поля ввода указано через изменение количества строк.
    final override fun setMinimumHeight(minHeight: Int) = Unit

    final override fun setFocusable(focusable: Boolean) = Unit // нельзя менять.

    final override fun setFocusable(focusable: Int) = Unit // нельзя менять.

    final override fun setFocusableInTouchMode(focusableInTouchMode: Boolean) =
        Unit // нельзя менять.

    final override fun setFocusedByDefault(isFocusedByDefault: Boolean) = Unit // нельзя менять.

    /**
     * Состояния не восстанавливаются при повторяющихся айдишниках внутри разметки input_view, поэтому пришлось
     * переопределить этот метод, чтобы не завязываться на айдишники внутренних вьюшек.
     */
    final override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>?) {
        if (id == NO_ID || !isSaveEnabled) return
        val state = onSaveInstanceState() ?: return
        container?.put(id, state)
    }

    /**
     * Состояния не восстанавливаются при повторяющихся айдишниках внутри разметки input_view, поэтому пришлось
     * переопределить этот метод, чтобы не завязываться на айдишники внутренних вьюшек.
     */
    final override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>?) {
        if (id == NO_ID || !isSaveEnabled) return
        onRestoreInstanceState(container?.get(id))
    }

    @CallSuper
    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(STATE_SUPER, super.onSaveInstanceState())
        bundle.putParcelable(STATE_INPUT_VIEW, inputView.onSaveInstanceState())
        // добавить другие вьюшки если понадобится.
        bundle.putParcelable(STATE_THIS, BaseInputViewState(inputView.isFocused, isExpandedTitle))
        return bundle
    }

    @CallSuper
    override fun onRestoreInstanceState(state: Parcelable?) {
        val bundle = state as? Bundle ?: return
        bundle.classLoader = BaseInputViewState::class.java.classLoader
        super.onRestoreInstanceState(bundle.getParcelableUniversally(STATE_SUPER))
        inputView.onRestoreInstanceState(bundle.getParcelableUniversally(STATE_INPUT_VIEW))
        // добавить другие вьюшки если понадобится.
        bundle.getParcelableUniversally<BaseInputViewState>(STATE_THIS)?.let {
            isExpandedTitle = it.isExpandedTitle
            // TextView$SavedState содержит это состояние, но есть проблемы с восстановлением.
            if (it.isFocused) {
                getActivity().window?.let { window ->
                    window.setSoftInputMode(
                        window.attributes.softInputMode or WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
                    )
                }
                inputView.post { inputView.requestFocus() }
            }
        }
        controller.updateHintCallback.onChange()
    }

    override fun dispatchHoverEvent(event: MotionEvent): Boolean {
        return if (inputViewAccessibilityDelegate?.dispatchHoverEvent(event) == true) {
            true
        } else {
            super.dispatchHoverEvent(event)
        }
    }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
        inputViewAccessibilityDelegate?.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val delegateDispatchEventResult = inputViewAccessibilityDelegate?.dispatchKeyEvent(event) == true
        val superClassDispatchEventResult = super.dispatchKeyEvent(event)
        return delegateDispatchEventResult || superClassDispatchEventResult
    }

    override fun getNextFocusLeftId(): Int = inputView.nextFocusLeftId

    override fun getNextFocusRightId(): Int = inputView.nextFocusRightId

    override fun getNextFocusUpId(): Int = inputView.nextFocusUpId

    override fun getNextFocusDownId(): Int = inputView.nextFocusDownId

    override fun getNextFocusForwardId(): Int = inputView.nextFocusForwardId

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getNextClusterForwardId(): Int = inputView.nextClusterForwardId

    override fun setNextFocusLeftId(nextFocusLeftId: Int) {
        inputView.nextFocusLeftId = nextFocusLeftId
    }

    override fun setNextFocusRightId(nextFocusRightId: Int) {
        inputView.nextFocusRightId = nextFocusRightId
    }

    override fun setNextFocusUpId(nextFocusUpId: Int) {
        inputView.nextFocusUpId = nextFocusUpId
    }

    override fun setNextFocusDownId(nextFocusDownId: Int) {
        inputView.nextFocusDownId = nextFocusDownId
    }

    override fun setNextFocusForwardId(nextFocusForwardId: Int) {
        inputView.nextFocusForwardId = nextFocusForwardId
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setNextClusterForwardId(nextClusterForwardId: Int) {
        inputView.nextClusterForwardId = nextClusterForwardId
    }

    override fun setMinimumWidth(minWidth: Int) {
        super.setMinimumWidth(minWidth)
        inputView.minimumWidth = minWidth
    }

    /**
     * Установка accessibility делегата.
     */
    internal fun initAccessibilityDelegate(delegate: ExploreByTouchHelper) {
        inputViewAccessibilityDelegate = delegate
        ViewCompat.setAccessibilityDelegate(this, delegate)
    }

    /** @SelfDocumented */
    protected fun configureMaxWidthTextLayouts(availableWidth: Int) = with(controller) {
        val maxWidth = availableWidth - horizontalPadding
        titleView.configure {
            this.maxWidth = maxWidth
        }
        validationStatusView.configure {
            this.maxWidth = maxWidth
        }
    }

    /** @SelfDocumented */
    protected fun getWidthMainParts(): Int =
        horizontalPadding + getClearWidth() + getIconWidth() + getProgressWidth()

    /** @SelfDocumented */
    protected fun getHeightMainParts(): Int =
        verticalPadding + getTitleHeight() + controller.validationStatusView.height

    /** @SelfDocumented */
    protected fun configureUnderlineBounds(width: Int, height: Int) {
        underlineBounds.set(
            paddingStart.toFloat(),
            0f,
            width + paddingStart.toFloat(),
            height - underlinePaint.strokeWidth / 2 - paddingBottom
        )
    }

    /** @SelfDocumented */
    protected fun getResultWidth(width: Int): Int = with(controller) {
        val maxWidthTitleAndValidation = max(titleView.width, validationStatusView.width)
        return if (width >= maxWidthTitleAndValidation) {
            width
        } else {
            maxWidthTitleAndValidation
        }
    }

    /** @SelfDocumented */
    protected fun layoutMainParts() = with(controller) {
        titleView.layout(paddingStart, paddingTop)
        val isFocused = inputView.isFocused
        inputView.layout(
            paddingStart,
            (underlineBounds.bottom + underlinePaint.strokeWidth / 2 - inputView.measuredHeight).toInt()
        )
        if (isFocused) inputView.requestFocus()
        clearView.layout(
            inputView.right,
            (inputView.bottom - inputView.paddingBottom - clearView.height) -
                (inputView.lineHeight - clearView.height) / 2
        )
        validationStatusView.layout(
            paddingStart,
            (underlineBounds.bottom + underlinePaint.strokeWidth / 2).toInt()
        )
    }

    /** @SelfDocumented */
    protected fun drawMainParts(canvas: Canvas) = with(controller) {
        titleView.draw(canvas)
        clearView.draw(canvas)
        iconView.draw(canvas)
        if (isProgressVisible) {
            canvas.withTranslation(
                inputView.right.toFloat() + innerSpacing,
                (baseline - progressView.bounds.height()).toFloat(),
                progressView::draw
            )
        }
        canvas.drawLine(
            underlineBounds.left,
            underlineBounds.bottom,
            underlineBounds.right,
            underlineBounds.bottom,
            underlinePaint
        )
        validationStatusView.draw(canvas)
    }

    private fun getClearWidth() = if (isClearVisible || controller.clearView.isVisible) {
        controller.clearView.width
    } else {
        0
    }

    private fun getIconWidth() = if (controller.iconView.isVisible) {
        controller.iconView.width
    } else {
        0
    }

    private fun getProgressWidth() = if (isProgressVisible) {
        controller.progressView.bounds.width() + controller.innerSpacing * 2
    } else {
        0
    }

    private fun getTitleHeight() = if (showPlaceholderAsTitle || controller.titleView.isVisible) {
        controller.titleView.height
    } else {
        0
    }

    companion object {

        /**
         * Состояние суперкласса.
         */
        private const val STATE_SUPER = "STATE_SUPER"

        /**
         * Состояние текущего класса.
         */
        private const val STATE_THIS = "STATE_THIS"

        /**
         * Состояние вложенного поля ввода.
         */
        private const val STATE_INPUT_VIEW = "STATE_INPUT_VIEW"
    }
}