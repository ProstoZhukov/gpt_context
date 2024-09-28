package ru.tensor.sbis.common.exceptions

import android.content.Context
import ru.tensor.sbis.common.R
import ru.tensor.sbis.design.text_span.SimpleInformationView
import ru.tensor.sbis.design.stubview.StubViewCase

/**
 * Интерфейс обработчика ошибок.
 *
 * @author am.boldinov
 */
interface ErrorHandler<EMPTY_VIEW_CONTENT> {

    /**
     * Получить данные для отображения в представлении.
     */
    fun getErrorData(exception: Throwable?): ErrorData<EMPTY_VIEW_CONTENT>

}

/**
 * Обработчк ошибок для отображения заглушек по стандарту.
 * Из коробки поддерживает основные сценарии, возникающие повсеместно.
 * Ключевые ресурсы открыты для переопределения для случаев, когда стандартная ошибка требует нестандартной обработки:
 * например - заглушки, отличающейся от стандартной.
 */
open class StubErrorHandler(private val context: Context): ErrorHandler<SimpleInformationView.Content> {

    /**
     * Ключевой ресурс ошибки отсутствия соединения с интернетом
     */
    open val noInternetKey = StubViewCase.NO_CONNECTION.messageRes

    /**
     * Ключевой ресурс ошибки отсутствия запрашиваемой записи
     */
    open val notFoundKey = StubViewCase.NO_SEARCH_RESULTS.messageRes

    /**
     * Ключевой ресурс ошибки, природа которой не определена
     */
    open val commonErrorKey = StubViewCase.SBIS_ERROR.messageRes

    /**
     * Ключ ошибки отсутствия прав на чтение запрашиваемой записи
     */
    open val noRightsKey = -1  // нет аналога в CommonStubCase, доступа к ресурсам из common нет

    override fun getErrorData(exception: Throwable?): ErrorData<SimpleInformationView.Content> {
        return when (exception) {
            is LoadDataException -> {
                val userMessage =
                    if (exception.type == LoadDataException.Type.NO_INTERNET_CONNECTION) {
                        context.getString(R.string.common_no_network_available_check_connection)
                    } else {
                        exception.errorMessage
                    }
                val content = SimpleInformationView.Content(context, getHeaderResByErrorType(exception.type), 0, 0)
                ErrorData(userMessage, content)
            }
            else                 -> {
                ErrorData(
                    context.getString(commonErrorKey),
                    SimpleInformationView.Content(context, commonErrorKey, 0, 0)
                )
            }
        }
    }

    private fun getHeaderResByErrorType(errorType: LoadDataException.Type): Int {
        return when (errorType) {
            LoadDataException.Type.NO_INTERNET_CONNECTION -> noInternetKey
            LoadDataException.Type.NOT_FOUND              -> notFoundKey
            LoadDataException.Type.INCORRECT_LOADING_PARAMS,
            LoadDataException.Type.DEFAULT                -> commonErrorKey
            LoadDataException.Type.NO_RIGHTS              -> noRightsKey
            else                                          -> 0 //не обрабатываем not_loaded_yet
        }
    }
}