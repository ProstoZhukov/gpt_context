package ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.comment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.ColorInt
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.common.util.AdjustResizeHelper
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.communicator.crm.conversation.R
import ru.tensor.sbis.communicator.crm.conversation.databinding.CommunicatorCrmReassignCommentFragmentBinding
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.comment.di.DaggerCrmReassignCommentComponent
import ru.tensor.sbis.communicator.declaration.crm.providers.CRMAnotherOperatorParams
import ru.tensor.sbis.design_dialogs.dialogs.container.Container
import ru.tensor.sbis.design_dialogs.dialogs.content.Content
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreatorParcelable
import ru.tensor.sbis.design_dialogs.dialogs.content.utils.containerAs
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanelPeekHeight
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableDelegate
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableDialogFragment

/**
 * Экран ввода комментария при перезначении консультации другому оператору.
 *
 * @author da.zhukov
 */
class CrmReassignCommentFragment : BaseFragment(),
    Content,
    AdjustResizeHelper.KeyboardEventListener,
    FragmentBackPress {

    /**
     * Реализация создателя экземпляра фрагмента.
     */
    @Parcelize
    private class ContentCreator(val params: CRMAnotherOperatorParams) : ContentCreatorParcelable {

        override fun createFragment() =
            CrmReassignCommentFragment().withArgs {
                putParcelable(CRM_ANOTHER_OPERATOR_PARAMS, params)
            }
    }

    companion object {

        /**
         * Запрос результата.
         */
        const val REQUEST = "REQUEST_CRM_ANOTHER_OPERATOR_COMMENT"

        /**
         * Ключ для получения параметров.
         */
        const val CRM_ANOTHER_OPERATOR_PARAMS = "CRM_ANOTHER_OPERATOR_PARAMS"

        /**
         * Создаёт фрагмент ввода комментария.
         */
        fun newInstance(params: CRMAnotherOperatorParams, @ColorInt color: Int) =
            ContainerMovableDialogFragment.Builder()
                .setContentCreator(ContentCreator(params))
                .setExpandedPeekHeight(MovablePanelPeekHeight.FitToContent())
                .setDefaultHeaderPaddingEnabled(true)
                .setOpenAnimIgnored(true)
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                .setContainerBackgroundColor(color)
                .build()

    }

    private var view: CrmReassignCommentViewImpl? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerCrmReassignCommentComponent.factory().create(
            viewFactory = {
                val root: View = it.findViewById(R.id.crm_reassign_comment_root)
                CrmReassignCommentViewImpl(
                    CommunicatorCrmReassignCommentFragmentBinding.bind(root)
                ).also { view ->
                    this.view = view
                }
            },
            params = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requireArguments().getParcelable(CRM_ANOTHER_OPERATOR_PARAMS, CRMAnotherOperatorParams::class.java)!!
            } else {
                requireArguments().getParcelable(CRM_ANOTHER_OPERATOR_PARAMS)!!
            },
            context = requireContext()
        ).injector().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.communicator_crm_reassign_comment_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            containerAs<Container.Showable>()?.showContent()
        }
    }

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        view?.onKeyboardMeasure(keyboardHeight)
        return true
    }

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        view?.onKeyboardMeasure(keyboardHeight)
        return true
    }

    override fun onBackPressed() = when (val lastFragment = childFragmentManager.fragments.lastOrNull()) {
        is FragmentBackPress -> {
            lastFragment.onBackPressed()
        }
        is Content -> {
            lastFragment.onBackPressed()
        }
        is ContainerMovableDelegate -> {
            lastFragment.backPressed()
            true
        }
        else -> false
    }
}