package ru.tensor.sbis.communicator.push

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.communicator.common.push.MessagesPushAction
import ru.tensor.sbis.communicator.common.push.MessagesPushManager
import java.util.*

/** @SelfDocumented */
class MessagesPushManagerImpl : MessagesPushManager {

    private val mPublishSubject = PublishSubject.create<MessagesPushAction>()

    override fun getObservable(): Observable<MessagesPushAction> {
        return mPublishSubject
    }

    override fun executeAction(action: MessagesPushAction) {
        mPublishSubject.onNext(action)
    }
}