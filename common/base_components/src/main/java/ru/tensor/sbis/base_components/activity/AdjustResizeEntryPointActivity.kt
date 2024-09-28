package ru.tensor.sbis.base_components.activity

import ru.tensor.sbis.base_components.activity.behaviour.AdjustResizeBehaviour
import ru.tensor.sbis.base_components.keyboard.KeyboardAware
import ru.tensor.sbis.entrypoint_guard.activity.contract.ActivityBehaviour
import ru.tensor.sbis.entrypoint_guard.activity.contract.ActivityContentFactory

/**
 * [EntryPoint] аналог [AdjustResizeActivity].
 *
 * @author kv.martyshenko
 */
abstract class AdjustResizeEntryPointActivity<T : AdjustResizeEntryPointActivity<T>>(
    contentFactory: ActivityContentFactory<T>,
    vararg behaviours: ActivityBehaviour<T>
) : TrackingEntryPointActivity<T>(
    contentFactory = contentFactory,
    AdjustResizeBehaviour(),
    *behaviours
),
    KeyboardAware