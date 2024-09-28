package ru.tensor.sbis.onboarding_tour.ui

import android.content.pm.PackageManager
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.tensor.sbis.mvi_extension.attachBinder
import ru.tensor.sbis.mvi_extension.provideStore
import ru.tensor.sbis.mvi_extension.router.buffer.BufferStatePolicy
import ru.tensor.sbis.mvi_extension.router.navigator.WeakLifecycleNavigator
import ru.tensor.sbis.onboarding_tour.ui.store.OnboardingTourStore
import ru.tensor.sbis.onboarding_tour.ui.store.OnboardingTourStore.*
import ru.tensor.sbis.onboarding_tour.ui.store.OnboardingTourStoreFactory
import ru.tensor.sbis.onboarding_tour.ui.TourView.*
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.PageCommand.*

/**
 * Посредник (Binder) между функциональностью бизнес логики [OnboardingTourStore] и UI [TourView].
 */
internal class TourController @AssistedInject constructor(
    @Assisted private val fragment: Fragment,
    @Assisted private val viewFactory: (View) -> TourView,
    private val router: TourRouter,
    private val storeFactory: OnboardingTourStoreFactory
) {
    private val store = fragment.provideStore(storeFactory::create)

    /** Лаунчер запроса пермишенов [ActivityResultContracts.RequestMultiplePermissions]. */
    private val permissionsRequest = fragment.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        store.accept(
            Intent.GrantedPermissions(
                position = store.state.pagePosition,
                notGranted = permissions.filter { !it.value }.keys.toList(),
                onRequest = true
            )
        )
    }

    init {
        bindViewToRouter()
        bindViewToStore()
        subscribeToLabels()
    }

    /**
     * Установить связь (binding) между роутером и вью.
     */
    private fun bindViewToRouter() = router.attachNavigator(
        WeakLifecycleNavigator(
            entity = fragment,
            bufferStatePolicy = BufferStatePolicy.ViewModel(ROUTER_STATE_KEY, fragment)
        )
    )

    /**
     * Установить связь (binding) между входными и выходными потоками данных с учетом жизненного цикла.
     */
    private fun bindViewToStore() {
        fragment.attachBinder(
            BinderLifecycleMode.CREATE_DESTROY,
            viewFactory
        ) { view ->
            bind {
                view.events.map(::eventToIntent).filterNotNull() bindTo store
                store.states.map(::stateToModel) bindTo view
            }
        }
    }

    /**
     * Подписаться на [Label].
     */
    private fun subscribeToLabels() = fragment.run {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                store.labels.onEach(::processLabel).collect()
            }
        }
    }

    /**
     * Метод конвертер события вью [TourView.Event] в намерения обрабатываемые [OnboardingTourStore].
     */
    private fun eventToIntent(event: Event): Intent? = when (event) {
        is Event.OnPageChanged -> {
            router.cancelCommandContinuation()
            Intent.OnPageChanged(event.position)
        }

        is Event.CheckPermissions -> Intent.GrantedPermissions(
            position = event.position,
            notGranted = getNotGrantedPermissions(event.permissions),
            onRequest = false
        )

        is Event.RequestPermissions -> requestPermissions(event)
        is Event.OnLinkClick -> router.openInBrowserApp(event.url).orNoIntent()
        is Event.OnCommandClick -> Intent.InitDeferredCommand(
            position = event.position,
            command = event.command,
            isLastPage = event.isLastPage
        )

        is Event.OnCloseClick -> {
            router.closeTour()
            Intent.OnCloseTour
        }
    }

    private fun processLabel(label: Label) {
        when (label) {
            is Label.InitiateCommand -> Intent.ObserveDeferredCommand(
                position = label.position,
                flow = label.command(fragment, label.byUser),
                isLastPage = label.isLastPage
            ).also(store::accept)

            is Label.PageCommandResult -> if (label.isLastPage && label.action.moveOn) {
                router.closeTour()
                store.accept(Intent.OnCloseTour)
            } else if (label.action != ResultantAction.NOTHING) {
                Intent.OnCommandPerformed(
                    position = label.position,
                    action = label.action
                ).also(store::accept)
            }
        }
    }

    /** Получить список не предоставленных разрешений. */
    private fun getNotGrantedPermissions(permissions: List<String>): List<String> =
        permissions.filter { permission ->
            ContextCompat.checkSelfPermission(
                fragment.requireActivity(),
                permission
            ) != PackageManager.PERMISSION_GRANTED
        }

    /** Выполнить запрос разрешений. */
    private fun requestPermissions(event: Event.RequestPermissions): Intent? {
        val notGrantedPermits = getNotGrantedPermissions(event.permissions)
        if (notGrantedPermits.isEmpty()) {
            return Intent.GrantedPermissions(event.position, emptyList(), true)
        } else {
            val simplePermits = notGrantedPermits.filter {
                !ActivityCompat.shouldShowRequestPermissionRationale(fragment.requireActivity(), it)
            }
            val rationalePermits = notGrantedPermits.filter {
                ActivityCompat.shouldShowRequestPermissionRationale(fragment.requireActivity(), it)
            }
            if (simplePermits.isNotEmpty() || (rationalePermits.isNotEmpty() && event.rationaleCommand != null)) {
                if (simplePermits.isNotEmpty()) {
                    permissionsRequest.launch(simplePermits.toTypedArray())
                }
                if (rationalePermits.isNotEmpty() && event.rationaleCommand != null) {
                    router.performRationale(rationalePermits, event.rationaleCommand) { reRequest ->
                        if (reRequest) {
                            permissionsRequest.launch(rationalePermits.toTypedArray())
                        } else {
                            Intent.GrantedPermissions(event.position, rationalePermits, true).let(store::accept)
                        }
                    }
                }
                return null
            } else {
                return Intent.GrantedPermissions(event.position, rationalePermits, true)
            }
        }
    }

    /**
     * Конвертер состояния [OnboardingTourStore] в состояние вью [TourView], вью-модель.
     */
    private fun stateToModel(state: State): Model = with(state) {
        Model(
            position = pagePosition,
            arePermissionsChecked = arePermissionsChecked,
            requirePermissions = requirePermissions,
            isTransitionBlocked = requirePermissions || requireCommand,
            isSwipeSupported = isSwipeSupported,
            isSwipeClosable = swipeCloseable,
            backgroundEffect = backgroundEffect,
            passages = passages.map(StatePassage::copy)
        )
    }

    @Suppress("unused")
    private fun Unit.orNoIntent() = null

    companion object {
        /**@SelfDocumented */
        const val ROUTER_STATE_KEY = "OnboardingRouter"
    }
}