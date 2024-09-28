package ru.tensor.sbis.application_tools.logsender.model

import java.io.File

/**
 * Данные для формирования запроса для доставки дампа логов на облако
 *
 * @author us.bessonov
 */
internal data class LogsData(
    val header: JsonHeader,
    val creationDate: String,
    val packageId: String,
    val projectId: String,
    val logFile: File,
    val zippedLogFile: File
)