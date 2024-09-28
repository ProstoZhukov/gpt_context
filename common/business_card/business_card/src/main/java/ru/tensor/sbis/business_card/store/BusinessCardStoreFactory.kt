package ru.tensor.sbis.business_card.store

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import ru.tensor.sbis.business_card.contract.internal.BusinessCardStore
import ru.tensor.sbis.business_card.contract.internal.BusinessCardStore.Intent
import ru.tensor.sbis.business_card.contract.internal.BusinessCardStore.Label
import ru.tensor.sbis.business_card.contract.internal.BusinessCardStore.State
import ru.tensor.sbis.business_card_host_decl.ui.model.BusinessCard
import ru.tensor.sbis.mvi_extension.AndroidStoreFactory
import javax.inject.Inject

/** Фабрика создающая [BusinessCardStore], также здесь определяется логика обработки всех событий */
internal class BusinessCardStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory
) {

    /** Метод создания store */
    fun create(): BusinessCardStore =
        object : BusinessCardStore,
            Store<Intent, State, Label> by AndroidStoreFactory(storeFactory).create(
                name = "BusinessCardStoreFactory",
                initialState = State,
                executorFactory = { ExecutorImpl() },
                reducer = { _ -> this }
            ) {}

    /**@SelfDocumented*/
    internal class ExecutorImpl : CoroutineExecutor<Intent, Unit, State, Unit, Label>() {

        override fun executeIntent(intent: Intent, getState: () -> State) = intent.handle(this)

        /**@SelfDocumented*/
        internal fun back() = publish(Label.NavigateBack)

        /**@SelfDocumented*/
        internal fun toShareLink(params: BusinessCard) = publish(Label.NavigateToShareLink(params))
    }
}