package ru.tensor.sbis.appdesign.context_menu

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * ViewPagerAdapter для демонстрации работы с компонентом контектное меню
 *
 * @author ma.kolpakov
 */
class ContextMenuPagerAdapter(
    fragmentManager: FragmentManager,
    private vararg val fragments: ContextMenuPagerFragment
) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getCount() = fragments.size

    override fun getItem(position: Int) = fragments[position]

    override fun getPageTitle(position: Int) = getItem(position).title
}