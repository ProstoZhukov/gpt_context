package ru.tensor.sbis.share_menu.ui.store

import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import ru.tensor.sbis.share_menu.utils.base_mvi.executor.SimpleExecutor
import ru.tensor.sbis.share_menu.utils.base_mvi.reducer.SimpleReducer
import ru.tensor.sbis.share_menu.ui.view.ShareMenuContentDelegate
import ru.tensor.sbis.share_menu.ui.store.domain.executor.Intent
import ru.tensor.sbis.share_menu.ui.store.domain.executor.ShareMenuInitAction
import ru.tensor.sbis.share_menu.ui.store.domain.Label
import ru.tensor.sbis.share_menu.ui.store.domain.State
import ru.tensor.sbis.toolbox_decl.share.ShareData
import javax.inject.Inject

/**
 * MVI-Store компонента меню для "поделиться".
 *
 * @author vv.chekurda
 */
internal interface ShareMenuStore : Store<Intent, State, Label> {

    /**
     * Делегат контента для управления состоянием меню.
     */
    val menuContentDelegate: ShareMenuContentDelegate

    /**
     * Фабрика для создания MVI-Store.
     *
     * @property storeFactory фабрика сторов.
     * @property initAction инициализирующее действие.
     * @property shareData данные, которыми пользователь делится.
     */
    class Factory @Inject constructor(
        private val storeFactory: StoreFactory,
        private val initAction: ShareMenuInitAction,
        private val shareData: ShareData
    ) {

        fun create(): ShareMenuStore =
            object : ShareMenuStore,
                Store<Intent, State, Label> by storeFactory.create(
                    name = SHARE_MENU_STORE_NAME,
                    initialState = State(shareData),
                    bootstrapper = SimpleBootstrapper(initAction),
                    executorFactory = ::SimpleExecutor,
                    reducer = SimpleReducer()
                ) {

                override val menuContentDelegate = ShareMenuContentDelegate(this)
            }
    }
}

private const val SHARE_MENU_STORE_NAME = "ShareMenuStore"