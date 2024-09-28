package ru.tensor.sbis.common_attachments;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import ru.tensor.sbis.disk.decl.attach_helper.MediaAttachmentsParams;
import ru.tensor.sbis.disk.decl.attach_helper.MediaParams;

/**
 * Интерфейс объекта-вложения.
 * */
public interface AttachmentView extends AttachmentMenuActionListener {

    /*** @selfDocumented */
    void showToast(@StringRes int toastTextId);

    /*** @selfDocumented */
    void showToast(@NonNull CharSequence toastText);

    /*** Запуск камеры, чтобы сделать фото. */
    boolean startCameraToTakePhoto(@NonNull String photoUriString);

    /*** Открыть галерею для выбора фото. */
    boolean openPhotoGalleryToTakeFile(@NonNull MediaAttachmentsParams mediaAttachmentsParams);

    /*** Открыть проводник, чтобы выбрать документ. */
    boolean openDocumentsRepositoryToTakeFile(@NonNull MediaAttachmentsParams mediaAttachmentsParams);

    /*** Открыть документ в новой Activity. */
    void openSelectDocumentActivity(@NonNull MediaAttachmentsParams mediaAttachmentsParams);

    /*** Открыть документ в новой Activity. */
    void openSelectDocumentFromClipboardActivity(@NonNull MediaAttachmentsParams mediaAttachmentsParams);

    /*** Получить список недоступных permission. */
    @NonNull
    List<String> getNotGrantedPermissions(@NonNull List<String> requiredPermissions);

    /*** @SelfDocumented */
    void requestPermissions(@NonNull List<String> requiredPermissions);

    /*** @SelfDocumented */
    void showBottomMenu(@NonNull MediaParams params);

}
