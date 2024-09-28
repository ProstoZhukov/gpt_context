package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.list

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.entity.screen.ReadStatusScreenEntity
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.reload.ReadStatusListUpdateActions
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.live_data.ReadStatusListVMLiveData
import ru.tensor.sbis.list.base.domain.boundary.View
import ru.tensor.sbis.list.base.presentation.ListScreenVM
import ru.tensor.sbis.list.base.presentation.ListScreenVMImpl

/**
 * Реализация вью-модели секции списка статусов прочитанности сообщения
 * @see ReadStatusListScreenVM
 *
 * @property listScreenVM  вью-модель компонента списка
 * @property liveData      параметры состояния
 * @property updateActions действия для вызовов обновлениея списка
 * @property networkUtils  утилиты для работы с состоянием сети
 *
 * @author vv.chekurda
 */
internal class ReadStatusListScreenVMImpl(
    private val listScreenVM: ListScreenVMImpl<ReadStatusScreenEntity>,
    private val liveData: ReadStatusListVMLiveData,
    private val updateActions: ReadStatusListUpdateActions,
    private val networkUtils: NetworkUtils
) : ViewModel(),
    ReadStatusListScreenVM,
    ListScreenVM by listScreenVM,
    View<ReadStatusScreenEntity> by listScreenVM {

    private val disposer = CompositeDisposable()
    private var networkSubscription: Disposable? = null

    init {
        subscribeOnNetworkErrors()
    }

    /**
     * Подписка на события ошибок сети
     */
    private fun subscribeOnNetworkErrors() {
        liveData.networkErrorObservable
            .subscribe{ subscribeOnNetworkConnection() }
            .storeIn(disposer)
    }

    /**
     * Подписка на ожидание появления сети для обновления списка
     */
    private fun subscribeOnNetworkConnection() {
        if (!networkUtils.isConnected) {
            // Если сети на данный момент нет и еще не подписаны - запускаем ожидание соединения
            if (networkSubscription?.isDisposed != false) {
                networkSubscription =
                    networkUtils.networkStateObservable()
                        .mergeWith(networkUtils.networkStateObservable())
                        .subscribe {
                            if (networkUtils.isConnected) { // проверяем явно, т.к критично отписаться сразу
                                updateActions.onNetworkConnected()
                                networkSubscription?.dispose()
                            }
                        }
            }
        } else {
            networkSubscription?.dispose()
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposer.dispose()
        networkSubscription?.dispose()
    }
}