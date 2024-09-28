package ru.tensor.sbis.business_tools_decl.reporting;

import androidx.annotation.NonNull;

import io.reactivex.Observable;
import ru.tensor.sbis.business_tools_decl.reporting.event.ReportingReadEvent;
import ru.tensor.sbis.business_tools_decl.reporting.event.RequirementResultEvent;
import ru.tensor.sbis.business_tools_decl.reporting.event.RequirementStateChangeEvent;
import ru.tensor.sbis.plugin_struct.feature.Feature;

/**
 * Интерфейс поставщика событий отчетности
 *
 * @author ev.grigoreva
 */
public interface ReportingEventProvider extends Feature {

    /**
     * @return подписка на события обновления состояния требования
     */
    @NonNull
    Observable<RequirementStateChangeEvent> getRequirementStateChangeEventObservable();

    /**
     * @return подписка на события прочтения отчета (открытие карточки - загрузка информации с сервера)
     */
    @NonNull
    Observable<ReportingReadEvent> getReportingReadEvent();

    /**
     * @return подписка на события выполнения действия с требованием
     */
    @NonNull
    Observable<RequirementResultEvent> getRequirementResultEventObservable();
}
