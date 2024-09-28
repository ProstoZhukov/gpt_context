package ru.tensor.sbis.mvp.interactor.crudinterface.command;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;

/**
 * Базовая реализация команды загрузки данных с несколькими этапами пост-обработки.
 *
 * @param <DATA_MODEL> - изначальная модель данных, загружаемая из источника
 * @param <VIEW_MODEL> - итоговая вью модель, результат работы команды
 * @author am.boldinov
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings({"unused", "RedundantSuppression"})
public abstract class BaseFetchCommand<DATA_MODEL, VIEW_MODEL> extends BaseCommand {

    /**
     * Выполнить загрузку данных из указанного источника с несколькими этапами пост-обработки.
     *
     * @param source  - источник данных
     * @param refresh - если true - обновляем данные, иначе - загружаем
     * @return observable загрузки данных
     */
    @NonNull
    protected Observable<VIEW_MODEL> fetch(@NonNull Observable<DATA_MODEL> source, boolean refresh) {
        // Обработка загруженных данных
        Function<DATA_MODEL, DATA_MODEL> dataModelProcessor = getAfterFetchProcessor(refresh);
        if (dataModelProcessor != null) {
            source = source.map(dataModelProcessor);
        }
        // Трансформация загруженных данных
        Observable<VIEW_MODEL> mapped = source.map(getMapper(refresh));
        // Обработка трансформированных данных
        Function<VIEW_MODEL, VIEW_MODEL> viewModelProcessor = getAfterMappingProcessor(refresh);
        if (viewModelProcessor != null) {
            mapped = mapped.map(viewModelProcessor);
        }
        return performAction(
                mapped.compose(getFetchBackgroundSchedulers())
        );
    }

    /**
     * Получить маппер для транформации модели данных во вью модель.
     *
     * @param refresh - если true - обновляем данные, иначе - загружаем
     * @return функцию-трансформатор
     */
    @NonNull
    protected abstract Function<DATA_MODEL, VIEW_MODEL> getMapper(boolean refresh);

    /**
     * Пост-обработчик загруженных данных.
     *
     * @param refresh - если true - обновляем данные, иначе - загружаем
     * @return функцию-обработчик или null, если обработка не требуется
     */
    @Nullable
    protected Function<DATA_MODEL, DATA_MODEL> getAfterFetchProcessor(boolean refresh) {
        return null;
    }

    /**
     * Пост-обработчик трансформированных данных.
     *
     * @param refresh - если true - обновляем данные, иначе - загружаем
     * @return функцию-обработчик или null, если обработка не требуется
     */
    @Nullable
    protected Function<VIEW_MODEL, VIEW_MODEL> getAfterMappingProcessor(boolean refresh) {
        return null;
    }

    /**
     * @return трансформер для определения потока загрузки данных и потока вывода
     */
    @NonNull
    protected ObservableTransformer<VIEW_MODEL, VIEW_MODEL> getFetchBackgroundSchedulers() {
        return getObservableBackgroundSchedulers();
    }
}
