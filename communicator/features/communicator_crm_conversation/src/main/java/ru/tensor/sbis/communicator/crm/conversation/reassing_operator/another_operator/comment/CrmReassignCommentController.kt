package ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.comment

import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.comment.CrmReassignCommentFragment.Companion.CRM_ANOTHER_OPERATOR_PARAMS
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.comment.CrmReassignCommentView.*
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.comment.store.CrmReassignCommentStore.*
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.comment.store.CrmReassignCommentStoreFactory
import ru.tensor.sbis.mvi_extension.attachRxJavaBinder
import ru.tensor.sbis.mvi_extension.provideStore
import ru.tensor.sbis.mvi_extension.router.navigator.WeakLifecycleNavigator
import ru.tensor.sbis.mvi_extension.rx.observableEvents
import ru.tensor.sbis.mvi_extension.rx.observableLabels
import ru.tensor.sbis.mvi_extension.rx.observableStates

/**
 * Контроллер, обеспечивающий связку компонентов Android с компонентами MVI.
 *
 * @author da.zhukov
 */
internal class CrmReassignCommentController @AssistedInject constructor(
    @Assisted private val fragment: Fragment,
    viewFactory: (View) -> CrmReassignCommentView,
    private val storeFactory: CrmReassignCommentStoreFactory
) {

    private val store = fragment.provideStore {
        storeFactory.create()
    }

    private val router = CrmReassignCommentRouter()

    init {
        router.attachNavigator(WeakLifecycleNavigator(fragment))
        fragment.attachRxJavaBinder(BinderLifecycleMode.CREATE_DESTROY, viewFactory) { view ->
            view.observableEvents() bind {
                when (it) {
                    is Event.ReassignClick -> store.accept(Intent.ReassignClick(it.comment))
                }
            }

            store.observableLabels() bind {
                when (it) {
                    is Label.ReassignClick -> {
                        fragment.parentFragment?.parentFragmentManager?.setFragmentResult(
                            CrmReassignCommentFragment.REQUEST,
                            bundleOf(CRM_ANOTHER_OPERATOR_PARAMS to it.params)
                        )
                        router.close()
                    }
                }
            }
            store.observableStates().map { it.toModel() } bindTo view
        }
    }

    private fun State.toModel(): Model {
        return Model(currentComment)
    }
}