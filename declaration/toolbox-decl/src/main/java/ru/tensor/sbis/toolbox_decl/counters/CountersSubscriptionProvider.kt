package ru.tensor.sbis.toolbox_decl.counters

import kotlinx.coroutines.flow.Flow
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Предоставляет возможность подписки на обновления счётчиков.
 *
 * @author us.bessonov
 */
interface CountersSubscriptionProvider : Feature {

    /** @SelfDocumented */
    val counters: Flow<Map<String, UnreadAndTotalCounterModel>>
}