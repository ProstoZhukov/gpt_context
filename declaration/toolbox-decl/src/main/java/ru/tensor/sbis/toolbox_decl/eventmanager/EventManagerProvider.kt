package ru.tensor.sbis.toolbox_decl.eventmanager

import ru.tensor.sbis.plugin_struct.feature.Feature

/** @SelfDocumented */
interface EventManagerProvider : Feature {

    /** @SelfDocumented */
    fun getEventManagerServiceSubscriberFactory(): EventManagerServiceSubscriberFactory
}