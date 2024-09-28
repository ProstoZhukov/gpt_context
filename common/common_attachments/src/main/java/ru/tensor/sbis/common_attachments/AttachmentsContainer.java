package ru.tensor.sbis.common_attachments;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kotlin.Unit;
import ru.tensor.sbis.design.list_utils.decoration.dsl.DecorationExtensionKt;
import ru.tensor.sbis.design.list_utils.decoration.offset.BaseOffsetProvider;
import ru.tensor.sbis.design.list_utils.decoration.predicate.position.OffsetPredicate;

/**
 * Кастомный RecyclerView для работы с вложениями.
 **/
public class AttachmentsContainer extends RecyclerView {

    @NonNull
    private final AttachmentsContainerAdapter mAttachmentsContainerAdapter;

    /**
     * Оригинальная высота установленная во вне. Используется для восстановления и заполняется перед
     * изменением высоты в {@link AttachmentsContainer#setVisibility(AttachmentsViewVisibility)}
     */
    private int mOriginalHeight;
    private final int mPartialHeight;
    @NonNull
    private AttachmentsViewVisibility attachmentsVisibility = AttachmentsViewVisibility.VISIBLE;

    /*** @SelfDocumented */
    public AttachmentsContainer(@NonNull Context context) {
        this(context, null);
    }

    /*** @SelfDocumented */
    public AttachmentsContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /*** @SelfDocumented */
    public AttachmentsContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mAttachmentsContainerAdapter = new AttachmentsContainerAdapter(getResources().getDimensionPixelSize(R.dimen.attachments_item_width_message));
        init();
        setClipToPadding(false);
        DecorationExtensionKt.decorate(this, decoration -> {
            decoration.setOffsets(
                    new BaseOffsetProvider(
                            0,
                            0,
                            getResources().getDimensionPixelSize(R.dimen.common_attachments_item_offset),
                            0
                    )
            );
            decoration.setPredicate(new OffsetPredicate(0, 1));
            return Unit.INSTANCE;
        });

        mOriginalHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        mPartialHeight = getResources().getDimensionPixelSize(R.dimen.attachments_item_height_partial);
    }

    private void init() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        setLayoutManager(linearLayoutManager);
        setItemAnimator(new DefaultItemAnimator());
        setAdapter(mAttachmentsContainerAdapter);
        setClickable(true);
    }

    /*** @SelfDocumented */
    public void setActionsListener(@Nullable AttachmentsContainerAdapter.OnAttachmentsActionsListener listener) {
        mAttachmentsContainerAdapter.setActionsListener(listener);
    }

    /*** @SelfDocumented */
    public void setAttachments(@Nullable List<Attachment> attachmentsList) {
        mAttachmentsContainerAdapter.setAttachments(attachmentsList);
    }

    /*** @SelfDocumented */
    public void updateAttachmentProgress(int position, @NonNull Attachment attachment) {
        mAttachmentsContainerAdapter.updateAttachmentProgress(position, attachment);
    }

    /*** @SelfDocumented */
    public void setAttachmentResourceHolder(@NonNull AttachmentResourcesHolder attachmentResourceHolder) {
        mAttachmentsContainerAdapter.setAttachmentResourceHolder(attachmentResourceHolder);
    }

    /**
     * Высота в сжатом состоянии. Вычисляется на основании стандарта
     */
    public int getPartialHeight() {
        return mPartialHeight;
    }

    /*** @SelfDocumented */
    public void update() {
        mAttachmentsContainerAdapter.update();
    }

    /*** @SelfDocumented */
    public void setVisibility(@NonNull AttachmentsViewVisibility visibility) {
        ViewGroup.LayoutParams lp = getLayoutParams();
        switch (visibility) {
            case VISIBLE:
                if (lp.height != mOriginalHeight) {
                    lp.height = mOriginalHeight;
                }
                setVisibility(View.VISIBLE);
                break;
            case PARTIALLY:
                if (lp.height != mPartialHeight) {
                    lp.height = mPartialHeight;
                }
                setVisibility(View.VISIBLE);
                break;
            case GONE:
                setVisibility(View.GONE);
                lp.height = mOriginalHeight;
                break;
            default:
                throw new AssertionError("Unexpected visibility");
        }
        attachmentsVisibility = visibility;
    }

    /*** @SelfDocumented */
    @NonNull
    public AttachmentsViewVisibility getAttachmentsVisibility() {
        return attachmentsVisibility;
    }
}
