package ru.tensor.sbis.message_panel.view

import org.mockito.kotlin.*
import io.reactivex.Observable
import junitparams.JUnitParamsRunner
import junitparams.custom.combined.CombinedParameters
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import ru.tensor.sbis.attachments.ui.view.register.AttachmentsViewVisibility
import ru.tensor.sbis.message_panel.helper.TextAreaHeightFunction
import ru.tensor.sbis.message_panel.viewModel.livedata.MessagePanelLiveData
import kotlin.random.Random

private const val ENOUGH_HEIGHT = Int.MAX_VALUE
private const val PARTIAL_HEIGHT = Int.MAX_VALUE / 2
private const val NOT_ENOUGH_HEIGHT = 0

/**
 * @author vv.chekurda
 * @since 12/17/2019
 */
@RunWith(JUnitParamsRunner::class)
class MessagePanelFitContentDelegateTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private lateinit var liveData: MessagePanelLiveData
    @Mock
    private lateinit var availabilityFunction: SpaceAvailabilityFunction
    @Mock
    private lateinit var heightFunction: TextAreaHeightFunction

    private lateinit var fitDelegate: MessagePanelFitContentDelegate

    @Before
    fun setUp() {
        fitDelegate = MessagePanelFitContentDelegate(availabilityFunction, heightFunction)
    }

    /**
     * Проверка отсутствия побочных эффектов: результат определяется только через [availabilityFunction]
     */
    @Test
    @CombinedParameters(
        "true,false",
        "true,false",
        "true,false",
        "$ENOUGH_HEIGHT,$PARTIAL_HEIGHT,$NOT_ENOUGH_HEIGHT"
    )
    fun `When message panel has enough space, then recipients and quote should be visible`(
        quotePanelVisible: Boolean,
        recipientsVisibility: Boolean,
        newDialogModeEnabled: Boolean,
        availableHeight: Int
    ) {
        val availability: SpaceForViewsAvailability = mock()

        whenever(liveData.quotePanelVisible).thenReturn(Observable.just(quotePanelVisible))
        whenever(liveData.recipientsVisibility).thenReturn(Observable.just(recipientsVisibility))
        whenever(
            availabilityFunction.apply(
                quotePanelVisible,
                recipientsVisibility,
                availableHeight,
                newDialogModeEnabled
            )
        )
            .thenReturn(availability)

        whenever(liveData.attachmentsVisibility).thenReturn(Observable.empty())
        whenever(liveData.newDialogModeEnabled).thenReturn(Observable.just(newDialogModeEnabled))

        fitDelegate.bind(liveData)
        fitDelegate.setPanelMaxHeight(availableHeight)

        verify(availabilityFunction, only()).apply(
            quotePanelVisible,
            recipientsVisibility,
            availableHeight,
            newDialogModeEnabled
        )

        verify(availability).hasSpaceForAttachments
        verify(availability).hasSpaceForRecipients

        verify(liveData).setHasSpaceForAttachments(any())
        verify(liveData).setHasSpaceForRecipients(any())
    }

    @Test
    @CombinedParameters(
        "true,false",
        "true,false",
        "true,false",
        "$ENOUGH_HEIGHT,$PARTIAL_HEIGHT,$NOT_ENOUGH_HEIGHT"
    )
    fun `When availability result isn't changed, then view model should receive it only once`(
        quotePanelVisible: Boolean,
        recipientsVisibility: Boolean,
        newDialogModeEnabled: Boolean,
        availableHeight: Int
    ) {
        val changedHeight = availableHeight - 1

        whenever(liveData.attachmentsVisibility).thenReturn(Observable.empty())
        whenever(liveData.quotePanelVisible).thenReturn(Observable.just(quotePanelVisible))
        whenever(liveData.recipientsVisibility).thenReturn(Observable.just(recipientsVisibility))
        whenever(liveData.newDialogModeEnabled).thenReturn(Observable.just(newDialogModeEnabled))
        whenever(
            availabilityFunction.apply(
                quotePanelVisible,
                recipientsVisibility,
                availableHeight,
                newDialogModeEnabled
            )
        )
            .thenReturn(mock())
        whenever(
            availabilityFunction.apply(
                quotePanelVisible,
                recipientsVisibility,
                changedHeight,
                newDialogModeEnabled
            )
        )
            .thenReturn(mock())

        fitDelegate.bind(liveData)
        fitDelegate.setPanelMaxHeight(availableHeight)
        // повторная установка
        fitDelegate.setPanelMaxHeight(availableHeight)
        // изменившиеся параметры
        fitDelegate.setPanelMaxHeight(changedHeight)

        // вызывается на каждое изменение
        verify(availabilityFunction, times(2)).apply(
            quotePanelVisible,
            recipientsVisibility,
            availableHeight,
            newDialogModeEnabled
        )
        verify(availabilityFunction).apply(quotePanelVisible, recipientsVisibility, changedHeight, newDialogModeEnabled)

        // вызывается только пори изменении результата
        verify(liveData, times(2)).setHasSpaceForAttachments(any())
        verify(liveData, times(2)).setHasSpaceForRecipients(any())
    }

    @Test
    @CombinedParameters(
        "GONE,PARTIALLY,VISIBLE",
        "true,false",
        "true,false",
        "true,false"
    )
    fun `When message panel max height changed, then height function should receive latest arguments`(
        attachmentsVisibility: String,
        quotePanelVisible: Boolean,
        recipientsVisibility: Boolean,
        newDialogModeEnabled: Boolean
    ) {
        val messagePanelMaxHeight = Random.nextInt(0, Int.MAX_VALUE)
        val visibility = AttachmentsViewVisibility.valueOf(attachmentsVisibility)

        whenever(liveData.attachmentsVisibility).thenReturn(Observable.just(visibility))
        whenever(liveData.quotePanelVisible).thenReturn(Observable.just(quotePanelVisible))
        whenever(liveData.recipientsVisibility).thenReturn(Observable.just(recipientsVisibility))
        whenever(liveData.newDialogModeEnabled).thenReturn(Observable.just(newDialogModeEnabled))

        whenever(availabilityFunction.apply(any(), any(), any(), any())).thenReturn(mock())
        // возвращается какое-то число меньше высоты панели. Деление на 2 для примера
        whenever(heightFunction.apply(any(), any(), any(), any())).thenReturn(messagePanelMaxHeight / 2)

        fitDelegate.bind(liveData)
        fitDelegate.setPanelMaxHeight(messagePanelMaxHeight)

        verify(heightFunction).apply(visibility, quotePanelVisible, recipientsVisibility, messagePanelMaxHeight)
    }
}