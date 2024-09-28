package ru.tensor.sbis.scanner.ui.viewimage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.SerialDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import ru.tensor.sbis.common.rx.consumer.FallbackErrorConsumer;
import ru.tensor.sbis.common.util.CommonUtils;
import kotlin.Pair;
import ru.tensor.sbis.scanner.adapter.scannedimagelist.item.ScannedImageListItem;
import ru.tensor.sbis.scanner.data.interactor.editimage.EditImageInteractor;
import ru.tensor.sbis.scanner.data.interactor.scannedimagelist.ScannedImageListInteractor;
import ru.tensor.sbis.scanner.generated.ScannerPageInfo;
import ru.tensor.sbis.scanner.generated.ScannerRotateAngle;

/**
 * @author am.boldinov
 */
public class ViewImagePresenterImpl implements ViewImageContract.Presenter {

    @NonNull
    private final EditImageInteractor mEditImageInteractor;
    @NonNull
    private final ScannedImageListInteractor mImageListInteractor;
    @Nullable
    private ViewImageContract.View mView;
    @NonNull
    private final CompositeDisposable mActionDisposables = new CompositeDisposable();
    @NonNull
    private SerialDisposable mImageListDisposable = new SerialDisposable();

    @Nullable
    private List<ScannedImageListItem> mImageList;
    private boolean mReloadPageOnViewStarted;
    private Integer mCurrentPageId;

    public ViewImagePresenterImpl(@NonNull EditImageInteractor editImageInteractor, @NonNull ScannedImageListInteractor scannedImageListInteractor) {
        mEditImageInteractor = editImageInteractor;
        mImageListInteractor = scannedImageListInteractor;
    }

    @Override
    public void attachView(@NonNull ViewImageContract.View view) {
        mView = view;
        mView.setEnabledControls(true);
        loadImageList();
    }

    @Override
    public void detachView() {
        mImageListDisposable.dispose();
        mView = null;
    }

    @Override
    public void onViewStarted() {
        if (mReloadPageOnViewStarted) {
            mReloadPageOnViewStarted = false;
            loadImageList();
        }
    }

    @Override
    public void onDestroy() {
        mActionDisposables.dispose();
    }

    @Override
    public void setScannerPageInfo(@Nullable ScannerPageInfo scannerPageInfo) {
        if (scannerPageInfo != null) {
            mCurrentPageId = scannerPageInfo.getId();
            if (mImageList != null) {
                final ScannedImageListItem imageListItem = getCurrentImageItem();
                if (imageListItem != null) {
                    imageListItem.setPageInfo(scannerPageInfo);
                    displayImageList(mImageList, false);
                }
            }
        } else {
            loadImageList();
        }
    }

    @Override
    public void onCloseButtonClick() {
        if (mView != null) {
            mView.switchToMainScreen();
        }
    }

    @Override
    public void onEditButtonClick() {
        final Runnable completeAction = new Runnable() {
            @Override
            public void run() {
                final ScannedImageListItem imageListItem = getCurrentImageItem();
                if (mView != null && imageListItem != null) {
                    mView.switchToEditImageScreen(imageListItem.getPageInfo());
                }
            }
        };
        rotateCurrentImageIfNeed(completeAction, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                completeAction.run(); //независимо от успешности поворота изображения перекидываем на экран с редактированием
            }
        });
    }

    @Override
    public void onDoneButtonClick() {
        rotateImagesIfNeed(new Runnable() {
            @Override
            public void run() {
                switchToUploadImageScreen();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                if (mView != null) {
                    mView.showImageProcessingError();
                }
            }
        });
    }

    @Override
    public void onRotateButtonClick() {
        final ScannedImageListItem imageItem = getCurrentImageItem();
        if (imageItem != null) {
            imageItem.rotateCcw();
        }
    }

    @Override
    public void onDeleteButtonClick() {
        final ScannedImageListItem imageItem = getCurrentImageItem();
        if (imageItem == null) {
            return;
        }
        mActionDisposables.add(
            preparePageAction(mImageListInteractor.deleteScannedImage(imageItem.getPageInfo().getId()), false)
                .subscribe(new Consumer<List<ScannedImageListItem>>() {
                    @Override
                    public void accept(List<ScannedImageListItem> scannedImageListItems) throws Exception {
                        if (scannedImageListItems.isEmpty()) {
                            if (mView != null) {
                                mView.switchToScannerScreen();
                            }
                        } else {
                            mCurrentPageId = findCurrentPageIdBySelectedPosition(scannedImageListItems, imageItem.getPagePosition() - 2); // устанавливаем предыдущую страницу, pagePosition считается от 1
                            displayImageList(scannedImageListItems, true);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mView != null) {
                            mView.showImageDeletingError();
                        }
                    }
                })
        );
    }

    @Override
    public void onNextSnapshotButtonClick() {
        final Runnable completeAction = new Runnable() {
            @Override
            public void run() {
                if (mView != null) {
                    mView.switchToScannerScreen();
                }
            }
        };
        rotateImagesIfNeed(completeAction, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                completeAction.run();
            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPageId = findCurrentPageIdBySelectedPosition(mImageList, position);
        displayCurrentPage(false);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void switchToUploadImageScreen() {
        if (mView != null) {
            mReloadPageOnViewStarted = true;
            mView.switchToImageListScreen();
        }
    }

    private void loadImageList() {
        if (mImageListDisposable.isDisposed()) {
            mImageListDisposable = new SerialDisposable();
        }
        mImageListDisposable.set(
            mImageListInteractor.getScannedImages()
                .subscribe(new Consumer<List<ScannedImageListItem>>() {
                    @Override
                    public void accept(List<ScannedImageListItem> scannedImageListItems) throws Exception {
                        displayImageList(scannedImageListItems, false);
                    }
                }, new FallbackErrorConsumer() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        super.accept(throwable);
                        if (mView != null) {
                            mView.showImageLoadingError();
                        }
                    }
                })
        );
    }

    private void rotateImagesIfNeed(@NonNull Runnable completeAction, @Nullable Consumer<Throwable> errorConsumer) {
        final List<Pair<Integer, ScannerRotateAngle>> rotateList = new ArrayList<>();
        if (mImageList != null) {
            for (ScannedImageListItem imageListItem : mImageList) {
                ScannerRotateAngle rotateAngle = imageListItem.getRotation().toAngleRotate();
                if (rotateAngle == ScannerRotateAngle.NOT_ROTATE) {
                    continue;
                }
                rotateList.add(new Pair<>(imageListItem.getPageInfo().getId(), rotateAngle));
            }
        }
        if (rotateList.isEmpty()) {
            completeAction.run();
            return;
        }
        mActionDisposables.add(
            preparePageAction(mEditImageInteractor.rotateImages(rotateList), true)
                .subscribe(new Consumer<List<ScannerPageInfo>>() {
                    @Override
                    public void accept(List<ScannerPageInfo> scannerPageInfos) throws Exception {
                        if (!CommonUtils.isEmpty(mImageList)) {
                            for (ScannerPageInfo pageInfo : scannerPageInfos) {
                                final ScannedImageListItem imageListItem = findImageItemById(mImageList, pageInfo.getId());
                                if (imageListItem != null) {
                                    imageListItem.setPageInfo(pageInfo);
                                }
                            }
                            displayImageList(mImageList, false);
                        }
                        completeAction.run();
                    }
                }, new FallbackErrorConsumer() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                        super.accept(throwable);
                        if (errorConsumer != null) {
                            errorConsumer.accept(throwable);
                        }
                    }
                })
        );
    }

    private void rotateCurrentImageIfNeed(@NonNull Runnable completeAction, @Nullable Consumer<Throwable> errorConsumer) {
        final ScannedImageListItem imageItem = getCurrentImageItem();
        if (imageItem == null) {
            completeAction.run();
            return;
        }
        final ScannerRotateAngle angleRotate = imageItem.getRotation().toAngleRotate();
        if (angleRotate == ScannerRotateAngle.NOT_ROTATE) {
            completeAction.run();
            return;
        }
        mActionDisposables.add(
            preparePageAction(mEditImageInteractor.rotateImage(imageItem.getPageInfo().getId(), angleRotate), true)
                .subscribe(new Consumer<ScannerPageInfo>() {
                    @Override
                    public void accept(ScannerPageInfo scannerPageInfo) throws Exception {
                        setScannerPageInfo(scannerPageInfo);
                        completeAction.run();
                    }
                }, new FallbackErrorConsumer() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        super.accept(throwable);
                        if (errorConsumer != null) {
                            errorConsumer.accept(throwable);
                        }
                    }
                })
        );
    }

    @NonNull
    private <T> Observable<T> preparePageAction(@NonNull Observable<T> upstream, boolean showProgress) {
        if (mView != null) {
            mView.setEnabledControls(false);
            if (showProgress) {
                mView.showWaitProgress();
            }
        }
        return upstream.doAfterTerminate(new Action() {
            @Override
            public void run() throws Exception {
                if (mView != null) {
                    mView.setEnabledControls(true);
                    if (showProgress) {
                        mView.hideWaitProgress();
                    }
                }
            }
        });
    }

    private void displayImageList(@NonNull List<ScannedImageListItem> imageList, boolean smooth) {
        mImageList = imageList;
        if (mView != null) {
            mView.displayImageList(mImageList);
        }
        displayCurrentPage(smooth);
    }

    private void displayCurrentPage(boolean smooth) {
        final ScannedImageListItem imageItem = getCurrentImageItem();
        if (imageItem != null) {
            if (mView != null) {
                mView.displayCurrentPage(imageItem.getPagePosition(), smooth);
            }
        } else if (!CommonUtils.isEmpty(mImageList)) {
            final ScannedImageListItem lastItem = mImageList.get(mImageList.size() - 1);
            mCurrentPageId = lastItem.getPageInfo().getId();
            if (mView != null) {
                mView.displayCurrentPage(lastItem.getPagePosition(), smooth);
            }
        } else {
            mCurrentPageId = null;
        }
    }

    @Nullable
    private ScannedImageListItem getCurrentImageItem() {
        if (mImageList != null && mCurrentPageId != null) {
            return findImageItemById(mImageList, mCurrentPageId);
        }
        return null;
    }

    @Nullable
    private static Integer findCurrentPageIdBySelectedPosition(@Nullable List<ScannedImageListItem> imageList, int position) {
        if (imageList != null && position > -1 && position < imageList.size()) {
            return imageList.get(position).getPageInfo().getId();
        }
        return null;
    }

    @Nullable
    private static ScannedImageListItem findImageItemById(@NonNull List<ScannedImageListItem> imageList, int pageId) {
        for (ScannedImageListItem imageListItem : imageList) {
            if (imageListItem.getPageInfo().getId() == pageId) {
                return imageListItem;
            }
        }
        return null;
    }
}
