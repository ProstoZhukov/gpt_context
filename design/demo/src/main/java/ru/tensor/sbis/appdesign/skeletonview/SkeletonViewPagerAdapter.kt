package ru.tensor.sbis.appdesign.skeletonview

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * ViewPagerAdapter для демонстрации работы с компонентом SkeletonView
 *
 * @author us.merzlikina
 */
class SkeletonViewPagerAdapter(
    fragmentManager: FragmentManager,
    private vararg val fragments: SkeletonViewPagerFragment
) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount() = fragments.size

    override fun getItem(position: Int) = fragments[position]

    override fun getPageTitle(position: Int) = getItem(position).title
}