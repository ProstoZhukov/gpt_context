package ru.tensor.sbis.appdesign.skeletonview.viewgroup

import android.os.Bundle
import android.view.View
import android.widget.TextView
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.appdesign.skeletonview.SkeletonViewPagerFragment
import ru.tensor.sbis.appdesign.skeletonview.recyclerview.RecyclerViewListItem
import ru.tensor.sbis.design.skeleton_view.Skeleton

/**
 * Fragment для демонстрации работы компонентом SkeletonView в режиме layout
 *
 * @author us.merzlikina
 */
class ViewGroupFragment : SkeletonViewPagerFragment(R.layout.fragment_skeletonview_viewgroup, "ViewGroup") {
    override val skeleton: Skeleton
        get() = requireView().findViewById(R.id.skeletonLayout)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.avatarView).setText(RecyclerViewListItem.DEMO_ITEMS.first().avatarResId)
        skeleton.showSkeleton()
    }
}