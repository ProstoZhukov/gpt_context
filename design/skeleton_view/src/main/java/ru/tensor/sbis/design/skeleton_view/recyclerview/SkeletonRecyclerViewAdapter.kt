package ru.tensor.sbis.design.skeleton_view.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.skeleton_view.SkeletonConfig
import ru.tensor.sbis.design.skeleton_view.SkeletonView
import ru.tensor.sbis.design.skeleton_view.createSkeleton

/**
 * RecyclerViewAdapter для SkeletonRecyclerView
 *
 * @param layoutResId id ресурса, который будет использоваться в качестве маски в itemView
 * @param itemCount количество элементов для отображения
 * @param config конфигурация UI
 *
 * @author us.merzlikina
 */
internal class SkeletonRecyclerViewAdapter(
    @LayoutRes private val layoutResId: Int,
    private val itemCount: Int,
    private val config: SkeletonConfig
) : RecyclerView.Adapter<SkeletonRecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkeletonRecyclerViewHolder {
        val originView = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        val skeleton = createSkeleton(originView, config) as SkeletonView
        skeleton.layoutParams = originView.layoutParams
        skeleton.showSkeleton()
        return SkeletonRecyclerViewHolder(skeleton)
    }

    override fun onBindViewHolder(holder: SkeletonRecyclerViewHolder, position: Int) = Unit

    override fun getItemCount() = itemCount
}