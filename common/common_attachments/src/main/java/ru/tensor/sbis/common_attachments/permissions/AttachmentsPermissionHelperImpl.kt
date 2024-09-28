package ru.tensor.sbis.common_attachments.permissions

import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.disposables.SerialDisposable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import ru.tensor.sbis.common_attachments.AttachmentView

/**
 * @author sa.nikitin
 */
internal class AttachmentsPermissionHelperImpl : AttachmentsPermissionHelper {

    private val requestPermissionSerialDisposable: SerialDisposable = SerialDisposable()
    private val permissionsToRequest: MutableList<String> = mutableListOf()
    private val permissionsRequestResult: Subject<List<String>> = PublishSubject.create()

    override fun addPermissionsToRequest(permission: String) {
        permissionsToRequest.add(permission)
    }

    override fun requestPendingPermissions(view: AttachmentView) {
        if (permissionsToRequest.isNotEmpty()) {
            val notGrantedPermissions = view.getNotGrantedPermissions(permissionsToRequest)
            permissionsToRequest.clear()

            if (notGrantedPermissions.isNotEmpty()) {
                view.requestPermissions(notGrantedPermissions)
            }
        }
    }

    override fun withPermissions(
        view: AttachmentView,
        permissions: List<String>,
        onPermissionResult: (isGranted: Boolean) -> Unit
    ) {
        requestPermissionSerialDisposable.set(checkPermissions(view, permissions,
            onPermissionResult
        ))
    }

    override fun onPermissionsGranted(grantedPermissions: List<String>) {
        permissionsRequestResult.onNext(grantedPermissions)
    }

    private fun checkPermissions(
        view: AttachmentView,
        requiredPermissionsList: List<String>,
        onPermissionResult: (isGranted: Boolean) -> Unit
    ): Disposable {
        val notGrantedPermissions = view.getNotGrantedPermissions(requiredPermissionsList)
        if (notGrantedPermissions.isEmpty()) {
            onPermissionResult(true)
            return Disposables.disposed()
        }

        val resultDisposable = permissionsRequestResult.subscribe { grantedPermissionsList ->
            onPermissionResult(grantedPermissionsList.containsAll(notGrantedPermissions))
        }
        view.requestPermissions(notGrantedPermissions)
        return resultDisposable
    }
}