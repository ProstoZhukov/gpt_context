package ru.tensor.sbis.main_screen_common.viewmodel

import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.SerialDisposable
import ru.tensor.sbis.main_screen_common.interactor.MainScreenWidgetInteractor
import ru.tensor.sbis.verification_decl.permission.PermissionInfo
import ru.tensor.sbis.verification_decl.permission.PermissionScope
import timber.log.Timber

/**
 * Реализация [ViewModel] главного экрана.
 *
 * @property interactor интерактор.
 *
 * @author kv.martyshenko
 */
class MainScreenViewModel(
    private val interactor: MainScreenWidgetInteractor
) : ViewModel() {
    private val mutablePermissionsData = MutableLiveData<List<PermissionInfo>>()
    private var permissionSubscription = SerialDisposable()

    val permissionsData: LiveData<List<PermissionInfo>> = mutablePermissionsData

    /**
     * Метод для проверки прав на нужные области.
     *
     * @param scopes области, по которым нужна проверка прав.
     */
    @MainThread
    fun checkPermissions(scopes: Set<PermissionScope>) {
        if (scopes.isEmpty()) return

        val handleResult: (Result<List<PermissionInfo>>) -> Unit = { result ->
            result.getOrNull()?.let(mutablePermissionsData::setValue)
        }

        val subscription = interactor.listenPermissionChanges(scopes)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(handleResult, Timber::w)

        permissionSubscription.set(subscription)

        val previousScopes = mutablePermissionsData.value?.map { it.scope }
        if (previousScopes != null && previousScopes.containsAll(scopes)) return // полагаемся на обновление через RefreshCallback

        // Первую проверку делаем на MainThread, чтобы избежать дерганий элементов меню при старте.
        // Гарантируется, что вызов будет сделан к БД и вернет результаты быстро.
        val permissionsResult = kotlin.runCatching { interactor.checkPermissions(scopes) }
        handleResult(permissionsResult)
    }

    /**
     * Метод для обновления прав на нужные области.
     * Микросервис выполняет запрос на сервер раз в 10 минут и требует активной проверки извне.
     * Обращается синхронно к БД, результат отдается быстро.
     *
     * @param scopes области, по которым нужна проверка прав.
     */
    @AnyThread
    fun refreshPermissions(scopes: Set<PermissionScope>) {
        val permissionsResult = kotlin.runCatching { interactor.checkPermissions(scopes) }
        permissionsResult.getOrNull()?.let(mutablePermissionsData::postValue)
    }

    override fun onCleared() {
        permissionSubscription.dispose()
    }

}