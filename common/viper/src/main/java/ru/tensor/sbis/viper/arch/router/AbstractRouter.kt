package ru.tensor.sbis.viper.arch.router

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.android_ext_decl.AndroidComponent

/**
 * Базовый класс для Router.
 *
 * @author ga.malinskiy
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
abstract class AbstractRouter(protected val androidComponent: AndroidComponent) {

    protected fun replaceFragment(
        fragment: Fragment,
        containerId: Int,
        animations: FragmentTransactionCustomAnimations = FragmentTransactionCustomAnimations(),
        tag: String = fragment::class.java.simpleName
    ) {
        replaceFragment(
            androidComponent.getSupportFragmentManager(),
            fragment,
            containerId,
            animations,
            tag
        )
    }

    protected fun replaceFragment(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        containerId: Int,
        animations: FragmentTransactionCustomAnimations = FragmentTransactionCustomAnimations(),
        tag: String = fragment::class.java.simpleName
    ) {
        fragmentManager
            .beginTransaction()
            .setCustomAnimations(animations.enter, animations.exit, animations.popEnter, animations.popExit)
            .replace(containerId, fragment, tag)
            .commit()
    }

    protected fun replaceFragmentWithBackStack(
        fragment: Fragment,
        containerId: Int,
        animations: FragmentTransactionCustomAnimations = FragmentTransactionCustomAnimations(),
        tag: String = fragment::class.java.simpleName
    ) {
        replaceFragmentWithBackStack(
            androidComponent.getSupportFragmentManager(),
            fragment,
            containerId,
            animations,
            tag
        )
    }

    protected fun replaceFragmentWithBackStack(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        containerId: Int,
        animations: FragmentTransactionCustomAnimations = FragmentTransactionCustomAnimations(),
        tag: String = fragment::class.java.simpleName
    ) {
        fragmentManager
            .beginTransaction()
            .setCustomAnimations(animations.enter, animations.exit, animations.popEnter, animations.popExit)
            .replace(containerId, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }

    protected fun removeAllFragments() {
        val fragmentManager = androidComponent.getSupportFragmentManager()
        val transaction = fragmentManager.beginTransaction()
        fragmentManager.fragments.forEach { transaction.remove(it) }
        transaction.commit()
    }

    protected fun addFragment(
        fragment: Fragment,
        containerId: Int,
        animations: FragmentTransactionCustomAnimations = FragmentTransactionCustomAnimations(),
        tag: String = fragment::class.java.simpleName
    ) {
        addFragment(
            androidComponent.getSupportFragmentManager(),
            fragment,
            containerId,
            animations,
            tag
        )
    }

    protected fun addFragment(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        containerId: Int,
        animations: FragmentTransactionCustomAnimations = FragmentTransactionCustomAnimations(),
        tag: String = fragment::class.java.simpleName
    ) {
        fragmentManager
            .beginTransaction()
            .setCustomAnimations(animations.enter, animations.exit, animations.popEnter, animations.popExit)
            .add(containerId, fragment, tag)
            .commit()
    }

    protected fun addFragmentWithBackStack(
        fragment: Fragment,
        containerId: Int,
        animations: FragmentTransactionCustomAnimations = FragmentTransactionCustomAnimations(),
        tag: String = fragment::class.java.simpleName
    ) {
        addFragmentWithBackStack(
            androidComponent.getSupportFragmentManager(),
            fragment,
            containerId,
            animations,
            tag
        )
    }

    protected fun addFragmentWithBackStack(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        containerId: Int,
        animations: FragmentTransactionCustomAnimations = FragmentTransactionCustomAnimations(),
        tag: String = fragment::class.java.simpleName
    ) {
        fragmentManager
            .beginTransaction()
            .setCustomAnimations(animations.enter, animations.exit, animations.popEnter, animations.popExit)
            .add(containerId, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }

    protected fun setFragmentHoldersWeight(
        containerId: Int,
        containerWeight: Float,
        subContainerId: Int,
        subContainerWeight: Float
    ) {
        val container = androidComponent.getActivity()?.findViewById<View>(containerId)
        val subContainer = androidComponent.getActivity()?.findViewById<View>(subContainerId)

        container?.let {
            if (containerWeight <= 0) {
                container.isGone = true
            } else {
                val params = container.layoutParams as? ConstraintLayout.LayoutParams
                params?.let {
                    it.horizontalWeight = containerWeight
                    container.layoutParams = it
                }
                container.isGone = false
            }
        }

        subContainer?.let {
            if (subContainerWeight <= 0) {
                subContainer.isGone = true
            } else {
                val params = subContainer.layoutParams as? ConstraintLayout.LayoutParams
                params?.let {
                    it.horizontalWeight = subContainerWeight
                    subContainer.layoutParams = it
                }
                subContainer.isGone = false
            }
        }
    }

    protected fun getHostFragmentManager(fragment: Fragment?, hostContainerId: Int): FragmentManager? {
        if (fragment == null) return null

        val hostFragment = fragment
            .fragmentManager
            ?.findFragmentById(hostContainerId)

        return if (hostFragment != null) {
            hostFragment.fragmentManager
        } else {
            getHostFragmentManager(fragment.parentFragment, hostContainerId)
        }
    }

    protected fun popBackPressed() {
        popBackPressed(androidComponent.getSupportFragmentManager())
    }

    protected fun popBackPressed(fragmentManager: FragmentManager) {
        fragmentManager.popBackStack()
    }

    protected fun popBackStackInclusive() {
        popBackStackInclusive(androidComponent.getSupportFragmentManager())
    }

    protected fun popBackStackInclusive(fragmentManager: FragmentManager) {
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}