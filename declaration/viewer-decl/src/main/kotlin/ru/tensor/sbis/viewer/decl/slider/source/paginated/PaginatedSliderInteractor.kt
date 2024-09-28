package ru.tensor.sbis.viewer.decl.slider.source.paginated

import androidx.annotation.WorkerThread
import io.reactivex.Observable
import ru.tensor.sbis.viewer.decl.viewer.ViewerArgs
import java.io.Serializable
import java.util.EnumSet

/**
 * Интерактор просмотрщика вложений с пагинацией
 *
 * @author vv.chekurda
 */
@WorkerThread
interface PaginatedSliderInteractor<FILTER, DATA : ViewerArgs> : Serializable {

    fun list(filter: FILTER): Observable<PagedListResult<DATA>>

    fun refresh(filter: FILTER): Observable<PagedListResult<DATA>>

    fun setDataRefreshCallback(): Observable<EnumSet<PaginationRefreshEvent>>
}