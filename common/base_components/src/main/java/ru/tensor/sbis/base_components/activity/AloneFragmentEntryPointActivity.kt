package ru.tensor.sbis.base_components.activity

import ru.tensor.sbis.base_components.activity.behaviour.LegacyFragmentBackPressDelegationBehaviour
import ru.tensor.sbis.base_components.activity.content.AloneFragmentContentFactory
import ru.tensor.sbis.entrypoint_guard.activity.contract.ActivityBehaviour

/**
 * [EntryPoint] аналог [AloneFragmentContainerActivity].
 *
 * @author kv.martyshenko
 */
abstract class AloneFragmentEntryPointActivity<T : AloneFragmentEntryPointActivity<T>>(
    aloneFragmentContentFactory: AloneFragmentContentFactory<T>,
    vararg behaviours: ActivityBehaviour<T>
) : TrackingEntryPointActivity<T>(
    aloneFragmentContentFactory,
    LegacyFragmentBackPressDelegationBehaviour(aloneFragmentContentFactory.fragmentTag),
    *behaviours
)