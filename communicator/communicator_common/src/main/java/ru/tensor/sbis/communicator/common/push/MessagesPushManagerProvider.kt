package ru.tensor.sbis.communicator.common.push

import ru.tensor.sbis.plugin_struct.feature.Feature

/** @SelfDocumented */
interface MessagesPushManagerProvider : Feature {
    /** @SelfDocumented */
    val messagesPushManager: MessagesPushManager
}