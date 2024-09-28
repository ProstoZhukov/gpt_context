package ru.tensor.sbis.common_attachments;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import ru.tensor.sbis.common_attachments.R;

/**
 * Адаптер для работы с вложениями.
 **/
public class AttachmentsContainerAdapter extends RecyclerView.Adapter<AttachmentViewHolder> {

    @Nullable
    private List<Attachment> mAttachmentsList;

    private AttachmentResourcesHolder mResourcesHolder;
    @Nullable
    private OnAttachmentsActionsListener mAttachmentsActionListener;
    @Px
    private final int mTextAttachmentWidth;

    /**
     * Конструктор.
     *
     * @param textAttachmentWidth ширина текста вложения (в пикселях).
     **/
    public AttachmentsContainerAdapter(@Px int textAttachmentWidth) {
        mTextAttachmentWidth = textAttachmentWidth;
    }

    @NonNull
    @Override
    public AttachmentViewHolder onCreateViewHolder(ViewGroup parent, @AttachmentsItemType int viewType) {
        return new AttachmentViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(getLayoutByType(viewType), parent, false),
                mResourcesHolder, mAttachmentsActionListener, mTextAttachmentWidth);
    }

    @Override
    public void onBindViewHolder(@NonNull AttachmentViewHolder holder, final int position) {
        onSafeDataList(list -> holder.bind(list.get(position)));
    }

    @Override
    public void onBindViewHolder(@NonNull AttachmentViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty() && payloads.contains(Payload.PROGRESS)) {
            onSafeDataList(list -> holder.setProgress(list.get(position).getProgress()));
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    @AttachmentsItemType
    public int getItemViewType(int position) {
        if (position == 0)
            return ru.tensor.sbis.common_attachments.AttachmentsItemTypeKt.FIRST_ELEMENT;
        if (position == getItemCount() - 1)
            return ru.tensor.sbis.common_attachments.AttachmentsItemTypeKt.LAST_ELEMENT;
        return ru.tensor.sbis.common_attachments.AttachmentsItemTypeKt.MIDDLE_ELEMENT;
    }

    public int getItemCount() {
        return mAttachmentsList == null ? 0 : mAttachmentsList.size();
    }

    /*** @SelfDocumented */
    public void setAttachments(@Nullable List<Attachment> attachments) {
        mAttachmentsList = attachments;
        notifyDataSetChanged();
    }

    /*** @SelfDocumented */
    public void updateAttachmentProgress(int position, @NonNull Attachment attachment) {
        onSafeDataList(list -> list.set(position, attachment));
        notifyItemChanged(position, Payload.PROGRESS);
    }

    /*** @SelfDocumented */
    public void setAttachmentResourceHolder(@NonNull AttachmentResourcesHolder resourcesHolder) {
        mResourcesHolder = resourcesHolder;
    }

    /*** @SelfDocumented */
    public void update() {
        notifyDataSetChanged();
    }

    /*** @SelfDocumented */
    public void setActionsListener(@Nullable OnAttachmentsActionsListener actionsListener) {
        mAttachmentsActionListener = actionsListener;
    }

    /**
     * Слушатель для работы с вложениями.
     * */
    public interface OnAttachmentsActionsListener {

        /**
         * Инициировано действие "удаление".
         *
         * @param position поиция вложения.
         * */
        void onDeleteAttachmentClick(int position);

        /**
         * Инициировано действие "выбор вложения".
         *
         * @param position поиция вложения.
         * */
        void onAttachmentClick(int position);
    }

    private enum Payload {
        PROGRESS
    }

    @LayoutRes
    private int getLayoutByType(@AttachmentsItemType int viewType) {
        if (viewType == ru.tensor.sbis.common_attachments.AttachmentsItemTypeKt.FIRST_ELEMENT)
            return R.layout.attachments_container_first_item;
        if (viewType == ru.tensor.sbis.common_attachments.AttachmentsItemTypeKt.MIDDLE_ELEMENT)
            return R.layout.attachments_container_item;
        if (viewType == ru.tensor.sbis.common_attachments.AttachmentsItemTypeKt.LAST_ELEMENT)
            return R.layout.attachments_container_last_item;
        throw new AssertionError("Unexpected view type " + viewType);
    }

    /**
     * Метод добавлен как временное решение, до переезда на
     * {@link ru.tensor.sbis.attachments.ui.view.register.AttachmentsView}.
     *
     * Основная проблема, которая решилась - обновленте прогресса до установки данных
     */
    private void onSafeDataList(Consumer<List<Attachment>> nullSafeHandler) {
        List<Attachment> data = mAttachmentsList;
        if (data != null) {
            nullSafeHandler.accept(data);
        }
    }
}
