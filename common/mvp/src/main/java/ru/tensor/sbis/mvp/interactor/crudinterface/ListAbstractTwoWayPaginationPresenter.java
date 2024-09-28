package ru.tensor.sbis.mvp.interactor.crudinterface;

import androidx.annotation.NonNull;

import java.util.HashMap;

import ru.tensor.sbis.common.util.NetworkUtils;
import ru.tensor.sbis.crud.generated.DataRefreshCallback;
import ru.tensor.sbis.mvp.interactor.crudinterface.filter.ListFilter;
import ru.tensor.sbis.mvp.interactor.crudinterface.subscribing.SubscriptionManager;
import ru.tensor.sbis.mvp.presenter.BaseTwoWayPaginationView;

/**
 * Базовый класс презентера для списка с двунаправленной подгрузкой страниц
 *
 * @author am.boldinov
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
public abstract class ListAbstractTwoWayPaginationPresenter<VIEW extends BaseTwoWayPaginationView<DataModel>, DataModel, FILTER extends ListFilter, QUERY_FILTER>
        extends BaseListAbstractTwoWayPaginationPresenter<VIEW, DataModel, FILTER, QUERY_FILTER, DataRefreshCallback> {

    public ListAbstractTwoWayPaginationPresenter(@NonNull FILTER filter, @NonNull SubscriptionManager subscriptionManager, @NonNull NetworkUtils networkUtils) {
        super(filter, subscriptionManager, networkUtils);
    }

    @SuppressWarnings({"unused", "RedundantSuppression"})
    @NonNull
    @Override
    protected DataRefreshCallback getDataRefreshCallback() {
        return new DataRefreshCallback() {
            @Override
            public void execute(HashMap<String, String> params) {
                onRefreshCallback(params);
            }
        };
    }
}

