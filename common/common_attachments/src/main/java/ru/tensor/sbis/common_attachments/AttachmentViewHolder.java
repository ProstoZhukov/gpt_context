package ru.tensor.sbis.common_attachments;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import ru.tensor.sbis.common_attachments.R;
import ru.tensor.sbis.common_views.document.icon.DocumentIconParamsBuilder;
import ru.tensor.sbis.common_views.document.icon.DocumentIconParamsBuilding;
import ru.tensor.sbis.design.view_ext.LoadingImageViewProgressBar;
import ru.tensor.sbis.common.util.FileUtil;

/**
 * Объект-холдер для работы со вложениями.
 * */
class AttachmentViewHolder extends RecyclerView.ViewHolder {

    @NonNull
    private final SimpleDraweeView mPreview;
    @NonNull
    private final TextView mFileIconView;
    @NonNull
    private final TextView mFileTitleView;
    @NonNull
    private final View mFileContainer;
    @NonNull
    private final View mRemoveButton;
    @NonNull
    private final View mCertificateBadge;

    private final ImageView mProgressView;
    @Px
    private final int mTextAttachmentWidth;

    @NonNull
    private final AttachmentResourcesHolder mResourcesHolder;

    private final PipelineDraweeControllerBuilder mDraweeControllerBuilder;

    private final ImageRequestBuilder mImageRequestBuilder;

    private final ResizeOptions mResizeOptions;

    private Attachment mAttachment;

    private final DocumentIconParamsBuilding.MainBuildingStep mIconParamsBuilder;

    /*** @SelfDocumented */
    public AttachmentViewHolder(@NonNull final View itemView,
                                @NonNull final AttachmentResourcesHolder resourcesHolder,
                                @Nullable final AttachmentsContainerAdapter.OnAttachmentsActionsListener actionsListener,
                                @Px final int defaultAttachmentWidth) {
        super(itemView);
        mResourcesHolder = resourcesHolder;
        mTextAttachmentWidth = defaultAttachmentWidth;
        mDraweeControllerBuilder = Fresco.newDraweeControllerBuilder();
        mImageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.EMPTY);
        mResizeOptions = new ResizeOptions(defaultAttachmentWidth, itemView.getLayoutParams().height);
        mIconParamsBuilder = DocumentIconParamsBuilder.create(itemView.getContext());

        mRemoveButton = itemView.findViewById(R.id.remove_button);
        mPreview = itemView.findViewById(R.id.attachments_container_image_preview);
        mFileContainer = itemView.findViewById(R.id.attachments_container_file_container);
        mFileIconView = itemView.findViewById(R.id.attachments_container_file_preview);
        mFileTitleView = itemView.findViewById(R.id.attachments_container_file_name);
        mCertificateBadge = itemView.findViewById(R.id.attachment_item_certificate_badge);
        mProgressView = itemView.findViewById(R.id.attachment_progress);

        mProgressView.setImageDrawable(new LoadingImageViewProgressBar(itemView.getContext()));

        if (actionsListener != null) {
            mRemoveButton.setOnClickListener(v -> actionsListener.onDeleteAttachmentClick(getAdapterPosition()));
            itemView.setOnClickListener(v -> actionsListener.onAttachmentClick(getAdapterPosition()));
        }
    }

    /*** @SelfDocumented */
    public void bind(@NonNull Attachment attachment) {
        setProgress(attachment.getProgress());

        mAttachment = attachment;
        if (attachment.getSignaturesCount() != 0) {
            mCertificateBadge.setVisibility(View.VISIBLE);
        } else {
            mCertificateBadge.setVisibility(View.GONE);
        }


        FileUtil.FileType type = attachment.getFileType();
        if (type == FileUtil.FileType.IMAGE) {
            String attachmentUri = attachment.getUri();
            ImageRequest imageRequest = attachmentUri.isEmpty()
                    ? null
                    : mImageRequestBuilder
                            .setSource(Uri.parse(attachmentUri))
                            .setResizeOptions(mResizeOptions)
                            .build();

            DraweeController controller = mDraweeControllerBuilder
                    .setImageRequest(imageRequest)
                    .setOldController(mPreview.getController())
                    .build();
            mPreview.setController(controller);

            mPreview.setVisibility(View.VISIBLE);
            mFileContainer.setVisibility(View.GONE);
            itemView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            mFileIconView.setText(mResourcesHolder.getAttachmentIconText(type));
            mFileIconView.setTextColor(mResourcesHolder.getAttachmentColor(type));
            mFileTitleView.setText(attachment.getName());
            mFileContainer.setVisibility(View.VISIBLE);
            mPreview.setVisibility(View.GONE);
            itemView.getLayoutParams().width = mTextAttachmentWidth;
        }
        mPreview.getHierarchy().setPlaceholderImage(
            mIconParamsBuilder.fromType(type)
                .sizeRes(R.dimen.attachment_file_type_icon_size)
                .buildIconDrawable()
        );
        itemView.requestLayout();
    }

    /*** @SelfDocumented */
    public void setProgress(int progress) {
        final int oldProgress = mAttachment != null ? mAttachment.getProgress() : Integer.MIN_VALUE;

        if (oldProgress != progress) {
            if (mAttachment != null) {
                mAttachment.setProgress(progress);
            }
            if (progress == Attachment.MAX_PROGRESS) {
                mProgressView.setVisibility(View.GONE);
            } else {
                mProgressView.setVisibility(View.VISIBLE);
                mProgressView.setImageLevel(100 * progress);
            }
        }
    }

}
