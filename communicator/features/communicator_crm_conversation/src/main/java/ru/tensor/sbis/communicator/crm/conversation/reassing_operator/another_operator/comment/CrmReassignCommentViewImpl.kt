package ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.comment

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.arkivanov.mvikotlin.core.view.BaseMviView import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communicator.crm.conversation.databinding.CommunicatorCrmReassignCommentFragmentBinding
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.comment.CrmReassignCommentView.Event
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.comment.CrmReassignCommentView.Model
import ru.tensor.sbis.design.utils.extentions.setBottomPadding


/**
 * @author da.zhukov
 */
internal class CrmReassignCommentViewImpl(
    private val binding: CommunicatorCrmReassignCommentFragmentBinding
) : BaseMviView<Model, Event>(), CrmReassignCommentView {

    init {
        val lifecycleOwner: LifecycleOwner? = binding.root.findViewTreeLifecycleOwner()
        val scope = lifecycleOwner?.lifecycleScope

        with(binding) {
            crmReassignButton.setOnClickListener {
                scope?.launch {
                    dispatch(Event.ReassignClick(crmReassignComment.text ?: StringUtils.EMPTY))
                }
            }
        }
    }

    fun onKeyboardMeasure(keyboardHeight: Int): Boolean {
        setRootBottomPadding(keyboardHeight)
        return true
    }

    private fun setRootBottomPadding(padding: Int) {
        binding.crmReassignCommentRoot.setBottomPadding(padding )
    }
}