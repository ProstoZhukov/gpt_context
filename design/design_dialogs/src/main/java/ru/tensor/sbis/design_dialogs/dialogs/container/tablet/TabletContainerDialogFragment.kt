package ru.tensor.sbis.design_dialogs.dialogs.container.tablet

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import androidx.annotation.DimenRes
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.fragment.app.DialogFragment
import android.util.DisplayMetrics
import android.view.*
import android.view.Gravity.NO_GRAVITY
import android.view.View.NO_ID
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.tensor.sbis.design.design_dialogs.R
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design_dialogs.dialogs.container.tablet.ContentPendingShowState.*
import ru.tensor.sbis.design.utils.extentions.doOnNextGlobalLayout
import androidx.core.view.updateLayoutParams
import ru.tensor.sbis.design.view_ext.UiUtils
import ru.tensor.sbis.design_dialogs.dialogs.container.Container
import ru.tensor.sbis.design_dialogs.dialogs.container.base.BaseContainerDialogFragment
import ru.tensor.sbis.design_dialogs.dialogs.container.util.TabletContainerAnchorParamsHelper
import ru.tensor.sbis.design_dialogs.dialogs.container.util.restrictDialogContentWidthOnTablet

private const val CONTENT_SHOWN_STATE_KEY = "TabletContainerDialogFragment.CONTENT_SHOWN_STATE_KEY"

private enum class ContentPendingShowState {
    NONE,
    /**
     * Содержимое скрыто и будет показано моментально, как только это станет возможным
     */
    PENDING_SHOW,
    /**
     * Содержимое скрыто и будет показано с анимацией, как только это станет возможным
     */
    PENDING_SHOW_ANIMATED,
    /**
     * Ожидается вызов отображения содержимого с анимацией вручную
     */
    EXPECTING_MANUAL_SHOW_ANIMATED,
    /**
     * Содержимое отображается
     */
    SHOWN
}

/**
 * Базовый компонент для отображения всплывающего меню на планшете. Представляет собой контейнер,
 * основанный на [androidx.fragment.app.DialogFragment] и способный размещать внутри себя фрагменты с помощью метода [setContentCreator].
 * Ссылка на стандарт: http://axure.tensor.ru/MobileAPP/#p=%D0%B2%D1%81%D0%BF%D0%BB%D1%8B%D0%B2%D0%B0%D1%8E%D1%89%D0%B5%D0%B5_%D0%BE%D0%BA%D0%BD%D0%BE&g=1
 */
open class TabletContainerDialogFragment :
    BaseContainerDialogFragment(),
    Container.Showable,
    Container.Closeable,
    Container.Resizable {

    private val FADE_ANIMATION_DURATION = 150L
    private var horizontalMargin = 0
    private var hasHorizontalMargin = false

    /**
     * Ключ для хранения в аргументах параметров отобрежения фрагмента-контейнера.
     */
    private val VISUAL_PARAMS_ARG = TabletContainerDialogFragment::class.java.simpleName + ":VISUAL_PARAMS_ARG"
    protected val visualParams: VisualParams
        get() = requireArguments().getSerializable(VISUAL_PARAMS_ARG) as VisualParams

    /**
     * Ключ для хранения параметра о необходимости сразу отображать контент.
     */
    private val INSTANT_SHOW_CONTENT_ARG = TabletContainerDialogFragment::class.java.simpleName +
        ":INSTANT_SHOW_CONTENT_ARG"
    /**
     * Ключ для хранения параметра о необходимости сразу отображать контент.
     */
    private val CANCELABLE_PARAM_ARG = TabletContainerDialogFragment::class.java.simpleName + ":CANCELABLE_PARAM_ARG"

    /**
     * Корневая View.
     */
    private lateinit var rootView: View
    /**
     * View-контейнер для контента.
     */
    private lateinit var containerView: ViewGroup

    /**
     * View для ограничения области контента по бокам.
     */
    private var boundingView: View? = null
    /**
     * Слушатель изменения размеров [boundingView]
     */
    private var onBoundingViewGlobalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    /**
     * View-якорь.
     */
    private var anchorView: View? = null
    private var anchorParentView: View? = null
    /**
     * Слушатель изменения размеров [anchorView]
     */
    private var onAnchorViewGlobalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    /**
     * Слушатель изменения размеров [containerView]
     */
    private var onContainerViewGlobalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    /**
     * Слушатель global layout [anchorParentView]
     */
    private var onAnchorParentViewGlobalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null

    /**
     * Состояние ожидания отображения содержимого
     */
    private var contentPendingShowingState = NONE

    /**
     * Флаг, отображается ли содержимое в данный момент.
     */
    private var contentShown = false

    /**
     * Определяет, должно ли содержимое отображаться сразу, а не лишь после вызова [showContent]
     */
    private var isInstantShow = false

    private var animationRunnable: Runnable? = null

    /**
     * Область изначального расположения якоря
     */
    private var initialAnchorRect: Rect? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, visualParams.dialogStyle)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.run {
            requestFeature(Window.FEATURE_NO_TITLE)
            requestFeature(Window.FEATURE_ACTION_BAR)
            requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY)
        }
        return dialog
    }

    override fun getContainerLayoutRes() = R.layout.design_dialogs_tablet_container

    override fun getContainerViewId() = R.id.design_dialogs_fragment_container

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        horizontalMargin = resources.getDimensionPixelSize(RDesign.dimen.size_caption1_scaleOff)

        rootView = view
        containerView = view.findViewById(containerViewId)

        // применение параметров отображения, полученных в качестве аргумента
        arguments?.let { args ->
            isInstantShow = args.getBoolean(INSTANT_SHOW_CONTENT_ARG)
            val cancelable = args.getBoolean(CANCELABLE_PARAM_ARG, true)
            // закрытие окна по нажатию на область вне контента
            if (cancelable) {
                rootView.setOnClickListener { closeContainer() }
            }
            containerView.visibility = if (isInstantShow) View.VISIBLE else View.INVISIBLE
            applyVisualParams(visualParams)
        }
        if (savedInstanceState != null) {
            contentShown = savedInstanceState.getBoolean(CONTENT_SHOWN_STATE_KEY)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(CONTENT_SHOWN_STATE_KEY, contentShown)
    }

    override fun dismiss() {
        super.dismiss()
        contentShown = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        rootView.setOnClickListener(null)
        boundingView?.viewTreeObserver?.removeOnGlobalLayoutListener(onBoundingViewGlobalLayoutListener)
        anchorView?.viewTreeObserver?.removeOnGlobalLayoutListener(onAnchorViewGlobalLayoutListener)
        anchorParentView?.viewTreeObserver?.removeOnGlobalLayoutListener(onAnchorParentViewGlobalLayoutListener)
        containerView.viewTreeObserver?.removeOnGlobalLayoutListener(onContainerViewGlobalLayoutListener)
        boundingView = null
        anchorView = null
        onBoundingViewGlobalLayoutListener = null
        onAnchorViewGlobalLayoutListener = null
        onAnchorParentViewGlobalLayoutListener = null
        onContainerViewGlobalLayoutListener = null
    }

    override fun onDestroy() {
        super.onDestroy()
        animationRunnable?.let {
            handler.removeCallbacks(it)
            animationRunnable = null
        }
    }

    /**
     * Определение и применение специфических параметров отображения
     * @param visualParams объект для хранения параметров отображения
     */
    private fun applyVisualParams(visualParams: VisualParams) {
        dialog?.window?.setSoftInputMode(visualParams.softInputMode)

        // добавление gravity
        if (visualParams.gravity != NO_GRAVITY) applyGravity(visualParams.gravity)

        // добавление отступов по бокам
        if (visualParams.needHorizontalMargin) applyHorizontalMargin()

        // задание фиксированной ширины
        visualParams.fixedWidth?.let {
            setFixedWidth(it)
            // задание ширины по содержимому
        } ?: takeIf { visualParams.wrapWidth }?.run {
            setWrapWidth()
            // добавление ограничения по бокам
        } ?: visualParams.boundingObject?.also { boundingObject ->
            val boundingView = getBoundingView(boundingObject)
            prepareBoundingView(boundingView, boundingObject.ensureDefaultMinWidth)
        } ?: if (resources.getBoolean(RDesign.bool.is_tablet)) {
            restrictDialogContentWidthOnTablet(containerView)
        } else {

        }

        // задание высоты по доступной области
        changeHeightParams(wrapContent = visualParams.wrapHeight)

        // перекрытие области ActionBar-а
        setOverlayActionBar(visualParams.overlayActionBar)

        // закрепление якорем
        visualParams.anchor?.let { anchor ->
            val anchorView = if (anchor.viewId != NO_ID) {
                activity?.findViewById(anchor.viewId)
            } else {
                findViewWithTag(anchor.viewTag)
            }
            prepareAnchorView(
                anchorView,
                anchor.type,
                anchor.gravity,
                visualParams.softInputMode,
                visualParams.listenAnchorLayoutAlways
            )

            if (anchorView == null && anchor.viewTag != null) {
                // view якоря не найден по тегу - попробуем найти его после global layout
                val parentView = anchor.anchorParentTag?.let { findViewWithTag(it) }
                    ?: activity?.window?.decorView?.rootView
                parentView?.let {
                    anchorParentView = it
                    onAnchorParentViewGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
                        anchorParentView
                            ?.viewTreeObserver
                            ?.removeOnGlobalLayoutListener(onAnchorParentViewGlobalLayoutListener)
                        prepareAnchorView(
                            findViewWithTag(anchor.viewTag),
                            anchor.type,
                            anchor.gravity,
                            visualParams.softInputMode,
                            visualParams.listenAnchorLayoutAlways
                        )
                    }
                    anchorParentView
                        ?.viewTreeObserver
                        ?.addOnGlobalLayoutListener(onAnchorParentViewGlobalLayoutListener)
                }
            }
        }
    }

    private fun findViewWithTag(tag: String?): View? {
        return activity?.window?.decorView?.rootView?.findViewWithTag(tag ?: return null)
    }

    /**
     * Обработка View-якоря
     * @param anchorView View-якорь
     * @param anchorType тип закрепления
     * @param anchorGravity положение содержимого относительно якоря
     * @param softInputMode режим отображения клавиатуры
     * @param listenAnchorLayoutAlways флаг необходимости отслеживания якоря всегда
     */
    private fun prepareAnchorView(
        anchorView: View?,
        anchorType: AnchorType,
        anchorGravity: AnchorGravity,
        softInputMode: Int,
        listenAnchorLayoutAlways: Boolean
    ) {
        if (anchorView == null) {
            return
        }

        this.anchorView = anchorView

        // закрепление области контента якорем
        applyAnchorView(anchorView, anchorType, anchorGravity, softInputMode)

        // создание слушателя для View-якоря, на случай, если его размер изменится
        onAnchorViewGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            if (!listenAnchorLayoutAlways)
                anchorView.viewTreeObserver.removeOnGlobalLayoutListener(onAnchorViewGlobalLayoutListener)
            applyAnchorView(anchorView, anchorType, anchorGravity, softInputMode)
        }
        anchorView.viewTreeObserver.addOnGlobalLayoutListener(onAnchorViewGlobalLayoutListener)

        // обеспечение показа содержимого сразу в нужном месте, без моргания
        hideContentUntilProperlyAnchored(anchorView, anchorType, anchorGravity, softInputMode)
    }

    private fun hideContentUntilProperlyAnchored(
        anchorView: View,
        anchorType: AnchorType,
        anchorGravity: AnchorGravity,
        softInputMode: Int
    ) {
        with(containerView) {
            // не будем показывать view, пока не применим правильное позиционирование
            visibility = View.INVISIBLE
            if (contentPendingShowingState != SHOWN) {
                contentPendingShowingState = PENDING_SHOW
            }
            // подождём определения размеров
            onContainerViewGlobalLayoutListener =
                doOnNextGlobalLayout(skipWhile = { Rect().apply { getGlobalVisibleRect(this) }.isEmpty }) {
                    // выполним позиционирование, зная размеры содержимого
                    applyAnchorView(anchorView, anchorType, anchorGravity, softInputMode)
                    // после применения изменений содержимое можно будет показать
                    onContainerViewGlobalLayoutListener = doOnNextGlobalLayout {
                        when {
                            contentPendingShowingState == PENDING_SHOW_ANIMATED -> {
                                // отображение содержимого вручную уже выполнялось
                                showContentWithAnimation()
                            }
                            isInstantShow || contentPendingShowingState == SHOWN -> {
                                // показываем сразу
                                visibility = View.VISIBLE
                            }
                            else -> {
                                // ожидается отображение вручную
                                contentPendingShowingState = EXPECTING_MANUAL_SHOW_ANIMATED
                            }
                        }
                    }
                }
        }
    }

    /**
     * Закрепление области контента якорем
     * @param anchorView View-якорь
     * @param anchorType тип закрепления
     * @param anchorGravity положение содержимого относительно якоря
     * @param softInputMode режим отображения клавиатуры
     */
    private fun applyAnchorView(
        anchorView: View,
        anchorType: AnchorType,
        anchorGravity: AnchorGravity,
        softInputMode: Int
    ) {
        val availableHeight = getDisplayHeight()
        val availableWidth = getDisplayWidth()

        val rect = Rect()
        anchorView.getGlobalVisibleRect(rect)

        val decorOffsetTop = getDecorOffsetTop()

        if (!rect.isEmpty) {
            val params = containerView.layoutParams as FrameLayout.LayoutParams

            val contentRect = Rect().apply {
                containerView.getGlobalVisibleRect(this)
            }

            val anchorParamsHelper = TabletContainerAnchorParamsHelper(
                decorOffsetTop,
                availableHeight,
                availableWidth,
                containerView.width,
                rect,
                initialAnchorRect,
                contentRect,
                horizontalMargin
            )

            val lifted = liftContainerAboveKeyboardIfNeeded(
                softInputMode,
                params,
                rect
            )

            if (!lifted) anchorParamsHelper.applyAnchorType(anchorType, params)
            anchorParamsHelper.applyAnchorGravity(anchorGravity, params)

            // Без post в некоторых случаях не обновляется
            // https://online.sbis.ru/opendoc.html?guid=16debace-3d86-4d40-8117-3c2536f4d42c&client=3
            containerView.post { containerView.layoutParams = params }
        }
    }

    private fun liftContainerAboveKeyboardIfNeeded(
        softInputMode: Int,
        params: FrameLayout.LayoutParams,
        rect: Rect
    ): Boolean {
        val isSoftInputAdjustNothing = (softInputMode and SOFT_INPUT_ADJUST_NOTHING) == SOFT_INPUT_ADJUST_NOTHING
        if (isSoftInputAdjustNothing) {
            if (initialAnchorRect == null) initialAnchorRect = rect
            val keyboardHeight = getKeyboardHeight()
            if (keyboardHeight > 0) {
                params.gravity = Gravity.BOTTOM
                params.topMargin = horizontalMargin
                params.bottomMargin = horizontalMargin + keyboardHeight
                return true
            }
        }
        return false
    }

    private fun getDecorOffsetTop(): Int {
        return Rect().run {
            activity?.window?.decorView?.getWindowVisibleDisplayFrame(this)
            top
        }
    }

    private fun getKeyboardHeight(): Int {
        return (
            getRootWindowInsetsBottom(WindowInsetsCompat.Type.ime()) -
                getRootWindowInsetsBottom(WindowInsetsCompat.Type.systemBars())
            )
            .coerceAtLeast(0)
    }

    private fun getRootWindowInsetsBottom(typeMask: Int): Int {
        return activity?.window?.decorView?.let {
            ViewCompat.getRootWindowInsets(it)?.getInsets(typeMask)?.bottom
        } ?: 0
    }

    /**
     * Определение View, которая будет ограничивать область контента
     * @param boundingObject ограничивающий объект
     * @return ограничивающая View
     */
    private fun getBoundingView(boundingObject: BoundingObject): View? {
        return when (boundingObject.type) {
            BoundingObjectType.PARENT_FRAGMENT -> parentFragment?.view
            BoundingObjectType.TARGET_FRAGMENT -> targetFragment?.view
            BoundingObjectType.VIEW -> activity?.findViewById(boundingObject.viewId)
        }
    }

    /**
     * Обработка ограничивающей View
     * @param boundingView ограничивающая View
     */
    private fun prepareBoundingView(boundingView: View?, ensureDefaultMinWidth: Boolean) {
        if (boundingView == null) {
            return
        }

        this.boundingView = boundingView

        // ограничение области контента
        applyBoundingView(boundingView, ensureDefaultMinWidth)

        // создание слушателя для ограничивающего View, на случай, если его размер изменится
        onBoundingViewGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            boundingView.viewTreeObserver.removeOnGlobalLayoutListener(onBoundingViewGlobalLayoutListener)
            applyBoundingView(boundingView, ensureDefaultMinWidth)
        }
        boundingView.viewTreeObserver.addOnGlobalLayoutListener(onBoundingViewGlobalLayoutListener)
    }

    /**
     * Перекрытие области ActionBar-а контентом
     */
    private fun setOverlayActionBar(overlay: Boolean) {
        val paddingTop = if (overlay) 0 else UiUtils.getToolBarHeight(context)
        view?.run {
            setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
        }
    }

    /**
     * Применение gravity относительно экрана
     */
    private fun applyGravity(gravity: Int) {
        val params = containerView.layoutParams as FrameLayout.LayoutParams
        params.gravity = gravity
        containerView.layoutParams = params
    }

    /**
     * Применение горизонтальных отступов
     */
    private fun applyHorizontalMargin() {
        hasHorizontalMargin = true
        containerView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            rightMargin = horizontalMargin
            leftMargin = horizontalMargin
        }
    }

    /**
     * Ограничение области контента
     * @param view ограничивающая View
     * @param ensureDefaultMinWidth должна ли ширина быть не меньше стандартного минимального значения, вне зависимости
     * от ширины ограничивающего View
     */
    private fun applyBoundingView(view: View, ensureDefaultMinWidth: Boolean) {
        val rect = Rect()
        view.getGlobalVisibleRect(rect)
        applyBoundingRect(rect, ensureDefaultMinWidth)
    }

    /**
     * Ограничение области контента
     * @param boundingRect ограничивающий прямоугольник
     */
    private fun applyBoundingRect(boundingRect: Rect, ensureDefaultMinWidth: Boolean) {
        if (boundingRect.isEmpty) return
        prepareBoundingRect(boundingRect, ensureDefaultMinWidth)
        containerView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            leftMargin = boundingRect.left
            rightMargin = getDisplayWidth() - boundingRect.right
            width = boundingRect.width()
        }
    }

    /**
     * Изменение размера и положения ограничивающего прямоугольника при необходимости
     */
    private fun prepareBoundingRect(boundingRect: Rect, ensureDefaultMinWidth: Boolean) {
        if (ensureDefaultMinWidth) {
            prepareBoundingRectWithMinWidth(boundingRect)
        } else {
            prepareBoundingRectWithoutMinWidth(boundingRect)
        }
    }

    private fun prepareBoundingRectWithMinWidth(boundingRect: Rect) {
        val minWidth = resources.getDimensionPixelSize(R.dimen.design_dialogs_tablet_container_dialog_default_min_width)
        val hasSpaceForMargins = boundingRect.width() - minWidth >= 2 * horizontalMargin
        if (hasHorizontalMargin && hasSpaceForMargins || boundingRect.left == 0) {
            boundingRect.left += horizontalMargin
        }
        boundingRect.right = boundingRect.left + boundingRect.width().coerceAtLeast(minWidth)
        if (hasHorizontalMargin && hasSpaceForMargins || boundingRect.right == getDisplayWidth()) {
            boundingRect.right -= horizontalMargin
        }
    }

    private fun prepareBoundingRectWithoutMinWidth(boundingRect: Rect) {
        if (hasHorizontalMargin || boundingRect.left == 0) {
            boundingRect.left += horizontalMargin
        }
        if (hasHorizontalMargin || boundingRect.right == getDisplayWidth()) {
            boundingRect.right -= horizontalMargin
        }
    }

    private fun setWrapWidth() {
        containerView.layoutParams = containerView.layoutParams.apply {
            width = ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }

    private fun setFixedWidth(@DimenRes widthRes: Int) {
        containerView.layoutParams = containerView.layoutParams.apply {
            width = resources.getDimensionPixelSize(widthRes)
        }
    }

    override fun closeContainer() {
        content?.onCloseContent()
        dismissAllowingStateLoss()
    }

    override fun dismissAllowingStateLoss() {
        super.dismissAllowingStateLoss()
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_CANCELED, null)
    }

    override fun showContent() {
        hideProgress()
        showContentWithAnimation()
    }

    override fun changeHeightParams(wrapContent: Boolean) {
        containerView.layoutParams = containerView.layoutParams.apply {
            height = if (wrapContent) ViewGroup.LayoutParams.WRAP_CONTENT else ViewGroup.LayoutParams.MATCH_PARENT
        }
    }

    private fun showContentWithAnimation() {
        if (contentPendingShowingState == PENDING_SHOW) {
            contentPendingShowingState = PENDING_SHOW_ANIMATED
            return
        }
        if (!contentShown) {
            notifyStartShowingAnimation()
            animationRunnable = Runnable { notifyFinishShowingAnimation() }
            animationRunnable?.let { handler.postDelayed(it, FADE_ANIMATION_DURATION) }
        }
        contentShown = true
        contentPendingShowingState = SHOWN
        val fade = Fade().apply {
            duration = FADE_ANIMATION_DURATION
            interpolator = AccelerateInterpolator()
        }
        TransitionManager.beginDelayedTransition(containerView, fade)
        containerView.visibility = View.VISIBLE
        TransitionManager.endTransitions(containerView)
    }

    /**
     * Установка параметров отображения
     * @param visualParams набор параметров отображения
     * @return ссылка на самого себя
     */
    fun setVisualParams(visualParams: VisualParams): TabletContainerDialogFragment {
        getOrCreateArguments().putSerializable(VISUAL_PARAMS_ARG, visualParams)
        return this
    }

    /**
     * Установка режима отображения контента: мгновенно после появления
     * панели на экране или по сигналу через интерфейс [Showable]
     * @param instant - режим отображения контента
     * @return ссылка на самого себя
     */
    fun setInstant(instant: Boolean): TabletContainerDialogFragment {
        getOrCreateArguments().putBoolean(INSTANT_SHOW_CONTENT_ARG, instant)
        return this
    }

    fun setCancelableContainer(isCancelable: Boolean): TabletContainerDialogFragment {
        getOrCreateArguments().putBoolean(CANCELABLE_PARAM_ARG, isCancelable)
        return this
    }

    private fun getDisplayMetrics() = DisplayMetrics().apply {
        context?.let {
            val defaultDisplay = (it.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
            defaultDisplay.getMetrics(this)
        }
    }

    private fun getDisplayWidth() = getDisplayMetrics().widthPixels

    private fun getDisplayHeight() = getDisplayMetrics().heightPixels
}