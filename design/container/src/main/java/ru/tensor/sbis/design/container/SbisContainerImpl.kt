package ru.tensor.sbis.design.container

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.MeasureSpec
import android.view.View.OnTouchListener
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
import android.view.WindowManager.LayoutParams.WRAP_CONTENT
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.annotation.DimenRes
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat.ID_NULL
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.withResumed
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable.combineLatest
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.tensor.sbis.android_ext_decl.getParcelableUniversally
import ru.tensor.sbis.common.util.AdjustResizeHelper
import ru.tensor.sbis.common.util.AdjustResizeHelper.AdjustResizeHelperHost
import ru.tensor.sbis.design.container.databinding.SbisContainerBinding
import ru.tensor.sbis.design.container.databinding.SbisHeaderContainerBinding
import ru.tensor.sbis.design.container.locator.AnchorHorizontalLocator
import ru.tensor.sbis.design.container.locator.AnchorLocator
import ru.tensor.sbis.design.container.locator.AnchorVerticalLocator
import ru.tensor.sbis.design.container.locator.HorizontalLocator
import ru.tensor.sbis.design.container.locator.Locator
import ru.tensor.sbis.design.container.locator.ScreenHorizontalLocator
import ru.tensor.sbis.design.container.locator.ScreenLocator
import ru.tensor.sbis.design.container.locator.ScreenVerticalLocator
import ru.tensor.sbis.design.container.locator.TagAnchorHorizontalLocator
import ru.tensor.sbis.design.container.locator.TagAnchorVerticalLocator
import ru.tensor.sbis.design.container.locator.VerticalAlignment
import ru.tensor.sbis.design.container.locator.VerticalLocator
import ru.tensor.sbis.design.container.locator.calculator.AnchorPositionCalculator
import ru.tensor.sbis.design.container.locator.watcher.BaseAnchorWatcher
import ru.tensor.sbis.design.container.locator.watcher.DimUpdater
import ru.tensor.sbis.design.container.locator.watcher.RecyclerAnchorWatcher
import ru.tensor.sbis.design.container.locator.watcher.TagAnchorWatcher
import ru.tensor.sbis.design.header.BaseHeader
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.toolbar.util.StatusBarHelper.updateStatusBarMode
import ru.tensor.sbis.design.util.checkStatusBarShouldBeShown
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.checkNotNullSafe
import ru.tensor.sbis.design.utils.extentions.requestApplyInsetsWhenAttached
import ru.tensor.sbis.design.utils.extentions.setHorizontalMargin
import ru.tensor.sbis.design.utils.findViewParent
import ru.tensor.sbis.design.utils.getDimen
import ru.tensor.sbis.design.R as RDesign

/**
 * @author ma.kolpakov
 */
class SbisContainerImpl : DialogFragment(), SbisContainer, OnTouchListener {
    private var destroyLocators = true
    private var isDestroyed = false
    private var containerTag = CONTAINER_DEFAULT_TAG
    private val disposer = CompositeDisposable()
    private var customWidth = DEFAULT_CUSTOM_DIMENSION
    private var customHeight = DEFAULT_CUSTOM_DIMENSION
    private val dimUpdater = DimUpdater(DimType.SOLID)

    private val containerViewModel: ContainerViewModelImpl by viewModels()

    override fun getViewModel() = containerViewModel

    private var onDismissListener: (() -> Unit)? = null

    /**
     * Флаг, который показывает, был ли уже передан листенер во ViewModel.
     * Нужен, чтобы при пересоздании фрагмента во ViewModel не передавался
     * нулевой листенер.
     */
    private var onDismissListenerTransferredToViewModel
        get() = getOrCreateArguments().getBoolean(LISTENER_TRANSFERRED_ARG, false)
        set(value) = getOrCreateArguments().putBoolean(LISTENER_TRANSFERRED_ARG, value)

    override fun setOnDismissListener(listener: (() -> Unit)?) {
        onDismissListenerTransferredToViewModel = false
        onDismissListener = listener
        transferDismissListenerToViewModel()
    }

    private var creator: ContentCreator<Content>? = null
        private set(value) {
            field = value
            if (value is Parcelable) {
                getOrCreateArguments().putParcelable(CONTENT_CREATOR_ARG, value)
            }
        }

    private var replacedVerticalLocator: VerticalLocator? = null
    private var keyboardHeight = 0
    private var keyboardEnable = false

    private val adjustResizeHelperHost: AdjustResizeHelperHost by keyboardHelper(
        openAction = {
            if (replacedVerticalLocator != null) return@keyboardHelper
            dialog?.window?.decorView?.requestApplyInsetsWhenAttached()
        },
        closeAction = {
            replacedVerticalLocator ?: return@keyboardHelper
            dialog?.window?.decorView?.requestApplyInsetsWhenAttached()
        }
    )

    private val adjustResizeHelper by lazy { AdjustResizeHelper(adjustResizeHelperHost) }

    private val onApplyWindowInsetsListener by lazy {
        object : OnApplyWindowInsetsListener {

            private val keyboardVerticalLocator = ScreenVerticalLocator(alignment = VerticalAlignment.BOTTOM).apply {
                locator as ScreenLocator
                locator.rules = locator.rules.copy(defaultMarginBottom = false)
            }
            private val contentRect: Rect = Rect()

            private var srcHeight = 0

            private val contentView: View
                get() = requireView().findViewById<CardView>(R.id.sbis_container)

            override fun onApplyWindowInsets(view: View, insets: WindowInsetsCompat): WindowInsetsCompat {
                val keyboardHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom -
                    insets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.navigationBars()).bottom
                if (isContainerOverlay(keyboardHeight)) {
                    offsetToOpenKeyboard(keyboardHeight)
                } else {
                    offsetToCloseKeyboard()
                }
                return insets
            }

            private fun isContainerOverlay(keyboardHeight: Int): Boolean {
                if (contentView.height == 0) return false
                val rect = Rect()
                requireView().getDrawingRect(rect)
                val availableHeight = rect.height() - keyboardHeight
                val currentHeight = contentView.y + contentView.height
                return currentHeight > availableHeight
            }

            private fun offsetToOpenKeyboard(keyboardHeight: Int) {
                if (isAndroidNougat()) requireDialog().window?.setSoftInputMode(SOFT_INPUT_ADJUST_RESIZE)
                keyboardEnable = true
                if (replacedVerticalLocator != null) return
                this@SbisContainerImpl.keyboardHeight = keyboardHeight
                replacedVerticalLocator = getVerticalLocator()
                getOrCreateArguments().putParcelable(LOCATOR_V_ARG, keyboardVerticalLocator)
                applyWatcher(getHorizontalLocator(), keyboardVerticalLocator)
                disposer.clear()
                resizeRootIfNeeded(contentView.marginBottom, contentView.measuredHeight)
                contentView.subscribeToLocators(
                    getHorizontalLocator(), keyboardVerticalLocator
                )
                contentRect.apply {
                    bottom = contentView.measuredHeight
                    right = contentView.measuredWidth
                }

                srcHeight = if (customHeight > 0) customHeight else contentView.measuredHeight

                applyLocators(getHorizontalLocator(), getVerticalLocator(), contentRect)
            }

            private fun offsetToCloseKeyboard() {
                replacedVerticalLocator ?: return
                keyboardHeight = 0
                resizeRootIfNeeded(contentView.marginBottom, srcHeight)
                getOrCreateArguments().putParcelable(LOCATOR_V_ARG, replacedVerticalLocator)
                replacedVerticalLocator = null
                applyWatcher(getHorizontalLocator(), getVerticalLocator())
                disposer.clear()
                contentView.subscribeToLocators(
                    getHorizontalLocator(), getVerticalLocator()
                )

                contentRect.apply {
                    bottom = srcHeight
                    right = contentView.measuredWidth
                }
                applyLocators(getHorizontalLocator(), getVerticalLocator(), contentRect)
                keyboardEnable = false
            }
        }
    }

    /**
     * Метод [View.getViewTreeObserver] не гарантирует возврат одного и того же observer.
     * Поэтому мы сами сохраняем его.
     *
     * @see View.getViewTreeObserver
     */
    private var viewTreeObserver: ViewTreeObserver? = null

    private var nextContainerInPreviousPosition = true

    lateinit var content: Content

    override var dimType = DimType.SOLID
        set(value) {
            field = value
            dimUpdater.dimType = value
            getOrCreateArguments().putString(DIM_TYPE_ARG, value.name)
        }

    override var isAnimated = false
        set(value) {
            field = value
            dimUpdater.isAnimated = value
            getOrCreateArguments().putBoolean(IS_ANIMATED_ARG, value)
        }

    override var isCloseOnTouchOutside = true
        set(value) {
            field = value
            dimUpdater.isAnimated = value
            getOrCreateArguments().putBoolean(IS_CLOSE_ON_TOUCH_OUTSIDE_ARG, value)
        }

    override var isDialogCancelable = true
        set(value) {
            field = value
            getOrCreateArguments().putBoolean(IS_DIALOG_CANCELABLE_ARG, value)
        }

    override var isTranslateTouchToParent = false
        set(value) {
            field = value
            getOrCreateArguments().putBoolean(IS_TRANSLATE_TOUCH_TO_PARENT, value)
        }

    internal var cutoutBounds: Rect? = null
        set(value) {
            field = value
            dimUpdater.cutoutBounds = value
        }

    internal companion object {
        fun newInstance(
            contentCreator: ContentCreator<Content>,
            cutoutBounds: Rect? = null,
            tag: String = CONTAINER_DEFAULT_TAG
        ): SbisContainerImpl {
            val fragment = SbisContainerImpl()
            fragment.cutoutBounds = cutoutBounds
            fragment.creator = contentCreator
            fragment.containerTag = tag
            return fragment
        }
    }

    private var outerCutCornersRadius: Float = 0f
        set(value) {
            field = value
            dimUpdater.outerCutCornersRadius = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NO_FRAME, R.style.SbisContainerWindow)

        val creatorTmp: ContentCreator<Content>? = getOrCreateArguments().getParcelableUniversally(CONTENT_CREATOR_ARG)
        if (creatorTmp != null) {
            creator = creatorTmp
        }

        content = checkNotNullSafe(creator) {
            "ContentCreator can't be null [args $arguments parent activity $activity, parent fragment $parentFragment]"
        }?.createContent() ?: run {
            dismissAllowingStateLoss()
            return
        }

        customWidth = getCustomDimension(content.customWidth())
        customHeight = getCustomDimension(content.customHeight())

        val dimTypeString: String? = getOrCreateArguments().getString(DIM_TYPE_ARG)
        if (dimTypeString != null) {
            dimType = DimType.valueOf(dimTypeString)
        }
        isAnimated = getOrCreateArguments().getBoolean(IS_ANIMATED_ARG, false)
        isCloseOnTouchOutside = getOrCreateArguments().getBoolean(IS_CLOSE_ON_TOUCH_OUTSIDE_ARG, true)
        isDialogCancelable = getOrCreateArguments().getBoolean(IS_DIALOG_CANCELABLE_ARG, true)
        isTranslateTouchToParent = getOrCreateArguments().getBoolean(IS_TRANSLATE_TOUCH_TO_PARENT, false)
        mergeWatchers(getHorizontalLocator(), getVerticalLocator())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        isDestroyed = false

        updateStatusBarColorForDimType()

        val themeContext = if (content.theme() == R.style.SbisContainer)
            ThemeContextBuilder(requireContext(), R.attr.sbisContainerTheme, content.theme()).build()
        else
            ContextThemeWrapper(requireContext(), content.theme())

        val themedInflater = inflater.cloneInContext(themeContext)

        val headerView = content.getHeaderView(themeContext, this)
        val theme = themeContext.theme
        val attributes = theme.obtainStyledAttributes(R.styleable.SbisContainer)

        val rootView: ViewGroup = if (headerView != null) {
            (headerView as? BaseHeader)?.addCloseListener { containerViewModel.closeContainer() }
            val headerContent = SbisHeaderContainerBinding.inflate(themedInflater, container, false)
            if (dimType == DimType.SHADOW) headerContent.sbisContainer.elevation =
                resources.getDimension(R.dimen.container_elevation_size)
            (headerView as? BaseHeader)?.isDividerVisible = attributes.getBoolean(
                R.styleable.SbisContainer_SbisContainerHeaderDividerVisibility,
                true
            )
            headerContent.headerView.addView(headerView)
            headerContent.root
        } else {
            val containerView = SbisContainerBinding.inflate(themedInflater, container, false)
            if (dimType == DimType.SHADOW) containerView.sbisContainer.elevation =
                resources.getDimension(R.dimen.container_elevation_size)
            val color = attributes.getColor(
                R.styleable.SbisContainer_ContainerBackground,
                ContextCompat.getColor(themeContext, RDesign.color.palette_color_white1)
            )
            containerView.sbisContainer.setCardBackgroundColor(color)
            containerView.root
        }

        val containerContentView = rootView.findViewById<ViewGroup>(R.id.sbis_container_content)
        addContentOffsetIfNeed(containerContentView)

        when (val content = content) {
            is ViewContent -> {
                val view = content.getView(this, containerContentView)
                view.setContentHeightChangeListener()
                containerContentView.addView(view)
            }

            is FragmentContent -> {
                containerContentView.onNextHierarchyChange {
                    contentAdded()
                }
                val findFragmentByTag = childFragmentManager.findFragmentByTag(CONTAINER_CONTENT_TAG)
                if (findFragmentByTag == null) {
                    val fragment = content.getFragment(this)
                    childFragmentManager.beginTransaction()
                        .add(R.id.sbis_container_content, fragment, CONTAINER_CONTENT_TAG)
                        .commit()
                    fragment.setContentHeightChangeListener()
                } else {
                    content.onRestoreFragment(this, findFragmentByTag)
                }
            }

            else -> error("Content must implement one of two interfaces ViewContent or FragmentContent")
        }
        subscribeToVM()
        rootView.isClickable = false
        rootView.isFocusable = false
        if (isCloseOnTouchOutside) {
            rootView.setOnClickListener { _ ->
                dialog?.let { onCancel(it) }
                dismiss()
            }
        }
        isCancelable = isDialogCancelable
        outerCutCornersRadius = getOuterCutCornersRadius()
        return rootView
    }

    /**
     * Cм. [View.setContentHeightChangeListener].
     */
    private fun Fragment.setContentHeightChangeListener() = this@SbisContainerImpl.lifecycleScope.launch {
        withResumed {
            view?.setContentHeightChangeListener()
        }
    }

    /**
     * Метод вешает на [View] контента слушатель об изменении высоты. Если высота изменилась, при этом высота
     * контейнера [customHeight] не задана жёстко (не MATCH_PARENT или конкретная величина), тогда перемериваем
     * контейнер и обновляем положение относительно локаторов.
     */
    private fun View.setContentHeightChangeListener() {
        addOnLayoutChangeListener { _, _, newTop, _, newBottom, _, oldTop, _, oldBottom ->
            val oldViewHeight = oldBottom - oldTop
            val newViewHeight = newBottom - newTop
            if (oldViewHeight != 0 && newViewHeight != oldViewHeight && customHeight == 0) contentAdded()
        }
    }

    /**
     * Вытащить значение "радиуса скругления углов затемнения контейнера" из соответствующего атрибута
     */
    private fun getOuterCutCornersRadius(): Float = try {
        requireContext().getDimen(R.attr.containerDimCornerRadius)
    } catch (e: Resources.NotFoundException) {
        0f
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.apply {
            if (isTranslateTouchToParent) {
                decorView.setOnTouchListener(this@SbisContainerImpl)
            }
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

            if (!isAnimated) {
                attributes.windowAnimations = ID_NULL
            }

            ViewCompat.setOnApplyWindowInsetsListener(decorView, onApplyWindowInsetsListener)
            decorView.requestApplyInsetsWhenAttached()
        }
        checkStatusBarShouldBeShown()

        (content as? ViewContent)?.let {
            contentAdded()
            it.onViewCreated(this)
        }
        addAdjustResizeHelper(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        requireActivity().dispatchTouchEvent(event)
        return false
    }

    override fun onDestroyView() {
        if (replacedVerticalLocator != null) {
            getOrCreateArguments().putParcelable(LOCATOR_V_ARG, replacedVerticalLocator)
            replacedVerticalLocator = null
        }
        removeAdjustResizeHelper(requireView())
        super.onDestroyView()
        disposer.clear()
        isDestroyed = true
        if (destroyLocators) {
            dimUpdater.close()
            getVerticalLocator().dispose()
            getHorizontalLocator().dispose()
        }
    }

    override fun onPause() {
        super.onPause()
        if (creator !is Parcelable) {
            dismissAllowingStateLoss()
        }
    }

    override fun show(
        fragmentManager: FragmentManager,
        horizontalLocator: HorizontalLocator,
        verticalLocator: VerticalLocator,
        isSync: Boolean
    ) {
        if (fragmentManager.isStateSaved) return
        applyWatcher(horizontalLocator, verticalLocator)

        getOrCreateArguments().putParcelable(LOCATOR_H_ARG, horizontalLocator)
        getOrCreateArguments().putParcelable(LOCATOR_V_ARG, verticalLocator)

        if (isSync) {
            showNow(fragmentManager, containerTag)
        } else {
            show(fragmentManager, containerTag)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        lifecycleScope.launchWhenStarted {
            containerViewModel.onDismissContainer.emit(Unit)
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        lifecycleScope.launchWhenStarted {
            containerViewModel.onCancelContainer.emit(Unit)
        }
    }

    private fun getCustomDimension(@DimenRes customContentDimensionRes: Int): Int {
        if (customContentDimensionRes == ID_NULL) return DEFAULT_CUSTOM_DIMENSION
        val typedValue = TypedValue()
        resources.getValue(customContentDimensionRes, typedValue, true)
        return when (typedValue.resourceId) {
            RDesign.dimen.match_parent -> Int.MAX_VALUE
            RDesign.dimen.wrap_content -> 0
            else -> resources.getDimensionPixelSize(customContentDimensionRes)
        }
    }

    private fun contentAdded() {
        configureView(
            measureContent(requireView() as ViewGroup),
            requireView().findViewById(R.id.sbis_container)
        )
    }

    private fun subscribeToVM() = with(containerViewModel) {
        onCloseSelf.collectOnStart(viewLifecycleOwner) {
            if (!isDestroyed) dismiss()
        }

        onSetContent.collectOnStart(viewLifecycleOwner) { newContentCreator ->
            creator = newContentCreator
        }

        onUpdateContent.collectOnStart(viewLifecycleOwner) { newContentCreator ->
            val dimType = dimType
            val isAnimated = isAnimated
            // Отключаем уничтожение локаторов они будут пере использованы при показе нового контента.
            // Нужно что бы не пересоздавать вью с затенением во избежании промаргивания
            destroyLocators = false

            val horizontalLocator = getHorizontalLocator()
            val verticalLocator = getVerticalLocator()
            if (nextContainerInPreviousPosition) {
                fixedLocatorPosition(horizontalLocator.locator)
                fixedLocatorPosition(verticalLocator.locator)
            }
            newInstance(newContentCreator).apply {
                this.dimType = dimType
                this.isAnimated = isAnimated
            }.show(parentFragmentManager, horizontalLocator, verticalLocator)
        }
        transferDismissListenerToViewModel()
    }

    private fun fixedLocatorPosition(locator: Locator) {
        val anchorLocator = locator as? AnchorLocator
        anchorLocator?.apply {
            val anchorPositionCalculator = positionCalculator as AnchorPositionCalculator
            anchorPositionCalculator.srcAnchorData.force = true
            anchorPositionCalculator.alignmentPriority = listOf(anchorPositionCalculator.finalAlignment)
        }
    }

    private fun configureView(contentRect: Rect, view: CardView) {
        // Устанавливаем пустой листенер что бы фон не передавал клики родителю
        view.setOnClickListener { }

        view.updateLayoutParams<ViewGroup.LayoutParams> {
            if (customWidth > 0) {
                width = customWidth
            }
            if (customHeight > 0) {
                height = customHeight
            }
        }

        val horizontalLocator = getHorizontalLocator()
        val verticalLocator = getVerticalLocator()

        view.subscribeToLocators(horizontalLocator, verticalLocator)

        applyLocators(horizontalLocator, verticalLocator, contentRect)

    }

    private fun applyLocators(
        horizontalLocator: HorizontalLocator,
        verticalLocator: VerticalLocator,
        contentRect: Rect
    ) {
        val parent = findParent()
        verticalLocator.apply(requireView(), parent, contentRect)
        horizontalLocator.apply(requireView(), parent, contentRect)
    }

    private fun findParent() = parentFragment?.view?.rootView ?: requireActivity().window.decorView

    private fun View.subscribeToLocators(horizontalLocator: HorizontalLocator, verticalLocator: VerticalLocator) {
        disposer.add(
            combineLatest(
                horizontalLocator.offsetSubject,
                verticalLocator.offsetSubject
            ) { horizontalData, verticalData ->
                updateLayoutParams<FrameLayout.LayoutParams> {
                    gravity = horizontalData.gravity or verticalData.gravity

                    if (horizontalData.gravity == Gravity.END) {
                        rightMargin = horizontalData.position
                    } else {
                        leftMargin = horizontalData.position
                    }

                    if (verticalData.gravity == Gravity.BOTTOM) {
                        if (keyboardHeight == 0 || isAndroidNougat()) bottomMargin = verticalData.position
                        topMargin = 0
                    } else {
                        topMargin = verticalData.position
                        bottomMargin = 0
                    }

                    if (horizontalData.maxSize > 0) {
                        width = horizontalData.maxSize
                    }

                    val verticalSrcData = (getVerticalLocator().locator as ScreenLocator).positionCalculator.srcData
                    val verticalMaxSize = verticalData.maxSize - bottomMargin - topMargin
                    if (bottomMargin == 0) bottomMargin += verticalSrcData.marginEnd
                    height = when {
                        verticalMaxSize in 1 until customHeight -> verticalMaxSize
                        customHeight > 0 -> customHeight
                        else -> WRAP_CONTENT
                    }
                }

                visibility = VISIBLE
                dimUpdater.isAnimated = isAnimated
                if (horizontalLocator.locator !is AnchorLocator && horizontalLocator.locator !is AnchorLocator) {
                    dimUpdater.onAnchorUpdate(findParent() as ViewGroup, null)
                }
                if (isAnimated) {
                    startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.container_fade_in))
                }
            }.subscribe()
        )
    }

    private fun measureContent(rootView: ViewGroup): Rect {
        val contentRect = Rect()
        val displayMetrics = getDisplayMetrics()

        val measureWidth = if (customWidth == 0) {
            MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels, MeasureSpec.AT_MOST)
        } else {
            MeasureSpec.makeMeasureSpec(customWidth, MeasureSpec.EXACTLY)
        }
        val measureHeight = if (customHeight == 0) {
            MeasureSpec.makeMeasureSpec(displayMetrics.heightPixels, MeasureSpec.AT_MOST)
        } else {
            MeasureSpec.makeMeasureSpec(customHeight, MeasureSpec.EXACTLY)
        }
        rootView.measure(measureWidth, measureHeight)

        contentRect.right = if (customWidth > 0) customWidth else rootView.measuredWidth
        contentRect.bottom = if (customHeight > 0) customHeight else rootView.measuredHeight
        return contentRect
    }

    private fun applyWatcher(horizontalLocator: HorizontalLocator, verticalLocator: VerticalLocator) {
        if (horizontalLocator is ScreenHorizontalLocator && verticalLocator is ScreenVerticalLocator) return
        // horizontal
        when (horizontalLocator) {
            is AnchorHorizontalLocator -> {
                val anchorView: View = horizontalLocator.anchorView
                if (findViewParent<RecyclerView>(anchorView) != null) {
                    RecyclerAnchorWatcher()
                } else {
                    BaseAnchorWatcher()
                }.also {
                    it.dimUpdater = dimUpdater
                    it.setAnchorView(anchorView)
                    horizontalLocator.setWatcher(it, keyboardEnable)
                }
            }

            is TagAnchorHorizontalLocator -> {
                TagAnchorWatcher(horizontalLocator.anchorTag, horizontalLocator.parentTag).also {
                    it.dimUpdater = dimUpdater
                    horizontalLocator.setWatcher(it)
                }
            }

            is ScreenHorizontalLocator -> Unit
        }
        // vertical
        when (verticalLocator) {
            is AnchorVerticalLocator -> {
                val anchorView: View = verticalLocator.anchorView
                if (findViewParent<RecyclerView>(anchorView) != null) {
                    RecyclerAnchorWatcher()
                } else {
                    BaseAnchorWatcher()
                }.also {
                    it.dimUpdater = dimUpdater
                    it.setAnchorView(anchorView)
                    verticalLocator.setWatcher(it, keyboardEnable)
                }
            }

            is TagAnchorVerticalLocator -> {
                TagAnchorWatcher(verticalLocator.anchorTag, verticalLocator.parentTag).also {
                    it.dimUpdater = dimUpdater
                    verticalLocator.setWatcher(it)
                }
            }

            is ScreenVerticalLocator -> Unit
        }
    }

    /**
     * Метод устанавливающий один инстанс вотчера для обоих локаторов после восстановления при смене конфигурации
     */
    private fun mergeWatchers(horizontalLocator: HorizontalLocator, verticalLocator: VerticalLocator) {
        if (verticalLocator is AnchorVerticalLocator &&
            verticalLocator.anchorWatcher != null &&
            horizontalLocator is AnchorHorizontalLocator &&
            horizontalLocator.anchorWatcher != null
        ) {
            horizontalLocator.setWatcher(verticalLocator.anchorWatcher)
            horizontalLocator.anchorWatcher?.dimUpdater = dimUpdater
        }

        if (verticalLocator is TagAnchorVerticalLocator &&
            verticalLocator.anchorLocator.anchorWatcher != null &&
            horizontalLocator is TagAnchorHorizontalLocator &&
            horizontalLocator.anchorLocator.anchorWatcher != null
        ) {
            horizontalLocator.setWatcher(verticalLocator.anchorLocator.anchorWatcher)
            horizontalLocator.anchorLocator.anchorWatcher?.dimUpdater = dimUpdater
        }
    }

    private fun resizeRootIfNeeded(containerBottomMargin: Int, containerHeight: Int) {
        if (isAndroidNougat()) {
            return
        }
        val availableHeight = requireView().measuredHeight - keyboardHeight
        val parentBottomMargin = when {
            keyboardHeight == 0 -> 0
            availableHeight < containerHeight -> keyboardHeight
            else -> keyboardHeight - containerBottomMargin
        }
        if (requireView().marginBottom == parentBottomMargin) return
        requireView().updateLayoutParams<MarginLayoutParams> {
            bottomMargin = parentBottomMargin
        }
    }

    private fun addAdjustResizeHelper(view: View) {
        viewTreeObserver = view.viewTreeObserver
        viewTreeObserver?.addOnGlobalLayoutListener(adjustResizeHelper)
    }

    private fun removeAdjustResizeHelper(view: View) {
        val actualTreeObserver = view.viewTreeObserver
        if (viewTreeObserver != null && viewTreeObserver?.isAlive == true) {
            viewTreeObserver?.removeOnGlobalLayoutListener(adjustResizeHelper)
        } else if (actualTreeObserver.isAlive) {
            actualTreeObserver.removeOnGlobalLayoutListener(adjustResizeHelper)
        }
        viewTreeObserver = null
    }

    private fun getVerticalLocator(): VerticalLocator =
        getOrCreateArguments().getParcelableUniversally(LOCATOR_V_ARG) ?: ScreenVerticalLocator()

    private fun getHorizontalLocator(): HorizontalLocator =
        getOrCreateArguments().getParcelableUniversally(LOCATOR_H_ARG) ?: ScreenHorizontalLocator()

    private fun getOrCreateArguments(): Bundle {
        if (arguments == null) {
            arguments = Bundle()
        }
        return arguments as Bundle
    }

    private fun getDisplayMetrics() = DisplayMetrics().apply {
        val defaultDisplay = (requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        defaultDisplay.getMetrics(this)
    }

    /**
     * Если листенер уже передан во ViewModel, ничего не делаем.
     * Если же ещё не передан, то если ViewModel уже доступна,
     * сразу передаем листенер в неё, если не доступна -
     * сохраняем и передаём позже.
     */
    private fun transferDismissListenerToViewModel() {
        if (onDismissListenerTransferredToViewModel) {
            return
        }
        if (context != null) { // необходимое условие для получения viewModel
            containerViewModel.additionalOnDismissListener = onDismissListener
            onDismissListenerTransferredToViewModel = true
            onDismissListener = null
        }
    }

    private fun addContentOffsetIfNeed(containerContentView: View) {
        val horizontalMargin = if (content.useDefaultHorizontalOffset())
            Offset.M.getDimenPx(containerContentView.context)
        else 0
        containerContentView.setHorizontalMargin(horizontalMargin, horizontalMargin)
    }

    private fun isAndroidNougat() =
        Build.VERSION.SDK_INT == Build.VERSION_CODES.N || Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1

    private fun updateStatusBarColorForDimType() {
        if (dimType == DimType.SHADOW || dimType == DimType.NONE) {
            updateStatusBarMode(requireContext(), requireDialog().window, requireActivity().window.statusBarColor)
        }
    }

    private fun <T> Flow<T>.collectOnStart(lifecycleOwner: LifecycleOwner, action: (T) -> Unit) {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                collect {
                    action(it)
                }
            }
        }
    }
}

private const val DIM_TYPE_ARG = "SbisContainerImpl:DIM_TYPE_ARG"
private const val IS_ANIMATED_ARG = "SbisContainerImpl:IS_ANIMATED_ARG"
private const val CUT_OUT_CORNER_RADIUS = "SbisContainerImpl:DIM_CORNER_RADIUS"
private const val IS_CLOSE_ON_TOUCH_OUTSIDE_ARG = "SbisContainerImpl:IS_CLOSE_ON_TOUCH_OUTSIDE_ARG"
private const val IS_DIALOG_CANCELABLE_ARG = "SbisContainerImpl:IS_DIALOG_CANCELABLE_ARG"
private const val IS_TRANSLATE_TOUCH_TO_PARENT = "SbisContainerImpl:IS_TRANSLATE_TOUCH_TO_PARENT"
private const val CONTENT_CREATOR_ARG = "SbisContainerImpl:CONTENT_CREATOR_ARG"
private const val LOCATOR_V_ARG = "SbisContainerImpl:LOCATOR_V"
private const val LOCATOR_H_ARG = "SbisContainerImpl:LOCATOR_H"
private const val LISTENER_TRANSFERRED_ARG = "SbisContainerImpl:LISTENER_TRANSFERRED"
const val CONTAINER_DEFAULT_TAG = "ContainerFragment"
internal const val CONTAINER_CONTENT_TAG = "ContainerFragmentContent"
internal const val DEFAULT_CUSTOM_DIMENSION = 0
