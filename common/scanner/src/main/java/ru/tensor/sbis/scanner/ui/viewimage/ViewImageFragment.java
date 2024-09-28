package ru.tensor.sbis.scanner.ui.viewimage;

import static ru.tensor.sbis.scanner.files_picker_tab.ScannerTabKt.FILES_PICKER_SCANNER_REQUEST_CODE;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import ru.tensor.sbis.base_components.adapter.universal.pager.UniversalViewPagerAdapter;
import ru.tensor.sbis.base_components.util.DialogUtil;
import ru.tensor.sbis.design.view_ext.UiUtils;
import ru.tensor.sbis.edo_decl.scanner.ScannerResult;
import ru.tensor.sbis.mvp.presenter.BasePresenterFragment;
import ru.tensor.sbis.scanner.R;
import ru.tensor.sbis.scanner.adapter.scannedimagelist.item.ScannedImageListItem;
import ru.tensor.sbis.scanner.databinding.FragmentViewScannedImageBinding;
import ru.tensor.sbis.scanner.di.ScannerSingletonComponent;
import ru.tensor.sbis.scanner.di.editimage.DaggerEditImageComponent;
import ru.tensor.sbis.scanner.di.editimage.EditImageComponent;
import ru.tensor.sbis.scanner.di.editimage.EditImageModule;
import ru.tensor.sbis.scanner.generated.ScannerPageInfo;
import ru.tensor.sbis.scanner.ui.DocumentScannerContract;
import ru.tensor.sbis.scanner.ui.editimage.EditImageActivity;
import ru.tensor.sbis.scanner.ui.scannedimagelist.ScannedImageListActivity;

/**
 * @author am.boldinov
 */
public class ViewImageFragment extends BasePresenterFragment<ViewImageContract.View, ViewImageContract.Presenter> implements ViewImageContract.View {

    private static final int EDIT_IMAGE_REQUEST_CODE = 1000;
    private static final int SCANNED_IMAGE_LIST_REQUEST_CODE = 1001;

    public static ViewImageFragment newInstance() {
        return new ViewImageFragment();
    }

    private EditImageComponent mComponent;

    private FragmentViewScannedImageBinding mViewHolder;
    private UniversalViewPagerAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new UniversalViewPagerAdapter() {
            @Override
            protected int getItemLayout() {
                return R.layout.scanned_image_list_pager_item;
            }
        };
        mPresenter.setScannerPageInfo(getActivity().getIntent().getParcelableExtra(DocumentScannerContract.EXTRA_SCANNER_PAGE_INFO_KEY));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewHolder = DataBindingUtil.inflate(inflater, R.layout.fragment_view_scanned_image, container, false);
        mViewHolder.documentPager.setAdapter(mAdapter);
        mViewHolder.documentPager.addOnPageChangeListener(mPresenter);
        return mViewHolder.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewHolder.setPresenter(mPresenter);
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.onViewStarted();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewHolder.documentPager.removeOnPageChangeListener(mPresenter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            final ScannerPageInfo scannerPageInfo = data.getParcelableExtra(DocumentScannerContract.EXTRA_SCANNER_PAGE_INFO_KEY);
            getActivity().getIntent().putExtra(DocumentScannerContract.EXTRA_SCANNER_PAGE_INFO_KEY, scannerPageInfo);
            mPresenter.setScannerPageInfo(scannerPageInfo);
        }
        if (requestCode == SCANNED_IMAGE_LIST_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            requireActivity().setResult(Activity.RESULT_OK, data);
            requireActivity().finish();
        }
    }

    @Override
    protected int getPresenterLoaderId() {
        return R.id.view_image_presenter_loader_id;
    }

    @NonNull
    @Override
    protected ViewImageContract.View getPresenterView() {
        return this;
    }

    @NonNull
    @Override
    protected ViewImageContract.Presenter createPresenter() {
        return getEditImageComponent().getViewImagePresenter();
    }

    @Override
    protected void inject() {

    }

    @Override
    public void switchToEditImageScreen(@NonNull ScannerPageInfo scannerPageInfo) {
        Intent intent = EditImageActivity.getActivityIntent(getActivity(), scannerPageInfo);
        startActivityForResult(intent, EDIT_IMAGE_REQUEST_CODE);
    }

    @Override
    public void switchToImageListScreen() {
        final String requestCode = getActivity().getIntent().getStringExtra(DocumentScannerContract.EXTRA_REQUEST_CODE_KEY);
        Intent intent = ScannedImageListActivity.getActivityIntent(getActivity(), requestCode);
        startActivityForResult(intent, SCANNED_IMAGE_LIST_REQUEST_CODE);
    }

    @Override
    public void switchToScannerScreen() {
        getActivity().finish();
    }

    @Override
    public void switchToMainScreen() {
        ScannerSingletonComponent.fromContext(getContext())
                .getScannerEventManager()
                .dispatchScannerResult(
                        new ScannerResult(FILES_PICKER_SCANNER_REQUEST_CODE, new ArrayList<>())
                );
        getActivity().finishAffinity();
    }

    @Override
    public void setEnabledControls(boolean enabled) {
        UiUtils.setEnabledControls(mViewHolder.getRoot(), enabled);
    }

    @Override
    public void showWaitProgress() {
        DialogUtil.showProgressDialog(getContext(), getChildFragmentManager());
    }

    @Override
    public void hideWaitProgress() {
        DialogUtil.hideProgressDialog(getChildFragmentManager());
    }

    @Override
    public void showImageProcessingError() {
        showToast(R.string.scanner_image_processing_error);
    }

    @Override
    public void showImageDeletingError() {
        showToast(R.string.scanner_image_deleting_error);
    }

    @Override
    public void showImageLoadingError() {
        showToast(R.string.scanner_image_loading_error);
    }

    @Override
    public void displayCurrentPage(int page, boolean smooth) {
        if (page > 0) {
            mViewHolder.currentPage.setText(getString(R.string.scanner_next_page, page));
            final int targetItem = page - 1;
            if (mViewHolder.documentPager.getCurrentItem() != targetItem) {
                mViewHolder.documentPager.setCurrentItem(targetItem, smooth);
            }
        } else {
            mViewHolder.currentPage.setText(null);
        }
    }

    @Override
    public void displayImageList(@NonNull List<ScannedImageListItem> imageList) {
        mAdapter.setContent(imageList);
    }

    private EditImageComponent getEditImageComponent() {
        if (mComponent == null) {
            mComponent = DaggerEditImageComponent.builder()
                    .scannerSingletonComponent(ScannerSingletonComponent.fromContext(getContext()))
                    .editImageModule(new EditImageModule())
                    .build();
        }
        return mComponent;
    }
}
