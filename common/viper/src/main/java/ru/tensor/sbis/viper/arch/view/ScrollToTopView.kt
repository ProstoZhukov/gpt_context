package ru.tensor.sbis.viper.arch.view

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Handler
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import androidx.annotation.Px
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import ru.tensor.sbis.design.list_utils.AbstractListView
import ru.tensor.sbis.design.scroll_to_top.ScrollToTop
import ru.tensor.sbis.design.toolbar.Toolbar

/**
 * Интерфейс, предоставляющий возможность добавить на экран скроллинг с возможностью быстрого возвращения к началу списка.
 *
 * @author ga.malinskiy
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
interface ScrollToTopView : AppBarLayout.OnOffsetChangedListener {

    val handler: Handler
    var scrollToTopPanelHeight: Int
    var bottomNavBarHeight: Int
    var firstLaunch: Boolean
    var ignoreAppBarOffsetChanges: Boolean

    val collapsingToolbarLayout: CollapsingToolbarLayout
    val appBar: AppBarLayout
    val sbisListView: AbstractListView<*, *>?
    val sbisToolbar: Toolbar
    val scrollToTop: ScrollToTop

    /**
     * Функция, которая включает возможность быстрого возвращения к началу списка.
     */
    fun enableScrollToTop() {
        ignoreAppBarOffsetChanges = false

        val lp = collapsingToolbarLayout.layoutParams as AppBarLayout.LayoutParams
        lp.scrollFlags =
            AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS or AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
        collapsingToolbarLayout.layoutParams = lp

        val padding = bottomNavBarHeight + appBar.height
        sbisListView!!.setProgressBarVerticalMargin(0, padding / 2)
        sbisListView!!.setInformationViewVerticalPadding(0, padding)
    }

    /**
     * Функция, которая отключает возможность быстрого возвращения к началу списка.
     */
    fun disableScrollToTop() {
        ignoreAppBarOffsetChanges = true

        val lp = collapsingToolbarLayout.layoutParams as AppBarLayout.LayoutParams
        lp.scrollFlags = 0
        collapsingToolbarLayout.layoutParams = lp

        val padding = bottomNavBarHeight + appBar.height
        sbisListView!!.setProgressBarVerticalMargin(appBar.height / 2, padding / 2)
        sbisListView!!.setInformationViewVerticalPadding(appBar.height, padding)
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        val verticalOffsetAbs = Math.abs(verticalOffset)
        val appBarVisibleHeight = appBar.totalScrollRange - verticalOffsetAbs
        val listDesiredMargin: Int

        val padding = bottomNavBarHeight + appBar.height
        if (sbisListView != null && verticalOffset == 0 && firstLaunch) {
            handler.post {
                sbisListView!!.setProgressBarVerticalMargin(0, padding / 2)
                sbisListView!!.setInformationViewVerticalPadding(0, padding)
            }

            firstLaunch = false
        }

        val listViewParams = sbisListView!!.layoutParams as ViewGroup.MarginLayoutParams

        if (ignoreAppBarOffsetChanges) {
            listDesiredMargin = 0

            sbisToolbar.toolbarContainer.alpha = 1f
        } else {
            listDesiredMargin = if (appBarVisibleHeight <= scrollToTopPanelHeight) {
                if (scrollToTop.isGone) {
                    showScrollToTopPanelContent()
                }
                scrollToTopPanelHeight - appBarVisibleHeight
            } else {
                if (scrollToTop.isVisible) {
                    hideScrollToTopPanelContent()
                }
                0
            }
            sbisToolbar.toolbarContainer.alpha =
                1 - 2f * verticalOffsetAbs.toFloat() / appBar.totalScrollRange.toFloat()
        }

        if (useTopMargin()) updateSbisListViewTopMargin(listViewParams, listDesiredMargin)
    }

    fun useTopMargin() = false

    private fun updateSbisListViewTopMargin(listViewParams: ViewGroup.MarginLayoutParams, @Px topMargin: Int) {
        if (listViewParams.topMargin != topMargin) {
            listViewParams.topMargin = topMargin
            if (sbisListView != null) {
                sbisListView!!.requestLayout()
            }
        }
    }

    private fun showScrollToTopPanelContent() {
        val showContentAnimatorSet = AnimatorSet()
        showContentAnimatorSet.interpolator = AccelerateInterpolator(2f)

        val showTitleAnimator = ObjectAnimator.ofFloat(scrollToTop.titleTextView, "alpha", 0f, 1f)
        val showIconAnimator = ObjectAnimator.ofFloat(scrollToTop.iconTextView, "alpha", 0f, 1f)
        val showRightTextAnimator = ObjectAnimator.ofFloat(scrollToTop.rightTextView, "alpha", 0f, 1f)

        showContentAnimatorSet.playTogether(showTitleAnimator, showIconAnimator, showRightTextAnimator)

        showContentAnimatorSet.setDuration(150)
            .addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    scrollToTop.isVisible = true
                }

                override fun onAnimationEnd(animation: Animator) = Unit

                override fun onAnimationCancel(animation: Animator) = Unit

                override fun onAnimationRepeat(animation: Animator) = Unit
            })

        showContentAnimatorSet.start()
    }

    private fun hideScrollToTopPanelContent() {
        val hideContentAnimatorSet = AnimatorSet()
        hideContentAnimatorSet.interpolator = AccelerateInterpolator(2f)

        val hideTitleAnimator = ObjectAnimator.ofFloat(scrollToTop.titleTextView, "alpha", 1f, 0f)
        val hideIconAnimator = ObjectAnimator.ofFloat(scrollToTop.iconTextView, "alpha", 1f, 0f)
        val hideRightTextAnimator = ObjectAnimator.ofFloat(scrollToTop.rightTextView, "alpha", 1f, 0f)

        hideContentAnimatorSet.playTogether(hideTitleAnimator, hideIconAnimator, hideRightTextAnimator)

        hideContentAnimatorSet.setDuration(150)
            .addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    scrollToTop.isVisible = false
                }

                override fun onAnimationEnd(animation: Animator) = Unit

                override fun onAnimationCancel(animation: Animator) = Unit

                override fun onAnimationRepeat(animation: Animator) = Unit
            })

        hideContentAnimatorSet.start()
    }
}