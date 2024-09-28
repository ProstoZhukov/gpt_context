package ru.tensor.sbis.info_decl.knowledge_ui

import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.verification_decl.permission.PermissionLevel

/**
 * @author am.boldinov
 */
interface KnowledgePermissionHandler {

    fun handle(permission: PermissionLevel?): Boolean

    interface Provider : Feature {

        fun getPermissionHandler(): KnowledgePermissionHandler
    }
}