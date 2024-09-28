package ru.tensor.sbis.mvp.interactor.externalmedia;

import androidx.annotation.NonNull;

import java.io.File;

import io.reactivex.Observable;
import ru.tensor.sbis.mvp.data.model.PagedListResult;

/**
 * Created by pv.suvit on 13.11.2017.
 *
 * @author sa.nikitin
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings({"SpellCheckingInspection", "RedundantSuppression"})
public interface ExternalMediaInteractor {

    @NonNull
    Observable<PagedListResult<File>> getExternalMedia(int positionToStartFrom, int count, String[] supportedTypes);

}
