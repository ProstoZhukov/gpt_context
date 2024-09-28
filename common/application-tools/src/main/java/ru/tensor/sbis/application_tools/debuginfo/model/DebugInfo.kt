package ru.tensor.sbis.application_tools.debuginfo.model

import ru.tensor.sbis.application_tools.logcrashesinfo.crashes.models.Crash

/**
 * @author du.bykov
 *
 * Запись о краше.
 */
class DebugInfo : BaseDebugInfo {

    private lateinit var mCrash: Crash

    val crashTitle: String
        get() = mCrash.date

    val crashMessage: String
        get() = mCrash.place

    constructor(
        title: Int,
        description: String
    ) : super(
        title,
        description
    )

    constructor(crash: Crash) {
        type = Type.CRASH
        mCrash = crash
    }
}