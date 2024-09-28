package ru.tensor.sbis.common_views;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import ru.tensor.sbis.common.util.FileUtil;

/**
 * API холдера для вложения.
 */
public interface DetailAttachmentResourcesHolder {
    /*** @SelfDocumented */
    int TITLE = 1;

    /*** @SelfDocumented */
    int SUBTITLE = 2;

    /*** @SelfDocumented */
    int DISABLED = 3;

    /*** @SelfDocumented */
    int getDetailedAttachmentColor(@AttachmentFieldType int type);

    /*** @SelfDocumented */
    int getAttachmentColor(@NonNull FileUtil.FileType type);

    /*** @SelfDocumented */
    @NonNull
    String getAttachmentIconText(@NonNull FileUtil.FileType type);


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TITLE, SUBTITLE, DISABLED})
    @interface AttachmentFieldType {
    }
}
