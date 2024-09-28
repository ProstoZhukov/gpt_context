package ru.tensor.sbis.appdesign.selection.datasource

import androidx.lifecycle.Observer
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import ru.tensor.sbis.person_decl.profile.model.ActivityStatus
import ru.tensor.sbis.person_decl.profile.model.ProfileActivityStatus
import ru.tensor.sbis.person_decl.profile.ActivityStatusConductor
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author ma.kolpakov
 */
internal class DemoActivityStatusConductor : ActivityStatusConductor {

    override fun observeActivityStatusWithTimestamp(uuid: UUID): Observable<ProfileActivityStatus> =
        throw IllegalStateException("Unexpected method call")

    override fun forceUpdate(uuidList: List<UUID>) =
        throw IllegalStateException("Unexpected method call")

    override fun getRetainingObservable(): Observable<ActivityStatus> =
        throw IllegalStateException("Unexpected method call")

    override fun observeActivityStatus(uuid: UUID): Observable<ActivityStatus> =
        Observable.interval(2L, TimeUnit.SECONDS).map { ActivityStatus.values().random() }

    override fun observeActivityStatusLightweight(uuid: UUID, observer: Observer<ActivityStatus>): Disposable =
        throw IllegalStateException("Unexpected method call")

    override fun initializeLightweightObservables(uuidList: MutableList<UUID>) =
        throw IllegalStateException("Unexpected method call")
}