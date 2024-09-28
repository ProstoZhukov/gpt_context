package ru.tensor.sbis.base_components.activity

import ru.tensor.sbis.base_components.util.authAware
import ru.tensor.sbis.entrypoint_guard.activity.contract.ActivityBehaviour
import ru.tensor.sbis.entrypoint_guard.activity.contract.ActivityContentFactory
import ru.tensor.sbis.user_activity_track.activity.UserActivityTrackable
import ru.tensor.sbis.verification_decl.auth.AuthAware

/**
 * [EntryPoint] аналог [TrackingActivity].
 *
 * @author kv.martyshenko
 */
abstract class TrackingEntryPointActivity<T : TrackingEntryPointActivity<T>>(
    contentFactory: ActivityContentFactory<T>,
    vararg behaviours: ActivityBehaviour<T>
) : BaseEntryPointActivity<T>(
    contentFactory,
    *behaviours
),
    UserActivityTrackable,
    AuthAware by authAware() {

    override val isTrackActivityEnabled: Boolean = true

    override val screenName: String by lazy {
        this::class.java.name
    }
}