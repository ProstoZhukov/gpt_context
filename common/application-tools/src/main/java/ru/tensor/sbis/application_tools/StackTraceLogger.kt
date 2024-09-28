package ru.tensor.sbis.application_tools

import timber.log.Timber

/**
 * @author du.bykov
 *
 * Логирование стека методов исключения.
 */
class StackTraceLogger : Timber.DebugTree() {

    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        throwable: Throwable?
    ) {
        // DO NOT switch this to Thread.getCurrentThread().getStackTrace(). The test will pass
        // because Robolectric runs them on the JVM but on Android the elements are different.
        super.log(
            priority,
            tag,
            ClassMethodToMassageAppender()
                .createMessage(
                    message,
                    Throwable().stackTrace
                ),
            throwable
        )
    }
}