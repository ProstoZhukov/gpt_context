package ru.tensor.sbis.main_screen_decl.env

import android.content.Context
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.activity.result.ActivityResultCaller
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import ru.tensor.sbis.android_ext_decl.viewprovider.OverlayFragmentHolder

/**
 * Хост главного экрана. Содержит все необходимые компоненты для обеспечения работы.
 *
 * @property context
 * @property fragmentActivity
 * @property viewLifecycleOwner
 * @property viewLifecycleOwner
 * @property savedStateRegistryOwner
 * @property backPressedDispatcherOwner
 * @property resultCaller
 *
 * @author kv.martyshenko
 */
class MainScreenHost(
    val context: Context,
    val fragmentActivity: FragmentActivity,
    val viewLifecycleOwner: LifecycleOwner,
    val viewModelStoreOwner: ViewModelStoreOwner,
    val savedStateRegistryOwner: SavedStateRegistryOwner,
    val backPressedDispatcherOwner: OnBackPressedDispatcherOwner,
    val resultCaller: ActivityResultCaller,
    val overlayFragmentHolder: OverlayFragmentHolder?
) {

    @Suppress("unused")
    constructor(activity: AppCompatActivity) : this(
        context = activity,
        fragmentActivity = activity,
        viewLifecycleOwner = activity,
        viewModelStoreOwner = activity,
        savedStateRegistryOwner = activity,
        backPressedDispatcherOwner = activity,
        resultCaller = activity,
        overlayFragmentHolder = activity as? OverlayFragmentHolder
    )
}