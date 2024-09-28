package ru.tensor.sbis.common_views;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import ru.tensor.sbis.common.util.FileUtil;
import ru.tensor.sbis.common.util.cache.CacheTypeUtils;
import ru.tensor.sbis.design.SbisMobileIcon;
import ru.tensor.sbis.design.TypefaceManager;

/**
 * Кастомная реализация {@link RelativeLayout} для отображения вложений.
 */
public abstract class DetailedAttachmentView extends RelativeLayout {

    @NonNull
    private final TextView mIconView;
    @NonNull
    private final TextView mNameView;
    @NonNull
    private final TextView mAttributesView;

    @NonNull
    private DetailAttachmentResourcesHolder mResourcesHolder;

    public DetailedAttachmentView(Context context) {
        this(context, null);
    }

    public DetailedAttachmentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DetailedAttachmentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final Resources resources = getResources();
        final int iconSize = resources.getDimensionPixelSize(ru.tensor.sbis.design.R.dimen.attachment_preview_icon_size);
        final int nameSize = resources.getDimensionPixelSize(ru.tensor.sbis.design.R.dimen.item_full_attachment_info_name_text_size);
        final int nameMarginLeft = resources.getDimensionPixelOffset(ru.tensor.sbis.design.R.dimen.item_full_attachment_info_image_space);
        final int attributesSize = resources.getDimensionPixelSize(ru.tensor.sbis.design.R.dimen.item_full_attachment_info_attributes_text_size);
        final int attributesMarginTop = resources.getDimensionPixelOffset(ru.tensor.sbis.design.R.dimen.item_full_attachment_info_text_vertical_space);

        final int iconId = ru.tensor.sbis.common.R.id.detailed_attachment_view_icon_id;
        final int nameId = ru.tensor.sbis.common.R.id.detailed_attachment_view_name_id;

        mIconView = new TextView(context);
        mIconView.setTextSize(TypedValue.COMPLEX_UNIT_PX, iconSize);
        LayoutParams iconParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        iconParams.addRule(CENTER_VERTICAL);
        mIconView.setLayoutParams(iconParams);
        mIconView.setTypeface(TypefaceManager.getSbisMobileIconTypeface(context));
        mIconView.setId(iconId);

        mNameView = new TextView(context);
        mNameView.setTypeface(TypefaceManager.getRobotoRegularFont(context));
        mNameView.setEllipsize(TextUtils.TruncateAt.END);
        mNameView.setMaxLines(1);
        mNameView.setTextSize(TypedValue.COMPLEX_UNIT_PX, nameSize);
        LayoutParams nameParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        nameParams.leftMargin = nameMarginLeft;
        nameParams.addRule(RIGHT_OF, iconId);
        mNameView.setLayoutParams(nameParams);
        mNameView.setId(nameId);

        mAttributesView = new TextView(context);
        mAttributesView.setTypeface(TypefaceManager.getRobotoRegularFont(context));
        mAttributesView.setTextSize(TypedValue.COMPLEX_UNIT_PX, attributesSize);
        mAttributesView.setMaxLines(1);
        LayoutParams attributeParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        attributeParams.topMargin = attributesMarginTop;
        attributeParams.addRule(ALIGN_LEFT, nameId);
        attributeParams.addRule(ALIGN_START, nameId);
        attributeParams.addRule(BELOW, nameId);
        mAttributesView.setLayoutParams(attributeParams);

        addView(mIconView);
        addView(mNameView);
        addView(mAttributesView);
    }

    /*** @SelfDocumented */
    public void setResourcesHolder(@NonNull DetailAttachmentResourcesHolder resourcesHolder) {
        mResourcesHolder = resourcesHolder;
    }

    protected void setFolderInfo(@NonNull String name) {
        mNameView.setText(name);

        setAttributes(null, FileUtil.FileType.FOLDER);
    }

    protected void setFileInfo(@NonNull String name, @NonNull String extension, boolean encrypted, long fileSize) {
        mNameView.setText(name);
        String attributes = new StringBuilder()
                .append(extension)
                .append(" ")
                .append(CacheTypeUtils.prettySizeValueDotSeparator(getContext(), fileSize))
                .toString();

        if (encrypted) {
            setAttributes(
                    attributes,
                    String.valueOf(SbisMobileIcon.Icon.smi_lock.getCharacter()),
                    ContextCompat.getColor(getContext(), ru.tensor.sbis.design.R.color.file_type_encrypted)
            );
        } else {
            setAttributes(attributes, FileUtil.detectFileTypeByExtension(extension));
        }
    }

    protected void setFileInfo(@NonNull String name, @NonNull String extension) {
        mNameView.setText(name);
        String attributes = new StringBuilder()
                .append(extension)
                .toString();

        setAttributes(attributes, FileUtil.detectFileTypeByExtension(extension));
    }

    private void setAttributes(@Nullable String attributes, @NonNull FileUtil.FileType type) {
        setAttributes(attributes, mResourcesHolder.getAttachmentIconText(type), mResourcesHolder.getAttachmentColor(type));
    }

    private void setAttributes(@Nullable String attributes, @NonNull String icon, @ColorInt int color) {
        if (attributes == null) {
            mAttributesView.setVisibility(GONE);
            ((LayoutParams) mNameView.getLayoutParams()).addRule(CENTER_VERTICAL);
        } else {
            mAttributesView.setVisibility(VISIBLE);
            mAttributesView.setText(attributes);
            ((LayoutParams) mNameView.getLayoutParams()).addRule(CENTER_VERTICAL, 0);
        }
        mIconView.setText(icon);
        mIconView.setTextColor(color);
        mNameView.setTextColor(mResourcesHolder.getDetailedAttachmentColor(DetailAttachmentResourcesHolder.TITLE));
        mAttributesView.setTextColor(mResourcesHolder.getDetailedAttachmentColor(DetailAttachmentResourcesHolder.SUBTITLE));
    }
}

