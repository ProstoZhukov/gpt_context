package ru.tensor.sbis.main_screen.widget.interactor

import androidx.annotation.AnyThread
import androidx.annotation.WorkerThread
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.permission.generated.DataRefreshedCallback
import ru.tensor.sbis.permission.generated.PermissionService
import ru.tensor.sbis.platform.generated.Subscription
import ru.tensor.sbis.verification_decl.permission.PermissionChecker
import ru.tensor.sbis.verification_decl.permission.PermissionInfo
import ru.tensor.sbis.verification_decl.permission.PermissionRequestException
import ru.tensor.sbis.verification_decl.permission.PermissionScope

/**
 * Интерактор главного экрана.
 *
 * @property permissionChecker
 *
 * @author kv.martyshenko
 */
internal class MainScreenWidgetInteractor(
    private val permissionChecker: PermissionChecker
) {
    private val permissionService: DependencyProvider<PermissionService> =
        DependencyProvider.create { PermissionService.instance() }

    /**
     * Метод для получения информации по правам на переданные области.
     *
     * @param scopes области
     */
    @WorkerThread
    @Throws(PermissionRequestException::class)
    fun checkPermissions(scopes: Set<PermissionScope>): List<PermissionInfo> {
        return permissionChecker.checkPermissionsNow(scopes.toList())
    }

    /**
     * Метод для получения событий при изменении прав.
     *
     * Подписка и обновление информации происходят на [Schedulers.io()],
     * а получение итогового результата будет зависеть от финального [Flowable#observeOn()].
     * Если в явном виде при вызове данного метода переключения потока не будет,
     * то по умолчанию [Flowable#subscribe()] отработает на [Schedulers.io()].
     *
     * @param scopes области
     */
    @AnyThread
    fun listenPermissionChanges(scopes: Set<PermissionScope>): Flowable<Result<List<PermissionInfo>>> {
        return Flowable
            .create<Unit>(
                { emitter ->
                    val serializedEmitter = emitter.serialize()

                    var subscription: Subscription? =
                        permissionService.get().dataRefreshed().subscribe(object : DataRefreshedCallback() {
                            override fun onEvent() {
                                if (!serializedEmitter.isCancelled)
                                    serializedEmitter.onNext(Unit)
                            }
                        })

                    serializedEmitter.setCancellable {
                        subscription?.disable()
                        subscription = null
                    }
                },
                BackpressureStrategy.LATEST
            )
            .subscribeOn(Schedulers.io())
            // нужно переключить поток, так как контроллер отправит нам событие на одном из своих потоков,
            // и привяться к начальному io-потоку не получится.
            .observeOn(Schedulers.io())
            .map { kotlin.runCatching { checkPermissions(scopes) } }
    }

}