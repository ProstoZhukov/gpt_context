package ru.tensor.sbis.application_tools

import org.junit.Assert.assertEquals
import org.junit.Test

class ClassMethodToMassageAppenderTest {

    @Test
    fun `Should return message appended with class name`() {
        assertEquals(
            "(NotificationSettingsInteractorImpl.java:76) - Notification settings loaded from server successfully.",
            ClassMethodToMassageAppender().createMessage(
                getMessage(),
                createStackTrace()
            )
        )
    }

    @Test(expected = IllegalStateException::class)
    fun `Should throw exception with empty stacktrace`() {
        ClassMethodToMassageAppender().createMessage(
            getMessage(),
            emptyArray()
        )
    }

    @Test(expected = IllegalStateException::class)
    fun `Should throw exception if cant detect Timber class(missed proguard rule)`() {
        ClassMethodToMassageAppender().createMessage(
            getMessage(),
            createObfuscatedStackTrace()
        )
    }

    private fun getMessage() = "Notification settings loaded from server successfully."

    private fun createStackTrace(): Array<StackTraceElement> {
        return arrayOf(
            StackTraceElement(
                "ru.tensor.sbis.application_tools.StackTraceLogger",
                "log",
                "StackTraceLogger.java",
                11
            ),
            StackTraceElement(
                "timber.log.Timber\$Tree",
                "prepareLog",
                "Timber.java",
                532
            ),
            StackTraceElement(
                "timber.log.Timber\$Tree",
                "log",
                "Timber.java",
                405
            ),
            StackTraceElement(
                "timber.log.Timber\$1",
                "d",
                "Timber.java",
                243
            ),
            StackTraceElement(
                "timber.log.Timber",
                "d",
                "Timber.java",
                38
            ),
            StackTraceElement(
                "ru.tensor.sbis.common.push.NotificationSettingsInteractorImpl",
                "loadFromServerInternal",
                "NotificationSettingsInteractorImpl.java",
                76
            ),
            StackTraceElement(
                "ru.tensor.sbis.common.push.NotificationSettingsInteractorImpl",
                "loadFromServer",
                "NotificationSettingsInteractorImpl.java",
                43
            )
        )
    }

    private fun createObfuscatedStackTrace(): Array<StackTraceElement> {
        return arrayOf(
            StackTraceElement(
                "ru.tensor.sbis.sfds.sfsfd",
                "log",
                "StackTraceLogger.java",
                11
            ),
            StackTraceElement(
                "timber.log.ffsff\$Tree",
                "prepareLog",
                "Timber.java",
                532
            ),
            StackTraceElement(
                "timber.log.fffsvvs\$Tree",
                "log",
                "Timber.java",
                405
            ),
            StackTraceElement(
                "sf.sdf.swer\$1",
                "d",
                "Timber.java",
                243
            ),
            StackTraceElement(
                "sdsds.ssd.sdfsdf",
                "d",
                "Timber.java",
                38
            ),
            StackTraceElement(
                "aaa.tensor.sdf.sdfs.ss.sdf",
                "loadFromServerInternal",
                "NotificationSettingsInteractorImpl.java",
                76
            )
        )
    }
}