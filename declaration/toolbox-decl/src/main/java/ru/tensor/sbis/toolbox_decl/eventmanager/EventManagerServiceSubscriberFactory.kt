package ru.tensor.sbis.toolbox_decl.eventmanager

/** @SelfDocumented */
interface EventManagerServiceSubscriberFactory {
    /** @SelfDocumented */
    fun create(): EventManagerServiceSubscriber
}