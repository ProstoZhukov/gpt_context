package ru.tensor.sbis.scanner.data.interactor;


import androidx.annotation.NonNull;

import java.util.ArrayList;

import io.reactivex.Completable;
import io.reactivex.Observable;
import ru.tensor.sbis.scanner.data.model.ScannerPage;
import ru.tensor.sbis.scanner.generated.ScannerPageInfo;

/**
 * @author am.boldinov
 */
public interface ScannerPageInteractor {

    @NonNull
    Observable<Integer> getPageCountAsync();

    @NonNull
    Observable<ScannerPage> getLastPageAsync();

    @NonNull
    Observable<ScannerPage> getPageById(int pageId);

    @NonNull
    Observable<ArrayList<ScannerPageInfo>> getPageListSync();

    @NonNull
    Completable deletePageSync(int imageId);

    void deleteAllPagesAsync();
}
