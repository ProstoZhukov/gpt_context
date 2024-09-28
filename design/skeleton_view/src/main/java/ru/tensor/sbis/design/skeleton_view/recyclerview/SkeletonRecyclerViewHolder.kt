package ru.tensor.sbis.design.skeleton_view.recyclerview

import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.skeleton_view.SkeletonView

/**
 * SkeletonRecyclerViewHolder для SkeletonRecyclerViewAdapter
 *
 * @param layout разметка для itemView
 *
 * @author us.merzlikina
 */
internal class SkeletonRecyclerViewHolder(
    layout: SkeletonView
) : RecyclerView.ViewHolder(layout)