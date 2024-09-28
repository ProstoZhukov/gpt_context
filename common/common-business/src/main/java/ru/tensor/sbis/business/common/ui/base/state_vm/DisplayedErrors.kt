package ru.tensor.sbis.business.common.ui.base.state_vm

import ru.tensor.sbis.CXX.SbisException
import ru.tensor.sbis.business.common.R
import ru.tensor.sbis.business.common.ui.base.Error
import ru.tensor.sbis.business.common.ui.base.state_vm.UiErrorType.CHECK_PERMISSION_ERROR
import ru.tensor.sbis.business.common.ui.base.state_vm.UiErrorType.DATA_RECEIVING_ERROR
import ru.tensor.sbis.business.common.ui.base.state_vm.UiErrorType.NETWORK_ERROR
import ru.tensor.sbis.business.common.ui.base.state_vm.UiErrorType.NO_DATA_ERROR
import ru.tensor.sbis.business.common.ui.base.state_vm.UiErrorType.NO_DATA_FOR_FILTER
import ru.tensor.sbis.business.common.ui.base.state_vm.UiErrorType.NO_DATA_FOR_PERIOD
import ru.tensor.sbis.business.common.ui.base.state_vm.UiErrorType.NO_DATA_FOUND
import ru.tensor.sbis.business.common.ui.base.state_vm.UiErrorType.PERMISSION_ERROR
import ru.tensor.sbis.business.common.ui.base.state_vm.UiErrorType.UNKNOWN_ERROR
import ru.tensor.sbis.design.stubview.StubViewImageType
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.stubview.R as RStubView

/**
 * Тип ошибки
 */
interface ErrorType

/**
 * Набор типов ошибок возможных на пользовательском интерфейсе
 */
enum class UiErrorType : ErrorType {
    NO_DATA_ERROR,
    DATA_RECEIVING_ERROR,
    NO_DATA_FOR_PERIOD,
    NO_DATA_FOR_FILTER,
    NO_DATA_FOUND,
    UNKNOWN_ERROR,
    PERMISSION_ERROR,
    CHECK_PERMISSION_ERROR,
    NETWORK_ERROR
}

//TODO https://online.sbis.ru/opendoc.html?guid=076506d6-a6e4-4dd4-9440-b67f65781e3b
/**
 * Интерфейс описвывающий различные ошибки
 * Содержит описания ошибок по-умолчанию
 *
 * @author as.chadov
 *
 * @property noDataError ошибка отсутствия данных
 * @property dataReceivingError ошибка получения данных
 * @property noDataForPeriod ошибка отсутствия данных за выбранный период
 * @property noDataForFilter ошибка отсутствия результатов по фильтру
 * @property noDataFound ошибка отсутствия результатов поиска
 * @property unknownError неизвестная ошибка
 * @property permissionError ошибка прав доступа
 * @property networkError ошибка соединения с сетью
 * @property popupNetworkError ошибка соединения с сетью для отображения с помощью панели-информера
 */
interface DisplayedErrors {

    val noDataError: InformationVM
        get() = InformationVM(
            type = NO_DATA_ERROR,
            imageType = StubViewImageType.NOT_FOUND,
            headerResId = R.string.business_error_no_data
        )

    val dataReceivingError: InformationVM
        get() = InformationVM(
            type = DATA_RECEIVING_ERROR,
            imageType = StubViewImageType.ERROR,
            headerResId = R.string.business_error_receiving_data
        )

    val noDataForPeriod: InformationVM
        get() = InformationVM(
            type = NO_DATA_FOR_PERIOD,
            imageType = StubViewImageType.NO_DATA,
            headerResId = R.string.business_error_no_data_for_period,
            commentResId = R.string.business_try_change_period,
            activeCommentResId = R.string.business_try_change_period_active
        )

    val noDataForFilter: InformationVM
        get() = InformationVM(
            type = NO_DATA_FOR_FILTER,
            imageType = StubViewImageType.NOT_FOUND,
            commentResId = R.string.business_error_no_data_for_filter
        )

    val noDataFound: InformationVM
        get() = InformationVM(
            type = NO_DATA_FOUND,
            imageType = StubViewImageType.NO_DATA,
            commentResId = R.string.business_error_no_data_found
        )

    val unknownError: InformationVM
        get() = InformationVM(
            type = UNKNOWN_ERROR,
            commentResId = R.string.business_error_unknown
        )

    val permissionError: InformationVM
        get() = InformationVM(
            type = PERMISSION_ERROR,
            headerResId = R.string.business_error_permission,
            commentResId = R.string.business_no_permission_comment
        )

    val networkError: InformationVM
        get() = InformationVM(
            type = NETWORK_ERROR,
            imageType = StubViewImageType.ERROR,
            headerResId = RStubView.string.design_stub_view_no_connection_message,
            commentResId = RStubView.string.design_stub_view_no_connection_details,
            activeCommentResId = RStubView.string.design_stub_view_no_connection_details_clickable
        )

    val popupNetworkError: InformationVM
        get() = InformationVM(
            type = NETWORK_ERROR,
            headerResId = R.string.business_error_popup_network_header,
            popupIconResId = RDesign.string.design_mobile_icon_wifi_none
        )

    val checkPermissionError
        get() = InformationVM(
            type = CHECK_PERMISSION_ERROR,
            imageType = StubViewImageType.ERROR,
            headerResId = R.string.business_error_check_permission,
            commentResId = R.string.business_try_check_connection_and_update_again
        )

    val noCashboxError: InformationVM
        get() = InformationVM(
            type = DATA_RECEIVING_ERROR,
            imageType = StubViewImageType.NO_SALARY_DATA,
            headerResId = R.string.business_error_no_cashbox
        )

    /**
     * Преобразовывает исключение в ее отображаемое представление вью-модели
     * @param error любая ошибка или исключение
     * @param useForPopupNotification используется для показа через панель-информер
     *
     * @return вью-модель отображения ошибки
     */
    fun from(error: Throwable, useForPopupNotification: Boolean = false): InformationVM = when (error) {
        is Error -> from(error, useForPopupNotification)
        is SbisException -> {
            val message = error.errorUserMessage.takeIf(String::isNotBlank) ?: error.errorMessage
            InformationVM(errorText = message)
        }

        else -> {
            val message = error.message.orEmpty()
            unknownError.also { it.errorText = message }
        }
    }

    /**
     * Преобразовывает ошибку в ее отображаемое представление вью-модели
     * @param error пользовательская ошибка приложения
     * @param useForPopupNotification используется для показа через панель-информер
     *
     * @return вью-модель отображения ошибки
     */
    fun from(error: Error, useForPopupNotification: Boolean = false): InformationVM = when (error) {
        is Error.NoSearchDataError -> noDataFound
        is Error.NoDataReceivedError -> noDataError
        is Error.NoDataForPeriodError -> noDataForPeriod
        is Error.NoDataForFilterError -> noDataForFilter
        is Error.NoPermissionsError -> permissionError
        is Error.NetworkError,
        is Error.NoInternetConnection -> if (!useForPopupNotification) networkError else popupNetworkError

        is Error.UnknownError,
        is Error.NotLoadYetError -> dataReceivingError.apply {
            if (error.message.isNotBlank()) errorText = error.message
        }
    }
}