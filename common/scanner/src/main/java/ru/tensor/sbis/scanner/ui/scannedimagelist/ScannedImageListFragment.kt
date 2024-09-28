package ru.tensor.sbis.scanner.ui.scannedimagelist

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import ru.tensor.sbis.base_components.adapter.checkable.CheckHelper
import ru.tensor.sbis.base_components.fragment.dialog.CommonDialogFragment
import ru.tensor.sbis.base_components.util.DialogUtil
import ru.tensor.sbis.common.util.PermissionUtil
import ru.tensor.sbis.design.container.DimType
import ru.tensor.sbis.design.container.locator.AnchorHorizontalLocator
import ru.tensor.sbis.design.container.locator.AnchorVerticalLocator
import ru.tensor.sbis.design.container.locator.HorizontalAlignment
import ru.tensor.sbis.design.container.locator.VerticalAlignment
import ru.tensor.sbis.design.design_menu.SbisMenu
import ru.tensor.sbis.design.design_menu.SbisMenuItem
import ru.tensor.sbis.design.design_menu.showMenu
import ru.tensor.sbis.design.utils.getThemeDimension
import ru.tensor.sbis.mvp.presenter.BasePresenterFragment
import ru.tensor.sbis.mvp_extensions.view_state.EmptyViewState
import ru.tensor.sbis.scanner.R
import ru.tensor.sbis.scanner.adapter.scannedimagelist.ScannedImageListAdapter
import ru.tensor.sbis.scanner.adapter.scannedimagelist.item.ScannedImageListItem
import ru.tensor.sbis.scanner.databinding.FragmentScannedImageListBinding
import ru.tensor.sbis.scanner.di.ScannerSingletonComponent
import ru.tensor.sbis.scanner.di.scannedimagelist.DaggerScannedImageListComponent
import ru.tensor.sbis.scanner.di.scannedimagelist.ScannedImageListComponent
import ru.tensor.sbis.scanner.generated.ScannerPageInfo
import ru.tensor.sbis.scanner.ui.DocumentScannerActivity
import ru.tensor.sbis.scanner.ui.DocumentScannerContract
import ru.tensor.sbis.scanner.ui.editimage.EditImageActivity
import ru.tensor.sbis.scanner.ui.scannedimagelist.options.ScannedImageOption
import ru.tensor.sbis.scanner.util.ScannerUiUtil
import ru.tensor.sbis.scanner.view.decoration.GridSpacingItemDecoration

/**
 * @author sa.nikitin
 */
internal class ScannedImageListFragment :
    BasePresenterFragment<ScannedImageListContract.View, ScannedImageListContract.Presenter>(),
    ScannedImageListContract.View {
    private var mComponent: ScannedImageListComponent? = null
    private var mViewHolder: FragmentScannedImageListBinding? = null
    private var mCurrentEmptyViewState: EmptyViewState? = null
    private var mAdapter: ScannedImageListAdapter? = null

    companion object {
        private val PDF_NAME_DIALOG_TAG = CommonDialogFragment.COMMON_TAG + R.id.input_new_pdf_file_name
        private const val EDIT_IMAGE_REQUEST_CODE = 1001
        private const val MAX_PDF_NAME_LENGTH = 120

        //region instantiation
        fun newInstance(): ScannedImageListFragment {
            return ScannedImageListFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAdapter = ScannedImageListAdapter(presenter)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        FragmentScannedImageListBinding.inflate(inflater, container, false).run {
            mViewHolder = this
            toolbar.leftPanel.setOnClickListener { requireActivity().onBackPressed() }
            toolbar.rightText.setOnClickListener { presenter?.onSaveScannedImagesMenuItemClick() }
            toolbar.rightText.setPadding(
                toolbar.rightText.paddingLeft,
                toolbar.rightText.paddingTop,
                resources.getDimensionPixelSize(ru.tensor.sbis.design.toolbar.R.dimen.toolbar_right_buttons_inner_padding),
                toolbar.rightText.paddingBottom
            )
            listRefresher.isEnabled = false
            val imagesColumnNumber = resources.getInteger(R.integer.scanned_images_column_number)
            val gridLayoutManager = GridLayoutManager(context, imagesColumnNumber)
            list.layoutManager = gridLayoutManager
            list.addItemDecoration(
                GridSpacingItemDecoration(
                    imagesColumnNumber,
                    resources.getDimensionPixelSize(R.dimen.scanned_images_list_spacing)
                )
            )
            list.setEmptyView(emptyView)
            list.adapter = mAdapter
            root
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewHolder?.presenter = presenter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            presenter.onImageEdited()
        }
    }

    override fun onDestroyView() {
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(null)
        }
        super.onDestroyView()
    }

    //region BasePresenterFragment impl
    override fun getPresenterView(): ScannedImageListContract.View = this

    override fun createPresenter(): ScannedImageListContract.Presenter =
        scannedImageListComponent.scannedImageListPresenter


    override fun inject() {}
    private val scannedImageListComponent: ScannedImageListComponent
        get() {
            if (mComponent == null) {
                mComponent =
                    DaggerScannedImageListComponent.builder()
                        .requestCode(
                            requireActivity().intent.getStringExtra(DocumentScannerContract.EXTRA_REQUEST_CODE_KEY)!!
                        )
                        .scannerSingletonComponent(ScannerSingletonComponent.fromContext(requireContext()))
                        .build()
            }
            return mComponent!!
        }

    //endregion
    //region ScannedImageListView impl
    override fun attachCheckHelperToAdapter(checkHelper: CheckHelper<ScannedImageListItem>) {
        checkHelper.attachToAdapter(mAdapter!!)
    }

    override fun showLoadingProcess(show: Boolean) {
        mViewHolder?.listRefresher?.isRefreshing = show
    }

    override fun changeSaveScannedImagesMenuItemEnabled(enabled: Boolean) {
        ScannerUiUtil.setEnabledView(mViewHolder?.toolbar?.rightText, enabled)
    }

    override fun displayScannedImagesList(scannedImageList: List<ScannedImageListItem>) {
        mAdapter?.setContent(scannedImageList)
    }

    override fun showLoadScannedImagesError() {
        ScannerUiUtil.showErrorMessage(requireContext(), R.string.scanner_load_scanned_images_error)
    }

    override fun switchToEditScannedImageScreen(scannerPageInfo: ScannerPageInfo) {
        val intent = EditImageActivity.getActivityIntent(requireActivity(), scannerPageInfo)
        startActivityForResult(intent, EDIT_IMAGE_REQUEST_CODE)
    }

    override fun showSavingMethodChoiceMenu(options: List<ScannedImageOption>) {
        val anchor = mViewHolder?.toolbar
        val sbisMenu = SbisMenu(children = options.map { getOptionSavingMethodMenuItem(it) })
        anchor?.let {
            sbisMenu.showMenu(
                fragmentManager = childFragmentManager,
                verticalLocator = AnchorVerticalLocator(
                    alignment = VerticalAlignment.TOP,
                    force = false,
                    offsetRes = requireContext().getThemeDimension(ru.tensor.sbis.design.R.attr.offset_l)
                ).apply { anchorView = it },
                horizontalLocator = AnchorHorizontalLocator(
                    alignment = HorizontalAlignment.RIGHT,
                    force = false,
                    innerPosition = false
                ).apply { anchorView = it },
                dimType = DimType.SOLID,
                customWidth = R.dimen.scanned_images_saving_method_menu_width,
            )
        }
    }

    private fun getOptionSavingMethodMenuItem(option: ScannedImageOption) =
        SbisMenuItem(title = requireContext().getString(option.nameResId)) {
            presenter.onSavingMethodSelected(option)
        }

    override fun showProgressDialog() {
        DialogUtil.showProgressDialog(requireContext(), childFragmentManager)
    }

    override fun hideProgressDialog() {
        DialogUtil.hideProgressDialog(childFragmentManager)
    }

    override fun showDialogInputNewPDFFileName() {
        CommonDialogFragment.Builder(context, R.id.input_new_pdf_file_name)
            .title(R.string.scanner_input_new_pdf_file_name_dialog_title)
            .positiveButtonText(ru.tensor.sbis.common.R.string.dialog_button_ok)
            .negativeButtonText(ru.tensor.sbis.common.R.string.dialog_button_cancel)
            .inputField(null, true, MAX_PDF_NAME_LENGTH)
            .show(childFragmentManager, PDF_NAME_DIALOG_TAG)
    }

    override fun showSavingToPdfErrorMessage(errorMessage: String?) =
        ScannerUiUtil.showErrorMessage(
            requireContext(), errorMessage ?: getString(R.string.scanner_saving_to_pdf_error)
        )

    override fun showSavingToGalleryMessage(success: Boolean) {
        if (success) {
            ScannerUiUtil.showSuccessMessage(
                requireContext(),
                R.string.scanner_saving_to_gallery_success
            )
        } else {
            ScannerUiUtil.showErrorMessage(
                requireContext(),
                getString(R.string.scanner_saving_to_gallery_error)
            )
        }
    }

    override fun showSaveFilesOfflineError() {
        ScannerUiUtil.showNetworkErrorMessage(requireContext(), R.string.scanner_save_scanned_docs_offline_error)
    }

    override fun showOfflineWarning() {
        ScannerUiUtil.showNetworkErrorMessage(requireContext(), R.string.scanner_no_internet_connection_message)
    }

    override fun requestPermissions(requestCode: Int, vararg permissions: String) {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissions, requestCode)
                return
            }
        }
        presenter.onRequestPermissionsResult(requestCode, true)
    }

    override fun dispatchResult(uriList: List<String>) {
        val intent = Intent()
        intent.putStringArrayListExtra(DocumentScannerActivity.EXTRA_SCANNED_IMAGE_LIST_RESULT, ArrayList(uriList))
        requireActivity().setResult(Activity.RESULT_OK, intent)
    }

    override fun finish() {
        requireActivity().finish()
    }

    //endregion
    //region onRequestPermissionsResult
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        presenter.onRequestPermissionsResult(requestCode, PermissionUtil.verifyPermissions(grantResults))
    }

    //endregion
    //region EmptyViewBehaviour impl
    override fun updateEmptyViewState(emptyViewState: EmptyViewState) {
        if (mCurrentEmptyViewState != emptyViewState) {
            mCurrentEmptyViewState = emptyViewState
            when (emptyViewState) {
                EmptyViewState.EMPTY -> changeVisibilityEmptyView(false)
                EmptyViewState.DEFAULT -> {
                    changeEmptyViewMessage(R.string.scanner_empty_scanned_image_list)
                    changeVisibilityEmptyView(true)
                }
                EmptyViewState.ERROR -> {
                    changeEmptyViewMessage(R.string.scanner_load_scanned_images_error)
                    changeVisibilityEmptyView(true)
                }
                else -> Unit
            }
            mViewHolder?.list?.updateEmptyView()
        }
    }

    private fun changeEmptyViewMessage(@StringRes messageResId: Int) {
        mViewHolder?.emptyView?.fillData(null, getString(messageResId), null)
    }

    private fun changeVisibilityEmptyView(visible: Boolean) {
        mViewHolder?.emptyView?.visibility = if (visible) View.VISIBLE else View.GONE
    }

    //endregion
    //region OnInputEndListener impl
    override fun onDialogInputEnd(dialogCode: Int, parameters: Bundle?, enteredText: String) {
        if (dialogCode == R.id.input_new_pdf_file_name) {
            presenter.onNewPDFFileNameEntered(enteredText)
        }
    } //endregion
}