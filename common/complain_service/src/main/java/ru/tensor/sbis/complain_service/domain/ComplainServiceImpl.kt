package ru.tensor.sbis.complain_service.domain

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.communication_decl.complain.ComplainService
import ru.tensor.sbis.communication_decl.complain.data.ComplainEntityType
import ru.tensor.sbis.communication_decl.complain.data.ComplainParams
import ru.tensor.sbis.communication_decl.complain.data.ComplainReasonType
import ru.tensor.sbis.communication_decl.complain.data.ComplainResult
import ru.tensor.sbis.communication_decl.complain.data.ComplainStatus
import ru.tensor.sbis.complain.generated.EntityType
import ru.tensor.sbis.complain.generated.ReasonType
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.verification_decl.login.event.AuthEvent
import ru.tensor.sbis.complain.generated.ComplainService as ComplainServiceController
import java.util.UUID

/**
 * Реализация [ComplainService].
 *
 * @author da.zhukov
 */
internal class ComplainServiceImpl(loginInterface: LoginInterface) : ComplainService {

    private val controller by lazy(ComplainServiceController::instance)

    private val blockedPersons = mutableSetOf<UUID>()
    private val blockedListChangedSubject = PublishSubject.create<Unit>()
    private val disposable = CompositeDisposable()

    init {
        loginInterface.eventsObservable.subscribe {
            if (it.eventType == AuthEvent.EventType.LOGIN || it.eventType == AuthEvent.EventType.AUTHORIZED) {
                updateBlockedPersons()
            }
        }.storeIn(disposable)
    }

    override fun complain(params: ComplainParams): ComplainResult =
        controller.complain(
            params.entityType.toEntityType(),
            params.entityUUID,
            params.entityParentType?.toEntityType(),
            params.entityParentUuid,
            params.comment,
            params.reason.toReasonType(),
            params.additionalData
        ).toComplainResult()

    override fun blockPerson(uuid: UUID) {
        val result = controller.blockPerson(uuid)
        if (result.errorCode == ErrorCode.SUCCESS) {
            updateBlockedPersons()
        }
    }

    override fun unblockPerson(uuid: UUID) {
        val result = controller.unblockPerson(uuid)
        if (result.errorCode == ErrorCode.SUCCESS) {
            updateBlockedPersons()
        }
    }

    override fun isPersonBlocked(uuid: UUID): Boolean =
        blockedPersons.contains(uuid)

    override fun getBlockedListChangedObservable(): Observable<Unit> =
        blockedListChangedSubject.share()

    @Synchronized
    private fun updateBlockedPersons() {
        blockedPersons.clear()
        blockedPersons.addAll(controller.getBlockedPersons())
        blockedListChangedSubject.onNext(Unit)
    }

    private fun ComplainEntityType.toEntityType() =
        when (this) {
            ComplainEntityType.USER -> EntityType.USER
            ComplainEntityType.GROUP -> EntityType.GROUP
            ComplainEntityType.NEWS -> EntityType.NEWS
            ComplainEntityType.FORUM -> EntityType.FORUM
            ComplainEntityType.MESSAGE -> EntityType.MESSAGE
            ComplainEntityType.SABYGET_REVIEW -> EntityType.SABYGET_REVIEW
            ComplainEntityType.SABYGET -> EntityType.SABYGET
            ComplainEntityType.DIALOG -> EntityType.DIALOG
            ComplainEntityType.CHAT -> EntityType.CHAT
        }

    private fun ComplainReasonType.toReasonType() =
        when (this) {
            ComplainReasonType.SPAM -> ReasonType.SPAM
            ComplainReasonType.ANGRY -> ReasonType.ANGRY
        }

    private fun CommandStatus.toComplainResult() =
        ComplainResult(errorCode.toComplainStatus(), errorMessage)

    private fun ErrorCode.toComplainStatus() =
        when (this) {
            ErrorCode.SUCCESS -> ComplainStatus.SUCCESS
            ErrorCode.NETWORK_ERROR -> ComplainStatus.NETWORK_ERROR
            else -> ComplainStatus.OTHER_ERROR
        }
}