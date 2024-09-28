package ru.tensor.sbis.sale_point_qr_binder.internal.screens.binder.widget

import com.arkivanov.essenty.statekeeper.StateKeeper
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import ru.tensor.sbis.barcode_decl.barcodereader.Barcode
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.mvi_extension.AndroidStoreFactory
import ru.tensor.sbis.mvi_extension.LabelBufferStrategy
import ru.tensor.sbis.network_native.apiservice.WorkerService
import ru.tensor.sbis.network_native.apiservice.contract.ApiService
import ru.tensor.sbis.sale_point_qr_binder.BuildConfig
import ru.tensor.sbis.sale_point_qr_binder.R
import ru.tensor.sbis.sale_point_qr_binder.internal.screens.binder.model.SalePointBindInfo
import ru.tensor.sbis.sale_point_qr_binder.internal.screens.binder.widget.SalePointQrBinderStore.Intent
import ru.tensor.sbis.sale_point_qr_binder.internal.screens.binder.widget.SalePointQrBinderStore.Label
import ru.tensor.sbis.sale_point_qr_binder.internal.screens.binder.widget.SalePointQrBinderStore.State
import timber.log.Timber

/**
 * Описывает действия ([Intent]), состояния ([State]) и сайд-эффекты ([Label]).
 *
 * @author kv.martyshenko
 */
internal interface SalePointQrBinderStore : Store<Intent, State, Label> {

    /** @SelfDocumented */
    sealed interface Intent {
        class BindQrCode(val code: Barcode) : Intent
        object TryAgain : Intent
    }

    /** @SelfDocumented */
    sealed interface Label {
        class BindQrFailureLabel(val error: PlatformSbisString) : Label
        object BindQrCompleted : Label
    }

    /** @SelfDocumented */
    data class State(
        val isBindingInProgress: Boolean = false,
        val isBindingAllowed: Boolean = true
    )

    companion object
}

private interface SalePointQrBinderBLContract {
    sealed interface Action

    sealed interface Message {
        object BindInProgress : Message
        object BindCompleted : Message
        object BindFailed : Message
        object BindRetry : Message
    }

    class Executor(
        private val bindInfo: SalePointBindInfo,
        private val apiServiceProvider: ApiService.Provider
    ) : CoroutineExecutor<Intent, Action, State, Message, Label>() {

        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.BindQrCode -> {
                    val state = getState()
                    if (!state.isBindingAllowed) return

                    dispatch(Message.BindInProgress)

                    scope.launch {
                        val result = performBindRequest(intent.code.displayValue)

                        if (result.isSuccess) {
                            val apiResult = result.getOrThrow()
                            if (apiResult.isSuccess) {
                                dispatch(Message.BindCompleted)
                                publish(Label.BindQrCompleted)
                            } else {
                                handleError(
                                    PlatformSbisString.Value(
                                        apiResult.error.getString(
                                            WorkerService.EXTRA_ERROR_BODY_MESSAGE_DETAILS,
                                            "Unknown error"
                                        )
                                    )
                                )
                            }
                        } else {
                            val error = result.exceptionOrNull()!!

                            val errorMessage = if (BuildConfig.DEBUG) {
                                PlatformSbisString.Value(error.localizedMessage ?: error.message!!)
                            } else {
                                Timber.e(error)
                                PlatformSbisString.Res(R.string.spqrb_error_internal_bind_failure)
                            }
                            handleError(errorMessage)
                        }

                    }
                }

                Intent.TryAgain -> {
                    dispatch(Message.BindRetry)
                }
            }
        }

        private fun handleError(error: PlatformSbisString) {
            dispatch(Message.BindFailed)
            publish(Label.BindQrFailureLabel(error))
        }

        private suspend fun performBindRequest(code: String) = withContext(Dispatchers.IO) {
            kotlin.runCatching {
                val (service, rawParams) = bindInfo.bindUrl.split("?")

                apiServiceProvider.apiService()
                    .requestRaw(
                        buildRequestData(code, rawParams),
                        service
                    )
            }
        }

        private fun buildRequestData(code: String, externalParams: String): RequestBody {
            val requestBody = JSONObject()

            val params = externalParams.split("&")
            params.map { it.split("=") }
                .forEach { (key, value) ->
                    if (value.isNotBlank()) {
                        requestBody.put(key, value)
                    } else {
                        val bindParams = JSONObject().apply {
                            put("SalePoint", bindInfo.salePointId.toLongOrNull() ?: JSONObject.NULL)
                            put("Identifier", code)
                            put("Site", bindInfo.site ?: JSONObject.NULL)
                            put("HallKind", bindInfo.objectType ?: JSONObject.NULL)
                            put("ObjectId", bindInfo.objectIdentifier?.toIntOrNull() ?: JSONObject.NULL)
                            put("IsLink", true)
                            put("Hall", bindInfo.hall ?: JSONObject.NULL)
                        }
                        requestBody.put(key, bindParams)
                    }
                }

            return requestBody.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaType())
        }

    }

    class Reducer : com.arkivanov.mvikotlin.core.store.Reducer<State, Message> {

        override fun State.reduce(msg: Message): State {
            return when (msg) {
                Message.BindCompleted -> copy(
                    isBindingInProgress = false,
                    isBindingAllowed = false
                )

                is Message.BindFailed -> copy(
                    isBindingInProgress = false,
                    isBindingAllowed = false
                )

                Message.BindInProgress -> copy(
                    isBindingInProgress = true,
                    isBindingAllowed = false
                )

                Message.BindRetry -> copy(
                    isBindingInProgress = false,
                    isBindingAllowed = true
                )
            }
        }

    }
}

/**
 * Метод для создания [SalePointQrBinderStore].
 *
 * @param salePointId идентификатор точки продаж
 * @param link ссылка для обратной отправки
 * @param stateKeeper
 * @param apiServiceProvider
 */
internal fun SalePointQrBinderStore.Companion.create(
    bindInfo: SalePointBindInfo,
    stateKeeper: StateKeeper,
    apiServiceProvider: ApiService.Provider
): SalePointQrBinderStore {
    val labelBufferSize = 1 // наличие большого количества Label (> 1), считаем логической ошибкой раелизации Store.
    val storeFactory = AndroidStoreFactory(
        DefaultStoreFactory(),
        LabelBufferStrategy.Buffer(labelBufferSize)
    )
    return object :
        SalePointQrBinderStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "SalePointQrBinderStore#${bindInfo.salePointId}",
            initialState = State(),
            bootstrapper = SimpleBootstrapper(),
            executorFactory = {
                SalePointQrBinderBLContract.Executor(bindInfo, apiServiceProvider)
            },
            reducer = SalePointQrBinderBLContract.Reducer()
        ) {}
}