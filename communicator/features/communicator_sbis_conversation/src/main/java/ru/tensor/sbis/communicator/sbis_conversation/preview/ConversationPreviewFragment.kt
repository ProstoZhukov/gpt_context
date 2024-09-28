package ru.tensor.sbis.communicator.sbis_conversation.preview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ru.tensor.sbis.android_ext_decl.getSerializableUniversally
import ru.tensor.sbis.communication_decl.communicator.ui.ConversationParams
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewFragmentFactory
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewMenuAction
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewMenuAction.ThemeConversationPreviewMenuAction
import ru.tensor.sbis.communicator.declaration.ConversationPreviewMode
import ru.tensor.sbis.communicator.sbis_conversation.R
import ru.tensor.sbis.communicator.sbis_conversation.ui.ConversationFragment
import ru.tensor.sbis.communicator.sbis_conversation.databinding.ConversationPreviewDialogFragmentBinding
import ru.tensor.sbis.design.design_menu.SbisMenu
import ru.tensor.sbis.design.design_menu.SbisMenuItem
import ru.tensor.sbis.design.design_menu.api.MenuItem
import ru.tensor.sbis.design.design_menu.model.MenuItemSettings
import ru.tensor.sbis.design.design_menu.model.MenuSelectionStyle
import ru.tensor.sbis.design.theme.HorizontalPosition

/**
 * Диалог для отображения предпросмотра меню и контента.
 * Включает затемнение фона, управление кликами и возвращение результатов через ConversationPreviewRouter.
 *
 * @autor da.zhukov
 */
class ConversationPreviewDialogFragment : DialogFragment() {

    companion object : ConversationPreviewFragmentFactory {

        private const val CONVERSATION_CLICKED = "CONVERSATION_CLICKED"

        private const val CONVERSATION_PREVIEW_MODE = "CONVERSATION_PREVIEW_MODE"
        private const val CONVERSATION_PREVIEW_MENU_ACTIONS = "CONVERSATION_PREVIEW_MENU_ACTIONS"
        private const val CONVERSATION_PARAMS = "CONVERSATION_PARAMS"

        override fun create(
            menuItems: List<ConversationPreviewMenuAction>,
            mode: ConversationPreviewMode,
            params: ConversationParams
        ): DialogFragment {
            val fragment = ConversationPreviewDialogFragment()
            val args = Bundle().apply {
                putSerializable(CONVERSATION_PREVIEW_MENU_ACTIONS, ArrayList(menuItems))
                putSerializable(CONVERSATION_PREVIEW_MODE, mode)
                putSerializable(CONVERSATION_PARAMS, params)
            }
            fragment.arguments = args
            return fragment
        }
    }

    private var _binding: ConversationPreviewDialogFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var router: ConversationPreviewRouter

    private val conversationParams by lazy {
        requireArguments().getSerializableUniversally<ConversationParams>(CONVERSATION_PARAMS)!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.ConversationPreviewTransparentDialogTheme)
        router = ConversationPreviewRouter(this, conversationParams)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.also { window ->
            // Устанавливаем размеры окна на весь экран
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ConversationPreviewDialogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.conversationPreviewSbisMenuContainer.clipToOutline = true
        binding.conversationPreviewFragmentContainer.clipToOutline = true

        binding.conversationPreviewRootView.setOnClickListener {
            dismissAllowingStateLoss()
        }

        val conversationFragment = ConversationFragment.createConversationFragment(conversationParams)

        childFragmentManager.beginTransaction()
            .replace(binding.conversationPreviewFragmentContainer.id, conversationFragment)
            .commit()

        val sbisMenuView = SbisMenu(
            children = prepareMenuItems(),
            selectionStyle = MenuSelectionStyle.MARKER,
            hideDefaultDividers = true,
            selectionEnabled = true,
            needShowTitle = false,
            twoLinesItemsTitle = false
        ).createView(requireContext(), binding.conversationPreviewSbisMenuContainer)

        binding.conversationPreviewSbisMenuContainer.addView(sbisMenuView)

        childFragmentManager.setFragmentResultListener(CONVERSATION_CLICKED, viewLifecycleOwner) { _, _ ->
            router.openFullConversation()
        }
    }

    private fun prepareMenuItems(): List<MenuItem> {
        val menuItems = arguments?.getSerializableUniversally<ArrayList<ConversationPreviewMenuAction>>(
            CONVERSATION_PREVIEW_MENU_ACTIONS
        )

        fun ConversationPreviewMenuAction.toSbisMenuItem(): SbisMenuItem {
            return SbisMenuItem(
                title = requireContext().getString(textResId),
                icon = icon,
                settings = MenuItemSettings(
                    isDestructive = isDestructive,
                    iconAlignment = HorizontalPosition.LEFT
                ),
                handler = {
                    if (this is ThemeConversationPreviewMenuAction.GoToConversation) {
                        router.openFullConversation()
                    } else {
                        router.setResult(this)
                        dismissAllowingStateLoss()
                    }
                }
            )
        }

        return menuItems?.map {
            if (it is ThemeConversationPreviewMenuAction.DeleteGroup) {
                SbisMenu(
                    children = listOf(
                        ThemeConversationPreviewMenuAction.Report().toSbisMenuItem(),
                        it.toSbisMenuItem()
                    ),
                    title = requireContext().getString(it.textResId),
                    icon = it.icon,
                    settings = MenuItemSettings(
                        isDestructive = true,
                        iconAlignment = HorizontalPosition.LEFT
                    ),
                    selectionStyle = MenuSelectionStyle.MARKER,
                    hideDefaultDividers = true,
                    selectionEnabled = true,
                    needShowTitle = true,
                    twoLinesItemsTitle = false
                )
            } else {
                it.toSbisMenuItem()
            }
        } ?: emptyList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}