package ru.tensor.sbis.design.scroll_to_top

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import androidx.annotation.CallSuper
import androidx.annotation.Px
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import ru.tensor.sbis.design.view.input.searchinput.util.AppBarLayoutWithDynamicElevationBehavior
import ru.tensor.sbis.design.list_utils.util.AppBackgroundStateObserverDuringFragmentLifetime
import ru.tensor.sbis.design.utils.*
import kotlin.math.abs
import ru.tensor.sbis.design.R as RDesign

/**
 * Абстрактный класс хелпера для работы с компонентом [ScrollToTop]
 *
 * @author du.bykov
 */

const val DEFAULT_ANIMATION_DURATION: Long = 150
const val ANIMATION_ZERO_DURATION: Long = 0

@Deprecated("Отказываемся от ScrollToTopHelper", ReplaceWith("ScrollToTopSubscriptionHolder"))
abstract class AbstractScrollToTopHelper(
    fragment: Fragment
) : AppBarLayout.OnOffsetChangedListener {

    /**
     * Высота виджета
     */
    val scrollToTopPanelHeight: Int = fragment.resources.getDimensionPixelSize(RDesign.dimen.scroll_to_top_panel_height)

    /**
     * Высота ННП
     */
    val bottomNavBarHeight: Int = fragment.resources.getDimensionPixelSize(RDesign.dimen.bottom_navigation_height)

    protected var fragment: Fragment? = fragment

    protected val handler = Handler()

    protected var appBarLayout: AppBarLayout? = null
    private var collapsingToolbarLayout: CollapsingToolbarLayout? = null
    protected var scrollToTop: ScrollToTop? = null

    protected var ignoreAppBarOffsetChanges: Boolean = true

    protected var isFirstLaunch: Boolean = true
    @Suppress("MemberVisibilityCanBePrivate")
    protected var shouldAdjustProgressAndInformationViewPosition: Boolean = true
    private var behavior: AppBarLayoutWithDynamicElevationBehavior? = null
    private var isInitialAppBarState =
        true // Находится-ли AppBarLayout в изначальном состоянии (Не было событий скролла вниз)

    private var pinnedHeaderViewHelper: PinnedHeaderViewHelper? = null

    /**
     * Метод инициализации инструмента.
     * Выполнять инициализацию нужно после вызова [Fragment.onCreateView]
     */
    @JvmOverloads
    fun initViews(
        scrollToTop: ScrollToTop,
        appBarLayout: AppBarLayout,
        collapsingToolbarLayout: CollapsingToolbarLayout,
        pinnedHeaderView: View? = null,
        pinnedHeaderViewOffsetChangedListener: PinnedHeaderViewOffsetChangedListener? = null
    ) {
        this.scrollToTop = scrollToTop
        this.appBarLayout = appBarLayout
        this.collapsingToolbarLayout = collapsingToolbarLayout
        this.appBarLayout!!.addOnOffsetChangedListener(this)
        behavior = (appBarLayout.layoutParams as CoordinatorLayout.LayoutParams)
            .behavior as? AppBarLayoutWithDynamicElevationBehavior
        // запрещаем восстановление состояния панели "скролл в самый верх"
        scrollToTop.allowResetVisibilityFromSavedState(false)
        observeAppBackgroundState()
        pinnedHeaderView?.let {
            pinnedHeaderViewHelper = PinnedHeaderViewHelper(
                it,
                scrollToTop,
                ::updateListViewTopMargin,
                pinnedHeaderViewOffsetChangedListener
            )
        }
    }

    /**
     * Установка слушателя клика по виджету.
     */
    fun initListener(listener: View.OnClickListener?) {
        scrollToTop?.setOnClickListener(listener)
    }

    /**
     * Установка текста, отображаемого справа.
     */
    fun setRightText(rightText: String?) {
        scrollToTop?.rightText = rightText
    }

    /**
     * Установка заголовка.
     */
    fun setTitle(title: String) {
        scrollToTop?.title = title
    }

    /**
     * Освобождение ресурсов.
     */
    fun release() {
        this.appBarLayout?.removeOnOffsetChangedListener(this)
        appBarLayout = null
        behavior = null
        fragment = null
    }

    /**
     * Освобождение ресурсов и удаление ссылок на связанные объекты.
     */
    @CallSuper
    open fun dispose() {
        release()
        pinnedHeaderViewHelper = null
        collapsingToolbarLayout = null
        scrollToTop = null
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        val capturedFragment = fragment ?: return
        if (!capturedFragment.isAdded) return

        pinnedHeaderViewHelper?.onOffsetChanged(appBarLayout, verticalOffset)

        if (appBarLayout?.totalScrollRange == 0) return

        val verticalOffsetAbs = abs(verticalOffset)
        consumeInitialState(verticalOffsetAbs)
        val appBarVisibleHeight = appBarLayout?.totalScrollRange!! - verticalOffsetAbs
        val listDesiredMargin: Int

        setToolbarButtonsClickable(verticalOffset != 0)

        if (verticalOffset == 0 && isFirstLaunch) {
            handler.post {
                if (!capturedFragment.isAdded) {
                    return@post
                }
                if (shouldAdjustProgressAndInformationViewPosition) {
                    updateProgressAndInformationViewVerticalMargins()
                }
            }
            isFirstLaunch = false
        }

        if (ignoreAppBarOffsetChanges) {
            listDesiredMargin = 0
            setToolbarContentAlpha(1f)
        } else {
            listDesiredMargin = if (appBarVisibleHeight <= scrollToTopPanelHeight) {
                if (scrollToTop?.visibility != View.VISIBLE) {
                    showScrollToTopPanelContent()
                }
                scrollToTopPanelHeight - appBarVisibleHeight
            } else {
                if (scrollToTop?.visibility != View.GONE) {
                    hideScrollToTopPanelContent()
                }
                0
            }

            val contentAlpha = getToolbarContentAlpha(verticalOffsetAbs.toFloat(), appBarLayout)
            setToolbarContentAlpha(contentAlpha)
            onAlphaChanged(contentAlpha)
        }

        updateListTopMargin(listDesiredMargin)
    }

    protected open fun onAlphaChanged(alpha: Float) {

    }

    /**
     * Обработать возможное изначальное состояние [AppBarLayout].
     * В зависимости от последнего зарегистрированного значения оффсета определяется, в каком состоянии находится шапка.
     * [AppBarLayout] находится в изначальном состоянии с момента старта экрана вплоть до первого события скролла вниз.
     * После изменения значения [verticalOffsetAbs] на такое, которое будет больше, чем последнее зарегистрированное
     * (скролл вниз) - флаг изначального состояния будет сброшен.
     * При скролле вверх (с сопутствующим увеличением размера [AppBarLayout] и увеличением относительного значения offset вплоть до 0) ничего не происходит.
     * Позволяет реализовать специфическое поведение шапки
     * @see [onOffsetChanged]
     * @see [canChangeToolbarContentAlpha]
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected fun consumeInitialState(verticalOffsetAbs: Int) {
        if (isInitialAppBarState) {
            isInitialAppBarState = verticalOffsetAbs in 1..(behavior?.searchOffset ?: 1)
        }
    }

    /**
     * Получить значение альфы для контента тулбара
     * @return значение альфы
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected fun getToolbarContentAlpha(absOffset: Float, appBarLayout: AppBarLayout): Float {
        return if (!canChangeToolbarContentAlpha()) {
            1F
        } else {
            1 - 2f * absOffset / appBarLayout.totalScrollRange.toFloat()
        }
    }

    /** @SelfDocumented */
    fun isScrollToTopEnabled(): Boolean = !ignoreAppBarOffsetChanges

    /**
     * Отключить виджет
     */
    fun disableScrollToTop() {
        if (isScrollToTopEnabled()) {
            disableScrollToTopDirectly()
        }
    }

    /**
     * Отключить виджет без проверки текущего состояния
     */
    protected fun disableScrollToTopDirectly(updateMargins: Boolean = true) {
        ignoreAppBarOffsetChanges = true
        resetState()

        val lp = collapsingToolbarLayout?.layoutParams as AppBarLayout.LayoutParams
        lp.scrollFlags = 0
        collapsingToolbarLayout?.layoutParams = lp

        if (updateMargins) {
            updateProgressAndInformationViewVerticalMargins()
        }
    }

    /**
     * Включить виджет
     */
    fun enableScrollToTop() {
        if (!isScrollToTopEnabled()) {
            enableScrollToTopDirectly()
        }
    }

    /**
     * Включить виджет без проверки текущего состояния
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected fun enableScrollToTopDirectly(updateMargins: Boolean = true) {
        ignoreAppBarOffsetChanges = false

        val lp = collapsingToolbarLayout?.layoutParams as? AppBarLayout.LayoutParams
        lp?.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or
            AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS or
            AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
        collapsingToolbarLayout?.layoutParams = lp

        if (updateMargins) {
            updateProgressAndInformationViewVerticalMargins()
        }
    }

    /**
     * Сбросить текущее состояние виджета
     */
    @JvmOverloads
    fun resetState(animated: Boolean = true) {
        isFirstLaunch = true
        if (scrollToTop?.visibility != View.GONE) {
            hideScrollToTopPanelContent()
        }
        appBarLayout?.setExpanded(true, animated)
        setToolbarContentAlpha(1f)
        updateListTopMargin()
    }

    /** @SelfDocumented */
    fun disableAdjustProgressAndInformationViewPosition() {
        shouldAdjustProgressAndInformationViewPosition = false
    }

    /** @SelfDocumented */
    abstract fun updateProgressAndInformationViewVerticalMargins()

    /** @SelfDocumented */
    protected abstract fun updateListViewTopMargin(@Px topMargin: Int)

    /** @SelfDocumented */
    protected open fun setToolbarButtonsClickable(clickable: Boolean) {
    }

    /** @SelfDocumented */
    protected abstract fun setToolbarContentAlpha(alpha: Float)

    /**
     * Отобразить контент виджета
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected fun showScrollToTopPanelContent() {
        val customLayout = scrollToTop?.customView as? ViewGroup
        val showContentAnimatorSet = AnimatorSet()
        showContentAnimatorSet.interpolator = AccelerateInterpolator(2f)
        // Если контентом ScrollToTop является собственная ViewGroup, то анимируем чайлдов этой ViewGroup
        if (customLayout != null) {
            performAnimatingCustomInsides(showContentAnimatorSet, customLayout, 0f, 1f)
        } else {
            val showTitleAnimator = ObjectAnimator.ofFloat(scrollToTop?.titleTextView, "alpha", 0f, 1f)
            val showIconAnimator = ObjectAnimator.ofFloat(scrollToTop?.iconTextView, "alpha", 0f, 1f)
            val showRightTextAnimator = ObjectAnimator.ofFloat(scrollToTop?.rightTextView, "alpha", 0f, 1f)
            showContentAnimatorSet.playTogether(showTitleAnimator, showIconAnimator, showRightTextAnimator)
        }
        showContentAnimatorSet.setDuration(getAnimationDuration())
            .addListener(object : AnimationUtil.SimpleAnimatorListener() {
                override fun onAnimationStart(animation: Animator) {
                    scrollToTop?.visibility = View.VISIBLE
                }
            })

        showContentAnimatorSet.start()
    }

    /**
     * Скрыть контен виджета
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected fun hideScrollToTopPanelContent() {
        val customLayout = scrollToTop?.customView as? ViewGroup
        val hideContentAnimatorSet = AnimatorSet()
        hideContentAnimatorSet.interpolator = AccelerateInterpolator(2f)
        if (customLayout != null) {
            performAnimatingCustomInsides(hideContentAnimatorSet, customLayout, 1f, 0f)
        } else {
            val hideTitleAnimator = ObjectAnimator.ofFloat(scrollToTop?.titleTextView, "alpha", 1f, 0f)
            val hideIconAnimator = ObjectAnimator.ofFloat(scrollToTop?.iconTextView, "alpha", 1f, 0f)
            val hideRightTextAnimator = ObjectAnimator.ofFloat(scrollToTop?.rightTextView, "alpha", 1f, 0f)
            hideContentAnimatorSet.playTogether(hideTitleAnimator, hideIconAnimator, hideRightTextAnimator)
        }
        hideContentAnimatorSet.setDuration(getAnimationDuration())
            .addListener(object : AnimationUtil.SimpleAnimatorListener() {
                override fun onAnimationStart(animation: Animator) {
                    scrollToTop?.visibility = View.GONE
                }
            })
        hideContentAnimatorSet.start()
    }

    /**
     * Можно ли изменять прозрачность контента в тулбаре
     * @return true - можно
     */
    private fun canChangeToolbarContentAlpha(): Boolean =
        behavior?.isIgnoreSearchOffset == true && !isInitialAppBarState

    private fun performAnimatingCustomInsides(animatorSet: AnimatorSet, view: ViewGroup, vararg values: Float) {
        val customChildCount = view.childCount
        val animatorList = ArrayList<Animator>(customChildCount)
        for (i in customChildCount - 1 downTo 0 step 1) {
            animatorList.add(ObjectAnimator.ofFloat(view.getChildAt(i), "alpha", *values))
        }
        animatorSet.playTogether(animatorList)
    }

    private fun getAnimationDuration(): Long =
        if (isNeedToAnimateScrollTopPropertyChange()) DEFAULT_ANIMATION_DURATION else ANIMATION_ZERO_DURATION

    private fun observeAppBackgroundState() {
        val capturedFragment = fragment ?: return
        // При переходе приложения в фон шапка раскрывается (ССВ не должен отображаться после разворачивания приложения
        // или разблокировки, но при переходе на экран второго уровня должен оставаться в исходном состоянии - см.
        // https://online.sbis.ru/doc/815427e9-02c7-4611-9e7d-30c50872d02b)
        AppBackgroundStateObserverDuringFragmentLifetime()
            .observe(capturedFragment) { resetState(animated = false) }
    }

    private fun updateListTopMargin(@Px desiredMargin: Int = 0) {
        pinnedHeaderViewHelper?.updateListMargin(desiredMargin)
            ?: updateListViewTopMargin(desiredMargin)
    }

    /**
     * Нужно ли анимировать изменение альфы у чайлдов [ScrollToTop]
     * Фактически при false задается длительность анимации равная 0
     *
     * Переопределять по месту использования хелпера
     *
     * @return true - нужно анимировать
     */
    open fun isNeedToAnimateScrollTopPropertyChange(): Boolean {
        return true
    }
}