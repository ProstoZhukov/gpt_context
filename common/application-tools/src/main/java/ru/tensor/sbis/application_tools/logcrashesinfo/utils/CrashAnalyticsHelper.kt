package ru.tensor.sbis.application_tools.logcrashesinfo.utils

import android.content.Context
import ru.tensor.sbis.application_tools.R
import ru.tensor.sbis.application_tools.logcrashesinfo.crashes.models.Crash
import timber.log.Timber
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.Arrays
import java.util.Date

/**
 * @author du.bykov
 *
 * Утилитные методы для формирования данных о краше при его отправке.
 */
class CrashAnalyticsHelper(context: Context) {
    private val mDir: File
    private var mCurrentFile: File? = null

    private fun prepareOnStart() {
        val files = mDir.listFiles()
            ?.takeIf { it.size > MAX_CRASH_FILES }
            ?.let(::sortFiles)
            ?: return
        files.take(MAX_CRASH_FILES / 2)
            .forEach { it.delete() }
    }

    fun insertCrash(crashRecord: Crash) {
        val file = File(mDir, crashRecord.date + FILE_EXTENSION)
        try {
            if (file.createNewFile()) {
                val bw = BufferedWriter(FileWriter(file))
                bw.write(crashRecord.place + CRASH_INFO_REGEX)
                bw.write(crashRecord.reason + CRASH_INFO_REGEX)
                bw.write(crashRecord.stackTrace + CRASH_INFO_REGEX)
                bw.write(crashRecord.date + CRASH_INFO_REGEX)
                bw.flush()
                bw.close()
            }
        } catch (exception: Exception) {
            Timber.e(exception)
        }
    }

    fun deleteCrashes() {
        val files = mDir.listFiles() ?: return
        for (file in files) {
            file.delete()
        }
    }

    val fileToShare: File
        get() = mCurrentFile!!
    val crashes: List<Crash>
        get() {
            val files = sortFiles(mDir.listFiles())
                ?: return emptyList()
            val crashes = ArrayList<Crash>()
            for (file in files) {
                crashes.add(toCrash(readFile(file)))
            }
            return crashes
        }

    fun getCrashByName(crashFileName: String): Crash {
        val files = sortFiles(mDir.listFiles())
            ?: return getCrashStub(crashFileName)
        for (file in files) {
            if (file.name == crashFileName + FILE_EXTENSION) {
                mCurrentFile = file
                return toCrash(readFile(file))
            }
        }
        return getCrashStub(crashFileName)
    }

    fun getCrashByPosition(crashPosition: Int): Crash {
        val files = sortFiles(mDir.listFiles())
            ?: return getCrashStub("")
        mCurrentFile = files[crashPosition]
        return toCrash(readFile(mCurrentFile!!))
    }

    private fun getCrashStub(crashFileName: String): Crash {
        return Crash(crashFileName, "Краш заглушка: файл с крашем не найден", "", Date().toString())
    }

    private fun readFile(file: File): StringBuilder {
        val stringBuilder = StringBuilder()
        try {
            val bufferedReader = BufferedReader(FileReader(file))
            while (bufferedReader.ready()) {
                stringBuilder.append(bufferedReader.readLine())
            }
            bufferedReader.close()
        } catch (e: Exception) {
            Timber.e(e)
        }
        return stringBuilder
    }

    private fun toCrash(stringBuilder: StringBuilder): Crash {
        val crashInfo = stringBuilder.toString().split(CRASH_INFO_REGEX).toTypedArray()
        return Crash(
            crashInfo[0],
            crashInfo[1],
            stackTrace(crashInfo[2]),
            crashInfo[3]
        )
    }

    private fun stackTrace(stackTrace: String): String {
        val stackTraceArray = stackTrace.split(STACK_TRACE_REGEX).toTypedArray()
        val builder = StringBuilder()
        for (stackTraceElement in stackTraceArray) {
            builder.append(stackTraceElement)
            builder.append("\n")
        }
        return builder.toString()
    }

    /**
     * Отсортировать файлы по возрастанию времени модификации.
     * В отличие от реализации с обращением к [File.lastModified] в ходе сортировки и вычислением разницы этого значения
     * с конвертацией в [Int], предотвращает нарушение контракта сравнения, если результат [File.lastModified]
     * неконсистентный (например, при ошибке i/o), либо из-за переполнения [Int].
     */
    private fun sortFiles(files: Array<File>?): List<File>? {
        files ?: return null
        val modificationTimes = files.mapIndexed { i, it ->
            i to it.lastModified()
        }.toMutableList()
        // Сортируем индексы исходного массива.
        modificationTimes.sortBy { it.second }
        // Расставляем элементы исходного массива согласно сортировке.
        return modificationTimes.map { files[it.first] }
    }

    companion object {
        const val STACK_TRACE_REGEX = "/=/"
        private const val MAX_CRASH_FILES = 100
        private const val FILE_EXTENSION = ".txt"
        private const val CRASH_INFO_REGEX = "/:/"
    }

    init {
        mDir = File(context.filesDir, context.getString(R.string.application_tools_crashes_direction_name))
        if (!mDir.exists()) {
            mDir.mkdir()
        }
        prepareOnStart()
    }
}