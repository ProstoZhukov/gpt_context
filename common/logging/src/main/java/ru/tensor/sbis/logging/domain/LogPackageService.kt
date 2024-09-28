package ru.tensor.sbis.logging.domain

import io.reactivex.Completable
import ru.tensor.sbis.common.util.shakedetection.ShakeDetector
import ru.tensor.sbis.platform.logdelivery.generated.DiagnosticSettings
import ru.tensor.sbis.platform.logdelivery.generated.LogController
import ru.tensor.sbis.platform.logdelivery.generated.SymbolDiagnose
import java.util.UUID

/**
 * Реализация сервиса по работе с пакатами логов.
 *
 * @author av.krymov
 */
class LogPackageService {

    private val logController by lazy {
        LogController.instance()
    }

    /**
     * Запрос на отправку пакета логов.
     */
    fun requestToSendLogs(dataFiles: List<String>): Completable {
        return Completable.fromAction {
            logController.create(DiagnosticSettings(ArrayList(dataFiles)))
        }
    }

    /**
     * Удалить пакет логов.
     */
    fun delete(uuid: UUID): Completable {
        return Completable.fromAction {
            logController.delete(uuid)
        }
    }

    /**
     * Вызвать segfault.
     * Использовать только в связке с [ShakeDetector]
     */
    fun diagnoseSymbols(): Completable = Completable.fromAction {
        SymbolDiagnose.generateStack()
    }

    /**
     * Выгрузить логи в указанную директорию. Директория должна существовать на момент вызова.
     */
    fun exportLogs(directory: String): Completable {
        return Completable.fromAction {
            logController.exportLogs(directory)
        }
    }
}