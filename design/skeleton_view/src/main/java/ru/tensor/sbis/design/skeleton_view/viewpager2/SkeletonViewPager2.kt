package ru.tensor.sbis.design.skeleton_view.viewpager2

import androidx.annotation.LayoutRes
import androidx.viewpager2.widget.ViewPager2
import ru.tensor.sbis.design.skeleton_view.Skeleton
import ru.tensor.sbis.design.skeleton_view.SkeletonConfig
import ru.tensor.sbis.design.skeleton_view.SkeletonStyle
import ru.tensor.sbis.design.skeleton_view.recyclerview.SkeletonRecyclerViewAdapter

/**
 * Обертка над viewPager2, реализующая свойства Skeleton
 *
 * @param layoutResId id ресурса, который будет использоваться в качестве маски
 * @param itemCount количество элементов для отображения
 * @param config конфигурация UI
 *
 * @author us.merzlikina
 */
internal class SkeletonViewPager2(
    private val viewPager: ViewPager2,
    @LayoutRes layoutResId: Int,
    itemCount: Int,
    config: SkeletonConfig
) : Skeleton, SkeletonStyle by config {

    private val originalAdapter by lazy { viewPager.adapter }
    private val skeletonAdapter by lazy { SkeletonRecyclerViewAdapter(layoutResId, itemCount, config) }

    init {
        config.addValueObserver { skeletonAdapter.notifyDataSetChanged() }
    }

    override fun hideSkeleton() {
        viewPager.adapter = originalAdapter
    }

    override fun showSkeleton() {
        viewPager.adapter = skeletonAdapter
    }

    override fun isSkeletonActive(): Boolean = viewPager.adapter == skeletonAdapter
}