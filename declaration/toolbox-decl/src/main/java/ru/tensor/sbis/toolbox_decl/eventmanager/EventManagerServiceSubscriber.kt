package ru.tensor.sbis.toolbox_decl.eventmanager

import java.util.HashMap

/** @SelfDocumented */
interface EventManagerServiceSubscriber {

    /** @SelfDocumented */
    fun subscribeOnEvent(eventKey: String, eventCallback: (HashMap<String, String>) -> Unit)

    /** @SelfDocumented */
    fun enableSubscriptions()

    /** @SelfDocumented */
    fun disableSubscriptions()
}
