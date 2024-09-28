package ru.tensor.sbis.manage_features.presentation

import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.Observable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.verification_decl.account.PersonalAccount
import ru.tensor.sbis.verification_decl.account.UserAccount
import ru.tensor.sbis.manage_features.R
import ru.tensor.sbis.manage_features.data.GetValueInteractor
import ru.tensor.sbis.manage_features.data.DataSource

internal class ManageFeaturesViewModel(
    private val userID: Int,
    private val clientID: Int,
    nameOfUser: String,
    nameOfClient: String,
    private val interactor: GetValueInteractor
) : ViewModel() {

    private val disposable = SerialDisposable()
    private val _errors: PublishSubject<String> = PublishSubject.create()

    /** Observable излучающий строки ошибок */
    val errors: Observable<String> = _errors

    /** Имя пользователя */
    val userName = ObservableField(nameOfUser)

    /** Имя клиента (компании) */
    val clientName = ObservableField(nameOfClient)

    /** Идентификатор названия функционала */
    val featureName = ObservableField(DataSource.DEFAULT_FEATURE_NAME)

    /** Значение функционала */
    val value = ObservableField("")

    /** Идентификатор ресурса строки состояния функционала */
    val state = ObservableInt()

    /** Слушатель клика по галке в тулбаке */
    val checkClickListener = View.OnClickListener { getValue() }


    /**
     * Очистка ресурсов
     */
    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

    private fun getValue() {
        disposable.set(interactor.getValueWithCheck(featureName.get() ?: "", userID, clientID).subscribe(
            {
                state.set(R.string.manage_features_state_enabled)
                value.set(it)
            },
            {
                value.set("")
                state.set(R.string.manage_features_state_disabled)
                _errors.onNext(it.localizedMessage ?: "")
            }
        ))
    }

    /**@SelfDocumented */
    class Factory(
        private val currentAccount: UserAccount?,
        private val currentPersonalAccount: PersonalAccount,
        private val interactor: GetValueInteractor
    ) : ViewModelProvider.Factory {

        /**@SelfDocumented */
        @Suppress("UNCHECKED_CAST")
        override fun <VIEW_MODEL : ViewModel> create(modelClass: Class<VIEW_MODEL>): VIEW_MODEL {
            return ManageFeaturesViewModel(
                currentAccount?.userId ?: 0,
                currentAccount?.clientId ?: 0,
                "${currentAccount?.userName} ${currentAccount?.userSurname}",
                currentPersonalAccount.company ?: "",
                interactor
            ) as VIEW_MODEL
        }
    }
}