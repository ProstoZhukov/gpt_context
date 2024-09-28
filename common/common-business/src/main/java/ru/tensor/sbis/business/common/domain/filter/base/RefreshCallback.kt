package ru.tensor.sbis.business.common.domain.filter.base

import android.annotation.SuppressLint
import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.Companion.PRIVATE
import ru.tensor.sbis.business.common.domain.filter.base.RefreshCallbackError.NOT_IMPLEMENTED
import ru.tensor.sbis.business.common.domain.filter.base.RefreshCallbackError.NO_CONNECTION
import ru.tensor.sbis.business.common.domain.filter.base.RefreshCallbackError.NO_PERMISSION
import ru.tensor.sbis.business.common.domain.filter.base.RefreshCallbackError.NO_RIGHTS
import ru.tensor.sbis.business.common.domain.filter.base.RefreshCallbackError.UNKNOWN_ERROR
import ru.tensor.sbis.business.common.domain.filter.base.RefreshCallbackSyncResult.ERRORS
import ru.tensor.sbis.business.common.domain.filter.base.RefreshCallbackSyncResult.INCOMPLETE
import ru.tensor.sbis.business.common.domain.filter.base.RefreshCallbackSyncResult.values
import ru.tensor.sbis.business.common.ui.base.Error
import ru.tensor.sbis.crud.generated.DataRefreshCallback
import ru.tensor.sbis.common.BuildConfig
import timber.log.Timber

/**
 * Представление коллбэка синхронизации о получении данных в удобном для domain слоя виде.
 *
 * @param info карта с результатами синхронизации из модели контроллера [DataRefreshCallback]
 */
class RefreshCallback(private val info: Map<String, String>?) {

    /** Результаты синхронизации. */
    private val syncResult = extractResult()

    /** Синхронизация успешно завершена. */
    val isSuccess: Boolean
        get() = syncResult.isSuccess && isFail.not()

    /** Синхронизация провалилась. */
    val isFail: Boolean
        get(): Boolean = error != null || !syncResult.isSuccess

    /** Синхронизация успешно завершена сейчас или ранее. */
    val isComplete: Boolean = syncResult.isCompleted

    /** Хэш синхронизации. */
    val syncHash: String = info?.get(HASH_KEY).orEmpty()

    /** Признак наличия следующих данных. */
    val haveMore: Boolean = info?.get(HAVE_MORE_KEY).toBoolean()

    /** Объект ошибки. Содержит текст ошибки для отображения пользователю. */
    val error: Error?
        get() = extractError()

    /** Сообщение об ошибке ориентированное на отображение пользователю */
    val userMessage: String
        get() = when {
            info == null                             -> ""
            info.containsKey(USER_ERROR_MESSAGE_KEY) -> info[USER_ERROR_MESSAGE_KEY].orEmpty()
            else                                     -> errorMessage
        }

    /** Сообщение об ошибке. */
    val errorMessage: String
        get() = when {
            info == null                          -> ""
            info.containsKey(ERROR_MESSAGE_KEY_1) -> info[ERROR_MESSAGE_KEY_1].orEmpty()
            info.containsKey(ERROR_MESSAGE_KEY_2) -> info[ERROR_MESSAGE_KEY_2].orEmpty()
            else                                  -> ""
        }

    /**
     * Количество закэшированных записей.
     * Используется для итеративного поиска.
     */
    val receivedRecordsSize: Int
        get() = info?.get(RESULT_ITERATIVE_SIZE)?.toIntOrNull() ?: 0

    /**
     * Логировать состояние колбэка данных.
     */
    fun reportLog(
        expectedHash: String = "",
        filterName: String = ""
    ) {
        if (BuildConfig.DEBUG) {
            if (expectedHash.isNotEmpty()) {
                Timber.tag(javaClass.simpleName).w("$LOG_MSG $filterName: $syncHash == $expectedHash")
            }
            Timber.tag(javaClass.simpleName).w("$info")
        }
    }

    private fun extractResult(): RefreshCallbackSyncResult {
        val syncResult = info?.get(RESULT_KEY)
        val ordinal = if (syncResult == OPTIONAL_SYNC_ERROR_RESULT) {
            ERRORS.ordinal
        } else {
            syncResult?.toIntOrNull() ?: INCOMPLETE.ordinal
        }
        return values().getOrElse(ordinal) { INCOMPLETE }
    }

    @SuppressLint("VisibleForTests")
    private fun extractError(): Error? {
        val codeValue = info?.get(ERROR_CODE_KEY_1) ?: info?.get(ERROR_CODE_KEY_2)
        val code = codeValue?.toIntOrNull() ?: return null
        return when (RefreshCallbackError.fromValue(code)) {
            NO_CONNECTION   -> Error.NoInternetConnection()
            NO_PERMISSION,
            NO_RIGHTS       -> Error.NoPermissionsError(userMessage)
            UNKNOWN_ERROR,
            NOT_IMPLEMENTED -> Error.UnknownError(userMessage)
        }
    }

    companion object {
        @VisibleForTesting(otherwise = PRIVATE)
        const val HASH_KEY = "Hash"
        private const val RESULT_KEY = "Result"
        private const val HAVE_MORE_KEY = "HaveMore"
        private const val RESULT_ITERATIVE_SIZE = "Size"
        private const val ERROR_CODE_KEY_1 = "ErrorCode"
        private const val ERROR_CODE_KEY_2 = "Error"
        private const val ERROR_MESSAGE_KEY_1 = "ErrorMsg"
        private const val ERROR_MESSAGE_KEY_2 = "Error_message"
        private const val USER_ERROR_MESSAGE_KEY = "User_error_message"
        private const val OPTIONAL_SYNC_ERROR_RESULT = "ERROR"
        private const val LOG_MSG = "DATA REFRESH CALLBACK onEqualCallback compare hashes of callback and filter"
    }
}
