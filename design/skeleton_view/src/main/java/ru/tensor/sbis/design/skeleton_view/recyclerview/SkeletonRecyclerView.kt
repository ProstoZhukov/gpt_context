package ru.tensor.sbis.design.skeleton_view.recyclerview

import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.skeleton_view.Skeleton
import ru.tensor.sbis.design.skeleton_view.SkeletonConfig
import ru.tensor.sbis.design.skeleton_view.SkeletonStyle

/**
 * Обертка над recyclerView, реализующая свойства Skeleton
 *
 * @param layoutResId id ресурса, который будет использоваться в качестве маски в itemView
 * @param itemCount количество элементов для отображения
 * @param config конфигурация UI
 *
 * @author us.merzlikina
 */
internal class SkeletonRecyclerView(
    private val recyclerView: RecyclerView,
    @LayoutRes layoutResId: Int,
    itemCount: Int,
    config: SkeletonConfig
) : Skeleton, SkeletonStyle by config {

    private val originalAdapter = recyclerView.adapter
    private val skeletonAdapter by lazy { SkeletonRecyclerViewAdapter(layoutResId, itemCount, config) }

    init {
        config.addValueObserver { skeletonAdapter.notifyDataSetChanged() }
    }

    override fun hideSkeleton() {
        recyclerView.adapter = originalAdapter
    }

    override fun showSkeleton() {
        recyclerView.adapter = skeletonAdapter
    }

    override fun isSkeletonActive(): Boolean = recyclerView.adapter == skeletonAdapter
}