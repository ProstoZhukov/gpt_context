package ru.tensor.sbis.mvi_extension.router.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * Created by Aleksey Boldinov on 24.08.2022.
 */
fun FragmentManager.replaceFragment(
    fragment: Fragment,
    containerId: Int,
    tag: String = fragment::class.java.simpleName
) {
    beginTransaction()
        .replace(containerId, fragment, tag)
        .commit()
}

fun FragmentManager.replaceFragmentWithBackStack(
    fragment: Fragment,
    containerId: Int,
    tag: String = fragment::class.java.simpleName
) {
    beginTransaction()
        .replace(containerId, fragment, tag)
        .addToBackStack(tag)
        .commit()
}

fun FragmentManager.removeAllFragments() {
    beginTransaction().let { transaction ->
        fragments.forEach { transaction.remove(it) }
        transaction.commit()
    }
}

fun FragmentManager.addFragment(
    fragment: Fragment,
    containerId: Int,
    tag: String = fragment::class.java.simpleName
) {
    beginTransaction()
        .add(containerId, fragment, tag)
        .commit()
}

fun FragmentManager.addFragmentWithBackStack(
    fragment: Fragment,
    containerId: Int,
    tag: String = fragment::class.java.simpleName
) {
    beginTransaction()
        .add(containerId, fragment, tag)
        .addToBackStack(tag)
        .commit()
}

fun FragmentManager.allowPopBackStack(minBackStackEntryCount: Int = 1): Boolean {
    return !isStateSaved && backStackEntryCount >= minBackStackEntryCount
}