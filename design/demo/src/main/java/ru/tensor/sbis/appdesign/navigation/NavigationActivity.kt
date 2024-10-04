package ru.tensor.sbis.appdesign.navigation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.appdesign.databinding.ActivityNavigationBinding
import ru.tensor.sbis.design.navigation.view.adapter.NavAdapter
import ru.tensor.sbis.design.navigation.view.model.AllItemsUnselected
import ru.tensor.sbis.design.navigation.view.model.ItemSelected
import ru.tensor.sbis.design.navigation.view.model.ItemSelectedByUser
import ru.tensor.sbis.design.navigation.view.model.NavigationCounter
import ru.tensor.sbis.design.navigation.view.view.tabmenu.TabNavView
import ru.tensor.sbis.design.navigation.view.widget.CalendarWidget
import ru.tensor.sbis.design.navigation.view.widget.calendar.CalendarWidgetClickListener
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

class NavigationActivity : AppCompatActivity() {

    companion object {
        private const val MAX_COUNT = 1000
        private const val COUNTER_COMBINATIONS_NUMBER = 3
        private const val IS_DARK_THEME_KEY = "is_dark_theme"

        fun getIntent(context: Context, isDarkTheme: Boolean = false) =
            Intent(context, NavigationActivity::class.java).putExtra(IS_DARK_THEME_KEY, isDarkTheme)
    }

    private var isDarkTheme = false
    private val navigationTabToCounter: MutableMap<NavigationTab, NavigationCounter> =
        EnumMap(NavigationTab::class.java)
    private val navAdapter = NavAdapter<NavigationTab>(this, true)

    private lateinit var viewBinding: ActivityNavigationBinding
    private lateinit var tabNavView: TabNavView

    override fun onCreate(savedInstanceState: Bundle?) {
        isDarkTheme = intent.getBooleanExtra(IS_DARK_THEME_KEY, false)

        if (isDarkTheme) {
            setTheme(R.style.AppTheme_NoActionBar_Animation_Dark)
        } else {
            setTheme(R.style.AppTheme_NoActionBar_Animation)
        }

        super.onCreate(savedInstanceState)

        viewBinding = ActivityNavigationBinding.inflate(layoutInflater)
        tabNavView = viewBinding.tabNavView.root

        setContentView(viewBinding.root)

        initNavigation()
        initSwitchListeners()
    }

    override fun onDestroy() {
        navAdapter.dispose()

        super.onDestroy()
    }

    private fun initNavigation() {
        viewBinding.accordion.setAdapter(navAdapter)
        tabNavView.setAdapter(navAdapter)

        setTabsWithRandomCounters()

        val toolbarPlaceholder = findViewById<View>(R.id.toolbar_placeholder)
        if (toolbarPlaceholder != null) {
            tabNavView.adjustMenuBtnHeight(toolbarPlaceholder)
        }

        setOnAccordionHeaderClickListener()
        setOnAccordionFooterClickListener()
        setOnMenuButtonClick(findViewById(R.id.root_container), false)
        setOnNavigationEventListener()
    }

    private fun setTabsWithRandomCounters() {
        NavigationTab.values().forEachIndexed { index, navigationTab ->
            navAdapter.remove(navigationTab)

            // демо элемента меню с виджетом
            if (navigationTab == NavigationTab.CALENDAR) {
                // демо блок для элементов меню с виджетами. Текст и иконки могут меняться
                navAdapter.add(navigationTab, CalendarWidget(
                    Observable.interval(1L, TimeUnit.SECONDS).map(Long::toString),
                    Observable.interval(3L, TimeUnit.SECONDS).map {
                        if (it % 2L == 0L) R.string.design_mobile_icon_entry
                        else R.string.design_mobile_icon_calendar
                    },
                    CalendarWidgetListener()
                ))
                return@forEachIndexed
            }

            // демо элемента меню с дополнительным контентом
            if (navigationTab == NavigationTab.STATISTICS) {
                navAdapter.add(navigationTab, CallItemContent())
                return@forEachIndexed
            }

            val division = index % COUNTER_COMBINATIONS_NUMBER
            val count = if (division != 0) getRandomCount() else 0
            val totalCount = if (division != 1) count + getRandomCount() / 2 else 0

            navigationTabToCounter[navigationTab] = when (navigationTab) {
                NavigationTab.KASSA    -> MoneyNavigationCounter(count, totalCount)
                NavigationTab.MESSAGES -> IntervalNavigationCounter(count, totalCount)
                else                   -> NumberNavigationCounter(count, totalCount)
            }

            // демо элемента меню с дополнительным контентом и счётчиком
            if (navigationTab == NavigationTab.CALL) {
                navAdapter.add(navigationTab, CallItemContent(), navigationTabToCounter[navigationTab])
                return@forEachIndexed
            }

            navAdapter.add(navigationTab, navigationTabToCounter[navigationTab], navigationTab.parentItem)
        }

        tabNavView.hideItem(NavigationTab.TASKS_FROM_ME)
    }

    private fun getRandomCount(): Int = (0..MAX_COUNT).random()

    private fun initSwitchListeners() {
        viewBinding.swMenuBtn.setOnCheckedChangeListener { button, value ->
            setOnMenuButtonClick(button, value)
        }

        viewBinding.swRemoveTab.setOnCheckedChangeListener { _, value ->
            if (value) {
                navAdapter.remove(NavigationTab.MESSAGES)
            } else {
                try {
                    navAdapter.add(NavigationTab.MESSAGES, navigationTabToCounter[NavigationTab.MESSAGES])
                } catch (e: Exception) {
                    Timber.d(e)
                }
            }
        }

        viewBinding.swHideTab.setOnCheckedChangeListener { _, value ->
            if (value) {
                NavigationTab.NOTIFICATION.hideMenuItem()
            } else {
                NavigationTab.NOTIFICATION.showMenuItem()
            }
        }

        viewBinding.swChangeIcon.setOnCheckedChangeListener { _, value ->
            if (value) {
                NavigationTab.changeCalendarIcons(
                    getCalendarIconId(R.string.design_mobile_icon_calendar),
                    getCalendarIconId(R.string.design_mobile_icon_calendar_filled)
                )

                NavigationTab.changeCalendarText(R.string.navigation_current_date)
            } else {
                NavigationTab.changeCalendarIcons(
                    R.string.design_mobile_icon_calendar,
                    R.string.design_mobile_icon_calendar_filled
                )
                NavigationTab.changeCalendarText(R.string.navigation_calendar)
            }
        }

        viewBinding.swPinTabNavView.setOnCheckedChangeListener { _, value ->
            tabNavView.pinned = value
        }

        viewBinding.swCounter.setOnCheckedChangeListener { _, value ->
            val count = if (value) getRandomCount() else 0
            val totalCount = if (value) count + getRandomCount() else 0

            val counter = navigationTabToCounter[NavigationTab.DISK]

            if (counter is NumberNavigationCounter) {
                counter.count = count
                counter.totalCount = totalCount
            }
        }

        viewBinding.swDarkTheme.isChecked = isDarkTheme

        viewBinding.swDarkTheme.setOnCheckedChangeListener { _, isDarkTheme ->
            startActivity(getIntent(this, isDarkTheme))
            finish()
        }

        viewBinding.swLongTitle.isChecked = NavigationTab.TASKS.labelResId == R.string.navigation_task_long

        viewBinding.swLongTitle.setOnCheckedChangeListener { _, isChecked ->
            NavigationTab.changeTasksText(if (isChecked) {
                R.string.navigation_task_long
            } else {
                R.string.navigation_task
            })
        }

        viewBinding.swLongSubitemTitle.isChecked = NavigationTab.TASKS_FROM_ME.labelResId == R.string.navigation_tasks_from_me_long

        viewBinding.swLongSubitemTitle.setOnCheckedChangeListener { _, isChecked ->
            NavigationTab.changeTasksFromMeText(if (isChecked) {
                R.string.navigation_tasks_from_me_long
            } else {
                R.string.navigation_tasks_from_me
            })
        }

        viewBinding.btnRandomizeCounters.setOnClickListener {
            updateTabsCounters()
        }
    }

    private fun updateTabsCounters() {
        NavigationTab.values().forEachIndexed { index, navigationTab ->
            val counter = navigationTabToCounter[navigationTab]

            if (counter is NumberNavigationCounter) {
                val division = index % COUNTER_COMBINATIONS_NUMBER
                val count = if (division != 0) getRandomCount() else 0

                counter.count = count
                counter.totalCount =
                    if (division != 1) count + getRandomCount() / 2 else 0
            }
        }
    }

    private fun setOnAccordionHeaderClickListener() {
        viewBinding.accordion.header!!.setOnClickListener { view ->
            Snackbar.make(view, R.string.navigation_click_accordion_header_text, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun setOnAccordionFooterClickListener() {
        val usernameView: TextView = viewBinding.accordion.footer!!.findViewById(R.id.username)

        usernameView.setOnClickListener { view ->
            Snackbar.make(view, R.string.navigation_click_accordion_username_text, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun setOnMenuButtonClick(view: View, isSelfProceedMenuBtnClick: Boolean) {
        if (isSelfProceedMenuBtnClick) {
            tabNavView.menuBtn.setOnClickListener {
                Snackbar.make(view, R.string.navigation_click_menu_btn_text, Snackbar.LENGTH_LONG).show()
            }
            return
        }

        tabNavView.bindToNavigationDrawer(viewBinding.navigationDrawer)
    }

    private fun setOnNavigationEventListener() {
        navAdapter.navigationEvents.observe(this, Observer { event ->
            when (event) {
                is ItemSelectedByUser -> event.run {
                    if (viewBinding.navigationDrawer.isDrawerOpen(GravityCompat.START)) {
                        viewBinding.navigationDrawer.closeDrawers()
                    }

                    showFragment(event.selectedItem)
                }
                is ItemSelected       -> event.run {
                    Timber.d("%s selected programmatically", selectedItem)
                }
                AllItemsUnselected    -> Timber.d("All navigation items was unselected")
            }
        })
    }

    private fun showFragment(tab: NavigationTab) {
        val fragmentManager = supportFragmentManager
        var fragment: Fragment? = fragmentManager.findFragmentByTag(tab.name)
        if (fragment == null) {
            fragment = NavigationSampleDataFragment.newInstance(getString(tab.labelResId))
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment, tab.name)
                .commit()
        }
    }

    private fun NavigationTab.hideMenuItem() {
        viewBinding.accordion.hideItem(this)
        tabNavView.hideItem(this)
    }

    private fun NavigationTab.showMenuItem() {
        viewBinding.accordion.showItem(this)
        tabNavView.showItem(this)
    }

    @StringRes
    private fun getCalendarIconId(@StringRes defaultIconId: Int = R.string.design_mobile_icon_calendar): Int {
        val index = Calendar.getInstance().get(Calendar.DATE) - 1
        val typeArray = resources.obtainTypedArray(R.array.calendar_design_date_icons)
        val calendarIconId = typeArray.getResourceId(index, defaultIconId)

        typeArray.recycle()

        return calendarIconId
    }

    private inner class CalendarWidgetListener : CalendarWidgetClickListener {
        override fun onTitleClicked() {
            Toast.makeText(this@NavigationActivity, "Calendar widget title clicked", Toast.LENGTH_SHORT).show()
        }

        override fun onIconClicked() {
            Toast.makeText(this@NavigationActivity, "Calendar widget icon clicked", Toast.LENGTH_SHORT).show()
        }
    }
}