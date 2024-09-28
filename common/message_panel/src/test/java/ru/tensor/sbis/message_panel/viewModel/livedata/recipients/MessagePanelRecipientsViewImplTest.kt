package ru.tensor.sbis.message_panel.viewModel.livedata.recipients

import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.common.rx.RxContainer
import ru.tensor.sbis.design.message_panel.decl.recipients.RecipientsView.RecipientsViewData
import ru.tensor.sbis.design.message_panel.decl.recipients.data.RecipientItem
import ru.tensor.sbis.design.message_panel.decl.recipients.data.RecipientPersonItem
import ru.tensor.sbis.message_panel.interactor.recipients.MessagePanelRecipientsInteractor
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel
import ru.tensor.sbis.message_panel.viewModel.livedata.MessagePanelLiveData
import java.util.*

/**
 * @author vv.chekurda
 * Создан 10/8/2019
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class MessagePanelRecipientsViewImplTest {

    @Mock
    private lateinit var conversationUuid: UUID

    @Mock
    private lateinit var configFactory: RecipientSelectionConfigFactory

    @Mock
    private lateinit var interactor: MessagePanelRecipientsInteractor

    @Mock
    private lateinit var liveData: MessagePanelLiveData

    @Mock
    private lateinit var vm: MessagePanelViewModel<*, *, *>

    private lateinit var recipientsView: MessagePanelRecipientsView

    @Before
    fun setUp() {
        Mockito.lenient().`when`(liveData.hasSpaceForRecipients).thenReturn(Observable.just(true))
        Mockito.lenient().`when`(liveData.recipientsFeatureEnabled).thenReturn(Observable.just(true))
        whenever(vm.liveData).thenReturn(liveData)

        recipientsView = MessagePanelRecipientsViewImpl(
            viewModel = vm,
            selectionConfigFactory = configFactory,
            observeOn = Schedulers.single()
        )
    }

    @Test
    fun `Recipients panel visible by default`() {
        val visibilityObserver = recipientsView.recipientsVisibility.test()

        visibilityObserver.assertValue(true)
    }

    @Test
    fun `Recipients panel invisible if it force hidden`() {
        whenever(liveData.hasSpaceForRecipients).thenReturn(Observable.just(true))
        val visibilityObserver = recipientsView.recipientsVisibility.test()

        recipientsView.forceHideRecipientsPanel(true)

        visibilityObserver.awaitCount(2)
        assertFalse(visibilityObserver.values().last())
    }

    @Test
    fun `Recipients panel invisible if there is no space for it`() {
        whenever(liveData.hasSpaceForRecipients).thenReturn(Observable.just(false))
        val visibilityObserver = recipientsView.recipientsVisibility.test()

        visibilityObserver.assertValue(false)
    }

    @Test
    fun `Recipients panel invisible if it not hidden force but recipients visibility hide it`() {
        val visibilityObserver = recipientsView.recipientsVisibility.test()

        recipientsView.setRecipientsPanelVisibility(false)

        visibilityObserver.awaitCount(2)
        assertFalse(visibilityObserver.values().last())
    }

    //region Fix https://online.sbis.ru/opendoc.html?guid=86b36ec5-b381-4b97-a256-e6ef590a51e7
    @Test
    fun `When recipients view initialised, then it return one view data`() {
        // переход при получении идентификатора обсуждения null -> uuid
        whenever(liveData.conversationUuid).thenReturn(Observable.fromArray(RxContainer(null), RxContainer(conversationUuid)))
        whenever(liveData.isRecipientsHintEnabled).thenReturn(Observable.just(true))
        whenever(liveData.requireCheckAllMembers).thenReturn(Observable.just(true))
        whenever(vm.recipientsInteractor).thenReturn(interactor)
        // начальное состояние -> загружен пустой список
        whenever(liveData.recipients).thenReturn(Observable.fromArray(emptyList(), emptyList()))

        val data = recipientsView.recipientsViewData.test()

        data.await().assertValue(RecipientsViewData())
    }

    @Test
    fun `Given recipient list, when new one receive, then it should be ignored if it's content is equal`() {
        val recipient: RecipientItem = mock<RecipientPersonItem>()
        whenever(liveData.conversationUuid).thenReturn(Observable.just(RxContainer(conversationUuid)))
        whenever(liveData.isRecipientsHintEnabled).thenReturn(Observable.just(true))
        whenever(liveData.requireCheckAllMembers).thenReturn(Observable.just(true))
        whenever(vm.recipientsInteractor).thenReturn(interactor)
        // начальное состояние -> загружен пустой список
        whenever(liveData.recipients).thenReturn(Observable.fromArray(listOf(recipient), listOf(recipient)))

        val data = recipientsView.recipientsViewData.test()

        data.await().assertValue(RecipientsViewData(listOf(recipient)))
    }
    //endregion
}