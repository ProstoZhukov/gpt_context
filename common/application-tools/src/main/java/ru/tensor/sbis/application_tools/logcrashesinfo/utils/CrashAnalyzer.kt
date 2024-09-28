package ru.tensor.sbis.application_tools.logcrashesinfo.utils

import ru.tensor.sbis.application_tools.logcrashesinfo.crashes.models.Crash
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @author du.bykov
 *
 * Формирует аналитические данные о краше.
 */
class CrashAnalyzer internal constructor(private val mThrowable: Throwable) {
    val analysis: Crash
        get() {
            val factsBuilder = StringBuilder()
            val placeOfCrash: String
            val simpleDateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            val date = simpleDateFormat.format(Date())
            factsBuilder.append(mThrowable.localizedMessage)
            factsBuilder.append(CrashAnalyticsHelper.STACK_TRACE_REGEX)
            factsBuilder.append(stackTrace(mThrowable.stackTrace))
            factsBuilder.append(CrashAnalyticsHelper.STACK_TRACE_REGEX)
            if (mThrowable.cause != null) {
                factsBuilder.append("Caused By: ")
                val stackTrace = mThrowable.cause!!.stackTrace
                placeOfCrash = getCrashOriginatingClass(stackTrace)
                factsBuilder.append(stackTrace(stackTrace))
            } else {
                placeOfCrash = getCrashOriginatingClass(mThrowable.stackTrace)
            }
            val reason: String = if (mThrowable.localizedMessage != null) {
                mThrowable.localizedMessage!!
            } else {
                ""
            }
            return Crash(placeOfCrash, reason, factsBuilder.toString(), date)
        }

    private fun getCrashOriginatingClass(stackTraceElements: Array<StackTraceElement>): String {
        if (stackTraceElements.isNotEmpty()) {
            val stackTraceElement = stackTraceElements[0]
            return String.format(
                Locale.getDefault(),
                "%s:%d",
                stackTraceElement.className,
                stackTraceElement.lineNumber
            )
        }
        return ""
    }

    private fun stackTrace(stackTrace: Array<StackTraceElement>): String {
        val builder = StringBuilder()
        for (stackTraceElement in stackTrace) {
            builder.append("at ")
            builder.append(stackTraceElement.toString())
            builder.append(CrashAnalyticsHelper.STACK_TRACE_REGEX)
        }
        return builder.toString()
    }

    companion object {
        private const val DATE_FORMAT = "dd.MM kk:mm:ss"
    }
}