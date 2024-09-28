package ru.tensor.sbis.tasks.feature

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Аргументы для открытия карточки документа.
 *
 * @author aa.sviridov
 */
sealed class DocumentOpenArgs : Parcelable {

    /**
     * Глобальный идентификатор документа.
     */
    abstract val documentUuid: UUID

    /**
     * Глобальный идентификатор события по документу.
     */
    abstract val eventUuid: UUID?

    /**
     * Тип документа.
     */
    abstract val docType: String
}

/**
 * Аргументы для открытия карточки документа с предустановленой шапкой.
 *
 * @author aa.sviridov
 */
sealed class MainDetailsDocumentOpenArgs : DocumentOpenArgs() {

    /**
     * Идентификатор лица-владельца реестра.
     */
    abstract val ownerFaceUuid: UUID

    /**
     * Предустановленная шапка документа, которую следует отобразить сразу,
     * см. [DocumentMainDetails].
     */
    abstract val mainDetails: DocumentMainDetails

    final override val documentUuid: UUID
        get() = mainDetails.documentUuid

    final override val eventUuid: UUID?
        get() = mainDetails.eventUuid

    final override val docType: String
        get() = mainDetails.docType
}

/**
 * Аргументы для открытия карточки связанного документа с предустановленой шапкой.
 *
 * @author aa.sviridov
 */
sealed class LinkedMainDetailsDocumentOpenArgs : MainDetailsDocumentOpenArgs() {

    /**
     * Глобальный идентификатор связанного документа.
     */
    abstract val linkedDocumentUuid: UUID
}

/**
 * Открытие модуля с предустановленным документом как поддокумент.
 *
 * @author aa.sviridov
 */
@Parcelize
data class WithDocumentAsSubDoc(
    override val mainDetails: DocumentMainDetails,
    override val linkedDocumentUuid: UUID,
    override val ownerFaceUuid: UUID,
) : LinkedMainDetailsDocumentOpenArgs()

/**
 * Открытие модуля с предустановленным документом, реестром и папкой.
 * @property registryType тип реестра (На мне/От меня).
 * @property folderUuid глобальный идентификатор папки.
 *
 * @author aa.sviridov
 */
@Parcelize
data class WithDocumentAndRegistryArgs(
    override val mainDetails: DocumentMainDetails,
    override val ownerFaceUuid: UUID,
    val registryType: RegistryType,
    val folderUuid: UUID?
) : MainDetailsDocumentOpenArgs()


/**
 * Открытие модуля по глобальному идентификатору и глобальному идентификатору события по документу.
 * @property canCryptoRun true, если документ открывается с возможностью совершения криптооперации,
 * иначе false.
 * @property certificateThumbprint отпечаток сертификата, который будет использован для выполнения криптооперации.
 *
 * @author aa.sviridov
 */
@Parcelize
data class WithUuidAndEventUuidArgs(
    override val documentUuid: UUID,
    override val eventUuid: UUID?,
    override val docType: String,
    val canCryptoRun: Boolean = false,
    val certificateThumbprint: String? = null
) : DocumentOpenArgs()