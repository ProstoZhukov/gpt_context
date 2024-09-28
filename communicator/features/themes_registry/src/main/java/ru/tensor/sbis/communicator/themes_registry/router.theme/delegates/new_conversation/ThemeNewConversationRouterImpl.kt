package ru.tensor.sbis.communicator.themes_registry.router.theme.delegates.new_conversation

import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED
import ru.tensor.sbis.common.util.getParentAs
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryFacade.themesRegistryDependency
import ru.tensor.sbis.communicator.themes_registry.router.theme.delegates.BaseThemeRouterDelegate
import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.ChatRecipientSelectionActivity
import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.ChatSelectionType
import ru.tensor.sbis.communicator.themes_registry.ui.communication.ConversationRecipientSelectionFragmentCreator
import ru.tensor.sbis.communicator.common.ui.hostfragment.contracts.FabKeeper
import ru.tensor.sbis.communicator.themes_registry.ui.communication.ConversationTabChatCreationFragmentCreator
import ru.tensor.sbis.design_dialogs.dialogs.container.tablet.AnchorGravity
import ru.tensor.sbis.design_dialogs.dialogs.container.tablet.TabletContainerDialogFragment
import ru.tensor.sbis.design_dialogs.dialogs.container.tablet.VisualParamsBuilder
import java.util.*
import ru.tensor.sbis.communicator.design.R as RDesignCommunicator

/**
 * Реализация роутера создания новой переписки в реестре диалогов
 * @see [ThemeNewConversationRouter]
 *
 * @author vv.chekurda
 */
internal class ThemeNewConversationRouterImpl :
    BaseThemeRouterDelegate(),
    ThemeNewConversationRouter {

    override fun showNewDialogRecipientSelection(folderUuid: UUID?) = safeContext {
        if (!isTablet) {
            themesRegistryDependency
                .getDialogCreationActivityIntent(folderUuid)
                .let(::startActivity)
        } else {
            val tag = TabletContainerDialogFragment::class.java.simpleName
            if (requireActivity().supportFragmentManager.findFragmentByTag(tag) != null) {
                return
            }

            val containerTransaction = requireFragmentManager()
                .beginTransaction()
                .addToBackStack(tag)
            TabletContainerDialogFragment().setVisualParams(
                VisualParamsBuilder()
                    .belowActionBar()
                    .horizontalMargin()
                    .fixedWidth()
                    .wrapHeight(false)
                    .bottomAnchor(
                        requireFragment().getParentAs<FabKeeper>()?.fabId
                            ?: RDesignCommunicator.id.fab,
                        AnchorGravity.END
                    )
                    .listenAnchorLayoutAlways(true)
                    .softInputMode(SOFT_INPUT_STATE_UNCHANGED or SOFT_INPUT_ADJUST_NOTHING)
                    .build()
            )
                .setInstant(true)
                .setContentCreator(ConversationRecipientSelectionFragmentCreator())
                .show(containerTransaction, tag)
        }
    }

    override fun showNewChatCreation() = safeContext {
        if (!isTablet) {
            ChatRecipientSelectionActivity.newIntent(requireContext(), ChatSelectionType.ChatCreation)
                .let {
                    requireActivity().intent?.let { activityIntent ->
                        it.putExtras(activityIntent)
                    }
                    startActivity(it)
                }
        } else {
            val tag = TabletContainerDialogFragment::class.java.simpleName
            if (requireActivity().supportFragmentManager.findFragmentByTag(tag) != null) {
                return
            }

            val containerTransaction = requireFragmentManager()
                .beginTransaction()
                .addToBackStack(tag)
            TabletContainerDialogFragment().setVisualParams(
                VisualParamsBuilder()
                    .belowActionBar()
                    .horizontalMargin()
                    .fixedWidth()
                    .wrapHeight(false)
                    .bottomAnchor(
                        requireFragment().getParentAs<FabKeeper>()?.fabId
                            ?: RDesignCommunicator.id.fab,
                        AnchorGravity.END
                    )
                    .listenAnchorLayoutAlways(true)
                    .softInputMode(SOFT_INPUT_STATE_UNCHANGED or SOFT_INPUT_ADJUST_NOTHING)
                    .build()
            )
                .setInstant(true)
                .setContentCreator(ConversationTabChatCreationFragmentCreator())
                .show(containerTransaction, tag)
        }
    }
}