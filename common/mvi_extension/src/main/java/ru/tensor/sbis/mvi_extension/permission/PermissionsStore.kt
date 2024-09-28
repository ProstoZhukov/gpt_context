package ru.tensor.sbis.mvi_extension.permission

import com.arkivanov.mvikotlin.core.store.Store

/**
 * Created by Aleksey Boldinov on 31.08.2022.
 */
interface PermissionsStore : Store<PermissionsStore.Intent, Unit, PermissionsStore.Label> {

    sealed interface Intent {
        class RequestPermissions(val permissions: List<String>) : Intent
        class GrantPermissions(val permissions: List<String>) : Intent
        class ShowRationalePermissionsDialog(val permissions: List<String>) : Intent
        class PositiveRationalePermissionRequest(val permissions: List<String>) : Intent
    }

    sealed interface Label {
        class RequestedPermission(val permissions: List<String>) : Label
        class PermissionsGranted(val permissions: List<String>) : Label
        class ShowRationalePermissionsDialog(val permissions: List<String>) : Label
        class RequestRationalePermissions(val permissions: List<String>) : Label
    }
}
