package ru.tensor.sbis.appdesign.skeletonview.recyclerview

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.appdesign.skeletonview.SkeletonViewPagerFragment
import ru.tensor.sbis.design.skeleton_view.Skeleton
import ru.tensor.sbis.design.skeleton_view.createSkeleton

/**
 * Fragment для демонстрации работы компонентом SkeletonView в режиме recyclerview
 *
 * @author us.merzlikina
 */
class RecyclerViewFragment : SkeletonViewPagerFragment(R.layout.fragment_skeletonview_recyclerview, "RecyclerView") {
    private var recyclerSkeleton: Skeleton? = null

    override val skeleton: Skeleton
        get() = checkNotNull(recyclerSkeleton)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list = view.findViewById<RecyclerView>(R.id.list)

        list.adapter = RecyclerViewAdapter(RecyclerViewListItem.DEMO_ITEMS)

        recyclerSkeleton = createSkeleton(list, R.layout.skeleton_view_recycler_item_empty, SKELETON_ITEM_COUNT).apply { showSkeleton() }
    }

    override fun onDestroyView() {
        recyclerSkeleton = null
        super.onDestroyView()
    }

    companion object {
        private const val SKELETON_ITEM_COUNT = 30
    }
}