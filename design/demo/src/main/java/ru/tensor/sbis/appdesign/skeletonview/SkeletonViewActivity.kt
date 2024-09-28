package ru.tensor.sbis.appdesign.skeletonview

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.appdesign.skeletonview.recyclerview.RecyclerViewFragment
import ru.tensor.sbis.appdesign.skeletonview.viewgroup.ViewGroupFragment
import ru.tensor.sbis.design.skeleton_view.Skeleton

/**
 * Demo activity для демонстрации работы с компонентом SkeletonView
 *
 * @author us.merzlikina
 */
class SkeletonViewActivity : AppCompatActivity(R.layout.activity_skeletonview) {

    private lateinit var viewPagerAdapter: SkeletonViewPagerAdapter
    private lateinit var viewPager: ViewPager
    private var isDarkTheme = false

    override fun onCreate(savedInstanceState: Bundle?) {
        isDarkTheme = intent.getBooleanExtra(IS_DARK_THEME_KEY, false)
        setTheme(if (isDarkTheme) R.style.AppTheme_Dark else R.style.AppTheme)

        super.onCreate(savedInstanceState)
        findViewById<Button>(R.id.button).setOnClickListener {
            getSkeleton().let { if (it.isSkeletonActive()) hideSkeleton(it) else showSkeleton(it) }
        }

        viewPagerAdapter = SkeletonViewPagerAdapter(
            supportFragmentManager,
            RecyclerViewFragment(),
            ViewGroupFragment()
        )

        viewPager = findViewById(R.id.viewPager)
        viewPager.adapter = viewPagerAdapter
        findViewById<TabLayout>(R.id.tabLayout).setupWithViewPager(viewPager)

        viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                getSkeleton().let { if (it.isSkeletonActive()) showSkeleton(it) else hideSkeleton(it) }
            }
        })

        with (findViewById<Switch>(R.id.theme_switch)) {
            isChecked = isDarkTheme
            setOnCheckedChangeListener { _, isDarkTheme ->
                startActivity(Intent(context, SkeletonViewActivity::class.java).putExtra(IS_DARK_THEME_KEY, isDarkTheme))
                finish()
            }
        }
    }

    private fun getSkeleton(): Skeleton =
        viewPagerAdapter.getItem(viewPager.currentItem).skeleton

    private fun showSkeleton(skeleton: Skeleton) {
        skeleton.showSkeleton()
    }

    private fun hideSkeleton(skeleton: Skeleton) {
        skeleton.hideSkeleton()
    }

    companion object {
        private const val IS_DARK_THEME_KEY = "is_dark_theme"
    }
}