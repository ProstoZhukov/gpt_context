package ru.tensor.sbis.common_attachments;

import android.content.Context;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import ru.tensor.sbis.common_views.DetailAttachmentResourcesHolder;

/**
 * Дефолтная реализация объекта-холдера для работы с вложениями.
 **/
public class DefaultDetailAttachmentResourcesHolder extends AttachmentResourcesHolder implements DetailAttachmentResourcesHolder {

    private static final int sDetailedAttachmentColorsCount = 3;
    @ColorInt
    private final int[] mDetailedAttachmentColors = new int[sDetailedAttachmentColorsCount]; // 0 - disabled, 1 - name, 2 - attributes

    /*** @SelfDocumented */
    public DefaultDetailAttachmentResourcesHolder(@NonNull Context context) {
        super(context);
    }

    /*** @SelfDocumented */
    @Override
    public int getDetailedAttachmentColor(@AttachmentFieldType int type) {
        switch (type) {
            case DISABLED:
                if (mDetailedAttachmentColors[0] == 0) {
                    mDetailedAttachmentColors[0] = ContextCompat.getColor(mContext, android.R.color.secondary_text_dark);
                }
                return mDetailedAttachmentColors[0];

            case TITLE:
                if (mDetailedAttachmentColors[1] == 0) {
                    mDetailedAttachmentColors[1] = ContextCompat.getColor(mContext, ru.tensor.sbis.design.R.color.item_full_attachment_info_name_color);
                }
                return mDetailedAttachmentColors[1];

            case SUBTITLE:
            default:
                if (mDetailedAttachmentColors[2] == 0) {
                    mDetailedAttachmentColors[2] = ContextCompat.getColor(mContext, ru.tensor.sbis.design.R.color.item_full_attachment_info_info_color);
                }
                return mDetailedAttachmentColors[2];
        }
    }
}
