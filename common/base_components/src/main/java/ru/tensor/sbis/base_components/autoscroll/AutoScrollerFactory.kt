package ru.tensor.sbis.base_components.autoscroll

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * Хелпер для создания нужного авто-скроллера в зависимости от используемого [LayoutManager].
 *
 * @author am.boldinov
 */
object AutoScrollerFactory {

    /**
     * Создает одну из реализаций [AutoScroller] на основе переданного [LayoutManager].
     */
    fun create(
        layoutManager: LayoutManager,
        threshold: Int,
        matcher: BaseAutoScroller.Matcher? = null,
        onScrollToTop: () -> Unit = {}
    ): AutoScroller {
        return when (layoutManager) {
            is LinearLayoutManager        -> {
                if (matcher != null) {
                    object : LinearAutoScroller(layoutManager, threshold, matcher) {
                        override fun scrollToTop() {
                            super.scrollToTop()
                            onScrollToTop.invoke()
                        }
                    }
                } else {
                    object : LinearAutoScroller(layoutManager, threshold) {
                        override fun scrollToTop() {
                            super.scrollToTop()
                            onScrollToTop.invoke()
                        }
                    }
                }
            }
            is StaggeredGridLayoutManager -> {
                if (matcher != null) {
                    object : StaggeredGridAutoScroller(layoutManager, threshold, matcher) {
                        override fun scrollToTop() {
                            super.scrollToTop()
                            onScrollToTop.invoke()
                        }
                    }
                } else {
                    object : StaggeredGridAutoScroller(layoutManager, threshold) {
                        override fun scrollToTop() {
                            super.scrollToTop()
                            onScrollToTop.invoke()
                        }
                    }
                }
            }
            else                          -> error(
                "Unknown RecyclerView.LayoutManager, please create AutoScroller implementation manually"
            )
        }
    }
}