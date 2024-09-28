package ru.tensor.sbis.onboarding_tour.ui.store

import android.content.Context
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arkivanov.mvikotlin.core.store.Executor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.*
import org.robolectric.annotation.Config
import ru.tensor.sbis.onboarding_tour.contract.OnboardingTourDependency
import ru.tensor.sbis.onboarding_tour.data.TourContent
import ru.tensor.sbis.onboarding_tour.data.storage.TourStorage
import ru.tensor.sbis.onboarding_tour.domain.TourCreatorImpl
import ru.tensor.sbis.onboarding_tour.domain.TourInteractor
import ru.tensor.sbis.verification_decl.onboarding_tour.DevicePerformanceProvider
import ru.tensor.sbis.verification_decl.onboarding_tour.OnboardingTourProvider.Companion.DEFAULT_NAME
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.PageCommand
import ru.tensor.sbis.verification_decl.onboarding_tour.data.BackgroundEffect

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.P]
)
internal class TourExecutorTest {

    private val testDispatcher = StandardTestDispatcher(TestCoroutineScheduler())

    private val mockContext: Context = mock()
    private val mockDependency: OnboardingTourDependency = mock {
        on { loginInterface } doAnswer { null }
    }
    private val tourCreator = TourCreatorImpl(mockContext, mockDependency)
    private val mockTourStorage = mock<TourStorage> {
        on { getTour(any()) } doAnswer { tourContent }
    }
    private val mockDevicePerformanceProvider = mock<DevicePerformanceProvider>()
    private val spyTourInteractor = spy(TourInteractor(mockTourStorage, mockDevicePerformanceProvider, mock()))
    private val executor = TourExecutor(DEFAULT_NAME, spyTourInteractor, testDispatcher)
    private val mockCallbacks: Executor.Callbacks<OnboardingTourStore.State, Message, OnboardingTourStore.Label> =
        mock {
            on { state } doAnswer { mockState }
        }
    private lateinit var tourContent: TourContent
    private var mockState: OnboardingTourStore.State = mock()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        executor.init(mockCallbacks)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `On init with execute LoadTour load content`() = runTest {
        tourContent = tourCreator.create {
            page { }
            page { }
        } as TourContent
        executor.executeAction(Action.LoadTour(DEFAULT_NAME))
        advanceUntilIdle()

        verify(spyTourInteractor).getContent(DEFAULT_NAME)
        verify(mockCallbacks).onMessage(
            argThat {
                this is Message.UpdateTour && this.pageCount == 2 && this.pagePosition == 0
            }
        )
        verify(spyTourInteractor).markShown(eq(DEFAULT_NAME))
    }

    @Test
    fun `On init with execute LoadTour and animation check device hardware`() = runTest {
        tourContent = tourCreator.create {
            rules {
                backgroundEffect = BackgroundEffect.DYNAMIC
            }
            page { }
            page { }
        } as TourContent
        executor.executeAction(Action.LoadTour(DEFAULT_NAME))
        advanceUntilIdle()

        executor.executeIntent(OnboardingTourStore.Intent.ShowBackgroundEffect)
        verify(spyTourInteractor).isAnimated()
        verify(mockCallbacks).onMessage(Message.ShowAnimation)
    }

    @Test
    fun `On init with execute RestoreTour load content`() = runTest {
        tourContent = tourCreator.create {
            page { }
            page { }
            page { }
        } as TourContent
        executor.executeAction(Action.RestoreTour(DEFAULT_NAME, OnboardingTourStore.State(pageId = 1)))
        advanceUntilIdle()

        verify(spyTourInteractor).getContent(DEFAULT_NAME, 1)
        verify(mockCallbacks).onMessage(
            argThat {
                this is Message.UpdateTour && this.pageCount == 3 && this.pagePosition == 1
            }
        )
    }

    @Test
    fun `On page changed call Message_MoveToPosition`() = runTest {
        tourContent = tourCreator.create {
            page { }
            page { }
            page { }
        } as TourContent
        executor.executeAction(Action.LoadTour(DEFAULT_NAME))
        advanceUntilIdle()
        clearInvocations(mockCallbacks)

        executor.executeIntent(
            OnboardingTourStore.Intent.OnPageChanged(1)
        )
        verify(mockCallbacks).onMessage(eq(Message.MoveToPosition(1)))
    }

    @Test
    fun `When permission is granted, go to next page`() = runTest {
        tourContent = tourCreator.create {
            page { }
            page { }
            page { }
        } as TourContent
        mockState = createState(true)
        executor.executeAction(Action.LoadTour(DEFAULT_NAME))
        advanceUntilIdle()
        clearInvocations(mockCallbacks)

        executor.executeIntent(
            OnboardingTourStore.Intent.GrantedPermissions(0, emptyList(), true)
        )
        verify(mockCallbacks).onMessage(eq(Message.MoveToPosition(1)))
    }

    @Test
    fun `When mandatory permission is not granted, do not go to next page`() = runTest {
        tourContent = tourCreator.create {
            page { }
            page { }
            page { }
        } as TourContent
        mockState = createState(true)
        executor.executeAction(Action.LoadTour(DEFAULT_NAME))
        advanceUntilIdle()
        clearInvocations(mockCallbacks)

        executor.executeIntent(
            OnboardingTourStore.Intent.GrantedPermissions(0, listOf("permit"), true)
        )
        verify(mockCallbacks).onMessage(
            argThat {
                this is Message.GrantedPermissions && this.haveNotGranted
            }
        )
    }

    @Test
    fun `When optional permission is not granted, whatever go to next page anyway`() = runTest {
        tourContent = tourCreator.create {
            page { }
            page { }
            page { }
        } as TourContent
        mockState = createState(false)
        executor.executeAction(Action.LoadTour(DEFAULT_NAME))
        advanceUntilIdle()
        clearInvocations(mockCallbacks)

        executor.executeIntent(
            OnboardingTourStore.Intent.GrantedPermissions(0, listOf("permit"), true)
        )
        verify(mockCallbacks).onMessage(eq(Message.MoveToPosition(1)))
    }

    @Test
    fun `On OnCommandPerformed, move to the next page if possible`() = runTest {
        tourContent = tourCreator.create {
            page { }
            page { }
        } as TourContent
        mockState = createState(false)
        executor.executeAction(Action.LoadTour(DEFAULT_NAME))
        advanceUntilIdle()

        executor.executeIntent(
            OnboardingTourStore.Intent.OnCommandPerformed(0, PageCommand.ResultantAction.GO_AHEAD)
        )
        verify(mockCallbacks).onMessage(eq(Message.MoveToPosition(1)))

        clearInvocations(mockCallbacks)
        executor.executeIntent(
            OnboardingTourStore.Intent.OnCommandPerformed(0, PageCommand.ResultantAction.NOTHING)
        )
        verify(mockCallbacks, never()).onMessage(any())
    }

    @Test
    fun `On ObserveCommand result, publish page command result`() = runTest {
        tourContent = tourCreator.create {
            page { }
        } as TourContent
        executor.executeAction(Action.LoadTour(DEFAULT_NAME))
        advanceUntilIdle()

        val commandFlow = MutableSharedFlow<PageCommand.ResultantAction>()
        executor.executeIntent(
            OnboardingTourStore.Intent.ObserveDeferredCommand(0, commandFlow, true)
        )
        advanceUntilIdle()
        verify(mockCallbacks, never()).onLabel(any())

        commandFlow.emit(PageCommand.ResultantAction.GO_AHEAD)
        verify(mockCallbacks).onLabel(
            eq(
                OnboardingTourStore.Label.PageCommandResult(
                    position = 0,
                    action = PageCommand.ResultantAction.GO_AHEAD,
                    isLastPage = true
                )
            )
        )
    }

    private fun createState(isMandatory: Boolean = true): OnboardingTourStore.State = mock {
        on { passages } doAnswer {
            listOf(
                mock {
                    on { isMandatoryPermits } doReturn isMandatory
                },
                mock {
                    on { isMandatoryPermits } doReturn isMandatory
                }
            )
        }
    }
}