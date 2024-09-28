package ru.tensor.sbis.communicator.sbis_conversation;

import static ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.CONVERSATION_ACTIVITY_CONVERSATION_ARG;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.MenuItem;
import ru.tensor.sbis.base_components.AdjustResizeActivity;
import ru.tensor.sbis.base_components.fragment.FragmentBackPress;
import ru.tensor.sbis.communication_decl.communicator.ui.ConversationParams;
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature;
import ru.tensor.sbis.android_ext_decl.IntentAction;
import ru.tensor.sbis.communicator.sbis_conversation.ui.ConversationFragment;
import timber.log.Timber;

/** SelfDocumented */
public class ConversationActivity extends AdjustResizeActivity {

    @Override
    public int getContentViewId() {
        return R.id.communicator_conversation_fragment_container;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO https://online.sbis.ru/opendoc.html?guid=5d6aa3ce-3498-4362-9eff-b28624eeb6b4&client=3
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        boolean isNewConversation = getIntent().getBooleanExtra(CommunicatorCommonFeature.CONVERSATION_ACTIVITY_ARE_RECIPIENTS_SELECTED, false);
        boolean isSharing = getIntent().getBooleanExtra(CommunicatorCommonFeature.CONVERSATION_ACTIVITY_IS_SHARING, false);
        if (!(isNewConversation || isSharing)) {
            overridePendingTransition(ru.tensor.sbis.design.R.anim.right_in, ru.tensor.sbis.design.R.anim.nothing);
        } else {
            overridePendingTransition(ru.tensor.sbis.design.R.anim.nothing, ru.tensor.sbis.design.R.anim.nothing);
        }

        setContentView(R.layout.communicator_conversation_activity);

        if (savedInstanceState == null) {
            showConversationFragment(false);
        }

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            // Если бекстек опустошили - завершение
            if (!tryToSetLastFragmentBackSwipeAvailability(true)) {
                onViewGoneBySwipe();
            }
        });
    }

    @Override
    protected boolean swipeBackEnabled() {
        return false;
    }

    private void showConversationFragment(boolean addToBackStack) {
        tryToSetLastFragmentBackSwipeAvailability(false);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (addToBackStack) {
            fragmentTransaction.setCustomAnimations(ru.tensor.sbis.design.R.anim.right_in, ru.tensor.sbis.design.R.anim.right_out, ru.tensor.sbis.design.R.anim.right_in, ru.tensor.sbis.design.R.anim.right_out)
                               .addToBackStack(null);
        }
        Fragment fragment = null;

        if (getIntent().getSerializableExtra(CONVERSATION_ACTIVITY_CONVERSATION_ARG) != null) {
            ConversationParams conversationOpenParams = (ConversationParams) getIntent().getSerializableExtra(CONVERSATION_ACTIVITY_CONVERSATION_ARG);
            if (conversationOpenParams != null) {
                fragment = CommunicatorSbisConversationPlugin.INSTANCE
                        .getFeature()
                        .getConversationFragment(conversationOpenParams);
            }
        } else {
            fragment = CommunicatorSbisConversationPlugin.INSTANCE
                    .getFeature()
                    .getConversationFragment(getConversationArgs());
        }
        if (fragment != null) {
            fragmentTransaction.add(
                            R.id.communicator_conversation_fragment_container,
                            fragment,
                            ConversationFragment.class.getSimpleName()
                    ).commit();
        }
    }

    private Bundle getConversationArgs() {
        getIntent().putExtra(IntentAction.Extra.NEED_TO_ADD_FRAGMENT_TO_BACKSTACK, true);
        return getIntent().getExtras();
    }

    // Возвращает true, если удалось изменить свайпаемость у фрагмента в контейнере
    private boolean tryToSetLastFragmentBackSwipeAvailability(boolean isEnabled) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.communicator_conversation_fragment_container);
        if (!(fragment instanceof ConversationFragment)) {
            return false;
        }

        ConversationFragment conversationFragment = (ConversationFragment) fragment;
        conversationFragment.setBackSwipeAvailability(isEnabled);
        return true;
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        showConversationFragment(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isRunning() && !delegateBackPressToFragment()) {
            super.onBackPressed();
        }
        overridePendingTransition(ru.tensor.sbis.design.R.anim.nothing, ru.tensor.sbis.design.R.anim.right_out);
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
        } catch (Exception ex) {
            Timber.e(ex, "onDestroy ConversationActivity");
        }
    }

    private boolean delegateBackPressToFragment() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.communicator_conversation_fragment_container);
        return currentFragment instanceof FragmentBackPress && ((FragmentBackPress) currentFragment).onBackPressed();
    }
}
