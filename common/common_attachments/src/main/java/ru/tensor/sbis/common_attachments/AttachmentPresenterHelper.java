package ru.tensor.sbis.common_attachments;

import android.Manifest;
import android.os.Build;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import kotlin.Unit;
import ru.tensor.sbis.common.util.FileUriUtil;
import ru.tensor.sbis.common.util.ResourceProvider;
import ru.tensor.sbis.common_attachments.permissions.AttachmentsPermissionHelper;
import ru.tensor.sbis.common_attachments.permissions.AttachmentsPermissionHelperImpl;
import ru.tensor.sbis.disk.decl.attach_helper.MediaAttachmentsParams;
import ru.tensor.sbis.disk.decl.attach_helper.MediaOption;
import ru.tensor.sbis.disk.decl.attach_helper.MediaParams;
import ru.tensor.sbis.disk.decl.params.DiskDocumentParams;
import timber.log.Timber;

public abstract class AttachmentPresenterHelper<ATTACHMENT_VIEW extends AttachmentView> {

    /**
     * Максимальное кол-во вложений.
     */
    public static final int MAX_ATTACHMENTS_COUNT = 25;

    /**
     * Перечень элементов меню, которые могут быть обработаны непосредственно в
     * {@link AttachmentPresenterHelper}
     */
    public static final Set<MediaOption> SUPPORTED_OPTIONS = Collections.unmodifiableSet(EnumSet.of(
            MediaOption.MAKE_SNAPSHOT,
            MediaOption.DOCUMENT_FROM_CLIPBOARD,
            MediaOption.PHOTO_FROM_GALERY,
            MediaOption.FILE_FROM_STORE,
            MediaOption.DOCUMENT_FROM_DISK
    ));

    //region Fields
    //region Private
    @NonNull
    private final FileUriUtil mFileUriUtil;
    @NonNull
    private final ResourceProvider mResourceProvider;
    @Nullable
    protected AttachedListener mListener;
    @NonNull
    protected final AttachmentsPermissionHelper mPermissionHelper;
    @Nullable
    protected ATTACHMENT_VIEW mView;
    //endregion

    private int maxAttachmentsCount = MAX_ATTACHMENTS_COUNT;
    //endregion

    protected AttachmentPresenterHelper(@NonNull FileUriUtil fileUriUtil,
                                        @NonNull ResourceProvider resourceProvider) {
        mFileUriUtil = fileUriUtil;
        mResourceProvider = resourceProvider;
        mPermissionHelper = new AttachmentsPermissionHelperImpl();
    }

    /*** @SelfDocumented */
    public void setView(@Nullable ATTACHMENT_VIEW view) {
        mView = view;
        if (view != null) {
            mPermissionHelper.requestPendingPermissions(view);
        }
    }

    /*** @SelfDocumented */
    public void setAttachedListener(@Nullable AttachedListener attachedListener) {
        mListener = attachedListener;
    }

    /**
     * Изменить максимальное количество добавляемых за раз вложений
     * По умолчанию {@link AttachmentPresenterHelper#MAX_ATTACHMENTS_COUNT}
     */
    public void setMaxAttachmentsCount(int maxAttachmentsCount) {
        this.maxAttachmentsCount = maxAttachmentsCount;
    }

    /*** @SelfDocumented */
    public void onBottomMenuItemClick(@NonNull MediaOption option) {
        onBottomMenuItemClick(option, new MediaAttachmentsParams());
    }

    /**
     * Обрабатывает нажатие элемента меню
     *
     * @throws IllegalArgumentException если {@code option} не содержится в
     * {@link AttachmentPresenterHelper#SUPPORTED_OPTIONS}
     */
    public void onBottomMenuItemClick(@NonNull MediaOption option,
                                      @NonNull MediaAttachmentsParams attachmentsParams) {
        if (!SUPPORTED_OPTIONS.contains(option)) {
            throw new IllegalArgumentException("Unsupported option " + option);
        }

        if (attachmentsParams.getAddedAttachments().size() - attachmentsParams.getMaxAttachFiles() == 0) {
            showMaxAttachmentsCountMessage(attachmentsParams.getMaxAttachFiles());
            return;
        }
        switch (option) {
            case MAKE_SNAPSHOT:
                onAttachePhotoClick();
                break;
            case PHOTO_FROM_GALERY:
                onAttacheFileFromGalleryClick(attachmentsParams);
                break;
            case FILE_FROM_STORE:
                onAttacheFileFromDocumentsClick(attachmentsParams);
                break;
            case DOCUMENT_FROM_DISK:
                onAttacheDocumentFromDiskClick(attachmentsParams);
                break;
            case DOCUMENT_FROM_CLIPBOARD:
                onAttachDocumentFromClipboardClick(attachmentsParams);
                break;
        }
    }

    /**
     * Разрашение получено
     *
     * @param grantedPermissions список запрошенных разрешений
     **/
    public void onPermissionsGranted(@NonNull List<String> grantedPermissions) {
        mPermissionHelper.onPermissionsGranted(grantedPermissions);
    }

    /**
     * @param filesUris - list or attached files uris
     * @deprecated Используйте {@link #onFilesAttached(List, int)} вместо этого метода для правильного учёта общего количества вложений
     * и предотвращения добавления вложений сверх максимального кол-ва.
     */
    @Deprecated
    public void onFilesAttached(@NonNull List<String> filesUris) {
        onFilesAttached(filesUris, 0);
    }

    /**
     * Прикрепление файлов с запросом на получение прав на доступ к чтению с диска
     */
    public void onFilesAttached(@NonNull List<String> filesUris, int currentAttachmentsCount) {
        if (mView == null) {
            Timber.e("Unable to attach files");
            return;
        }
        List<String> requiredPermissionsList = getReadExternalStoragePermissions();

        mPermissionHelper.withPermissions(mView, requiredPermissionsList, isGranted -> {
            if (isGranted) {
                doOnFilesAttached(filesUris, currentAttachmentsCount);
            } else if (mView != null) {
                mView.showToast(ru.tensor.sbis.common.R.string.common_no_permission_error);
            }
            return Unit.INSTANCE;
        });
    }

    /**
     * Прикрепление файлов
     */
    protected void doOnFilesAttached(@NonNull List<String> filesUris, int currentAttachmentsCount) {
        int size = getAvailableAttachmentSizeToAdd(filesUris.size(), currentAttachmentsCount);
        // Предотвращение падения при добавлении вложения к задаче (падение зафиксировано, но воспроизвести не удалось)
        if (size == 1 && filesUris.get(0) == null) {
            if (mView != null) {
                mView.showToast(R.string.common_attachments_attach_file_error);
            }
            return;
        }
        if (mListener == null) {
            return;
        }
        List<String> validUris = new ArrayList<>(size);
        for (String uri : filesUris.subList(0, size)) {
            if (uri != null) {
                validUris.add(uri);
            }
        }
        mListener.addAttachments(validUris);

        //todo: show error if not all files was added
    }

    /**
     * @param diskDocumentFiles - список аттачментов с диска
     * @deprecated Используйте {@link #onDiskFilesAttached(List, int)} вместо этого метода для правильного учёта общего количества вложений
     * и предотвращения добавления вложений сверх максимального кол-ва.
     */
    @Deprecated
    public void onDiskFilesAttached(@NonNull List<DiskDocumentParams> diskDocumentFiles) {
        onDiskFilesAttached(diskDocumentFiles, 0);
    }

    /**
     * Прикрепление вложений
     *
     * @param diskDocumentFiles - список аттачментов с диска
     * @param currentAttachmentsCount - текущее число аттачментов
     */
    public void onDiskFilesAttached(@NonNull List<DiskDocumentParams> diskDocumentFiles, int currentAttachmentsCount) {
        int size = getAvailableAttachmentSizeToAdd(diskDocumentFiles.size(), currentAttachmentsCount);
        if (mListener != null) {
            mListener.addDiskAttachments(diskDocumentFiles.subList(0, size));
        }

        //todo: show error if not all files was added
    }

    /**
     * Прикрепление фотографии
     *
     * @param photoUriString - ссылка на фотографию
     * @param currentAttachmentsCount - текущее число аттачментов
     */
    public void onPhotoTaken(@NonNull String photoUriString, int currentAttachmentsCount) {
        LogAttachProcessKt.logAttachProcess(
                this,
                "onPhotoTaken " + photoUriString + " " + mListener.getClass().getSimpleName() + " " + mListener.hashCode()
        );
        if (mListener != null && getAvailableAttachmentSizeToAdd(1, currentAttachmentsCount) > 0) {
            mListener.addAttachment(photoUriString);
        }
    }

    protected abstract List<MediaOption> getMediaOptionsItems();

    /*** @SelfDocumented */
    public void onBottomMenuClick(@Nullable List<MediaOption> optionItems,
                                  @Nullable MediaAttachmentsParams mediaAttachmentsParams) {
        if (mView == null) {
            Timber.e("Unable to show attachments dialog without view");
            return;
        }
        List<String> requiredPermissionsList = getReadExternalStoragePermissions();

        mPermissionHelper.withPermissions(mView, requiredPermissionsList, isGranted -> {
            if (!isGranted) {
                if (mView != null) {
                    mView.showToast(ru.tensor.sbis.common.R.string.common_no_permission_error);
                }
                return Unit.INSTANCE;
            }

            List<MediaOption> items = getMediaOptionsItems();

            if (optionItems != null) {
                items.addAll(optionItems);
                int documentFromDiskIndex = items.indexOf(MediaOption.DOCUMENT_FROM_DISK);
                int signIndex = items.indexOf(MediaOption.SIGN);
                if (documentFromDiskIndex != -1 && signIndex != -1) {
                    items.remove(signIndex);
                    items.add(documentFromDiskIndex + 1, MediaOption.SIGN);
                }
            }

            // FIXME: https://online.sbis.ru/doc/d938064f-27eb-45c1-8d6f-b19ec2c8ea50
            if (mResourceProvider.getPackageName().contains("ru.tensor.saby.tasks")) {
                items.remove(MediaOption.DOCUMENT_FROM_DISK);
            }

            if (mView == null) {
                Timber.e("Attachments permissions granted but view is null. Unable to show it");
            } else {
                MediaParams params = new MediaParams.Builder()
                        .optionItems(items)
                        .attachmentsParams(mediaAttachmentsParams != null
                                ? mediaAttachmentsParams
                                : new MediaAttachmentsParams()
                        ).build();
                mView.showBottomMenu(params);
            }
            return Unit.INSTANCE;
        });
    }

    protected void onAttachePhotoClick() {
        if (mView == null) {
            Timber.e("Unable to handle photo attachment request");
            return;
        }
        List<String> requiredPermissionsList = new ArrayList<>(2);
        requiredPermissionsList.add(android.Manifest.permission.CAMERA);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            requiredPermissionsList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        mPermissionHelper.withPermissions(
                mView,
                requiredPermissionsList,
                isGranted -> {
                    if (mView == null) {
                        Timber.e("Unable to launch photo request");
                        return Unit.INSTANCE;
                    }
                    if (!isGranted) {
                        mView.showToast(ru.tensor.sbis.common.R.string.common_no_permission_error);
                        return Unit.INSTANCE;
                    }
                    String photoUriString = mFileUriUtil.generateSnapshotUri();
                    if (photoUriString == null) {
                        mView.showToast(R.string.common_attachments_generating_snapshot_error);
                    } else if (!mView.startCameraToTakePhoto(photoUriString)) {
                        mView.showToast(R.string.common_attachments_starting_camera_error);
                    }
                    return Unit.INSTANCE;
                }
        );
    }

    private void onAttacheFileFromGalleryClick(@NonNull MediaAttachmentsParams mediaAttachmentsParams) {
        if (mView == null) {
            Timber.e("Unable to handle gallery attachment request");
        } else if (!mView.openPhotoGalleryToTakeFile(mediaAttachmentsParams)) {
            mView.showToast(R.string.common_attachments_opening_gallery_error);
        }
    }

    private void onAttacheFileFromDocumentsClick(@NonNull MediaAttachmentsParams mediaAttachmentsParams) {
        if (mView == null) {
            Timber.e("Unable to handle documents attachment request");
        } else if (!mView.openDocumentsRepositoryToTakeFile(mediaAttachmentsParams)) {
            mView.showToast(R.string.common_attachments_opening_file_explorer_error);
        }
    }

    private void onAttacheDocumentFromDiskClick(@NonNull MediaAttachmentsParams mediaAttachmentsParams) {
        if (mView == null) {
            Timber.e("Unable to handle disk attachment request");
        } else {
            mView.openSelectDocumentActivity(mediaAttachmentsParams);
        }
    }

    private void onAttachDocumentFromClipboardClick(@NonNull MediaAttachmentsParams mediaAttachmentsParams) {
        if (mView == null) {
            Timber.e("Unable to handle clipboard attachment request");
        } else {
            mView.openSelectDocumentFromClipboardActivity(mediaAttachmentsParams);
        }
    }

    private int getAvailableAttachmentSizeToAdd(int filesToAddCount, int currentAttachmentsCount) {
        if ((currentAttachmentsCount + filesToAddCount) > maxAttachmentsCount) {
            showMaxAttachmentsCountMessage(maxAttachmentsCount);
            return maxAttachmentsCount - currentAttachmentsCount;
        } else {
            return filesToAddCount;
        }
    }

    private void showMaxAttachmentsCountMessage(int maxAttachmentsCount) {
        if (mView != null) {
            mView.showToast(
                    mResourceProvider.getQuantityString(
                            R.plurals.common_attachments_max_count_restriction,
                            maxAttachmentsCount,
                            maxAttachmentsCount
                    )
            );
        }
    }

    /**
     * Получение списка прав на доступ к чтению с диска
     */
    private List<String> getReadExternalStoragePermissions() {
        List<String> requiredPermissionsList = new ArrayList<>(1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requiredPermissionsList.add(Manifest.permission.READ_MEDIA_IMAGES);
            requiredPermissionsList.add(Manifest.permission.READ_MEDIA_VIDEO);
        } else {
            requiredPermissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        return requiredPermissionsList;
    }

    public interface AttachedListener {

        void addAttachment(@NonNull String uriString);

        void addDiskAttachment(@NonNull DiskDocumentParams diskDocumentParams);

        default void addAttachments(@NonNull List<String> uriStringList) {
            for (String uriString : uriStringList) {
                addAttachment(uriString);
            }
        }

        default void addDiskAttachments(@NonNull List<DiskDocumentParams> diskDocumentParamsList) {
            for (DiskDocumentParams params : diskDocumentParamsList) {
                addDiskAttachment(params);
            }
        }
    }

}