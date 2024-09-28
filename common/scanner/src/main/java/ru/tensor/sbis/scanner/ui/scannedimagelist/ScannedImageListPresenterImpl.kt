package ru.tensor.sbis.scanner.ui.scannedimagelist

import android.os.Build
import io.reactivex.disposables.CompositeDisposable
import ru.tensor.sbis.base_components.adapter.checkable.ObservableCheckCountHelper
import ru.tensor.sbis.common.exceptions.LoadDataException
import ru.tensor.sbis.common.rx.consumer.LoadingErrorConsumer
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.mvp.presenter.loadcontent.AbstractLoadContentPresenter
import ru.tensor.sbis.mvp_extensions.view_state.EmptyViewState
import ru.tensor.sbis.scanner.adapter.scannedimagelist.item.ScannedImageListItem
import ru.tensor.sbis.scanner.data.ScannerResultSupplier
import ru.tensor.sbis.scanner.data.interactor.scannedimagelist.ScannedImageListInteractor
import ru.tensor.sbis.scanner.ui.scannedimagelist.options.ScannedImageOption
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import ru.tensor.sbis.common.util.illegalState


/**
 * @author sa.nikitin
 */
internal class ScannedImageListPresenterImpl(//region fields
    private val mInteractor: ScannedImageListInteractor,
    private val mCheckHelper: ObservableCheckCountHelper<ScannedImageListItem>,
    private val mNetworkUtils: NetworkUtils,
    private val mResultSupplier: ScannerResultSupplier
) : AbstractLoadContentPresenter<ScannedImageListContract.View>(), ScannedImageListContract.Presenter {
    private var mScannedImageList: List<ScannedImageListItem>? = null
    private var disposables = CompositeDisposable()

    //endregion
    //region instantiation
    init {
        disposables.add(mCheckHelper.checkedCountObservable.subscribe(::updateSaveImagesMenuItemEnabled))
    }

    //endregion
    //region BasePresenter impl
    override fun attachView(view: ScannedImageListContract.View) {
        super.attachView(view)
        view.attachCheckHelperToAdapter(mCheckHelper)
        if (disposables.isDisposed) {
            view.hideProgressDialog()
        }
        if (mScannedImageList == null) {
            view.updateEmptyViewState(EmptyViewState.EMPTY)
            if (loadingStatus == LoadingStatus.NOT_LOADED) {
                startLoading(force = true, showProcess = true)
            } else {
                updateSaveImagesMenuItemEnabled()
            }
        } else {
            view.displayScannedImagesList(mScannedImageList!!)
            view.updateEmptyViewState(EmptyViewState.DEFAULT)
            updateSaveImagesMenuItemEnabled()
        }
    }

    override fun detachView() {
        super.detachView()
        mCheckHelper.detachFromAdapter()
    }

    override fun onDestroy() {
        disposables.dispose()
    }

    //endregion
    //region common
    private fun performViewAction(action: (ScannedImageListContract.View) -> Unit) {
        if (mView != null) {
            action.invoke(mView!!)
        }
    }

    private fun updateSaveImagesMenuItemEnabled(checkedCount: Int = mCheckHelper.checkedCount) {
        performViewAction { view ->
            view.changeSaveScannedImagesMenuItemEnabled(loadingStatus != LoadingStatus.IN_PROGRESS && checkedCount != 0)
        }
    }

    //endregion
    //region AbstractLoadContentPresenter impl
    override fun startLoading(force: Boolean, showProcess: Boolean) {
        super.startLoading(force, showProcess)
        updateSaveImagesMenuItemEnabled()
    }

    override fun loadContent(force: Boolean) {
        disposables.add(
            mInteractor.scannedImages.subscribe(
                { scannedImageListItems: List<ScannedImageListItem> ->
                    loadScannedImagesListSuccess(
                        scannedImageListItems
                    )
                },
                object : LoadingErrorConsumer() {
                    override fun onLoadException(exception: LoadDataException) {
                        onLoadingError(exception)
                    }
                })
        )
    }

    private fun loadScannedImagesListSuccess(scannedImageListItems: List<ScannedImageListItem>) {
        performViewAction { view ->
            view.displayScannedImagesList(scannedImageListItems)
            view.updateEmptyViewState(EmptyViewState.DEFAULT)
        }
        checkAllItemsAfterFirstLoad()
        mScannedImageList = scannedImageListItems
        onLoadingCompleted()
        updateSaveImagesMenuItemEnabled()
    }

    private fun checkAllItemsAfterFirstLoad() {
        if (mScannedImageList == null) {
            mCheckHelper.checkAll()
        }
    }

    override fun showLoadingError(
        view: ScannedImageListContract.View,
        exception: LoadDataException?
    ) {
        view.updateEmptyViewState(EmptyViewState.ERROR)
        if (mScannedImageList != null) {
            view.showLoadScannedImagesError()
        }
    }

    //endregion
    //region ScannedImageListPresenter impl
    override fun onSaveScannedImagesMenuItemClick() {
        if (!mNetworkUtils.isConnected) {
            performViewAction { view -> view.showOfflineWarning() }
        }
        performViewAction { view ->
            view.showSavingMethodChoiceMenu(createSavingMethodChoiceDialogItems())
        }
    }

    private fun createSavingMethodChoiceDialogItems(): List<ScannedImageOption> {
        val mediaOptions: MutableList<ScannedImageOption> = ArrayList(3)
        if (mNetworkUtils.isConnected) {
            mediaOptions.add(ScannedImageOption.SAVE_AS_IMAGE)
            mediaOptions.add(ScannedImageOption.SAVE_TO_PDF)
        }
        mediaOptions.add(ScannedImageOption.SAVE_TO_GALLERY)
        return mediaOptions
    }

    override fun onSavingMethodSelected(method: ScannedImageOption) {
        if (method == ScannedImageOption.SAVE_TO_GALLERY) {
            val permission =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    READ_MEDIA_IMAGES
                } else {
                    WRITE_EXTERNAL_STORAGE
                }
            performViewAction { view: ScannedImageListContract.View ->
                view.requestPermissions(
                    ScannedImageListContract.WRITE_GALLERY_PERMISSION_REQUEST_CODE,
                    permission
                )
            }
        } else if (mNetworkUtils.isConnected) {
            when (method) {
                ScannedImageOption.SAVE_AS_IMAGE -> finishWithScannedImages()
                ScannedImageOption.SAVE_TO_PDF -> showDialogInputNewPDFFileName()
                else -> illegalState { "Unexpected option" }
            }
        } else {
            performViewAction { view -> view.showSaveFilesOfflineError() }
        }
    }

    private fun finishWithScannedImages() {
        val checkedList = mCheckHelper.checked
        val uriList = mResultSupplier.getUrisFromPageInfoIndexList(
            checkedList.size
        ) { index: Int? -> checkedList[index!!].pageInfo }
        dispatchResult(uriList)
        finish()
    }

    private fun uploadCheckedImagesToGallery() {
        disposables.add(
            mInteractor.saveToGallery(mCheckHelper.checked)
                .subscribe({
                    performViewAction { view -> view.showSavingToGalleryMessage(true) }
                }) {
                    performViewAction { view -> view.showSavingToGalleryMessage(false) }
                }
        )
    }

    private fun showDialogInputNewPDFFileName() {
        performViewAction { view -> view.showDialogInputNewPDFFileName() }
    }

    override fun onNewPDFFileNameEntered(newPDFFileName: String) {
        saveToPdf(newPDFFileName)
    }

    override fun onImageEdited() {
        startLoading(force = true, showProcess = false)
    }

    private fun saveToPdf(newPDFFileName: String) {
        performViewAction { view -> view.showProgressDialog() }
        disposables.add(
            mInteractor.saveToPdf(mCheckHelper.checked, newPDFFileName)
                .doAfterTerminate { performViewAction { view -> view.hideProgressDialog() } }
                .subscribe({ pdfPath: String -> onSavingToPdfSuccess(pdfPath) }) { throwable: Throwable ->
                    onSavingToPdfError(throwable)
                }
        )
    }

    private fun onSavingToPdfSuccess(pdfPath: String) {
        val uriList = mResultSupplier.getUrisFromPath(pdfPath)
        dispatchResult(uriList)
        finish()
    }

    private fun onSavingToPdfError(throwable: Throwable) {
        performViewAction { view -> view.showSavingToPdfErrorMessage(throwable.message) }
    }

    override fun onRequestPermissionsResult(requestCode: Int, granted: Boolean) {
        if (requestCode == ScannedImageListContract.WRITE_GALLERY_PERMISSION_REQUEST_CODE && granted) {
            uploadCheckedImagesToGallery()
        }
    }

    private fun finish() {
        performViewAction { view -> view.finish() }
    }

    private fun dispatchResult(uriList: List<String>) {
        mResultSupplier.dispatchResult(uriList)
        performViewAction { view -> view.dispatchResult(uriList) }
    }

    //endregion
    //region ScannedImageListItemClickHandler impl
    override fun onItemClick(scannedImageListItem: ScannedImageListItem) {
        performViewAction { view -> view.switchToEditScannedImageScreen(scannedImageListItem.pageInfo) }
    }

    override fun onCheckboxClick(scannedImageListItem: ScannedImageListItem) {
        mCheckHelper.setChecked(scannedImageListItem, !mCheckHelper.isChecked(scannedImageListItem))
    }

    //endregion
}