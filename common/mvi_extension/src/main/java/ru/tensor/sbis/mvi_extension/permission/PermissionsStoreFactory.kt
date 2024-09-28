package ru.tensor.sbis.mvi_extension.permission

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor

/**
 * Created by Aleksey Boldinov on 31.08.2022.
 */
internal class PermissionsStoreFactory(
    private val storeFactory: StoreFactory
) {

    fun create(): PermissionsStore =
        object : PermissionsStore, Store<PermissionsStore.Intent, Unit, PermissionsStore.Label> by storeFactory.create(
            name = "PermissionsStore",
            initialState = Unit,
            executorFactory = PermissionsStoreFactory::ExecutorImpl,
        ) {}


    private class ExecutorImpl :
        CoroutineExecutor<PermissionsStore.Intent, Unit, Unit, Unit, PermissionsStore.Label>() {
        override fun executeIntent(intent: PermissionsStore.Intent, getState: () -> Unit) {
            when (intent) {
                is PermissionsStore.Intent.RequestPermissions ->
                    publish(PermissionsStore.Label.RequestedPermission(intent.permissions))

                is PermissionsStore.Intent.GrantPermissions -> publish(
                    PermissionsStore.Label.PermissionsGranted(
                        intent.permissions
                    )
                )

                is PermissionsStore.Intent.ShowRationalePermissionsDialog ->
                    publish(PermissionsStore.Label.ShowRationalePermissionsDialog(intent.permissions))

                is PermissionsStore.Intent.PositiveRationalePermissionRequest ->
                    publish(PermissionsStore.Label.RequestRationalePermissions(intent.permissions))
            }
        }
    }

}
