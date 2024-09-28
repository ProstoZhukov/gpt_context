package ru.tensor.sbis.mvi_extension.permission

import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ru.tensor.sbis.mvi_extension.attachRxJavaBinder
import ru.tensor.sbis.mvi_extension.provideStore
import ru.tensor.sbis.mvi_extension.rx.observableLabels

/**
 * Created by Aleksey Boldinov on 31.08.2022.
 */
internal class PermissionsController(
    private val activity: AppCompatActivity,
    private val permissionsStoreFactory: PermissionsStoreFactory
) {
    private val permissionsStore = activity.provideStore {
        permissionsStoreFactory.create()
    }
    private val locationPermissionRequest = activity.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissionsStore
            .accept(PermissionsStore.Intent.GrantPermissions(permissions.filter { it.value }.keys.toList()))
    }

    init {
        activity.attachRxJavaBinder {
            permissionsStore.observableLabels()
                .bind {
                    when (it) {
                        is PermissionsStore.Label.RequestedPermission -> {
                            val notGrantedPermissions = it.permissions.filter { permission ->
                                ContextCompat.checkSelfPermission(
                                    activity,
                                    permission
                                ) != PackageManager.PERMISSION_GRANTED
                            }
                            if (notGrantedPermissions.isEmpty())
                                permissionsStore.accept(
                                    PermissionsStore.Intent.GrantPermissions(
                                        it.permissions
                                    )
                                )
                            else
                                requestPermissions(notGrantedPermissions)
                        }

                        is PermissionsStore.Label.RequestRationalePermissions ->
                            locationPermissionRequest.launch(it.permissions.toTypedArray())

                        else -> {}
                    }
                }
        }
    }

    private fun requestPermissions(notGrantedPermissions: List<String>) {
        val requestRationalePermissions = notGrantedPermissions.filter {
            ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
        }
        val simplePermissions = notGrantedPermissions.filter {
            !ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
        }
        if (requestRationalePermissions.isNotEmpty()) {
            permissionsStore.accept(PermissionsStore.Intent.ShowRationalePermissionsDialog(requestRationalePermissions))
        }
        if (simplePermissions.isNotEmpty()) {
            locationPermissionRequest.launch(simplePermissions.toTypedArray())
        }
    }
}