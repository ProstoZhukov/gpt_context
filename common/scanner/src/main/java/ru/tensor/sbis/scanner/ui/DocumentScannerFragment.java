package ru.tensor.sbis.scanner.ui;

import static ru.tensor.sbis.scanner.files_picker_tab.ScannerTabKt.FILES_PICKER_SCANNER_REQUEST_CODE;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.camera.BaseCameraFragment;
import ru.tensor.sbis.camera.service.FlashState;
import ru.tensor.sbis.design.view_ext.SbisProgressBar;
import ru.tensor.sbis.design.view_ext.UiUtils;
import ru.tensor.sbis.edo_decl.scanner.ScannerResult;
import ru.tensor.sbis.modalwindows.dialogalert.PopupConfirmation;
import ru.tensor.sbis.scanner.BuildConfig;
import ru.tensor.sbis.scanner.R;
import ru.tensor.sbis.scanner.data.model.CornerPoint;
import ru.tensor.sbis.scanner.di.ScannerSingletonComponent;
import ru.tensor.sbis.scanner.di.scanner.DaggerDocumentScannerComponent;
import ru.tensor.sbis.scanner.di.scanner.DocumentScannerComponent;
import ru.tensor.sbis.scanner.generated.ScannerPageInfo;
import ru.tensor.sbis.scanner.ui.viewimage.ViewImageActivity;
import ru.tensor.sbis.scanner.util.PermissionDialogUtil;
import ru.tensor.sbis.scanner.view.FlashSwitchView;
import ru.tensor.sbis.scanner.view.documentfinder.DocumentFinderView;

/**
 * @author am.boldinov
 */
public class DocumentScannerFragment extends BaseCameraFragment<DocumentScannerContract.View, DocumentScannerContract.Presenter>
        implements DocumentScannerContract.View, PopupConfirmation.DialogYesNoWithTextListener, PopupConfirmation.DialogCancelListener {

    private static final int VIEW_IMAGE_ACTIVITY_REQUEST_CODE = 1000;
    private static final int APPLICATION_SETTINGS_ACTIVITY_REQUEST_CODE = 2000;

    private DocumentScannerComponent mComponent;

    private DocumentFinderView mFinderView;
    private FlashSwitchView mFlashSwitchView;
    private SbisProgressBar mProgressBar;
    private TextView mPageTextView;
    @Nullable
    private SimpleDraweeView mLastSnapshotView;
    private ViewGroup mSnapshotContainer;

    @NonNull
    public static DocumentScannerFragment newInstance() {
        return new DocumentScannerFragment();
    }

    @Override
    protected void initViews(@NonNull View root) {
        super.initViews(root);
        mPageTextView = root.findViewById(R.id.next_page);
        mFinderView = root.findViewById(R.id.finder_view);
        mFlashSwitchView = root.findViewById(R.id.flash_switch);
        mFlashSwitchView.setOnClickListener(v -> getPresenter().onSwitchFlashClick());
        mSnapshotContainer = root.findViewById(R.id.snapshot_container);
        mProgressBar = root.findViewById(R.id.snapshot_progress);
        root.findViewById(R.id.close_button).setOnClickListener(
                v -> {
                    pushEmptyScannerResult();
                    getActivity().onBackPressed();
                }
        );
    }

    @NonNull
    @Override
    protected DocumentScannerContract.View getPresenterView() {
        return this;
    }

    @NonNull
    @Override
    protected DocumentScannerContract.Presenter createPresenter() {
        return getDocumentScannerComponent().getPresenter();
    }

    @Override
    protected void inject() {
    }

    @Override
    public void displayStartingPreviewError() {
        //showToast(R.string.scanner_open_camera_error);
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_document_scanner;
    }

    @NonNull
    @Override
    protected TextureView createPreviewView(@NonNull View root) {
        return (TextureView) root.findViewById(R.id.camera_texture);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity().isFinishing()) {
            getPresenter().onScannerFinishing();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VIEW_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            requireActivity().setResult(Activity.RESULT_OK, data);
            finish();
        } else if (requestCode == APPLICATION_SETTINGS_ACTIVITY_REQUEST_CODE) {
            getPresenter().onAppSettingsClosed();
        }
    }

    public DocumentScannerComponent getDocumentScannerComponent() {
        if (mComponent == null) {
            mComponent = DaggerDocumentScannerComponent.builder()
                    .requestCode(getRequestCode())
                    .scannerSingletonComponent(ScannerSingletonComponent.fromContext(getContext()))
                    .build();
        }
        return mComponent;
    }

    @Override
    public void displayRectangle(@NonNull List<CornerPoint> coordinates) {
        mFinderView.setCoordinates(coordinates);
    }

    @Override
    public void setFlashViewVisibility(boolean visibility) {
        mFlashSwitchView.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    @Override
    public void switchFlashView(@NonNull FlashState flashState) {
        switch (flashState) {
            case AUTO:
                mFlashSwitchView.switchToAuto();
                break;
            case ON:
                mFlashSwitchView.switchToOn();
                break;
            case OFF:
                mFlashSwitchView.switchToOff();
                break;
        }
    }

    @Override
    public void switchToScannerResultScreen(@NonNull ScannerPageInfo scannerPageInfo) {
        Intent intent = ViewImageActivity.getActivityIntent(requireActivity(), scannerPageInfo, getRequestCode());
        startActivityForResult(intent, VIEW_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void showTakeSnapshotError() {
        showToast(R.string.scanner_take_snapshot_error);
    }

    @Override
    public void setEnabledControls(boolean enabled) {
        UiUtils.setEnabledControls(mSnapshotContainer, enabled);
        mFlashSwitchView.setEnabled(enabled);
    }

    @Override
    public void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void displayNextPage(int page) {
        if (page > 1) {
            mPageTextView.setText(getString(R.string.scanner_next_page, page));
        } else {
            mPageTextView.setText(null);
        }
    }

    @Override
    public void displayLastSnapshot(@NonNull String imageUri) {
        if (mLastSnapshotView == null) {
            if (BuildConfig.DEBUG) {
                throw new IllegalStateException("Unknown snapshot layout state, LastSnapshotView not found");
            }
            return;
        }
        mLastSnapshotView.setImageURI(imageUri);
        mLastSnapshotView.setOnClickListener(v -> getPresenter().onLastSnapshotPreviewClick());
        mLastSnapshotView.setVisibility(View.VISIBLE);
    }

    @Override
    public void inflateSingleSnapshotLayout() {
        mSnapshotContainer.removeAllViewsInLayout();
        LayoutInflater.from(getContext()).inflate(R.layout.scanner_single_snapshot_layout, mSnapshotContainer);
        mSnapshotContainer.findViewById(R.id.snapshot_button)
                .setOnClickListener(v -> getPresenter().onSnapshotClick());
        mLastSnapshotView = mSnapshotContainer.findViewById(R.id.last_snapshot);
        UiUtils.setEnabledControls(mSnapshotContainer, mSnapshotContainer.isEnabled());
    }

    @Override
    public void inflateMultiSnapshotLayout() {
        mSnapshotContainer.removeAllViewsInLayout();
        mLastSnapshotView = null;
        LayoutInflater.from(getContext()).inflate(R.layout.scanner_multi_snapshot_layout, mSnapshotContainer);
        mSnapshotContainer.findViewById(R.id.snapshot_button)
                .setOnClickListener(v -> getPresenter().onSnapshotClick());
        mSnapshotContainer.findViewById(R.id.fast_snapshot_button)
                .setOnClickListener(v -> getPresenter().onFastSnapshotClick());
        UiUtils.setEnabledControls(mSnapshotContainer, mSnapshotContainer.isEnabled());
    }

    @Override
    public void removeSnapshotLayout() {
        if (mSnapshotContainer.getChildCount() > 0) {
            mSnapshotContainer.removeAllViews();
            mLastSnapshotView = null;
        }
    }

    @Override
    public void dispatchResult(@NonNull List<String> uriList) {
        Intent data = new Intent();
        data.putStringArrayListExtra(DocumentScannerActivity.EXTRA_SCANNED_IMAGE_LIST_RESULT, new ArrayList<>(uriList));
        requireActivity().setResult(Activity.RESULT_OK, data);
    }

    @Override
    public void displayPermissionsDialog() {
        PermissionDialogUtil.showPermissionDialog(requireContext(), getChildFragmentManager());
    }

    @Override
    public void finish() {
        requireActivity().finish();
    }

    @Override
    public boolean onBackPressed() {
        pushEmptyScannerResult();
        return getPresenter().onBackPressed();
    }

    @Override
    public void onYes(int requestCode, @Nullable String text) {
        if (PermissionDialogUtil.isPermissionDialogRequestCode(requestCode)) {
            getPresenter().onPermissionDialogSettingsClick();
        }
    }

    @Override
    public void onNo(int requestCode, @Nullable String text) {
        if (PermissionDialogUtil.isPermissionDialogRequestCode(requestCode)) {
            getPresenter().onPermissionDialogCancel();
        }
    }

    @Override
    public void onCancel(int requestCode) {
        onNo(requestCode, null);
    }

    @Override
    public void openAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", requireContext().getPackageName(), null)
        );
        startActivityForResult(intent, APPLICATION_SETTINGS_ACTIVITY_REQUEST_CODE);
    }

    @NonNull
    private String getRequestCode() {
        return getActivity().getIntent().getStringExtra(DocumentScannerContract.EXTRA_REQUEST_CODE_KEY);
    }

    private void pushEmptyScannerResult() {
        ScannerSingletonComponent.fromContext(this.getContext())
                .getScannerEventManager()
                .dispatchScannerResult(
                        new ScannerResult(FILES_PICKER_SCANNER_REQUEST_CODE, new ArrayList<>())
                );
    }
}
