package ru.tensor.sbis.onboarding.domain.util

import android.content.pm.PackageManager.PERMISSION_GRANTED
import ru.tensor.sbis.onboarding.contract.providers.content.CustomAction
import ru.tensor.sbis.onboarding.contract.providers.content.FeaturePage
import ru.tensor.sbis.onboarding.contract.providers.content.SystemPermissions
import ru.tensor.sbis.onboarding.domain.OnboardingRepository
import ru.tensor.sbis.onboarding.ui.utils.RequestPermissionDelegate
import javax.inject.Inject

/**
 * Хелпер для работы с разрешениями и действиями приветственного экрана
 *
 * @author as.chadov
 *
 * @param repository репозиторий содержимого приветственного экрана
 * @param permissionDelegate делегат опроса системных разрешений из вью
 *
 * @property permissionResultActions мапа действий выполняемых по результатам запроса разрешений
 */
internal class PermissionHelper @Inject constructor(
    private val repository: OnboardingRepository,
    private val permissionDelegate: RequestPermissionDelegate
) {

    private val permissionResultActions = mutableMapOf<Int, (Boolean) -> Unit>()
    private var lastCode = INITIAL_REQUEST_PERMISSION_CODE

    /**
     * Проверить необработанные разрешения
     *
     * @uuid идентификатор фичи (экрана)
     * @return true если есть необработанные разрешения
     */
    fun hasUnresolvedPermissions(uuid: String): Boolean =
        hasRequiredSystemPermissions(uuid) || hasRequiredCustomAction(uuid)

    /**
     * Опросить системные разрешения и пользовательские действия фичи
     *
     * @uuid идентификатор фичи (экрана)
     * @onResultAction действие, которое нужно выполнить по завершении опроса
     */
    fun askPermissionsAndAction(
        uuid: String,
        onResultAction: (Boolean) -> Unit
    ) = when {
        hasRequiredCustomAction(uuid)      -> {
            val action = getCustomAction(uuid)!!
            if (action.isFinite) {
                action.processed = true
            }
            action.execute { isSuccess ->
                if (hasRequiredSystemPermissions(uuid)) {
                    requestSystemPermission(uuid, onResultAction)
                } else {
                    onResultAction(isSuccess)
                }
            }
        }
        hasRequiredSystemPermissions(uuid) -> requestSystemPermission(uuid, onResultAction)
        else                               -> onResultAction(false)
    }

    /**
     * Обработать результат системных разрешений
     *
     * @requestCode уникальный код идентификации системного разрешения
     * @grantResults результаты опроса системных разрешений
     */
    fun onRequestPermissionsResult(
        requestCode: Int,
        grantResults: IntArray
    ) {
        if (permissionResultActions.containsKey(requestCode)) {
            val postponedAction = permissionResultActions.remove(requestCode)
            val granted =
                grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED
            postponedAction?.let { action -> action(granted) }
        }
    }

    private fun requestSystemPermission(
        featureUuid: String,
        onResultAction: (Boolean) -> Unit
    ) {
        val permissions = getSystemPermissions(featureUuid)
        if (permissions.isProcessing.not()) {
            permissions.isProcessing = true
            val requestCode = ++lastCode
            permissionResultActions[requestCode] = { granted ->
                onResultAction(granted)
                permissions.isProcessed = true
            }
            permissionDelegate.requestPermissions(permissions.values, requestCode)
        }
    }

    private fun hasRequiredSystemPermissions(uuid: String): Boolean {
        val permissions = getSystemPermissions(uuid)
        return if (permissions.isEmpty) {
            false
        } else permissions.isProcessed.not()
    }


    private fun hasRequiredCustomAction(uuid: String) =
        getCustomAction(uuid).let { it != null && !it.processed }

    private fun getSystemPermissions(uuid: String): SystemPermissions {
        val page = repository.findPageSafely(uuid)
        return if (page is FeaturePage) {
            page.permissions
        } else {
            SystemPermissions.EMPTY
        }
    }

    private fun getCustomAction(uuid: String): CustomAction? =
        repository.findPageSafely(uuid)?.let {
            if (it is FeaturePage && it.action != CustomAction.EMPTY) {
                it.action
            } else null
        }
}

private const val INITIAL_REQUEST_PERMISSION_CODE = 100
