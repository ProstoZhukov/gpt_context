package ru.tensor.sbis.common_attachments.permissions

import ru.tensor.sbis.common_attachments.AttachmentPresenterHelper
import ru.tensor.sbis.common_attachments.AttachmentView

/**
 * Вспомогательный класс для работы с полномочиями для вложений в [AttachmentPresenterHelper]
 *
 * @author sa.nikitin
 */
interface AttachmentsPermissionHelper {

    /**
     * Добавляет полномочия в список для запроса перед выполнением операции над вложениями
     *
     * @see requestPendingPermissions
     */
    fun addPermissionsToRequest(permission: String)

    /**
     * Запрос полномочий, которые добавлены методом [addPermissionsToRequest]
     */
    fun requestPendingPermissions(view: AttachmentView)

    /**
     * Проверяет наличие полномочий [permissions] и выполняет их запрос, если ещё не предоставлен. После получения
     * полномочий выполняется целевое действие [onPermissionResult]
     */
    fun withPermissions(view: AttachmentView, permissions: List<String>, onPermissionResult: (isGranted: Boolean) -> Unit)

    /**
     * Принимает список предоставленных пользователем полномочий
     */
    fun onPermissionsGranted(grantedPermissions: List<String>)
}