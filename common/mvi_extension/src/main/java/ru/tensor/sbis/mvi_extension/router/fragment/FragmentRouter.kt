package ru.tensor.sbis.mvi_extension.router.fragment

import androidx.fragment.app.Fragment
import ru.tensor.sbis.mvi_extension.router.BaseRouter

/**
 * Created by Aleksey Boldinov on 24.08.2022.
 */
open class FragmentRouter : BaseRouter<Fragment>() {

    // region Fragment Extensions
    protected fun Fragment.popBackStackChildFragmentIfNeed(): Boolean {
        return if (childFragmentManager.allowPopBackStack()) {
            childFragmentManager.popBackStack()
            true
        } else false
    }

    protected fun Fragment.replaceChildFragment(
        fragment: Fragment,
        containerId: Int,
        tag: String = fragment::class.java.simpleName
    ) {
        childFragmentManager.replaceFragment(fragment, containerId, tag)
    }

    protected fun Fragment.replaceChildFragmentWithBackStack(
        fragment: Fragment,
        containerId: Int,
        tag: String = fragment::class.java.simpleName
    ) {
        childFragmentManager.replaceFragmentWithBackStack(fragment, containerId, tag)
    }

    protected fun Fragment.addChildFragment(
        fragment: Fragment,
        containerId: Int,
        tag: String = fragment::class.java.simpleName
    ) {
        childFragmentManager.addFragment(fragment, containerId, tag)
    }

    protected fun Fragment.addChildFragmentIfNeed(
        fragment: Fragment,
        containerId: Int,
        tag: String = fragment::class.java.simpleName
    ) {
        if (childFragmentManager.findFragmentByTag(tag) == null) {
            childFragmentManager.addFragment(fragment, containerId, tag)
        }
    }

    protected fun Fragment.addChildFragmentWithBackStack(
        fragment: Fragment,
        containerId: Int,
        tag: String = fragment::class.java.simpleName
    ) {
        childFragmentManager.addFragmentWithBackStack(fragment, containerId, tag)
    }

    protected fun Fragment.addChildFragmentWithBackStackIfNeed(
        fragment: Fragment,
        containerId: Int,
        tag: String = fragment::class.java.simpleName
    ) {
        if (childFragmentManager.findFragmentByTag(tag) == null) {
            childFragmentManager.addFragmentWithBackStack(fragment, containerId, tag)
        }
    }

    protected fun Fragment.removeAllChildFragments() {
        childFragmentManager.removeAllFragments()
    }
    // endregion
}