package ru.tensor.sbis.logging.log_packages.domain

import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.SchedulerSupport
import io.reactivex.schedulers.Schedulers
import ru.tensor.sbis.logging.domain.LogPackageService
import ru.tensor.sbis.common.util.shakedetection.ShakeDetector
import java.util.*
import javax.inject.Inject

/**
 * Интерактора для экрана списка пакетов логов.
 *
 * @param logPackageService
 *
 * @author av.krymov
 */
class LogPackageInteractor @Inject constructor(
    private val logPackageService: LogPackageService
) {

    /**
     * Отправить пакет логов.
     */
    fun sendLog(dataFiles: List<String>): Completable {
        return logPackageService.requestToSendLogs(dataFiles)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * Удалить пакет логов.
     */
    fun removeLogPackage(uuid: UUID): Completable {
        return logPackageService.delete(uuid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * Вызвать segfault
     * Использовать только в связке с [ShakeDetector]
     */
    fun symbolDiagnose(): Completable {
        return logPackageService.diagnoseSymbols()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * Выгрузить логи в указанную директорию. Директория должна существовать на момент вызова
     */
    @SchedulerSupport(SchedulerSupport.IO)
    fun exportLogs(directory: String): Completable {
        return logPackageService.exportLogs(directory)
            .subscribeOn(Schedulers.io())
    }
}