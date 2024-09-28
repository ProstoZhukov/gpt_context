package ru.tensor.sbis.design.list_utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.Px
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.design.list_utils.util.RecyclerViewScrollTracer
import ru.tensor.sbis.design.progress.SbisLoadingIndicator
import ru.tensor.sbis.design.progress.SbisPullToRefresh

/**
 * Container with RecyclerView inside SwipeRefreshLayout, InformationView and ProgressBar.
 * Useful for registers (ex. Contacts/Dialogs/Notifications)
 */
abstract class AbstractListView<InformationView : View, InformationViewContent> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var pullToRefresh: SbisPullToRefresh? = null
    private var pullToRefreshEnabled = true
    private var informationView: InformationView
    private var progressBar: SbisLoadingIndicator? = null
    private var showProgressBar: Runnable = Runnable {
        progressResolver.setCanShowProgress(true)
        updateViewState()
    }
    private var progressBarHandler: Handler? = null
    private var progressText: TextView? = null
    private var adapter: RecyclerView.Adapter<*>? = null
    private val progressResolver = ProgressResolver()
    private var forcedRecyclerVisibility = UNSPECIFIED
    private var shouldFadeInRecyclerViewBackground = true

    private val dataObserver: RecyclerView.AdapterDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            updateViewState()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            updateViewState()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            updateViewState()
        }
    }

    /** @SelfDocumented */
    var recyclerView: RecyclerView
        private set

    /** @SelfDocumented */
    var isIgnoreProgress = false

    /**
     * Возвращает задержку в миллисекундах при отображении прогрессбара
     */
    var progressDelayMillis = 0
        private set

    /** @SelfDocumented */
    var isRefreshing: Boolean
        get() = pullToRefresh?.isRefreshing == true
        set(refreshing) {
            pullToRefresh?.isRefreshing = refreshing
        }

    /** @SelfDocumented */
    val isInformationViewVisible: Boolean
        get() = informationView.visibility == VISIBLE


    /** @SelfDocumented */
    val recycledViewPool: RecyclerView.RecycledViewPool
        get() = recyclerView.recycledViewPool


    /** @SelfDocumented */
    val layoutManager: LayoutManager?
        get() = recyclerView.layoutManager

    /** @SelfDocumented */
    @get:Px
    var recyclerViewBottomPadding: Int
        get() = recyclerView.paddingBottom
        set(paddingBottom) {
            recyclerView.setPadding(
                recyclerView.paddingLeft,
                recyclerView.paddingTop,
                recyclerView.paddingRight,
                paddingBottom
            )
        }

    init {
        var withSwipe = true
        var matchScreen = true
        var withProgress = true
        var applyProgressColor = true
        var recyclerViewFitsSystemWindows = true
        var placeholderTopGravity = false
        var recyclerViewBackground = UNSPECIFIED

        context.withStyledAttributes(attrs, R.styleable.AbstractListView, defStyleAttr) {
            withSwipe = getBoolean(R.styleable.AbstractListView_withSwipe, true)
            matchScreen = getBoolean(R.styleable.AbstractListView_matchScreen, true)
            withProgress = getBoolean(R.styleable.AbstractListView_withProgress, true)
            applyProgressColor = getBoolean(R.styleable.AbstractListView_applyProgressColor, true)
            progressDelayMillis = getInteger(R.styleable.AbstractListView_progressDelayMillis, 0)
            recyclerViewBackground =
                getColor(R.styleable.AbstractListView_recyclerViewBackgroundColor, UNSPECIFIED)
            shouldFadeInRecyclerViewBackground =
                getBoolean(R.styleable.AbstractListView_fadeInRecyclerViewBackground, true)
            recyclerViewFitsSystemWindows =
                getBoolean(R.styleable.AbstractListView_recyclerViewFitsSystemWindows, true)
            placeholderTopGravity = getBoolean(R.styleable.AbstractListView_placeholderTopGravity, false)
        }

        recyclerView = RecyclerViewWithBackgroundAndItemsAppearanceSync(
            ContextThemeWrapper(context, R.style.ListUtilsVerticalScrollbarRecyclerView)
        )
        recyclerView.id = R.id.list_view_recycler_view_id
        recyclerView.clipToPadding = false
        recyclerView.fitsSystemWindows = recyclerViewFitsSystemWindows
        var swipeProgressDiameter = 0
        val screenParamInt: Int = if (matchScreen) {
            LayoutParams.MATCH_PARENT
        } else {
            LayoutParams.WRAP_CONTENT
        }
        val informationViewPaddingHorizontal =
            resources.getDimensionPixelOffset(ru.tensor.sbis.design.R.dimen.empty_view_padding)
        val informationViewLayoutParams = LayoutParams(screenParamInt, screenParamInt)
        informationViewLayoutParams.gravity = Gravity.CENTER
        informationView = createInformationView(this)
        if (informationView.id == NO_ID) {
            informationView.id = R.id.list_view_information_view_id
        }
        informationView.visibility = GONE
        if (informationView is LinearLayout) {
            (informationView as LinearLayout).orientation = LinearLayout.VERTICAL
            if (placeholderTopGravity) {
                (informationView as LinearLayout).gravity = Gravity.TOP
            } else {
                (informationView as LinearLayout).gravity = Gravity.CENTER
            }
        }
        if (matchScreen) {
            minimumHeight = resources.getDimensionPixelOffset(ru.tensor.sbis.design.R.dimen.empty_view_min_height)
        }
        informationView.setPadding(
            informationViewPaddingHorizontal,
            informationView.paddingTop,
            informationViewPaddingHorizontal,
            informationView.paddingBottom
        )
        informationView.layoutParams = informationViewLayoutParams
        this.addView(informationView)
        if (withSwipe) {
            pullToRefresh = SbisPullToRefresh(context).also { pullToRefresh ->
                pullToRefresh.addView(recyclerView, LayoutParams.MATCH_PARENT, screenParamInt)
                this.addView(pullToRefresh, LayoutParams.MATCH_PARENT, screenParamInt)
                swipeProgressDiameter = pullToRefresh.progressCircleDiameter
            }
        } else {
            this.addView(recyclerView, LayoutParams.MATCH_PARENT, screenParamInt)
        }
        if (withProgress) {
            progressBar = SbisLoadingIndicator(context, attrs).also { progress ->
                progress.id = R.id.list_view_progress_view_id
                progress.strictlyUseProgressDrawable = !applyProgressColor
                val progressParams: LayoutParams = if (swipeProgressDiameter != 0) {
                    LayoutParams(swipeProgressDiameter, swipeProgressDiameter)
                } else {
                    LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT
                    )
                }
                progressParams.gravity = Gravity.CENTER
                progress.layoutParams = progressParams
            }

            this.addView(progressBar)
            progressText =
                TextView(
                    context,
                    null,
                    android.R.attr.textViewStyle,
                    ru.tensor.sbis.design.R.style.SbisTextView
                ).also { progressText ->
                    val textParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                    textParams.gravity = Gravity.CENTER
                    textParams.topMargin =
                        resources.getDimensionPixelOffset(ru.tensor.sbis.design.R.dimen.progress_bar_text_margin)
                    progressText.layoutParams = textParams
                    progressText.setTextColor(
                        ContextCompat.getColor(
                            context,
                            ru.tensor.sbis.design.R.color.text_color_black_2
                        )
                    )
                }
            this.addView(progressText)

            progressBarHandler = Handler()
        }
        if (recyclerViewBackground != UNSPECIFIED) {
            setRecyclerViewBackgroundColor(recyclerViewBackground)
        }
        restartProgress()
        addScrollTracing()
    }

    fun setLayoutManager(layoutManager: LayoutManager?) {
        recyclerView.layoutManager = layoutManager
    }

    /** @SelfDocumented */
    fun setEmptyViewTextColor(@ColorInt color: Int) {
        setEmptyViewTextColor(informationView, color)
    }

    /** @SelfDocumented */
    fun setRecyclerViewTopPadding(@Px paddingTop: Int) {
        recyclerView.setPadding(
            recyclerView.paddingLeft,
            paddingTop,
            recyclerView.paddingRight,
            recyclerView.paddingBottom
        )
    }

    /** @SelfDocumented */
    fun setInformationViewPaddingBottom(@Px paddingBottom: Int) {
        setInformationViewVerticalPadding(informationView.paddingTop, paddingBottom)
    }

    /** @SelfDocumented */
    fun setInformationViewVerticalPadding(@Px paddingTop: Int, @Px paddingBottom: Int) {
        informationView.setPadding(
            informationView.paddingLeft,
            paddingTop,
            informationView.paddingRight,
            paddingBottom
        )
        informationView.invalidate()
    }

    /** @SelfDocumented */
    fun setInformationViewMarginBottom(@Px marginBottom: Int) {
        val lp = informationView.layoutParams as MarginLayoutParams
        setInformationViewVerticalMargin(lp.topMargin, marginBottom)
    }

    /** @SelfDocumented */
    fun setInformationViewVerticalMargin(@Px marginTop: Int, @Px marginBottom: Int) {
        informationView.updateLayoutParams<MarginLayoutParams> {
            setMargins(
                leftMargin,
                marginTop,
                rightMargin,
                marginBottom
            )
        }
    }

    /** @SelfDocumented */
    fun setProgressBarVerticalMargin(@Px marginTop: Int, @Px marginBottom: Int) {
        progressBar?.updateLayoutParams<MarginLayoutParams> {
            setMargins(
                0,
                marginTop,
                0,
                marginBottom
            )
        }
    }

    /** @SelfDocumented */
    fun setProgressBarPaddingBottom(@Px paddingBottom: Int) {
        progressBar?.let { progress ->
            setProgressBarVerticalPadding(progress.paddingTop, paddingBottom)
        }
    }

    /** @SelfDocumented */
    fun setProgressBarVerticalPadding(@Px paddingTop: Int, @Px paddingBottom: Int) {
        progressBar?.let { progress ->
            progress.setPadding(
                progress.paddingLeft,
                paddingTop,
                progress.paddingRight,
                paddingBottom
            )
            progress.invalidate()
        }
    }

    /** @SelfDocumented */
    fun setRecyclerViewBackgroundColor(@ColorInt color: Int) {
        recyclerView.setBackgroundColor(color)
    }

    /** @SelfDocumented */
    fun setRecyclerViewVerticalScrollbarEnabled(enabled: Boolean) {
        recyclerView.isVerticalScrollBarEnabled = enabled
    }

    /** @SelfDocumented */
    fun setRecyclerViewHorizontalScrollbarEnabled(enabled: Boolean) {
        recyclerView.isHorizontalScrollBarEnabled = enabled
    }

    /** @SelfDocumented */
    fun setSwipeProgressViewOffset(scale: Boolean, start: Int, end: Int) {
        pullToRefresh?.setProgressViewOffset(scale, start, end)
    }

    /** @SelfDocumented */
    open fun setSwipeColorSchemeResources(@ColorRes vararg colorResIds: Int) {
        pullToRefresh?.setColorSchemeResources(*colorResIds)
    }

    /** @SelfDocumented */
    fun setOnRefreshListener(onRefreshListener: SwipeRefreshLayout.OnRefreshListener?) {
        pullToRefresh?.setOnRefreshListener(onRefreshListener)
    }

    /** @SelfDocumented */
    fun setSwipeRefreshEnabled(enabled: Boolean) {
        pullToRefresh?.let {
            pullToRefreshEnabled = enabled
            it.isEnabled = enabled
        }
    }

    /** @SelfDocumented */
    fun addOnScrollListener(scrollListener: RecyclerView.OnScrollListener) {
        recyclerView.addOnScrollListener(scrollListener)
    }

    /** @SelfDocumented */
    fun removeOnScrollListener(scrollListener: RecyclerView.OnScrollListener) {
        recyclerView.removeOnScrollListener(scrollListener)
    }

    /** @SelfDocumented */
    fun clearOnScrollListener() {
        recyclerView.clearOnScrollListeners()
    }

    /** @SelfDocumented */
    open fun scrollToPosition(position: Int) {
        recyclerView.scrollToPosition(position)
    }

    /** @SelfDocumented */
    open fun smoothScrollToPosition(position: Int) {
        recyclerView.smoothScrollToPosition(position)
    }

    /** @SelfDocumented */
    fun scrollRecyclerBy(@Px x: Int, @Px y: Int) {
        recyclerView.scrollBy(x, y)
    }

    /** @SelfDocumented */
    fun setHasFixedSize(hasFixedSize: Boolean) {
        recyclerView.setHasFixedSize(hasFixedSize)
    }

    /** @SelfDocumented */
    fun addItemDecoration(decor: RecyclerView.ItemDecoration?) {
        recyclerView.addItemDecoration(decor!!)
    }

    /** @SelfDocumented */
    fun removeItemDecoration(decor: RecyclerView.ItemDecoration?) {
        recyclerView.removeItemDecoration(decor!!)
    }

    /** @SelfDocumented */
    open fun setAdapter(adapter: RecyclerView.Adapter<*>?) {
        this.adapter?.unregisterAdapterDataObserver(dataObserver)
        this.adapter = adapter
        this.adapter?.registerAdapterDataObserver(dataObserver)
        recyclerView.adapter = adapter
        updateViewState()
    }

    /** @SelfDocumented */
    open fun swapAdapter(
        adapter: RecyclerView.Adapter<*>?,
        removeAndRecycleExistingViews: Boolean
    ) {
        this.adapter?.unregisterAdapterDataObserver(dataObserver)
        this.adapter = adapter
        this.adapter?.registerAdapterDataObserver(dataObserver)
        recyclerView.swapAdapter(adapter, removeAndRecycleExistingViews)
        updateViewState()
    }

    /** @SelfDocumented */
    open fun updateViewState() {
        val adapterIsEmpty = adapter == null || adapter?.itemCount == 0
        val showProgress = progressResolver.canShowProgress() && adapterIsEmpty
        pullToRefresh?.isEnabled = pullToRefreshEnabled && !showProgress
        progressBar?.isVisible = !isIgnoreProgress && showProgress
        progressText?.isVisible = !isIgnoreProgress && showProgress
        informationView.isVisible = !progressResolver.isInProgress && adapterIsEmpty
        val recyclerViewVisibility = when {
            forcedRecyclerVisibility != UNSPECIFIED -> forcedRecyclerVisibility
            adapterIsEmpty -> INVISIBLE
            else -> VISIBLE
        }
        recyclerView.visibility = recyclerViewVisibility
        if (shouldFadeInRecyclerViewBackground && recyclerViewVisibility == INVISIBLE) {
            hideRecyclerViewBackground()
        }
    }

    /** @SelfDocumented */
    fun setRecyclerViewVisibilityStatus(visibility: Int) {
        forcedRecyclerVisibility = visibility
        updateViewState()
    }

    /** @SelfDocumented */
    fun showInformationViewData(content: InformationViewContent?) {
        applyInformationViewData(informationView, content)
        setInProgress(false)
        updateViewState()
    }

    /** @SelfDocumented */
    fun hideInformationView() {
        setInProgress(true)
        updateViewState()
    }

    /**
     * Отличается от метода [hideInformationView] тем, что учитывает задержку,
     * установленную для появления прогресс бара, то есть заглушка будет скрыта синхронно с появлением прогресса,
     * исключая белый экран.
     */
    fun postHideInformationView() {
        setInProgress(true)
        if (progressDelayMillis <= 0) {
            updateViewState()
        }
    }

    /** @SelfDocumented */
    fun ignoreProgress(ignore: Boolean) {
        isIgnoreProgress = ignore
        updateViewState()
    }

    /** @SelfDocumented */
    fun setInProgress(inProgress: Boolean) {
        if (inProgress) {
            restartProgress()
        }
        progressResolver.isInProgress = inProgress
    }

    /** @SelfDocumented */
    fun disableOnChangeAnimation() {
        (recyclerView.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
    }

    /** @SelfDocumented */
    fun setProgressText(text: CharSequence?) {
        progressText?.text = text
    }

    /** @SelfDocumented */
    fun restartProgress() {
        if (progressDelayMillis > 0) {
            progressResolver.setCanShowProgress(false)
            progressBarHandler?.removeCallbacks(showProgressBar)
            progressBarHandler?.postDelayed(showProgressBar, progressDelayMillis.toLong())
        } else {
            progressResolver.setCanShowProgress(true)
        }
    }

    /**
     * Скролит recycler к первому элементу
     */
    open fun scrollToTop() {
        scrollToPosition(0)
    }

    /**
     * @return есть ли пространство для скролла к верхней границе списка
     */
    fun canScrollToTop(): Boolean {
        return recyclerView.canScrollVertically(-1)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val stateBundle = Bundle()
        stateBundle.putParcelable(SUPER_BUNDLE, super.onSaveInstanceState())
        stateBundle.putBoolean(PROGRESS_STATE, progressResolver.isInProgress)
        return stateBundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            super.onRestoreInstanceState(state.getParcelable(SUPER_BUNDLE))
            setInProgress(state.getBoolean(PROGRESS_STATE))
        }
        updateViewState()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        // свайп находится поверх EmptyView и перехватывает все жесты
        if (pullToRefresh?.isEnabled == true && informationView.visibility == VISIBLE) {
            informationView.dispatchTouchEvent(ev)
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun setVerticalScrollBarEnabled(verticalScrollBarEnabled: Boolean) {
        recyclerView.isVerticalScrollBarEnabled = verticalScrollBarEnabled
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setOnTouchListener(l: OnTouchListener) {
        recyclerView.setOnTouchListener(l)
    }

    /** @SelfDocumented */
    protected abstract fun createInformationView(container: ViewGroup): InformationView

    /** @SelfDocumented */
    protected abstract fun applyInformationViewData(
        informationView: InformationView,
        content: InformationViewContent?
    )

    /** @SelfDocumented */
    protected open fun setEmptyViewTextColor(
        informationView: InformationView,
        @ColorInt color: Int
    ) {
        //by default do nothing
    }

    private fun setRecyclerViewBackgroundAlpha(alpha: Int) {
        recyclerView.background?.alpha = alpha
    }

    private fun hideRecyclerViewBackground() {
        setRecyclerViewBackgroundAlpha(0)
    }

    private fun addScrollTracing() {
        val idName = try {
            resources.getResourceName(id)
        } catch (e: Exception) {
            StringUtils.EMPTY
        }
        addOnScrollListener(RecyclerViewScrollTracer("${javaClass.simpleName}#$idName"))
    }

    /**
     * Необходим для изменения значения alpha фона синхронно c элементами списка при их анимированном появлении
     */
    private inner class RecyclerViewWithBackgroundAndItemsAppearanceSync(context: Context) : RecyclerView(context) {
        override fun onDraw(c: Canvas) {
            increaseRecyclerViewBackgroundAlphaAccordingToItemAlpha()
            super.onDraw(c)
        }

        private fun increaseRecyclerViewBackgroundAlphaAccordingToItemAlpha() {
            val background = background
            val firstItem = getChildAt(0)

            if (!shouldFadeInRecyclerViewBackground || background == null || firstItem == null) return

            val currentAlpha = background.alpha
            val newAlpha = (ALPHA_OPAQUE * firstItem.alpha).toInt()
            if (newAlpha > currentAlpha) {
                setRecyclerViewBackgroundAlpha(newAlpha)
            }
        }
    }

    companion object {
        private val SUPER_BUNDLE = AbstractListView::class.java.simpleName + ".SUPER_BUNDLE"
        private val PROGRESS_STATE = AbstractListView::class.java.simpleName + ".PROGRESS_STATE"
        private const val ALPHA_OPAQUE = 255

        /** @SelfDocumented */
        const val UNSPECIFIED = -1
    }
}

private class ProgressResolver {
    var isInProgress = true
    private var showProgress = false

    fun setCanShowProgress(canShowProgress: Boolean) {
        showProgress = canShowProgress
    }

    fun canShowProgress(): Boolean {
        return showProgress && isInProgress
    }
}