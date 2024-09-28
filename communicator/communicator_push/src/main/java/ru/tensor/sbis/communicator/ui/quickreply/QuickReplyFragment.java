package ru.tensor.sbis.communicator.ui.quickreply;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import ru.tensor.sbis.communicator.quickreply.QuickReplyModel;
import ru.tensor.sbis.design.profile.person.PersonView;
import ru.tensor.sbis.design.profile_decl.person.InitialsStubData;
import ru.tensor.sbis.design.profile_decl.person.PersonData;
import ru.tensor.sbis.mvp.presenter.BasePresenterFragment;
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent;
import ru.tensor.sbis.communicator.di.quickreply.DaggerQuickReplyComponent;
import ru.tensor.sbis.communicator.di.quickreply.QuickReplyComponent;
import ru.tensor.sbis.communicator.push.R;
import ru.tensor.sbis.communicator.push.controller.BaseMessageNotificationController;
import ru.tensor.sbis.communicator.push.model.MessagePushModel;
import ru.tensor.sbis.design_dialogs.fragment.AlertDialogFragment;

/**
 * Created by aa.mironychev on 08.08.17.
 */

public class QuickReplyFragment extends BasePresenterFragment<QuickReplyContract.View, QuickReplyContract.Presenter>
        implements
        QuickReplyContract.View,
        AlertDialogFragment.YesNoListener {

    private static final int DIALOG_CODE_SEND_COMMENT = 0;

    // Views
    private PersonView mPersonView;
    private TextView mNameView;
    private TextView mTargetMessageView;
    private EditText mEditMessageView;
    private ImageView mSendMessageButton;

    public static QuickReplyFragment newInstance(Bundle args) {
        QuickReplyFragment fragment = new QuickReplyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // region onCreate
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private QuickReplyModel getModel() {
        Bundle extras = getArguments();
        if (extras != null) {
            return new QuickReplyModel(
                    (UUID) extras.getSerializable(BaseMessageNotificationController.QUICK_REPLY_DIALOG_UUID_KEY),
                    (UUID) extras.getSerializable(BaseMessageNotificationController.QUICK_REPLY_MESSAGE_UUID_KEY),
                    (MessagePushModel.Sender) extras
                            .getSerializable(BaseMessageNotificationController.QUICK_REPLY_RECIPIENT_PERSON_MODEL_KEY),
                    extras.getString(BaseMessageNotificationController.QUICK_REPLY_ACTIVITY_TARGET_MESSAGE_KEY),
                    extras.getBoolean(BaseMessageNotificationController.QUICK_REPLY_IS_COMMENT_KEY)
            );
        }
        return null;
    }
    // endregion

    // region onCreateView
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.communicator_fragment_quick_reply, container, false);
        initToolbar(view);
        initViews(view);
        return view;
    }

    private void initToolbar(View root) {
        Toolbar toolbar = root.findViewById(R.id.communicator_toolbar);
        toolbar.setNavigationIcon(ru.tensor.sbis.communicator.design.R.drawable.communicator_ic_close_white);
        toolbar.setContentInsetStartWithNavigation(0); // Remove indent between "Home" and "Title" in toolbar
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews(View root) {
        mPersonView = root.findViewById(R.id.communicator_recipient_icon);
        mNameView = root.findViewById(R.id.communicator_recipient_name);
        mTargetMessageView = root.findViewById(R.id.communicator_target_message);
        mEditMessageView = root.findViewById(R.id.communicator_edit_message);
        mSendMessageButton = root.findViewById(R.id.communicator_send_message_button);
    }
    // endregion

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initListeners();
    }

    @Override
    public void onDestroyView() {
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(null);
        }
        super.onDestroyView();
    }

    private void initListeners() {
        mSendMessageButton.setOnClickListener(v -> mPresenter.onSendMessageClick());
        mEditMessageView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.onMessageChanged(s.toString());
            }
        });
    }

    @Override
    public void setRecipientData(@Nullable UUID personUuid, @Nullable String photoUrl, @Nullable String name) {
        InitialsStubData initials = null;
        if (name != null) {
            initials = InitialsStubData.InitialsHelper.createByFullName(name);
        }
        mPersonView.setData(
                new PersonData(
                    personUuid,
                    photoUrl,
                    initials
                )
        );
        mNameView.setText(name);
    }

    @Override
    public void setTargetMessage(@Nullable String message) {
        mTargetMessageView.setText(message);
    }

    @Override
    public void showIsCommentDialog() {
        AlertDialogFragment dialogFragment = AlertDialogFragment.newInstance(
                DIALOG_CODE_SEND_COMMENT, null,
                getString(ru.tensor.sbis.communicator.design.R.string.communicator_dialog_send_comment_message), true,
                ru.tensor.sbis.communicator.design.R.string.communicator_dialog_send_comment_ok_label,
                ru.tensor.sbis.communicator.design.R.string.communicator_dialog_send_comment_cancel_label);
        dialogFragment.show(
                getChildFragmentManager(), AlertDialogFragment.class.getSimpleName());
    }

    @Override
    public void updateSendButtonEnabled(boolean enabled) {
        if (mSendMessageButton != null) {
            mSendMessageButton.setEnabled(enabled);
        }
    }

    @Override
    public void showSendingError() {
        showToast(ru.tensor.sbis.communicator.design.R.string.communicator_failed_to_send_quick_message);
    }

    @Override
    public void close() {
        requireActivity().onBackPressed();
    }

    @Override
    public String getMessage() {
        return mEditMessageView.getText().toString();
    }

    // region YesNoListener
    @Override
    public void onYes(int dialogCode) {
        if (dialogCode == DIALOG_CODE_SEND_COMMENT) {
            mPresenter.onSendCommentConfirm();
        }
    }

    @Override
    public void onNo(int dialogCode) {
        //ignore
    }

    @Override
    public void onItem(int dialogCode, int which) {
    }
    // endregion

    // region Dagger
    private QuickReplyComponent mComponent;

    @Override
    protected void inject() {
        mComponent = DaggerQuickReplyComponent.builder()
                .communicatorCommonComponent(CommunicatorCommonComponent.getInstance(requireContext()))
                .quickReplyModel(getModel())
                .build();
    }

    @NonNull
    @Override
    protected QuickReplyContract.View getPresenterView() {
        return this;
    }

    @NonNull
    @Override
    protected QuickReplyContract.Presenter createPresenter() {
        return mComponent.getPresenter();
    }
    // endregion
}
