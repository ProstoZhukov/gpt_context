package ru.tensor.sbis.application_tools

import java.util.regex.Pattern

/**
 * @author du.bykov
 *
 * Класс предназначен для добавления имени метода в сообщения для лога
 */
internal class ClassMethodToMassageAppender {

    /**
     * Выбирает из stackTrace метод, из которого вызвано логирование Timber и добавляет этот метод с номером строки в начало текста сообщения
     *
     * @param message - текст сообщения, переданный для записи в лог
     *
     * @param stackTrace - стек вызванных методов, до вызова записи в лог
     */
    fun createMessage(
        message: String,
        stackTrace: Array<StackTraceElement>
    ): String {
        val callStackIndex = findIndexBeforeTimber(stackTrace)
        val clazz = extractClassName(stackTrace[callStackIndex])
        val lineNumber = stackTrace[callStackIndex].lineNumber

        return "($clazz.java:$lineNumber) - $message"
    }

    private fun findIndexBeforeTimber(stackTrace: Array<StackTraceElement>): Int {
        val penultimate = stackTrace.size - 2
        for (i in penultimate downTo 0) {
            if (stackTrace[i].className.contains(TIMBER_CLASS)) return i + 1
        }
        throw IllegalStateException(
            "Synthetic stacktrace didn't have enough elements: are you using proguard?"
        )
    }

    /**
     * Extract the class name without any anonymous class suffixes (e.g., `Foo$1`
     * becomes `Foo`).
     */
    private fun extractClassName(element: StackTraceElement): String {
        var tag = element.className
        val m = ANONYMOUS_CLASS.matcher(tag)
        if (m.find()) {
            tag = m.replaceAll("")
        }
        return tag.substring(tag.lastIndexOf('.') + 1)
    }

}

private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")
private const val TIMBER_CLASS = "Timber"