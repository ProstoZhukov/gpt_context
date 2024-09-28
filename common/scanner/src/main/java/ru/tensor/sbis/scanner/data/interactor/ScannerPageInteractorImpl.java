package ru.tensor.sbis.scanner.data.interactor;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.internal.functions.Functions;
import io.reactivex.schedulers.Schedulers;
import ru.tensor.sbis.common.data.DependencyProvider;
import ru.tensor.sbis.common.rx.consumer.FallbackErrorConsumer;
import ru.tensor.sbis.mvp.interactor.BaseInteractor;
import ru.tensor.sbis.scanner.data.model.ScannerPage;
import ru.tensor.sbis.scanner.generated.ScannerApi;
import ru.tensor.sbis.scanner.generated.ScannerPageInfo;

/**
 * @author am.boldinov
 */
public class ScannerPageInteractorImpl extends BaseInteractor implements ScannerPageInteractor {

    @NonNull
    private final DependencyProvider<ScannerApi> mScannerApi;

    public ScannerPageInteractorImpl(@NonNull DependencyProvider<ScannerApi> scannerApi) {
        super();
        mScannerApi = scannerApi;
    }

    @NonNull
    @Override
    public Observable<Integer> getPageCountAsync() {
        return Observable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return mScannerApi.get().pageList().size();
            }
        })
            .subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    @Override
    public Observable<ScannerPage> getLastPageAsync() {
        return Observable.fromCallable(new Callable<ScannerPage>() {
            @Override
            public ScannerPage call() throws Exception {
                return getLastPage();
            }
        })
            .subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    @Override
    public Observable<ScannerPage> getPageById(int pageId) {
        return Observable.fromCallable(new Callable<ScannerPage>() {
            @Override
            public ScannerPage call() throws Exception {
                List<ScannerPageInfo> pageInfoList = mScannerApi.get().pageList();
                for (int i = 0; i < pageInfoList.size(); i++) {
                    final ScannerPageInfo pageInfo = pageInfoList.get(i);
                    if (pageInfo.getId() == pageId) {
                        return new ScannerPage(pageInfo, i + 1);
                    }
                }
                return new ScannerPage();
            }
        })
            .subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    @Override
    public Observable<ArrayList<ScannerPageInfo>> getPageListSync() {
        return Observable.fromCallable(new Callable<ArrayList<ScannerPageInfo>>() {
            @Override
            public ArrayList<ScannerPageInfo> call() throws Exception {
                return mScannerApi.get().pageList();
            }
        });
    }

    @NonNull
    @Override
    public Completable deletePageSync(int imageId) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                mScannerApi.get().deletePage(imageId);
            }
        });
    }

    @Override
    public void deleteAllPagesAsync() {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                mScannerApi.get().deleteAllPages();
            }
        })
            .onErrorComplete()
            .subscribeOn(Schedulers.single())
            .subscribe(Functions.EMPTY_ACTION, FallbackErrorConsumer.DEFAULT);
    }

    @WorkerThread
    @NonNull
    private ScannerPage getLastPage() {
        List<ScannerPageInfo> pageList = mScannerApi.get().pageList();
        if (pageList.size() > 0) {
            return new ScannerPage(pageList.get(pageList.size() - 1), pageList.size());
        }
        return new ScannerPage();
    }
}
