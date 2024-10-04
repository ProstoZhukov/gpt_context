package ru.tensor.sbis.appdesign.context_menu

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.appdesign.context_menu.cloud_view.CloudViewFragment
import ru.tensor.sbis.appdesign.context_menu.simple_view.SimpleViewFragment
import ru.tensor.sbis.appdesign.skeletonview.recyclerview.RecyclerViewFragment
import ru.tensor.sbis.appdesign.skeletonview.viewgroup.ViewGroupFragment
import ru.tensor.sbis.design.skeleton_view.Skeleton

/**
 * Demo activity для демонстрации работы с компонентом контекстное меню
 *
 * @author ma.kolpakov
 */
class ContextMenuActivity : AppCompatActivity(R.layout.activity_context_menu) {

    private lateinit var viewPagerAdapter: ContextMenuPagerAdapter
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewPagerAdapter = ContextMenuPagerAdapter(
            supportFragmentManager,
            SimpleViewFragment(),
            CloudViewFragment()
        )
        viewPager = findViewById(R.id.viewPager)
        viewPager.adapter = viewPagerAdapter
        findViewById<TabLayout>(R.id.tabLayout).setupWithViewPager(viewPager)
    }
}