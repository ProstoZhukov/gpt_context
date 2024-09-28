package ru.tensor.sbis.verification_decl.permission

/**
 * Интерфейс, который предоставляет информацию об управляемой полномочиями области
 *
 * @author ma.kolpakov
 * Создан 11/26/2018
 */
interface PermissionScope {

    companion object {
        /**
         * Этот идентификатор нужно указывать для областей, у которых идентификатор в "онлайне"
         * (сейчас) не определен
         */
        const val UNKNOWN_SCOPE_ID: String = ""
    }

    /**
     * Идентификатор области
     */
    val id: String
}