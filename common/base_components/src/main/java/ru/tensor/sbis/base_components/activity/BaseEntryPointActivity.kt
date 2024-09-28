package ru.tensor.sbis.base_components.activity

import android.app.Activity
import androidx.fragment.app.Fragment
import ru.tensor.sbis.android_ext_decl.AndroidComponent
import ru.tensor.sbis.base_components.activity.behaviour.DisableAutofillBehaviour
import ru.tensor.sbis.base_components.activity.behaviour.FullScreenBehaviour
import ru.tensor.sbis.base_components.activity.behaviour.HotThemeReloadBehaviour
import ru.tensor.sbis.entrypoint_guard.activity.FlexibleEntryPointActivity
import ru.tensor.sbis.entrypoint_guard.activity.contract.ActivityBehaviour
import ru.tensor.sbis.entrypoint_guard.activity.contract.ActivityContentFactory

/**
 * [EntryPoint] аналог [BaseActivity].
 *
 * @author kv.martyshenko
 */
abstract class BaseEntryPointActivity<T : BaseEntryPointActivity<T>>(
    contentFactory: ActivityContentFactory<T>,
    vararg behaviours: ActivityBehaviour<T>
) : FlexibleEntryPointActivity<T>(
    contentFactory,
    HotThemeReloadBehaviour(),
    DisableAutofillBehaviour(),
    FullScreenBehaviour(),
    *behaviours
),
    AndroidComponent {

    // region AndroidComponent
    override fun getActivity(): Activity {
        return this
    }

    override fun getFragment(): Fragment? {
        return null
    }
    // endregion

}