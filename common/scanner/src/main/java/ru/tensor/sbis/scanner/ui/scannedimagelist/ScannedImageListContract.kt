package ru.tensor.sbis.scanner.ui.scannedimagelist

import ru.tensor.sbis.base_components.adapter.checkable.CheckHelper
import ru.tensor.sbis.base_components.fragment.dialog.CommonDialogFragment.OnInputEndListener
import ru.tensor.sbis.mvp.presenter.BasePresenter
import ru.tensor.sbis.mvp.presenter.loadcontent.LoadContentView
import ru.tensor.sbis.mvp_extensions.view_state.EmptyViewBehaviour
import ru.tensor.sbis.scanner.adapter.scannedimagelist.clickhandler.ScannedImageListItemClickHandler
import ru.tensor.sbis.scanner.adapter.scannedimagelist.item.ScannedImageListItem
import ru.tensor.sbis.scanner.generated.ScannerPageInfo
import ru.tensor.sbis.scanner.ui.scannedimagelist.options.ScannedImageOption

/**
 * @author sa.nikitin
 */
interface ScannedImageListContract {
    interface View : LoadContentView, EmptyViewBehaviour, OnInputEndListener {

        fun attachCheckHelperToAdapter(checkHelper: CheckHelper<ScannedImageListItem>)

        fun changeSaveScannedImagesMenuItemEnabled(enabled: Boolean)

        fun displayScannedImagesList(scannedImageList: List<ScannedImageListItem>)

        fun showLoadScannedImagesError()

        fun switchToEditScannedImageScreen(scannerPageInfo: ScannerPageInfo)

        fun showSavingMethodChoiceMenu(options: List<ScannedImageOption>)

        fun showProgressDialog()

        fun hideProgressDialog()

        fun showDialogInputNewPDFFileName()

        fun showSavingToPdfErrorMessage(errorMessage: String?)

        fun showSavingToGalleryMessage(success: Boolean)

        fun showSaveFilesOfflineError()

        fun showOfflineWarning()

        fun requestPermissions(requestCode: Int, vararg permissions: String)

        fun dispatchResult(uriList: List<String>)

        fun finish()
    }

    interface Presenter : BasePresenter<View>, ScannedImageListItemClickHandler {
        fun onSaveScannedImagesMenuItemClick()

        fun onSavingMethodSelected(method: ScannedImageOption)

        fun onNewPDFFileNameEntered(newPDFFileName: String)

        fun onImageEdited()

        fun onRequestPermissionsResult(requestCode: Int, granted: Boolean)
    }

    companion object {
        const val WRITE_GALLERY_PERMISSION_REQUEST_CODE = 10000
    }
}