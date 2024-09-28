package ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.lifecycleScope
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.android_ext_decl.getSerializableUniversally
import ru.tensor.sbis.communication_decl.crm.CrmChannelListFragmentFactory.Companion.CRM_CHANNEL_NAME
import ru.tensor.sbis.communication_decl.crm.CrmChannelListFragmentFactory.Companion.CRM_CHANNEL_OPERATOR_RESULT
import ru.tensor.sbis.communication_decl.crm.CrmChannelListFragmentFactory.Companion.CRM_CHANNEL_ORIGIN_ID
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.router.CRMAnotherOperatorRouter
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.store.CRMAnotherOperatorStore
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.store.CRMAnotherOperatorStoreFactory
import ru.tensor.sbis.communicator.design.R
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle
import ru.tensor.sbis.design_notification.popup.state_machine.util.DisplayDuration
import ru.tensor.sbis.mvi_extension.attachBinder
import ru.tensor.sbis.mvi_extension.provideStore
import ru.tensor.sbis.mvi_extension.router.navigator.WeakLifecycleNavigator
import java.util.UUID

/**
 * Контроллер, обеспечивающий связку компонентов Android с компонентами MVI.
 *
 * @author da.zhukov
 */
internal class CRMAnotherOperatorController @AssistedInject constructor(
    @Assisted private val fragment: Fragment,
    @Assisted viewFactory: (View) -> CRMAnotherOperatorView,
    private val crmAnotherOperatorStoreFactory: CRMAnotherOperatorStoreFactory
) : FragmentResultListener {

    private val store = fragment.provideStore { crmAnotherOperatorStoreFactory.create(it) }
    private val backButtonClick: () -> Unit = { router.onBackPressed() }
    private val router = CRMAnotherOperatorRouter()

    init {
        with(fragment) {
            attachBinder(BinderLifecycleMode.CREATE_DESTROY, viewFactory) { view ->
                bind {
                    view.events.map(::toIntent) bindTo store
                    store.states.map(::toModel) bindTo view
                    store.labels bindTo { it.consume() }
                }
            }
            router.attachNavigator(WeakLifecycleNavigator(this))
        }
    }

    private fun toIntent(event: CRMAnotherOperatorView.Event): CRMAnotherOperatorStore.Intent = when (event) {
        is CRMAnotherOperatorView.Event.EnterSearchQuery -> CRMAnotherOperatorStore.Intent.SearchQuery(event.query)
        is CRMAnotherOperatorView.Event.OnItemClick -> CRMAnotherOperatorStore.Intent.OnItemClick(event.operatorId)
        is CRMAnotherOperatorView.Event.BackButtonClick -> CRMAnotherOperatorStore.Intent.BackButtonClick
        is CRMAnotherOperatorView.Event.FilterClick -> CRMAnotherOperatorStore.Intent.FilterClick
    }

    private fun toModel(state: CRMAnotherOperatorStore.State) =
        CRMAnotherOperatorView.Model(
            query = state.query,
            filter = state.filter
        )

    private fun CRMAnotherOperatorStore.Label.consume() = when (this) {
        CRMAnotherOperatorStore.Label.BackButtonClick -> backButtonClick.invoke()
        CRMAnotherOperatorStore.Label.FilterClick -> router.openChannelsForFilter()
        CRMAnotherOperatorStore.Label.ShowNetworkError -> showNetworkError()
    }

    private fun showNetworkError() {
        SbisPopupNotification.push(
            SbisPopupNotificationStyle.ERROR,
            fragment.context?.getString(R.string.communicator_sync_error_message) ?: StringUtils.EMPTY,
            SbisMobileIcon.Icon.smi_WiFiNone.character.toString(),
            DisplayDuration.Default
        )
    }

    override fun onFragmentResult(requestKey: String, result: Bundle) {
        when (requestKey) {
            CRM_CHANNEL_OPERATOR_RESULT -> {
                val originUuid = result.getSerializableUniversally<UUID>(CRM_CHANNEL_ORIGIN_ID)
                val channelNameForFilter = result.getString(CRM_CHANNEL_NAME, StringUtils.EMPTY)
                fragment.lifecycleScope.launch {
                    store.accept(
                        CRMAnotherOperatorStore.Intent.ApplyFilter(originUuid, channelNameForFilter)
                    )
                }
            }
        }
    }
}