package ru.tensor.sbis.e_signatures_decl.details

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Конфигурация экрана деталей сертификата.
 * @property certificateThumbprint Отпечаток сертификата.
 * @property contractorFaceId Идентификатор контрагента.
 * @property pfxCloudId Идентификатор PFX.
 * @property operationId Идентификатор операции.
 * @property hasCloudOperation True, если для сертификата есть запрос на операцию (например, копирование).
 * @property mobileDeviceId Идентификатор мобильного устройства (текущего).
 * @property certOwnerMobileDeviceId Идентификатор мобильного устройства для взаимодействия.
 * @property operationType Тип операции (импорт/экспорт).
 * @property isOperationLaunchRequired True, если операцию надо начать выполнять сразу после запуска экрана.
 * @property hasActionButtons True, если на экране должны быть отображены кнопки с действиями.
 *
 * @author vv.malyhin
 */
@Deprecated("Переход на ru.tensor.sbis.e_signatures_decl.card")
@Parcelize
class CertificateDetailsScreenConfig(
    val certificateThumbprint: String,
    val contractorFaceId: Long? = null,
    val pfxCloudId: String? = null,
    val operationId: String? = null,
    val hasCloudOperation: Boolean = !operationId.isNullOrBlank(),
    val isOnCurrentDevice: Boolean? = null,
    val mobileDeviceId: String? = null,
    val certOwnerMobileDeviceId: String? = null,
    val operationType: Int? = null,
    val isOperationLaunchRequired: Boolean = false,
    val hasActionButtons: Boolean = false,
) : Parcelable

/**
 * Типы операций копирования. Используется для разделения операций, приходящих из облака в виде числа.
 * @property id Код операции.
 *
 * @author vv.malyhin
 */
enum class CopyingOperationType(val id: Int) {
    IMPORT(1),
    EXPORT(2)
}