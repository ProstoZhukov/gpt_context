package ru.tensor.sbis.common.util

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

/** @SelfDocumented */
val ViewModelStoreOwner.appContext: Context
    get() =
        when (this) {
            is Fragment -> requireContext().applicationContext
            is AppCompatActivity -> this.applicationContext
            else -> throw IllegalArgumentException("Unexpected ViewModelStore")
        }

/** @SelfDocumented */
val ViewModelStoreOwner.fragmentManager: FragmentManager
    get() =
        when (this) {
            is Fragment -> childFragmentManager
            is AppCompatActivity -> supportFragmentManager
            else -> throw IllegalArgumentException("Unexpected ViewModelStore")
        }

/** @SelfDocumented */
inline fun <reified T : ViewModel> ViewModelStoreOwner.findViewModel(
    key: String? = null,
    onException: (Throwable) -> Unit = {}
): T? =
    try {
        ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    throw IllegalArgumentException("Unexpected view model creation")
            }
        )
            .run {
                if (key != null) {
                    get(key, T::class.java)
                } else {
                    get(T::class.java)
                }
            }
    } catch (e: Throwable) {
        onException(e)
        null
    }

/** @SelfDocumented */
inline fun <reified T : ViewModel> ViewModelStoreOwner.safeRequireViewModel(key: String? = null): T? =
    findViewModel(key) { safeThrow(it) }

/** @SelfDocumented */
inline fun <reified T : ViewModel> Fragment.findViewModelHierarchical(
    key: String? = null,
    onException: (Throwable) -> Unit = {}
): T? {
    var parentFragment: Fragment? = parentFragment
    while (parentFragment != null) {
        val viewModel: T? = parentFragment.findViewModel(key, onException)
        if (viewModel != null) {
            return viewModel
        }
        parentFragment = parentFragment.parentFragment
    }
    return activity?.findViewModel(key, onException)
}

/** @SelfDocumented */
inline fun <reified T : ViewModel> findOrCreateViewModel(
    storeOwner: ViewModelStoreOwner,
    crossinline create: () -> T
): T =
    findOrCreateViewModel(storeOwner = storeOwner, key = null, create = create)

/** @SelfDocumented */
inline fun <reified T : ViewModel> findOrCreateViewModel(
    storeOwner: ViewModelStoreOwner,
    key: String?,
    crossinline create: () -> T
): T =
    ViewModelProvider(
        storeOwner,
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return create() as T
            }
        }
    )
        .run {
            if (key != null) {
                get(key, T::class.java)
            } else {
                get(T::class.java)
            }
        }