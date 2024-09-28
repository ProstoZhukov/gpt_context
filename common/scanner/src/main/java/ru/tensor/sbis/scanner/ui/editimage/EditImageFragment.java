package ru.tensor.sbis.scanner.ui.editimage;

import android.app.Activity;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.tensor.sbis.mvp.presenter.BasePresenterFragment;
import ru.tensor.sbis.base_components.util.DialogUtil;
import ru.tensor.sbis.scanner.R;
import ru.tensor.sbis.scanner.data.model.CornerPoint;
import ru.tensor.sbis.scanner.databinding.FragmentEditScannedImageBinding;
import ru.tensor.sbis.scanner.di.ScannerSingletonComponent;
import ru.tensor.sbis.scanner.di.editimage.DaggerEditImageComponent;
import ru.tensor.sbis.scanner.di.editimage.EditImageComponent;
import ru.tensor.sbis.scanner.di.editimage.EditImageModule;
import ru.tensor.sbis.scanner.ui.DocumentScannerContract;
import ru.tensor.sbis.scanner.generated.ScannerPageInfo;

/**
 * @author am.boldinov
 */
public class EditImageFragment extends BasePresenterFragment<EditImageContract.View, EditImageContract.Presenter> implements EditImageContract.View {

    public static EditImageFragment newInstance() {
        return new EditImageFragment();
    }

    private EditImageComponent mComponent;

    private FragmentEditScannedImageBinding mViewHolder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.setScannerPageInfo(getActivity().getIntent().getParcelableExtra(DocumentScannerContract.EXTRA_SCANNER_PAGE_INFO_KEY));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewHolder = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_scanned_image, container, false);
        return mViewHolder.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewHolder.setPresenter(mPresenter);
    }

    @Override
    protected int getPresenterLoaderId() {
        return R.id.edit_image_presenter_loader_id;
    }

    @NonNull
    @Override
    protected EditImageContract.View getPresenterView() {
        return this;
    }

    @NonNull
    @Override
    protected EditImageContract.Presenter createPresenter() {
        return getEditImageComponent().getEditImagePresenter();
    }

    @Override
    protected void inject() {

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

    @Override
    public void displayImage(@NonNull String imageUri, @Nullable List<CornerPoint> cornerPointList, int imageWidth, int imageHeight) {
        mViewHolder.documentImage.setImage(imageUri, cornerPointList, imageWidth, imageHeight);
    }

    @Override
    public void rotateImage() {
        mViewHolder.documentImage.rotate();
    }

    @Override
    public void finishScreen() {
        getActivity().setResult(Activity.RESULT_CANCELED);
        getActivity().finish();
    }

    @Override
    public void finishScreenWithResult(@NonNull ScannerPageInfo scannerPageInfo) {
        Intent result = new Intent();
        result.putExtra(DocumentScannerContract.EXTRA_SCANNER_PAGE_INFO_KEY, scannerPageInfo);
        getActivity().setResult(Activity.RESULT_OK, result);
        getActivity().finish();
    }

    @Override
    public void showImageProcessingError() {
        showToast(R.string.scanner_image_processing_error);
    }

    @Override
    public void showWaitProgress() {
        DialogUtil.showProgressDialog(getContext(), getChildFragmentManager());
    }

    @Override
    public void hideWaitProgress() {
        DialogUtil.hideProgressDialog(getChildFragmentManager());
    }
}
