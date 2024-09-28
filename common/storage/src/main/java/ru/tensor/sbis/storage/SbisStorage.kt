package ru.tensor.sbis.storage

import android.content.Context
import androidx.annotation.WorkerThread
import ru.tensor.sbis.verification_decl.account.UserAccount
import ru.tensor.sbis.verification_decl.login.LoginInterface
import java.io.File

/**
 * Хранилище
 *
 * @property context
 * @property loginInterfaceProvider поставщик интерфейса авторизации
 *
 * @author sa.nikitin
 */
abstract class SbisStorage(
    protected val context: Context,
    protected val loginInterfaceProvider: LoginInterface.Provider?
) {

    @WorkerThread
    @Throws(IllegalStateException::class)
    protected fun userDir(parentDir: File): File {
        return userDirOrNull(parentDir) ?: throw IllegalStateException("Current user is null")
    }

    @WorkerThread
    protected fun userDirOrNull(parentDir: File): File? {
        if(!StoragePlugin.customizationOptions.userDirEnabled) {
            return null
        }
        return loginInterfaceProvider?.loginInterface
            ?.getCurrentAccount()
            ?.let { File(parentDir, userDirName(it)) }
    }

    protected abstract fun userDirName(currentUser: UserAccount): String

}