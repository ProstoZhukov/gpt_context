package ru.tensor.sbis.base_components.fragment

import android.annotation.SuppressLint
import androidx.activity.OnBackPressedCallback
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.android_ext_decl.viewprovider.OverlayFragmentHolder
import ru.tensor.sbis.design.R
import timber.log.Timber
import ru.tensor.sbis.common.R as RCommon

/**
 * Реализация использует [activity] для управления отображением фрагментов и обработки события навигации "Назад"
 * через собственный колбек.
 *
 * При использовании конструктора по умолчанию, необходима явная инициализация.
 * @see [init]
 *
 * @author du.bykov
 */
class ActivityOverlayFragmentHolder() : OverlayFragmentHolder {

    private lateinit var activity: AppCompatActivity
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal var needAddBackPressCallback: Boolean = true

    constructor(activity: AppCompatActivity) : this() {
        init(activity)
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            activity.supportFragmentManager.popBackStack()
        }
    }

    /**
     * Инициализирует инструмент, указывая целевую [AppCompatActivity].
     */
    fun init(activity: AppCompatActivity, needAddBackPressCallback: Boolean = true) {
        this.activity = activity
        this.needAddBackPressCallback = needAddBackPressCallback
        if (this.needAddBackPressCallback) {
            activity.apply {
                addBackPressCallback()
                listenToBackStackToActivateCallback()
            }
        }
    }


    override fun setFragment(fragment: Fragment, swipeable: Boolean) = setFragmentWithTag(fragment, swipeable, null)

    @SuppressLint("CommitTransaction")
    override fun setFragmentWithTag(fragment: Fragment, swipeable: Boolean, tag: String?) {
        val transaction = activity
            .supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)

        if (swipeable) {
            transaction.setCustomAnimations(
                R.anim.right_in,
                R.anim.right_out,
                R.anim.right_in,
                R.anim.right_out
            )
        }
        transaction.add(RCommon.id.overlay_container, getFragmentToAdd(swipeable, fragment), tag)
            .commitAllowingStateLoss()
    }

    override fun hasFragment() = getFragmentFromContainer() != null

    override fun getExistingFragment(tag: String?) = if (tag == null) {
        getFragmentFromContainer()
    } else {
        activity.supportFragmentManager.findFragmentByTag(tag)
    }

    override fun removeFragment() {
        if (isBackStackEmpty() || activity.supportFragmentManager.isStateSaved) return

        activity.supportFragmentManager.popBackStack()
    }

    override fun removeFragmentImmediate() {
        if (isBackStackEmpty() || activity.supportFragmentManager.isStateSaved) return

        activity.supportFragmentManager.popBackStackImmediate()
    }

    override fun handlePressed(): Boolean {
        if (!hasFragment()) return false

        val fragment = findFragment() ?: return false

        if (fragment is FragmentBackPress && fragment.onBackPressed()){
            return true
        }

        removeFragment()

        return true
    }

    private fun isBackStackEmpty() = activity.supportFragmentManager.backStackEntryCount == 0

    private fun getFragmentToAdd(
        swipeable: Boolean,
        fragment: Fragment
    ) = if (swipeable) wrapFragmentWithSwipeable(fragment) else fragment

    private fun AppCompatActivity.listenToBackStackToActivateCallback() {
        supportFragmentManager.addOnBackStackChangedListener {
            onBackPressedCallback.isEnabled = supportFragmentManager.hasFragmentInContainer()
        }
    }

    private fun AppCompatActivity.addBackPressCallback() {
        onBackPressedDispatcher.addCallback(
            activity,
            onBackPressedCallback
        )
    }

    private fun wrapFragmentWithSwipeable(fragment: Fragment): SwipeBackContainerFragment =
        SwipeBackContainerFragment().also {
            it.fragment = fragment
        }

    private fun getFragmentFromContainer() = activity.supportFragmentManager.findFragmentById(RCommon.id.overlay_container)

    private fun findFragment(): Fragment? {
        val fragment = getFragmentFromContainer()
        if (fragment == null) {
            Timber.e("Fragment is not found")
            return null
        }

        if (fragment !is SwipeBackContainerFragment) return fragment

        return fragment.findNestedFragment()
    }

    private fun FragmentManager.hasFragmentInContainer() =
        backStackEntryCount > 0 && findFragmentById(RCommon.id.overlay_container) != null
}