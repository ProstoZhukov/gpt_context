package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Trace;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder;
import ru.tensor.sbis.base_components.adapter.BaseTwoWayPaginationAdapter;
import ru.tensor.sbis.base_components.adapter.OnItemClickListener;
import ru.tensor.sbis.base_components.adapter.contacts.holder.OnContactPhotoClickListener;
import ru.tensor.sbis.communicator.common.data.theme.FoldersConversationRegistryItem;
import ru.tensor.sbis.communicator.common.data.theme.StubConversationRegistryItem;
import ru.tensor.sbis.communicator.common.themes_registry.DialogListActionsListener;
import ru.tensor.sbis.communicator.common.util.header_date.DateViewHolder;
import ru.tensor.sbis.communicator.common.util.layout_manager.CommunicatorLayoutManager;
import ru.tensor.sbis.communicator.core.views.contact_view.ContactView;
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.ConversationItemsViewPool;
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.diffutil.ThemeListDiffCallback;
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.utils.ConversationChechHelperUtilKt;
import ru.tensor.sbis.communicator.themes_registry.R;
import ru.tensor.sbis.design.folders.FoldersView;
import ru.tensor.sbis.design.folders.support.utils.stub_integration.StubViewMediator;
import ru.tensor.sbis.design.list_header.DateTimeAdapter;
import ru.tensor.sbis.design.list_header.ListDateViewUpdater;
import ru.tensor.sbis.design.stubview.StubView;
import ru.tensor.sbis.design.stubview.StubViewMode;
import ru.tensor.sbis.persons.ConversationRegistryItem;
import ru.tensor.sbis.persons.ContactVM;
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.holders.ChatListActionsListener;
import ru.tensor.sbis.swipeablelayout.SwipeableHolderInterface;
import ru.tensor.sbis.swipeablelayout.SwipeableViewBinderHelper;
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel;
import ru.tensor.sbis.communicator.core.contract.AttachmentClickListener;
import ru.tensor.sbis.common.util.UUIDUtils;
import ru.tensor.sbis.swipeablelayout.viewpool.SwipeMenuViewPool;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static ru.tensor.sbis.communicator.common.data.theme.ConversationMapperKt.getNewStubItem;

/**
 * Адаптер списка диалогов / чатов.
 *
 * @author rv.krohalev
 */
@UiThread
public class ConversationListAdapter
        extends BaseTwoWayPaginationAdapter<ConversationRegistryItem>
        implements DateTimeAdapter {

    public static final int CONVERSATION_ITEM = 1;
    private static final int CONTACT_ITEM = 2;
    private static final int PAGING_LOADING_ERROR_ITEM = 3;
    private static final int HOLDER_FOLDERS = 4;
    private static final int HOLDER_STUB = 5;
    private static final int NOTIFICATION_ITEM = 6;

    // максимальное количество переиспользуемых холдеров каждого типа
    private static final int MAX_RECYCLED_VIEWS_FOR_TYPE = 100;

    @NonNull
    private final SwipeableViewBinderHelper<UUID> mSwipeHelper = new SwipeableViewBinderHelper<>();
    @NonNull
    private final ConversationItemsViewPool mItemsViewPool;

    @NonNull
    private final SwipeMenuViewPool mSwipeMenuViewPool;

    @NonNull
    private final ListDateViewUpdater mDateUpdater;

    @Nullable
    private OnItemClickListener<ContactVM> mOnContactClickListener;

    @Nullable
    private OnContactPhotoClickListener<ContactVM> mOnContactPhotoClickListener;

    private DialogListActionsListener mDialogListActionsListener;
    private ChatListActionsListener mChatListActionsListener;

    private ConversationItemClickHandler mItemClickHandler;

    private boolean mIsPhotoCollageVisible;

    private boolean showPagingLoadingError = false;

    @Nullable
    private AttachmentClickListener mAttachmentClickListener;

    private FoldersView foldersView;
    private FoldersViewHolderHelper foldersViewHolderHelper;

    private StubViewMediator mStubViewMediator;

    private final Boolean mIsSharingMode;

    private final SearchDialogContactViewStyleProvider conversationListStyleProvider = new SearchDialogContactViewStyleProvider();

    public ConversationListAdapter(
            @NonNull ConversationItemsViewPool itemsViewPool,
            @NonNull SwipeMenuViewPool swipeMenuViewPool,
            @NonNull ListDateViewUpdater dateUpdater,
            @NonNull Boolean isSharingMode) {
        mItemsViewPool = itemsViewPool;
        mSwipeMenuViewPool = swipeMenuViewPool;
        mDateUpdater = dateUpdater;
        mIsSharingMode = isSharingMode;
    }

    public void setStubViewMediator(StubViewMediator stubMediator) {
        mStubViewMediator = stubMediator;
    }

    public void closeFoldersView() {
        if (foldersView != null) {
            foldersView.showCompactFolders();
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        // задаем максимальное количество переиспользуемых холдеров для каждого типа
        RecyclerView.RecycledViewPool recycledViewPool = recyclerView.getRecycledViewPool();
        recycledViewPool.setMaxRecycledViews(CONVERSATION_ITEM, MAX_RECYCLED_VIEWS_FOR_TYPE);
        recycledViewPool.setMaxRecycledViews(CONTACT_ITEM, MAX_RECYCLED_VIEWS_FOR_TYPE);
        recycledViewPool.setMaxRecycledViews(HOLDER_FOLDERS, 1);
        recycledViewPool.setMaxRecycledViews(NOTIFICATION_ITEM, MAX_RECYCLED_VIEWS_FOR_TYPE);
        if (foldersView == null && mRecyclerView != null) {
            resetFoldersView();
            foldersViewHolderHelper.attachFoldersView(foldersView);
        }
        conversationListStyleProvider.setResourceCacheEnabled(true);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        conversationListStyleProvider.setResourceCacheEnabled(false);
        conversationListStyleProvider.clearReferences();
    }

    private void resetFoldersView() {
        if (mRecyclerView != null) {
            foldersView = mItemsViewPool.getFoldersView();
        }
    }

    //region Base
    @NonNull
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public AbstractViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case CONVERSATION_ITEM, NOTIFICATION_ITEM:
                Trace.beginSection("ConversationListAdapter#onCreateViewHolder");
                ConversationItemViewHolder holder = new ConversationItemViewHolder(
                        parent,
                        mItemClickHandler,
                        mDialogListActionsListener,
                        mChatListActionsListener,
                        mAttachmentClickListener,
                        mItemsViewPool,
                        mSwipeMenuViewPool,
                        mIsSharingMode
                );
                Trace.endSection();
                return holder;

            case PAGING_LOADING_ERROR_ITEM:
                return new AbstractViewHolder<>(LayoutInflater.from(parent.getContext())
                        .inflate(ru.tensor.sbis.communicator.design.R.layout.communicator_item_list_paging_error_with_spacing, parent, false));

            case HOLDER_FOLDERS:
                ViewGroup container = new FrameLayout(parent.getContext());
                container.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
                container.setId(R.id.themes_registry_folders_container_id);
                return new DialogFoldersHolder(container);

            case HOLDER_STUB:
                StubView stubView = new StubView(parent.getContext());
                stubView.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, MATCH_PARENT));
                int orientation = parent.getResources().getConfiguration().orientation;
                if (orientation == ORIENTATION_LANDSCAPE) {
                    stubView.setMode(StubViewMode.BLOCK, true);
                } else {
                    stubView.setMode(StubViewMode.BASE, true);
                }
                return new DialogsStubViewHolder(stubView);

            case CONTACT_ITEM:
                ContactView contactView = new ContactView(parent.getContext(), conversationListStyleProvider);
                SearchDialogContactViewHolder<ContactVM> contactViewHolder = new SearchDialogContactViewHolder<>(
                        contactView
                );
                contactViewHolder.setOnContactClickListener(mOnContactClickListener);
                contactViewHolder.setOnContactPhotoClickListener(mOnContactPhotoClickListener);
                return contactViewHolder;

            default:
                return super.onCreateViewHolder(parent, viewType);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(@NonNull AbstractViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        CommunicatorLayoutManager communicatorLayoutManager = null;
        if (mRecyclerView != null) {
            communicatorLayoutManager = (CommunicatorLayoutManager) mRecyclerView.getLayoutManager();
        }
        if (holder.getItemViewType() == HOLDER_STUB) {
            DialogsStubModel stub = (DialogsStubModel) getItem(position);
            DialogsStubViewHolder stubViewHolder = (DialogsStubViewHolder) holder;
            mStubViewMediator.setStubView(stubViewHolder.getStubView());
            if (stub != null && stub.getContent() != null) {
                stubViewHolder.getStubView().setContent(stub.getContent());
            }
            if (communicatorLayoutManager != null) {
                communicatorLayoutManager.setVerticalScrollEnabled(false);
            }
            return;
        }

        if (holder.getItemViewType() == CONTACT_ITEM) {
            holder.bind(getItem(position));
            SearchDialogContactViewHolder<ContactVM> searchDialogContactViewHolder = (SearchDialogContactViewHolder<ContactVM>) holder;
            searchDialogContactViewHolder.showSeparator(!isNextItemInstanceofConversationModel(position));
        } else if (holder.getItemViewType() != HOLDER_FOLDERS) {
            ConversationModel model = (ConversationModel) getItem(position);
            if (communicatorLayoutManager != null) {
                communicatorLayoutManager.setVerticalScrollEnabled(true);
            }
            if (model != null && isSwipeableViewHolder(holder.getItemViewType())) {
                SwipeableHolderInterface swipeableViewHolder = (SwipeableHolderInterface) holder;
                model.setFormattedDateTime(mDateUpdater.getFormattedDate(position));

                ConversationItemViewHolder conversationViewHolder = (ConversationItemViewHolder) swipeableViewHolder;
                boolean isCheckModeEnable = getCheckHelper() != null && getCheckHelper().isCheckModeEnabled() && !mIsSharingMode;
                conversationViewHolder.setCheckModeEnabled(isCheckModeEnable);
                boolean isSearchByContactModeEnable = !mIsPhotoCollageVisible && !mIsSharingMode;
                conversationViewHolder.setSearchByContactModeEnabled(isSearchByContactModeEnable);
                conversationViewHolder.bind(model);
                if (!mIsSharingMode) mSwipeHelper.bind(swipeableViewHolder.getSwipeableLayout(), getUuidForSwipeMenu(model));
            } else {
                holder.bind(model);
            }
        } else {
            if (foldersView == null && mRecyclerView != null && foldersViewHolderHelper != null) {
                resetFoldersView();
                foldersViewHolderHelper.attachFoldersView(foldersView);
            }

            assert this.foldersView != null;
            @NonNull View foldersView = this.foldersView;

            ViewGroup foldersContainer = (ViewGroup) foldersView.getParent();
            ViewGroup currentContainer = (ViewGroup) holder.itemView;
            if (foldersContainer != null && foldersContainer != currentContainer) {
                foldersContainer.removeView(foldersView);
            }
            if (currentContainer.getChildCount() == 0) {
                currentContainer.addView(foldersView);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AbstractViewHolder<ConversationRegistryItem> holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty()) {
            if (payloads.contains(DateViewHolder.CHANGE_DATE_PAYLOAD) && holder instanceof DateViewHolder) {
                ConversationModel model = (ConversationModel) getItem(position);
                DateViewHolder dateViewHolder = (DateViewHolder) holder;
                if (model != null && model.getFormattedDateTime() != null ) {
                    dateViewHolder.setFormattedDateTime(model.getFormattedDateTime());
                }
                if (payloads.size() > 1) {
                    super.onBindViewHolder(holder, position, payloads);
                }
            } else {
                super.onBindViewHolder(holder, position, payloads);
            }
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public void onViewRecycled(@NonNull AbstractViewHolder<ConversationRegistryItem> holder) {
        super.onViewRecycled(holder);
        if (holder.getItemViewType() == HOLDER_STUB) {
            // если заглушка не видна, можно отвязать её
            mStubViewMediator.setStubView(null);
        }
    }

    @Override
    public void onSavedInstanceState(@NonNull Bundle outState) {
        super.onSavedInstanceState(outState);
        mSwipeHelper.saveStates(outState, ConversationListAdapter.class);
        if (foldersView != null) {
            foldersView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mSwipeHelper.restoreStates(savedInstanceState, ConversationListAdapter.class);
        if (foldersView != null) {
            foldersView.onRestoreInstanceState(savedInstanceState);
        }
    }
    //endregion

    /** SelfDocumented */
    public void setItemClickHandler(ConversationItemClickHandler handler) {
        mItemClickHandler = handler;
    }

    /** SelfDocumented */
    public void setFoldersViewHolderHelper(FoldersViewHolderHelper helper) {
        foldersViewHolderHelper = helper;
    }

    /**
     * Метод для показа / скрытия коллажей и аватарок у ячеек списка.
     */
    @SuppressLint("NotifyDataSetChanged")
    public void changeItemsCollagesVisibility(boolean isVisible) {
        boolean isChanged = mIsPhotoCollageVisible != isVisible;
        mIsPhotoCollageVisible = isVisible;
        if (isChanged) notifyDataSetChanged();
    }

    /** Вернуть позицию последнего контакта перед началом списка диалогов */
    public int lastContactPositionBeforeDialogs() {
        int result = -1;
        List<ConversationRegistryItem> items = mContent;
        int firstDialogPosition = -1;
        for (int i = 0; i < items.size(); i++) {
            ConversationRegistryItem item = mContent.get(i);
            if (item instanceof ConversationModel) {
                if (!((ConversationModel)item).isChatForView()) {
                    firstDialogPosition = i;
                }
                break;
            }
        }
        if (firstDialogPosition > 0) {
            ConversationRegistryItem item = items.get(firstDialogPosition - 1);
            if (item instanceof  ContactVM) {
                result = firstDialogPosition - 1;
            }
        }
        return result;
    }

    /** SelfDocumented */
    private boolean isNextItemInstanceofConversationModel(int currentItemPosition) {
        return currentItemPosition == getContent().size() - 1 || getItem(currentItemPosition + 1) instanceof ConversationModel;
    }

    /** SelfDocumented */
    public void clear() {
        if (foldersView != null) {
            foldersViewHolderHelper.detachFoldersView(foldersView);
        }

        setDialogListActionsListener(null);
        setChatListActionsListener(null);
        setItemClickHandler(null);
        setAttachmentClickListener(null);
        setFoldersViewHolderHelper(null);
        mItemsViewPool.flush();
        mSwipeHelper.clearEntries();
    }

    //region AbstractTwoWayPaginationAdapter
    @Override
    public int getItemViewType(int position) {
        int itemPosition = position - mOffset;
        if (showPagingLoadingError && itemPosition == mContent.size()
            && !(getItem(0) instanceof FoldersConversationRegistryItem)) {
            return PAGING_LOADING_ERROR_ITEM;
        } else {
            return super.getItemViewType(position);
        }
    }

    @Override
    protected int getItemType(@Nullable ConversationRegistryItem conversationRegistryItem) {
        int type;
        if (conversationRegistryItem instanceof ConversationModel &&
                ((ConversationModel) conversationRegistryItem).isNotice()) {
            type = NOTIFICATION_ITEM;
        } else if (conversationRegistryItem instanceof ConversationModel) {
            type = CONVERSATION_ITEM;
        } else if (conversationRegistryItem instanceof ContactVM) {
            type = CONTACT_ITEM;
        } else if (conversationRegistryItem instanceof FoldersConversationRegistryItem) {
            type = HOLDER_FOLDERS;
        } else if(conversationRegistryItem instanceof StubConversationRegistryItem) {
            type = HOLDER_STUB;
        } else {
            type = HOLDER_EMPTY;
        }

        return type;
    }

    @Override
    public boolean isChecked(int position) {
        if (mCheckHelper == null) return false;
        final ConversationRegistryItem item = getItem(position);
        if (item == null) return false;
        return ConversationChechHelperUtilKt.isChecked(mCheckHelper, item);
    }

    @Override
    protected int getHolderBottomPaddingHeight(@NonNull Context context) {
        // Используем размер контейнера для Fab
        return context.getResources().getDimensionPixelSize(ru.tensor.sbis.communicator.design.R.dimen.communicator_fab_container_height);
    }

    //endregion

    //region Public Interface
    public void setDialogListActionsListener(@Nullable DialogListActionsListener listener) {
        mDialogListActionsListener = listener;
    }

    public void setChatListActionsListener(@Nullable ChatListActionsListener listener) {
        mChatListActionsListener = listener;
    }

    public void setAttachmentClickListener(@Nullable AttachmentClickListener attachmentsClickListener) {
        mAttachmentClickListener = attachmentsClickListener;
    }

    public void setOnContactClickListener(@Nullable OnItemClickListener<ContactVM> onContactClickListener) {
        mOnContactClickListener = onContactClickListener;
    }

    public void setOnContactPhotoClickListener(@Nullable OnContactPhotoClickListener<ContactVM> onContactPhotoClickListener) {
        mOnContactPhotoClickListener = onContactPhotoClickListener;
    }

    /**
     * Прогреть классы ячеек (верифицирование классов).
     */
    public void warmUpDex(Context context) {
        new ConversationItemViewHolder(
            new FrameLayout(context),
            mItemClickHandler,
            mDialogListActionsListener,
            mChatListActionsListener,
            mAttachmentClickListener,
            new ConversationItemsViewPool(context, false),
            mSwipeMenuViewPool,
            mIsSharingMode
        ).bind(getNewStubItem());
    }
    //endregion

    //region HeaderDateView

    @Override
    public Date getItemDateTime(int position) {
        ConversationRegistryItem item = getItem(position);
        Long timestamp = item instanceof ConversationModel ? ((ConversationModel) item).getTimestamp() : null;
        return timestamp != null ? new Date(timestamp) : null;
    }

    //endregion

    //region swipe layout
    public void closeSwipePanel(UUID uuid) {
        mSwipeHelper.close(uuid);
    }

    public void closeAllSwipePanels() {
        mSwipeHelper.closeAll();
    }

    public void clearSwipeMenuState() {
        mSwipeHelper.clearEntries();
    }

    private boolean isSwipeableViewHolder(int holderViewType) {
        return holderViewType == CONVERSATION_ITEM || holderViewType == NOTIFICATION_ITEM;
    }

    //endregion

    //region AbstractSelectableListAdapter override methods
    @Override
    protected boolean needSaveStateInAdapter() {
        return false;
    }

    @Override
    @NonNull
    public ConversationModel provideStubItem() {
        return getNewStubItem();
    }
    //endregion

    public void showPagingLoadingProgress(boolean progress) {
        if (progress) {
            // сбрасываем состояние ошибки пагинации при загрузке страницы
            showPagingLoadingError = false;
        }
        if (mShowOlderLoadingProgress != progress) {
            showOlderLoadingProgress(progress);
        }
    }

    public void showPagingLoadingError() {
        if (showPagingLoadingError) {
            return;
        }
        showPagingLoadingError = true;
        int errorItemPosition = getContent().size() + mOffset;
        if (!mWithBottomEmptyHolder) {
            notifyItemInserted(errorItemPosition);
        } else {
            notifyItemChanged(errorItemPosition);
        }
    }

    public void resetPagingLoadingIndicator() {
        showPagingLoadingError = false;
        showPagingLoadingProgress(false);
    }

    public void setData(
            @Nullable List<ConversationRegistryItem> oldList,
            @Nullable List<ConversationRegistryItem> newList,
            int offset,
            boolean forceNotifyDataSetChanged) {
        mOffset = offset;
        DiffUtil.DiffResult diffResult = null;

        boolean notifyDataSetChanged = newList == null || oldList == null || forceNotifyDataSetChanged;
        if (!notifyDataSetChanged) {
            diffResult = DiffUtil.calculateDiff(new ThemeListDiffCallback(oldList, newList));
        }

        setContent(newList, notifyDataSetChanged);

        if (diffResult != null) {
            diffResult.dispatchUpdatesTo(new ConversationListUpdateCallback(this, offset));
        }
    }

    @Override
    public void setData(@Nullable List<ConversationRegistryItem> dataList, int offset) {
        setData(null, dataList, offset, false);
    }

    @Override
    protected boolean isMatching(@NonNull ConversationRegistryItem src,
                                 @NonNull ConversationRegistryItem item) {
        return src instanceof ConversationModel && item instanceof ConversationModel &&
                UUIDUtils.equals(((ConversationModel) src).getCompareUuid(), ((ConversationModel) item).getCompareUuid());
    }

    @Nullable
    private UUID getUuidForSwipeMenu(@Nullable ConversationModel model) {
        return model == null ? null : model.getCompareUuid();
    }
}