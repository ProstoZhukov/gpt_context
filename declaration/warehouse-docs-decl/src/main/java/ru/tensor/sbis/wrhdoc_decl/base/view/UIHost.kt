package ru.tensor.sbis.wrhdoc_decl.base.view

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.result.ActivityResultCaller
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import ru.tensor.sbis.android_ext_decl.AndroidComponent

/**
 * Компонент хоста-экрана.
 *
 * @author as.mozgolin
 */
class UIHost(
    private val activityProvider: () -> ComponentActivity,
    private val screenContextProvider: () -> Context,
    private val fragmentManagerProvider: () -> FragmentManager,
    private val lifecycleOwnerProvider: () -> LifecycleOwner,
    private val viewLifecycleOwnerProvider: () -> LifecycleOwner,
    private val savedStateRegistryOwnerProvider: () -> SavedStateRegistryOwner,
    private val viewModelStoreOwnerProvider: () -> ViewModelStoreOwner,
    private val resultCallerProvider: () -> ActivityResultCaller,
    private val backPressedDispatcherProvider: () -> OnBackPressedDispatcher,
    private val androidComponentProvider: () -> AndroidComponent
) {

    fun getActivity(): ComponentActivity = activityProvider()

    fun getScreenContext(): Context = screenContextProvider()

    fun getFragmentManager(): FragmentManager = fragmentManagerProvider()

    fun getLifecycleOwner(): LifecycleOwner = lifecycleOwnerProvider()

    fun getViewLifecycleOwner(): LifecycleOwner = viewLifecycleOwnerProvider()

    fun getSavedStateRegistryOwner(): SavedStateRegistryOwner = savedStateRegistryOwnerProvider()

    fun getViewModelStoreOwner(): ViewModelStoreOwner = viewModelStoreOwnerProvider()

    fun getActivityResultCaller(): ActivityResultCaller = resultCallerProvider()

    fun getOnBackPressedDispatcher(): OnBackPressedDispatcher = backPressedDispatcherProvider()

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Used in legacy components. Don't use it.")
    fun getAndroidComponent(): AndroidComponent = androidComponentProvider()

    companion object {

        /**
         * Метод для формирования [UIHost] на основе [Activity].
         *
         * @param activity
         */
        fun from(activity: AppCompatActivity): UIHost {
            val androidComponent = object : AndroidComponent {
                override fun getActivity(): Activity {
                    return activity
                }

                override fun getFragment(): Fragment? {
                    return null
                }

                override fun getSupportFragmentManager(): FragmentManager {
                    return activity.supportFragmentManager
                }

            }
            return UIHost(
                activityProvider = { activity },
                screenContextProvider = { activity },
                fragmentManagerProvider = { activity.supportFragmentManager },
                lifecycleOwnerProvider = { activity },
                viewLifecycleOwnerProvider = { activity },
                savedStateRegistryOwnerProvider = { activity },
                viewModelStoreOwnerProvider = { activity },
                resultCallerProvider = { activity },
                backPressedDispatcherProvider = { activity.onBackPressedDispatcher },
                androidComponentProvider = { androidComponent }
            )
        }

        /**
         * Метод для формирования [UIHost] на основе [Fragment].
         *
         * @param fragment
         */
        fun from(fragment: Fragment): UIHost {
            val androidComponent = object : AndroidComponent {
                override fun getActivity(): Activity? {
                    return fragment.activity
                }

                override fun getFragment(): Fragment {
                    return fragment
                }

                override fun getSupportFragmentManager(): FragmentManager {
                    return fragment.childFragmentManager
                }

            }
            return UIHost(
                activityProvider = { fragment.requireActivity() },
                screenContextProvider = { fragment.requireContext() },
                fragmentManagerProvider = { fragment.childFragmentManager },
                lifecycleOwnerProvider = { fragment },
                viewLifecycleOwnerProvider = { fragment.viewLifecycleOwner },
                savedStateRegistryOwnerProvider = { fragment },
                viewModelStoreOwnerProvider = { fragment },
                resultCallerProvider = { fragment },
                backPressedDispatcherProvider = { fragment.requireActivity().onBackPressedDispatcher },
                androidComponentProvider = { androidComponent }
            )
        }

    }
}